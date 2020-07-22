package net.minecraft.text;

import com.google.common.collect.Lists;
import java.util.List;
import java.util.Objects;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_5481;
import net.minecraft.util.Language;

public abstract class BaseText implements MutableText {
	protected final List<Text> siblings = Lists.<Text>newArrayList();
	private class_5481 field_26623 = class_5481.field_26385;
	@Nullable
	@Environment(EnvType.CLIENT)
	private Language field_26624;
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

	@Environment(EnvType.CLIENT)
	@Override
	public class_5481 method_30937() {
		Language language = Language.getInstance();
		if (this.field_26624 != language) {
			this.field_26623 = language.method_30934(this);
			this.field_26624 = language;
		}

		return this.field_26623;
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
