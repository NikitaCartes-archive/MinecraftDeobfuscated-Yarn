package net.minecraft.text;

import com.google.common.collect.Lists;
import java.util.List;
import java.util.Objects;

public abstract class BaseText implements MutableText {
	protected final List<Text> siblings = Lists.<Text>newArrayList();
	private Style style = Style.EMPTY;

	@Override
	public MutableText append(Text text) {
		this.siblings.add(text);
		return this;
	}

	@Override
	public String asString() {
		return "";
	}

	@Override
	public List<Text> getSiblings() {
		return this.siblings;
	}

	@Override
	public MutableText setStyle(Style style) {
		this.style = style;
		return this;
	}

	@Override
	public Style getStyle() {
		return this.style;
	}

	public abstract BaseText copy();

	@Override
	public final MutableText shallowCopy() {
		BaseText baseText = this.copy();
		baseText.siblings.addAll(this.siblings);
		baseText.setStyle(this.style);
		return baseText;
	}

	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		} else if (!(obj instanceof BaseText)) {
			return false;
		} else {
			BaseText baseText = (BaseText)obj;
			return this.siblings.equals(baseText.siblings) && Objects.equals(this.getStyle(), baseText.getStyle());
		}
	}

	public int hashCode() {
		return Objects.hash(new Object[]{this.getStyle(), this.siblings});
	}

	public String toString() {
		return "BaseComponent{style=" + this.style + ", siblings=" + this.siblings + '}';
	}
}
