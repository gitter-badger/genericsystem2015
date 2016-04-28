package org.genericsystem.distributed.cacheonserver.ui.list;

import org.genericsystem.distributed.ui.HtmlElement;
import org.genericsystem.distributed.ui.components.HtmlOption;
import org.genericsystem.distributed.ui.components.HtmlSelect;
import org.genericsystem.distributed.ui.models.GenericCompositeModel;
import org.genericsystem.distributed.ui.models.GenericModel;

public class TypeSelectHtml<M extends GenericCompositeModel<SUBMODEL>, SUBMODEL extends GenericModel> extends HtmlSelect<M> {

	public TypeSelectHtml(HtmlElement<?, ?, ?> parent) {
		super(parent);
	}

	@Override
	protected void initChildren() {
		new HtmlOption<SUBMODEL>(this).forEach(GenericCompositeModel<SUBMODEL>::getSubModels).bindText(GenericModel::getString);
	}
}
