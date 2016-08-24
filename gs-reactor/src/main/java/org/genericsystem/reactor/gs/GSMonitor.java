package org.genericsystem.reactor.gs;

import org.genericsystem.reactor.gstag.HtmlButton;
import org.genericsystem.reactor.model.GenericModel;

public class GSMonitor extends GSSection {

	public GSMonitor(GSTag parent) {
		this(parent, FlexDirection.ROW);
	}

	public GSMonitor(GSTag parent, FlexDirection direction) {
		super(parent, direction);
		addStyle("justify-content", "space-around");
		addStyle("padding", "10px");
		new HtmlButton(this) {
			{
				setText("Save");
				bindAction(GenericModel::flush);
			}
		};
		new HtmlButton(this) {
			{
				setText("Cancel");
				bindAction(GenericModel::cancel);
			}
		};
		// new GSButton(this) {
		// {
		// setText("Collect");
		// bindAction(model -> System.gc());
		// }
		// };
	}

}