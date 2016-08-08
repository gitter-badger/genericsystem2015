package org.genericsystem.reactor.gs;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.genericsystem.api.core.exceptions.RollbackException;
import org.genericsystem.common.Generic;
import org.genericsystem.reactor.ReactorStatics;
import org.genericsystem.reactor.gs.GSSingleLinkComponentEditor.GSLinkComponentAdder;
import org.genericsystem.reactor.gs.GSSingleLinkComponentEditor.GSLinkComponentCreator;
import org.genericsystem.reactor.gs.GSSingleLinkComponentEditor.GSLinkComponentEditor;
import org.genericsystem.reactor.gstag.GSHyperLink;
import org.genericsystem.reactor.model.GenericModel;

import javafx.beans.binding.Bindings;
import javafx.beans.property.Property;

public class GSLinkEditor extends GSSection {

	protected GSSingleLinkComponentEditor components;

	public GSLinkEditor(GSTag parent) {
		this(parent, GSLinkComponentEditor::new);
	}

	public GSLinkEditor(GSTag parent, GSLinkComponentConstructor constructor) {
		super(parent, FlexDirection.ROW);
		createNewProperty(ReactorStatics.COMPONENTS);
		initProperty(ReactorStatics.COMPONENTS, model -> new ArrayList<Property<GenericModel>>());
		components = constructor.build(this);
	}

	@FunctionalInterface
	public interface GSLinkComponentConstructor {
		GSSingleLinkComponentEditor build(GSTag parent);
	}

	public static class GSLinkEditorWithRemoval extends GSLinkEditor {

		public GSLinkEditorWithRemoval(GSTag parent) {
			super(parent);
			new GSHyperLink(this) {
				{
					addStyle("justify-content", "center");
					addStyle("text-decoration", "none");
					addStyle("height", "100%");
					setText("×");
					bindAction(GenericModel::remove);
				}
			};
		}
	}

	public static class GSLinkCreator extends GSLinkEditor {

		public GSLinkCreator(GSTag parent) {
			this(parent, GSLinkComponentCreator::new);
		}

		public GSLinkCreator(GSTag parent, GSLinkComponentConstructor constructor) {
			super(parent, constructor);
			if (parent != null && parent.getParent() != null && parent.getParent().getParent() instanceof GSInstanceCreator)
				addPostfixBinding(
						model -> ((Map<Generic, List<Property<GenericModel>>>) getProperty(ReactorStatics.COMPONENTS_MAP, model).getValue()).put(model.getGeneric(), (List<Property<GenericModel>>) getProperty(ReactorStatics.COMPONENTS, model).getValue()));
		}
	}

	public static class GSLinkAdder extends GSLinkCreator {

		public GSLinkAdder(GSTag parent) {
			super(parent, GSLinkComponentAdder::new);
			addStyle("height", "100%");
			new GSHyperLink(this) {
				{
					addStyle("justify-content", "center");
					addStyle("text-decoration", "none");
					addStyle("height", "100%");
					setText("+");
					bindStyle(ReactorStatics.DISPLAY, ReactorStatics.DISPLAY, model -> Bindings.createStringBinding(() -> {
						List<Generic> selectedGenerics = ((List<Property<GenericModel>>) getProperty(ReactorStatics.COMPONENTS, model).getValue()).stream().filter(obs -> obs.getValue() != null).map(obs -> obs.getValue().getGeneric())
								.filter(gen -> gen != null).collect(Collectors.toList());
						return selectedGenerics.size() + 1 == model.getGeneric().getComponents().size() ? "flex" : "none";
					}, ((List<Property<GenericModel>>) getProperty(ReactorStatics.COMPONENTS, model).getValue()).stream().toArray(Property[]::new)));
					bindAction(model -> {
						try {
							List<Property<GenericModel>> selectedComponents = (List<Property<GenericModel>>) getProperty(ReactorStatics.COMPONENTS, model).getValue();
							List<Generic> selectedGenerics = selectedComponents.stream().filter(obs -> obs.getValue() != null).map(obs -> obs.getValue().getGeneric()).filter(gen -> gen != null).collect(Collectors.toList());
							model.getGenerics()[3].setHolder(model.getGeneric(), null, selectedGenerics.stream().toArray(Generic[]::new));
							selectedComponents.stream().forEach(sel -> sel.setValue(null));
						} catch (RollbackException e) {
							e.printStackTrace();
						}
					});
				}
			};
		}
	}
}