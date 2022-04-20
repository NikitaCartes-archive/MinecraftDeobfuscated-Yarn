package net.minecraft.text;

import com.google.common.collect.Lists;
import java.util.List;
import java.util.Objects;
import java.util.function.UnaryOperator;
import javax.annotation.Nullable;
import net.minecraft.class_7417;
import net.minecraft.util.Formatting;
import net.minecraft.util.Language;

/**
 * A text with mutation operations.
 */
public class MutableText implements Text {
	private final class_7417 field_39005;
	private final List<Text> field_39006;
	private Style field_39007;
	private OrderedText field_39008 = OrderedText.EMPTY;
	@Nullable
	private Language field_39009;

	MutableText(class_7417 arg, List<Text> list, Style style) {
		this.field_39005 = arg;
		this.field_39006 = list;
		this.field_39007 = style;
	}

	public static MutableText method_43477(class_7417 arg) {
		return new MutableText(arg, Lists.<Text>newArrayList(), Style.EMPTY);
	}

	@Override
	public class_7417 asString() {
		return this.field_39005;
	}

	@Override
	public List<Text> getSiblings() {
		return this.field_39006;
	}

	/**
	 * Sets the style of this text.
	 */
	public MutableText setStyle(Style style) {
		this.field_39007 = style;
		return this;
	}

	@Override
	public Style getStyle() {
		return this.field_39007;
	}

	/**
	 * Appends a literal text with content {@code text} to this text's siblings.
	 * 
	 * @param text the literal text content
	 */
	public MutableText append(String text) {
		return this.append(Text.method_43470(text));
	}

	/**
	 * Appends a text to this text's siblings.
	 * 
	 * @param text the sibling
	 */
	public MutableText append(Text text) {
		this.field_39006.add(text);
		return this;
	}

	/**
	 * Updates the style of this text.
	 * 
	 * @see Text#getStyle()
	 * @see #setStyle(Style)
	 * 
	 * @param styleUpdater the style updater
	 */
	public MutableText styled(UnaryOperator<Style> styleUpdater) {
		this.setStyle((Style)styleUpdater.apply(this.getStyle()));
		return this;
	}

	/**
	 * Fills the absent parts of this text's style with definitions from {@code
	 * styleOverride}.
	 * 
	 * @see Style#withParent(Style)
	 * 
	 * @param styleOverride the style that provides definitions for absent definitions in this text's style
	 */
	public MutableText fillStyle(Style styleOverride) {
		this.setStyle(styleOverride.withParent(this.getStyle()));
		return this;
	}

	/**
	 * Adds some formattings to this text's style.
	 * 
	 * @param formattings an array of formattings
	 */
	public MutableText formatted(Formatting... formattings) {
		this.setStyle(this.getStyle().withFormatting(formattings));
		return this;
	}

	/**
	 * Add a formatting to this text's style.
	 * 
	 * @param formatting a formatting
	 */
	public MutableText formatted(Formatting formatting) {
		this.setStyle(this.getStyle().withFormatting(formatting));
		return this;
	}

	@Override
	public OrderedText asOrderedText() {
		Language language = Language.getInstance();
		if (this.field_39009 != language) {
			this.field_39008 = language.reorder(this);
			this.field_39009 = language;
		}

		return this.field_39008;
	}

	public boolean equals(Object object) {
		if (this == object) {
			return true;
		} else {
			return !(object instanceof MutableText mutableText)
				? false
				: this.field_39005.equals(mutableText.field_39005) && this.field_39007.equals(mutableText.field_39007) && this.field_39006.equals(mutableText.field_39006);
		}
	}

	public int hashCode() {
		return Objects.hash(new Object[]{this.field_39005, this.field_39007, this.field_39006});
	}

	public String toString() {
		StringBuilder stringBuilder = new StringBuilder(this.field_39005.toString());
		boolean bl = !this.field_39007.isEmpty();
		boolean bl2 = !this.field_39006.isEmpty();
		if (bl || bl2) {
			stringBuilder.append('[');
			if (bl) {
				stringBuilder.append("style=");
				stringBuilder.append(this.field_39007);
			}

			if (bl && bl2) {
				stringBuilder.append(", ");
			}

			if (bl2) {
				stringBuilder.append("siblings=");
				stringBuilder.append(this.field_39006);
			}

			stringBuilder.append(']');
		}

		return stringBuilder.toString();
	}
}
