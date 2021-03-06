package org.genericsystem.reactor.gs;

import io.vertx.core.json.JsonObject;

import java.util.Map;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;

import org.genericsystem.reactor.Context;
import org.genericsystem.reactor.HtmlDomNode;
import org.genericsystem.reactor.ReactorStatics;
import org.genericsystem.reactor.Tag;
import org.genericsystem.reactor.gstag.HtmlOption;
import org.genericsystem.reactor.model.ObservableListExtractor;
import org.genericsystem.reactor.model.StringExtractor;
import org.genericsystem.reactor.modelproperties.ComponentsDefaults;
import org.genericsystem.reactor.modelproperties.SelectionDefaults;

public class GSSelect extends Tag implements SelectionDefaults, ComponentsDefaults {

	public HtmlOption optionElement;

	private GSSelect(Tag parent) {
		super(parent, "select");
		options();
		init();
		createSelectionProperty();
		bindBiDirectionalSelection(optionElement);
	}

	@Override
	protected HtmlDomNode createNode(HtmlDomNode parent, Context modelContext) {
		return new HtmlDomNode(parent, modelContext, this) {

			@Override
			public void handleMessage(JsonObject json) {
				if (UPDATE.equals(json.getString(MSG_TYPE))) {
					((SelectionDefaults) getTag()).getSelectionIndex(getModelContext()).setValue(json.getInteger(SELECTED_INDEX));
				}
			}
		};
	}

	protected void options() {
		optionElement = new HtmlOption(this) {
			{
				bindText();
				forEach(GSSelect.this);
			}
		};
	}

	protected void init() {

	}

	public static class CompositeSelectWithEmptyEntry extends GSSelect {

		public CompositeSelectWithEmptyEntry(Tag parent) {
			super(parent);
			addPrefixBinding(model -> {
				if ("Color".equals(StringExtractor.SIMPLE_CLASS_EXTRACTOR.apply(model.getGeneric()))) {
					Map<String, String> map = getDomNodeStyles(model);
					ChangeListener<String> listener = (o, old, newValue) -> map.put("background-color", newValue);
					ObservableValue<String> observable = getSelectionString(model);
					observable.addListener(listener);
					map.put("background-color", observable.getValue());
				}
			});
			optionElement.addPrefixBinding(model -> {
				if ("Color".equals(StringExtractor.SIMPLE_CLASS_EXTRACTOR.apply(model.getGeneric().getMeta())))
					optionElement.getDomNodeStyles(model).put("background-color", optionElement.getGenericStringProperty(model).getValue());
			});
		}

		@Override
		protected void options() {
			new HtmlOption(this);
			super.options();
		}

		@Override
		protected void init() {
			setSelectionShift(1);
		}
	}

	public static class ColorsSelect extends GSSelect {

		public ColorsSelect(Tag parent) {
			super(parent);
			bindStyle("background-color", SELECTION_STRING);
			optionElement.bindStyle("background-color", ReactorStatics.BACKGROUND, model -> optionElement.getGenericStringProperty(model));
		}
	}

	public static class InstanceCompositeSelect extends GSSelect {

		public InstanceCompositeSelect(Tag parent) {
			super(parent);
			addPostfixBinding(model -> getSelectionProperty(model).addListener((ov, ova, nva) -> model.getGenerics()[1].updateComponent(nva.getGeneric(), model.getGenerics()[1].getComponents().indexOf(model.getGeneric()))));
		}

		@Override
		public ObservableListExtractor getObservableListExtractor() {
			return ObservableListExtractor.SUBINSTANCES_OF_META;
		}
	}
}
