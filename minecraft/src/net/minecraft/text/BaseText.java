package net.minecraft.text;

import com.google.common.collect.Lists;
import java.util.List;
import java.util.Objects;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.util.Language;

public abstract class BaseText implements MutableText {
	protected final List<Text> siblings = Lists.<Text>newArrayList();
	private OrderedText orderedText = OrderedText.EMPTY;
	@Nullable
	@Environment(EnvType.CLIENT)
	private Language previousLanguage;
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
	public OrderedText asOrderedText() {
		Language language = Language.getInstance();
		if (this.previousLanguage != language) {
			this.orderedText = language.reorder(this);
			this.previousLanguage = language;
		}

		return this.orderedText;
	}

	public boolean equals(Object o) {
		if (this == o) {
			return true;
		} else if (!(o instanceof BaseText)) {
			return false;
		} else {
			BaseText baseText = (BaseText)o;
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
