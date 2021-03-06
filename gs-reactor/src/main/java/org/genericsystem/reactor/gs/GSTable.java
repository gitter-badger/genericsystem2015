package org.genericsystem.reactor.gs;

import org.genericsystem.reactor.Tag;
import org.genericsystem.reactor.gs.GSSubcellDisplayer.LinkTitleDisplayer;
import org.genericsystem.reactor.gstag.HtmlH2;
import org.genericsystem.reactor.model.ObservableListExtractor;
import org.genericsystem.reactor.model.StringExtractor;
import org.genericsystem.reactor.modelproperties.SelectionDefaults;

/**
 * @author Nicolas Feybesse
 *
 */
public class GSTable extends GSComposite implements SelectionDefaults {

	public GSTable(Tag parent) {
		super(parent, FlexDirection.COLUMN);
	}

	public GSTable(Tag parent, FlexDirection flexDirection) {
		super(parent, flexDirection);
		addStyle("flex", "1");
	}

	@Override
	protected void header() {
		titleHeader();
		columnsTitleSection();
		columnsInputSection();
	}

	protected void titleHeader() {
		new GSSection(this, this.getReverseDirection()) {
			{
				addStyle("background-color", "#ffa500");
				addStyle("margin-right", "1px");
				addStyle("margin-bottom", "1px");
				addStyle("color", "red");
				addStyle("justify-content", "center");
				new HtmlH2(this) {
					{
						setStringExtractor(StringExtractor.MANAGEMENT);
						bindText();
					}
				};
			}
		};
	}

	protected void columnsTitleSection() {
		new GSComposite(this, this.getReverseDirection()) {

			@Override
			protected void header() {
				new LinkTitleDisplayer(this);
			}

			@Override
			protected void sections() {
				new LinkTitleDisplayer(this) {
					{
						forEach(ObservableListExtractor.ATTRIBUTES_OF_TYPE);
					}
				};
			}

			@Override
			protected void footer() {
				new GSSection(this, this.getDirection()) {
					{
						if (this.getDirection().equals(FlexDirection.ROW)) {
							addStyle("flex", "0");
							addStyle("min-width", "100px");
						} else {
							addStyle("flex", "1");
						}
						addStyle("min-width", "100px");
						addStyle("background-color", "#ffa5a5");
						addStyle("margin-right", "1px");
						addStyle("margin-bottom", "1px");
					}
				};
			}
		};
	}

	protected void columnsInputSection() {
		new GSInstanceBuilder(this, this.getReverseDirection());
	}

	@Override
	protected void sections() {
		Tag selectableTag = new GSRowDisplayer(this, this.getReverseDirection()) {
			{
				addStyle("flex", "1");
				forEach(ObservableListExtractor.SUBINSTANCES);
			}
		};
		bindSelection(selectableTag);
	}
}
