package org.genericsystem.ui;

public class ViewContext<N> {
	private final Element<N> template;
	private final N node;
	private final ViewContext<?> parent;

	public <CHILDNODE> ViewContext(ModelContext modelContext, Element<N> template, N node, ViewContext<?> parent) {
		this.template = template;
		this.node = node;
		this.parent = parent;
		init(modelContext);
	}

	private <CHILDNODE> void init(ModelContext modelContext) {
		modelContext.register(this);
		this.template.getBootList().forEach(boot -> boot.init(node));
		for (Binding<N, ?, ?> binding : template.bindings)
			binding.init(modelContext, this, null);
		for (Element<CHILDNODE> childElement : template.<CHILDNODE> getChildren()) {
			for (Binding<CHILDNODE, ?, ?> metaBinding : childElement.metaBindings)
				metaBinding.init(modelContext, (ViewContext<CHILDNODE>) this, (Element) childElement);
			if (childElement.metaBindings.isEmpty())
				new ViewContext<>(modelContext, childElement, childElement.createNode(), this);
		}
		if (parent != null) {
			// System.out.println("add node : " + node + " to parent : " + getParent().getNode() + " list = " + template.getGraphicChildren(getParent().getNode()));
			template.getGraphicChildren(parent.getNode()).add(node);
		}
	}

	public N getNode() {
		return node;
	}

	void destroyChild() {
		template.getGraphicChildren(parent.getNode()).remove(getNode());
	}

}