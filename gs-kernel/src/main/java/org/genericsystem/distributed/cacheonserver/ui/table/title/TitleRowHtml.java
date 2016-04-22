package org.genericsystem.distributed.cacheonserver.ui.table.title;

import org.genericsystem.distributed.cacheonserver.ui.table.TypeTableHtml;
import org.genericsystem.distributed.ui.components.HtmlLabel;
import org.genericsystem.distributed.ui.components.HtmlSection;
import org.genericsystem.distributed.ui.components.HtmlStrong;

public class TitleRowHtml extends HtmlSection {

	public TitleRowHtml(TypeTableHtml parent) {
		super(parent);
		addStyleClass("gsrow");
		addStyleClass("gssubtitlerow");
	}

	@Override
	protected void initChildren() {
		new HtmlLabel(new HtmlStrong(new HtmlSection(this).addStyleClass("gscell").addStyleClass("gstitlecell"))).bindText(TitleRowModel::getFirstCellString);
		new TitleCellHtml(this).forEach(TitleRowModel::getSubModels);
		new HtmlSection(this).addStyleClass("gscell").addStyleClass("gsbuttoncell");
	}
}