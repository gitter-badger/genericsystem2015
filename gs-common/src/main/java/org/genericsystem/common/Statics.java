package org.genericsystem.common;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.genericsystem.defaults.DefaultGeneric;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Nicolas Feybesse
 *
 */
public class Statics {

	private static Logger log = LoggerFactory.getLogger(Statics.class);

	private static ThreadLocal<Long> threadDebugged = new ThreadLocal<>();

	public final static String ENGINE_VALUE = "Engine";

	public static final long MILLI_TO_NANOSECONDS = 1000000L;

	public static final int ATTEMPT_SLEEP = 15;
	public static final int ATTEMPTS = 50;
	public static final int HTTP_ATTEMPTS = 1;

	public static final long CONCURRENCY_CONTROL_EXCEPTION = -1;
	public static final long OTHER_EXCEPTION = -2;
	public static final long ROLLBACK_EXCEPTION = -1;

	public static final long GARBAGE_PERIOD = 1000L;
	public static final long GARBAGE_INITIAL_DELAY = 1000L;
	public static final long LIFE_TIMEOUT = 1386174608777L;// 30 minutes
	public final static String DEFAULT_HOST = "0.0.0.0";
	public final static int DEFAULT_PORT = 8080;
	public final static int ENGINES_DEFAULT_PORT = 8082;

	public final static Vertex[] EMPTY = new Vertex[] {};

	public static final long SERVER_TIMEOUT = 1000;
	public static final TimeUnit SERVER_TIMEOUT_UNIT = TimeUnit.MILLISECONDS;

	public static void debugCurrentThread() {
		threadDebugged.set(System.currentTimeMillis());
	}

	public static void stopDebugCurrentThread() {
		threadDebugged.remove();
	}

	public static boolean isCurrentThreadDebugged() {
		return threadDebugged.get() != null;
	}

	public static void logTimeIfCurrentThreadDebugged(String message) {
		if (isCurrentThreadDebugged())
			log.info(message + " : " + (System.currentTimeMillis() - threadDebugged.get()));
	}

	public static class Supers<T extends DefaultGeneric<T>> extends ArrayList<T> {
		private static final long serialVersionUID = 6163099887384346235L;

		public Supers(List<T> adds) {
			adds.forEach(this::add);
		}

		public Supers(List<T> adds, T lastAdd) {
			this(adds);
			add(lastAdd);
		}

		public Supers(List<T> adds, List<T> otherAdds) {
			this(adds);
			otherAdds.forEach(this::add);
		}

		@Override
		public boolean add(T candidate) {
			for (T element : this)
				if (element.inheritsFrom(candidate))
					return false;
			Iterator<T> it = iterator();
			while (it.hasNext())
				if (candidate.inheritsFrom(it.next()))
					it.remove();
			return super.add(candidate);
		}
	}

	// public static <T extends IVertex<T>> List<T>
	// reverseCollections(Collection<T> linkedHashSet) {
	// List<T> dependencies = new ArrayList<>(linkedHashSet);
	// Collections.reverse(dependencies);
	// return dependencies;
	// }

}
