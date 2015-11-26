package org.genericsystem.distributed.cacheonclient;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

import javafx.collections.ObservableList;

import org.genericsystem.api.core.exceptions.ConcurrencyControlException;
import org.genericsystem.common.Generic;
import org.genericsystem.kernel.Statics;
import org.testng.annotations.Test;

@Test
public class ObservableListPromiseTest extends AbstractTest {
	@Test(invocationCount = 1)
	public void test001_ObservableEngineDependenciesTest() throws InterruptedException, ExecutionException, TimeoutException {
		CocClientEngine engine = new CocClientEngine();

		ObservableList<Generic> dependenciesObservableList = engine.getCurrentCache().getDependenciesPromise(engine).get(Statics.SERVER_TIMEOUT, Statics.SERVER_TIMEOUT_UNIT);

		// System.out.println("- " + engine.getCurrentCache().getDependencies(engine).toList().equals(dependenciesObservableList.get(Statics.SERVER_TIMEOUT, Statics.SERVER_TIMEOUT_UNIT)));
		// System.out.println("- " + dependenciesObservableList.get(Statics.SERVER_TIMEOUT, Statics.SERVER_TIMEOUT_UNIT));
		// System.out.println("- " + engine.getCurrentCache().getDependencies(engine).toList());
		assert engine.getCurrentCache().getDependencies(engine).toList().equals(dependenciesObservableList);
	}

	@Test(invocationCount = 1)
	public void test001_ObservableEngineDependenciesNotBloquingTest() throws InterruptedException, ExecutionException, TimeoutException {
		CocClientEngine engine = new CocClientEngine();

		CompletableFuture<ObservableList<Generic>> dependenciesPromise = engine.getCurrentCache().getDependenciesPromise(engine);

		dependenciesPromise.thenAccept((dependenciesObservableList) -> {
			assert engine.getCurrentCache().getDependencies(engine).toList().equals(dependenciesObservableList);
		});
	}

	@Test(invocationCount = 1)
	public void test002_ObservableEngineDependenciesAddTest() throws InterruptedException, ExecutionException, TimeoutException, ConcurrencyControlException {
		CocClientEngine engine = new CocClientEngine();

		CompletableFuture<ObservableList<Generic>> dependenciesObservableList = engine.getCurrentCache().getDependenciesPromise(engine);

		assert engine.getCurrentCache().getDependencies(engine).toList().equals(dependenciesObservableList.get(Statics.SERVER_TIMEOUT, Statics.SERVER_TIMEOUT_UNIT));

		Generic g1 = engine.addInstance("Instance0");
		Generic g2 = engine.addInstance("Instance1");
		engine.addInstance("Instance2");

		g1.remove();
		engine.getCurrentCache().tryFlush();
		g2.remove();

		assert engine.getCurrentCache().getDependencies(engine).toList().equals(dependenciesObservableList.get(Statics.SERVER_TIMEOUT, Statics.SERVER_TIMEOUT_UNIT));
	}

	@Test(invocationCount = 1)
	public void test003_ObservableEngineDependenciesClearTest() throws InterruptedException, ConcurrencyControlException, ExecutionException, TimeoutException {

		CocClientEngine engine = new CocClientEngine();

		CompletableFuture<ObservableList<Generic>> dependenciesObservableList = engine.getCurrentCache().getDependenciesPromise(engine);

		engine.addInstance("InstanceL1_0");
		engine.addInstance("InstanceL1_1");
		Generic l1g2 = engine.addInstance("InstanceL1_2");
		engine.addInstance("InstanceL1_3");
		engine.addInstance("InstanceL1_4");

		assert engine.getCurrentCache().getDependencies(engine).toList().equals(dependenciesObservableList.get(Statics.SERVER_TIMEOUT, Statics.SERVER_TIMEOUT_UNIT));

		engine.getCurrentCache().mount();

		Generic l2g0 = engine.addInstance("InstanceL2_0");
		Generic l2g1 = engine.addInstance("InstanceL2_1");
		engine.addInstance("InstanceL2_2");
		l2g0.remove();
		l1g2.remove();

		assert engine.getCurrentCache().getDependencies(engine).toList().equals(dependenciesObservableList.get(Statics.SERVER_TIMEOUT, Statics.SERVER_TIMEOUT_UNIT));

		engine.getCurrentCache().mount();

		l2g1.remove();
		engine.addInstance("InstanceL3_0");
		engine.addInstance("InstanceL3_1");
		engine.addInstance("InstanceL3_2");

		engine.getCurrentCache().clear();

		// System.out.println("- " + engine.getCurrentCache().getDependencies(engine).toList().equals(dependenciesObservableList.get(Statics.SERVER_TIMEOUT, Statics.SERVER_TIMEOUT_UNIT)));
		// System.out.println("- " + dependenciesObservableList.get(Statics.SERVER_TIMEOUT, Statics.SERVER_TIMEOUT_UNIT));
		// System.out.println("- " + engine.getCurrentCache().getDependencies(engine).toList());

		assert engine.getCurrentCache().getDependencies(engine).toList().equals(dependenciesObservableList.get(Statics.SERVER_TIMEOUT, Statics.SERVER_TIMEOUT_UNIT));
	}
}