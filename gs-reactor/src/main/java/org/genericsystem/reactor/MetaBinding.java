package org.genericsystem.reactor;

import java.util.function.BiFunction;
import java.util.function.Function;

import javafx.collections.ObservableList;

import org.genericsystem.common.Generic;

public class MetaBinding<BETWEEN> {
	private final Function<Context, ObservableList<BETWEEN>> betweenChildren;
	private final BiFunction<Context, BETWEEN, Context> modelBuilder;

	private static BiFunction<Context, Generic, Context> MODEL_BUILDER = (model, generic) -> new Context(model, Context.addToGenerics(generic, model.getGenerics()));
	private static BiFunction<Context, Context, Context> MODEL_CLONER = (model, subModel) -> new Context(model, subModel.getGenerics());

	public MetaBinding(Function<Context, ObservableList<BETWEEN>> betweenChildren, BiFunction<Context, BETWEEN, Context> modelBuilder) {
		this.betweenChildren = betweenChildren;
		this.modelBuilder = modelBuilder;
	}

	public ObservableList<BETWEEN> buildBetweenChildren(Context model) {
		return betweenChildren.apply(model);
	}

	public Context buildModel(Context parent, BETWEEN betweenChild) {
		return modelBuilder.apply(parent, betweenChild);
	}

	static MetaBinding<Context> selectMetaBinding(Function<Context, ObservableList<Context>> betweenChildren) {
		return new MetaBinding<Context>(betweenChildren, MODEL_CLONER);
	}

	static MetaBinding<Generic> forEachMetaBinding(Function<Context, ObservableList<Generic>> betweenChildren) {
		return new MetaBinding<Generic>(betweenChildren, MODEL_BUILDER);
	}

}