package org.genericsystem.reactor.gs;

import java.util.function.BiConsumer;
import java.util.function.Consumer;

import org.genericsystem.reactor.Tag;
import org.genericsystem.reactor.gstag.HtmlH1;
import org.genericsystem.reactor.gstag.HtmlH2;

public class GSSection extends Tag {
	private final FlexDirection direction;

	public GSSection(Tag parent) {
		this(parent, FlexDirection.COLUMN);
	}

	public GSSection(Tag parent, FlexDirection direction) {
		super(parent, "section");
		this.direction = direction;
		addStyle("display", "flex");
		addStyle("flex-direction", direction.toString());
		addStyle("flex-wrap", "nowrap");
	}

	public FlexDirection getDirection() {
		return direction;
	}

	public FlexDirection getReverseDirection() {
		return getDirection().reverse();
	}

	public static class GenericColumn extends GSSection {
		public GenericColumn(Tag parent) {
			super(parent, FlexDirection.COLUMN);
		}
	}

	public static class GenericRow extends GSSection {
		public GenericRow(Tag parent) {
			super(parent, FlexDirection.ROW);
		}
	}

	public static class GenericRowWrapper extends GSSection {
		public GenericRowWrapper(Tag parent, FlexDirection direction, Consumer<Tag> consumer) {
			super(parent, direction);
			addStyle("justify-content", "center");
			consumer.accept(this);
		}

		public <T> GenericRowWrapper(Tag parent, FlexDirection direction, BiConsumer<Tag, T> consumer, T arg) {
			super(parent, direction);
			addStyle("justify-content", "center");
			consumer.accept(this, arg);
		}
	}

	public static class GenericH1Section extends GenericRowWrapper {
		public GenericH1Section(Tag parent, String text) {
			super(parent, FlexDirection.ROW, HtmlH1::new, text);
		}
	}

	public static class GenericH2Section extends GenericRowWrapper {
		public GenericH2Section(Tag parent, String text) {
			super(parent, FlexDirection.ROW, HtmlH2::new, text);
		}
	}
}