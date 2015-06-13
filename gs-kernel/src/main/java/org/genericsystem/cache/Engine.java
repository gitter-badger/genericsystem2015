package org.genericsystem.cache;

import java.io.Serializable;

import javassist.util.proxy.MethodHandler;
import javassist.util.proxy.ProxyObject;

import org.genericsystem.cache.Cache.ContextEventListener;
import org.genericsystem.common.AbstractContext;
import org.genericsystem.common.AbstractRoot;
import org.genericsystem.common.Vertex;
import org.genericsystem.kernel.Root;
import org.genericsystem.kernel.Statics;

public class Engine extends AbstractRoot<Generic> implements Generic {

	private Root server;

	public Engine(Class<?>... userClasses) {
		this(Statics.ENGINE_VALUE, userClasses);
	}

	public Engine(Serializable engineValue, Class<?>... userClasses) {
		this(engineValue, null, userClasses);
	}

	public Engine(Serializable engineValue, String persistentDirectoryPath, Class<?>... userClasses) {
		super(engineValue, persistentDirectoryPath, userClasses);
	}

	@Override
	public Engine getRoot() {
		return this;
	}

	@Override
	protected void initSubRoot(Serializable engineValue, String persistentDirectoryPath, Class<?>... userClasses) {
		server = new Root(engineValue, persistentDirectoryPath, userClasses);
	};

	@Override
	public Cache newCache() {
		return new Cache(this);
	}

	@Override
	protected void flushContext() {
		getCurrentCache().flush();
	}

	public Cache newCache(ContextEventListener<Generic> listener) {
		return new Cache(new Transaction(this), listener);
	}

	protected Cache start(Cache cache) {
		contextWrapper.set(cache);
		return cache;
	}

	protected void stop(Cache cache) {
		assert contextWrapper.get() == cache;
		contextWrapper.set(null);
	}

	@Override
	public Cache getCurrentCache() {
		Cache currentCache = (Cache) contextWrapper.get();
		if (currentCache == null)
			throw new IllegalStateException("Unable to find the current cache. Did you miss to call start() method on it ?");
		return currentCache;
	}

	public static class LocalContextWrapper extends InheritableThreadLocal<Cache> implements Wrapper<Generic> {
		@Override
		public void set(AbstractContext<Generic> context) {
			super.set((Cache) context);
		}
	}

	@Override
	protected Wrapper<Generic> buildContextWrapper() {
		return new LocalContextWrapper();
	}

	@Override
	public Generic getGenericByTs(long ts) {
		Generic generic = idsMap.get(ts);
		if (generic == null) {
			@SuppressWarnings("unchecked")
			Vertex vertex = (((AbstractRootWrapper) ((ProxyObject) server.getGenericByTs(ts)).getHandler()).getVertex());
			if (vertex == null)
				return null;
			Class<?> clazz = server.getAnnotedClass(server.getGenericByTs(ts));
			generic = init(clazz, vertex);// , ts == vertex.getMeta() ? null : getGenericByTs(vertex.getMeta()), ts, vertex.getMeta(), vertex.getSupers(), vertex.getValue(), vertex.getComponents(), vertex.getLifeManager());
			idsMap.put(ts, generic);
		}
		return generic;
	}

	@Override
	public final Generic[] newTArray(int dim) {
		return new Generic[dim];
	}

	@Override
	public void close() {
		server.close();
	}

	@Override
	protected Class<Generic> getTClass() {
		return Generic.class;
	}

	public Root getServer() {
		return server;
	}

	@Override
	protected MethodHandler buildHandler(Vertex vertex) {
		return new EngineWrapper(vertex);
	}

	class EngineWrapper extends AbstractRootWrapper {

		private EngineWrapper(Vertex vertex) {
			super(vertex);
		}

		@Override
		protected Engine getRoot() {
			return Engine.this;
		}
	}
}