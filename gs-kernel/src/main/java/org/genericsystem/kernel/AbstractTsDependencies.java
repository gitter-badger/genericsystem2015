package org.genericsystem.kernel;

import java.util.Spliterators;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import org.genericsystem.common.AbstractIterator;
import org.genericsystem.common.Generic;
import org.genericsystem.kernel.AbstractServer.RootServerHandler;

/**
 * @author Nicolas Feybesse
 *
 */
abstract class AbstractTsDependencies {

	private Node head = null;
	private Node tail = null;
	private final ConcurrentHashMap<Generic, Generic> map = new ConcurrentHashMap<>();

	public abstract LifeManager getLifeManager();

	public Generic get(Generic generic, long ts) {
		Generic result = map.get(generic);// this no lock read requires a concurrent hash map
		if (result == null) {
			LifeManager lifeManager = getLifeManager();
			lifeManager.readLock();
			try {
				result = map.get(generic);
				lifeManager.atomicAdjustLastReadTs(ts);
			} finally {
				lifeManager.readUnlock();
			}
		}

		if (result != null && ((RootServerHandler) result.getProxyHandler()).getLifeManager().isAlive(ts))
			return result;
		return null;
	}

	public void add(Generic element) {
		assert element != null;
		// assert getLifeManager().isWriteLockedByCurrentThread();
		Node newNode = new Node(element);
		if (head == null)
			head = newNode;
		else
			tail.next = newNode;
		tail = newNode;
		Generic result = map.put(element, element);
		assert result == null;
	}

	public boolean remove(Generic generic) {
		assert generic != null : "generic is null";
		assert head != null : "head is null";

		Node currentNode = head;

		Generic currentContent = currentNode.content;
		if (generic.equals(currentContent)) {
			Node next = currentNode.next;
			head = next != null ? next : null;
			return true;
		}

		Node nextNode = currentNode.next;
		while (nextNode != null) {
			Generic nextGeneric = nextNode.content;
			Node nextNextNode = nextNode.next;
			if (generic.equals(nextGeneric)) {
				nextNode.content = null;
				if (nextNextNode == null)
					tail = currentNode;
				currentNode.next = nextNextNode;
				map.remove(generic);
				return true;
			}
			currentNode = nextNode;
			nextNode = nextNextNode;
		}
		return false;
	}

	public Stream<Generic> stream(long ts) {
		return StreamSupport.stream(Spliterators.spliteratorUnknownSize(new InternalIterator(ts), 0), false);
	}

	private class InternalIterator extends AbstractIterator<Node, Generic> {

		private final long ts;

		private InternalIterator(long iterationTs) {
			ts = iterationTs;
		}

		@Override
		protected void advance() {
			for (;;) {
				Node nextNode = (next == null) ? head : next.next;
				if (nextNode == null) {
					LifeManager lifeManager = getLifeManager();
					lifeManager.readLock();
					try {
						nextNode = (next == null) ? head : next.next;
						if (nextNode == null) {
							next = null;
							lifeManager.atomicAdjustLastReadTs(ts);
							return;
						}
					} finally {
						lifeManager.readUnlock();
					}
				}
				next = nextNode;
				Generic content = next.content;
				if (content != null && ((RootServerHandler) content.getProxyHandler()).getLifeManager().isAlive(ts))
					break;

			}
		}

		@Override
		protected Generic project() {
			return next.content;
		}
	}

	private static class Node {
		Generic content;
		Node next;

		private Node(Generic content) {
			this.content = content;
		}
	}

}
