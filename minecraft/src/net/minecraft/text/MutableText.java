package net.minecraft.text;

import com.google.common.collect.Lists;
import java.util.List;
import java.util.Objects;
import java.util.function.UnaryOperator;
import javax.annotation.Nullable;
import net.minecraft.util.Formatting;
import net.minecraft.util.Language;

/**
 * The text implementation, with mutation operations.
 */
public class MutableText implements Text {
	private final TextContent content;
	private final List<Text> siblings;
	private Style style;
	private OrderedText ordered = OrderedText.EMPTY;
	@Nullable
	private Language language;

	MutableText(TextContent content, List<Text> siblings, Style style) {
		this.content = content;
		this.siblings = siblings;
		this.style = style;
	}

	/**
	 * Creates a piece of mutable text with the given content, with no sibling
	 * and style.
	 */
	public static MutableText of(TextContent content) {
		return new MutableText(content, Lists.<Text>newArrayList(), Style.EMPTY);
	}

	@Override
	public TextContent getContent() {
		return this.content;
	}

	@Override
	public List<Text> getSiblings() {
		return this.siblings;
	}

	/**
	 * Sets the style of this text.
	 */
	public MutableText setStyle(Style style) {
		this.style = style;
		return this;
	}

	@Override
	public Style getStyle() {
		return this.style;
	}

	/**
	 * Appends a literal text with content {@code text} to this text's siblings.
	 * 
	 * @param text the literal text content
	 */
	public MutableText append(String text) {
		return this.append(Text.literal(text));
	}

	/**
	 * Appends a text to this text's siblings.
	 * 
	 * @param text the sibling
	 */
	public MutableText append(Text text) {
		this.siblings.add(text);
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

	/**
	 * {@return the text with the RGB color {@color}}
	 */
	public MutableText withColor(int color) {
		this.setStyle(this.getStyle().withColor(color));
		return this;
	}

	@Override
	public OrderedText asOrderedText() {
		Language language = Language.getInstance();
		if (this.language != language) {
			this.ordered = language.reorder(this);
			this.language = language;
		}

		return this.ordered;
	}

	public boolean equals(Object o) {
		if (this == o) {
			return true;
		} else {
			return !(o instanceof MutableText mutableText)
				? false
				: this.content.equals(mutableText.content) && this.style.equals(mutableText.style) && this.siblings.equals(mutableText.siblings);
		}
	}

	public int hashCode() {
		return Objects.hash(new Object[]{this.content, this.style, this.siblings});
	}

	public String toString() {
		StringBuilder stringBuilder = new StringBuilder(this.content.toString());
		boolean bl = !this.style.isEmpty();
		boolean bl2 = !this.siblings.isEmpty();
		if (bl || bl2) {
			stringBuilder.append('[');
			if (bl) {
				stringBuilder.append("style=");
				stringBuilder.append(this.style);
			}

			if (bl && bl2) {
				stringBuilder.append(", ");
			}

			if (bl2) {
				stringBuilder.append("siblings=");
				stringBuilder.append(this.siblings);
			}

			stringBuilder.append(']');
		}

		return stringBuilder.toString();
	}
}
