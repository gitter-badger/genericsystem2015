package org.genericsystem.cache;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;

import org.genericsystem.api.core.IteratorSnapshot;
import org.genericsystem.api.core.Snapshot;
import org.genericsystem.api.core.exceptions.ConcurrencyControlException;
import org.genericsystem.api.core.exceptions.OptimisticLockConstraintViolationException;
import org.genericsystem.common.IDifferential;
import org.genericsystem.common.Vertex;

public class ClientTransaction implements IDifferential<ClientGeneric> {

	private final ClientEngine engine;
	private final long ts;

	protected ClientTransaction(ClientEngine engine, long ts) {
		this.engine = engine;
		this.ts = ts;
	}

	protected ClientTransaction(ClientEngine engine) {
		this(engine, engine.pickNewTs());
	}

	@Override
	public long getTs() {
		return ts;
	}

	// @Override
	// protected Checker<Generic> buildChecker() {
	// return new Checker<Generic>(Transaction.this) {
	// @Override
	// public void checkAfterBuild(boolean isOnAdd, boolean isFlushTime, Generic
	// vertex) throws RollbackException {
	// checkSystemConstraintsAfterBuild(isOnAdd, isFlushTime, vertex);// Check
	// only system constraints on transactions
	// }
	// };
	// }

	@Override
	public void apply(Snapshot<ClientGeneric> removes,
			Snapshot<ClientGeneric> adds) throws ConcurrencyControlException,
			OptimisticLockConstraintViolationException {
		assert adds.stream().allMatch(
				add -> add.getOtherTs()[0] == Long.MAX_VALUE);
		engine.getServer().apply(getTs(),
				removes.stream().mapToLong(g -> g.getTs()).toArray(),
				adds.stream().map(g -> g.getVertex()).toArray(Vertex[]::new));
		removes.forEach(remove -> remove.getComponents().forEach(
				component -> dependenciesMap.remove(component)));
		removes.forEach(remove -> remove.getComponents().forEach(
				superG -> dependenciesMap.remove(superG)));
		removes.forEach(remove -> dependenciesMap.remove(remove.getMeta()));
		removes.forEach(remove -> dependenciesMap.remove(remove));
		adds.forEach(add -> add.getComponents().forEach(
				component -> dependenciesMap.remove(component)));
		adds.forEach(add -> add.getComponents().forEach(
				superG -> dependenciesMap.remove(superG)));
		adds.forEach(add -> dependenciesMap.remove(add.getMeta()));
		adds.forEach(add -> dependenciesMap.remove(add));
		adds.forEach(add -> add.getOtherTs()[0] = getTs());
	}

	// @Override
	ClientEngine getRoot() {
		return engine;
	}

	private Map<ClientGeneric, Snapshot<ClientGeneric>> dependenciesMap = new HashMap<>();

	@Override
	public Snapshot<ClientGeneric> getDependencies(ClientGeneric generic) {
		Snapshot<ClientGeneric> dependencies = dependenciesMap.get(generic);
		if (dependencies == null) {
			final Map<ClientGeneric, ClientGeneric> container = Arrays
					.stream(engine.getServer().getDependencies(getTs(),
							generic.getTs()))
					.mapToObj(ts -> getRoot().getGenericById(ts))
					.collect(
							Collectors.toMap(
									g -> g,
									g -> g,
									(u, v) -> {
										throw new IllegalStateException(String
												.format("Duplicate key %s", u));
									}, LinkedHashMap::new));
			dependencies = new IteratorSnapshot<ClientGeneric>() {
				@Override
				public Iterator<ClientGeneric> iterator() {
					return container.keySet().iterator();
				}

				@Override
				public ClientGeneric get(Object o) {
					return container.get(o);
				}
			};
			Snapshot<ClientGeneric> result = dependenciesMap.put(generic,
					dependencies);
			assert result == null;
		}
		return dependencies;
	}
}