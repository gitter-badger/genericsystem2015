package org.genericsystem.distributed.cacheonclient.observables;

import java.util.concurrent.CompletableFuture;
import java.util.function.Function;
import java.util.function.Predicate;

import javafx.beans.binding.SetBinding;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.collections.ObservableSet;

import org.genericsystem.common.Vertex;

public class CompletableObservableSnapshot2<E> extends SetBinding<E> implements ObservableSnapshot<E> {

	private final CompletableListObservableValue<E> observable;

	public CompletableObservableSnapshot2(CompletableFuture<Vertex[]> promise, Function<Vertex, E> extractor) {
		observable = new CompletableListObservableValue<>(extractor);
		bind(observable);
		get();
		observable.launch(promise);

	}

	@Override
	protected ObservableSet<E> computeValue() {
		return new ListWrapperContainerObservableSnapshot<>(observable.getValue());
	}

	@Override
	public E get(int index) {
		return ((ObservableSnapshot<E>) get()).get(index);
	}

	@Override
	public ObservableSnapshot<E> filtered(Predicate<E> predicate) {
		return new FilterObservableSnapshotImpl<>(this, predicate);
	}

	@Override
	public ObservableSnapshot<E> filtered(ObservableValue<Predicate<E>> predicate) {
		return new ObservableFilterObservableSnapshotImpl<>(this, predicate);
	}

	@Override
	public ObservableSnapshot<E> concat(ObservableSnapshot<E> toConcatenate) {
		return new ConcatObservableSnapshotImpl<>(this, toConcatenate);
	}

	@Override
	public ObservableList<E> toObservableList() {
		return ((ObservableSnapshot<E>) get()).toObservableList();
	}
}