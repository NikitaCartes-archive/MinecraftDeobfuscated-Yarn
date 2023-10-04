package net.minecraft.text;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Objects;
import java.util.Optional;
import javax.annotation.Nullable;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;

/**
 * The style of a {@link Text}, representing cosmetic attributes. It includes
 * font, formatting, click/hover events (actions), color, etc.
 * 
 * <p>A style is immutable.
 * 
 * @see Text
 */
public class Style {
	/**
	 * An empty style.
	 */
	public static final Style EMPTY = new Style(null, null, null, null, null, null, null, null, null, null);
	/**
	 * The identifier for the default font of a style.
	 */
	public static final Identifier DEFAULT_FONT_ID = new Identifier("minecraft", "default");
	@Nullable
	final TextColor color;
	@Nullable
	final Boolean bold;
	@Nullable
	final Boolean italic;
	@Nullable
	final Boolean underlined;
	@Nullable
	final Boolean strikethrough;
	@Nullable
	final Boolean obfuscated;
	@Nullable
	final ClickEvent clickEvent;
	@Nullable
	final HoverEvent hoverEvent;
	@Nullable
	final String insertion;
	@Nullable
	final Identifier font;

	private static Style of(
		Optional<TextColor> color,
		Optional<Boolean> bold,
		Optional<Boolean> italic,
		Optional<Boolean> underlined,
		Optional<Boolean> strikethrough,
		Optional<Boolean> obfuscated,
		Optional<ClickEvent> optional,
		Optional<HoverEvent> optional2,
		Optional<String> optional3,
		Optional<Identifier> optional4
	) {
		Style style = new Style(
			(TextColor)color.orElse(null),
			(Boolean)bold.orElse(null),
			(Boolean)italic.orElse(null),
			(Boolean)underlined.orElse(null),
			(Boolean)strikethrough.orElse(null),
			(Boolean)obfuscated.orElse(null),
			(ClickEvent)optional.orElse(null),
			(HoverEvent)optional2.orElse(null),
			(String)optional3.orElse(null),
			(Identifier)optional4.orElse(null)
		);
		return style.equals(EMPTY) ? EMPTY : style;
	}

	private Style(
		@Nullable TextColor color,
		@Nullable Boolean bold,
		@Nullable Boolean italic,
		@Nullable Boolean underlined,
		@Nullable Boolean strikethrough,
		@Nullable Boolean obfuscated,
		@Nullable ClickEvent clickEvent,
		@Nullable HoverEvent hoverEvent,
		@Nullable String insertion,
		@Nullable Identifier font
	) {
		this.color = color;
		this.bold = bold;
		this.italic = italic;
		this.underlined = underlined;
		this.strikethrough = strikethrough;
		this.obfuscated = obfuscated;
		this.clickEvent = clickEvent;
		this.hoverEvent = hoverEvent;
		this.insertion = insertion;
		this.font = font;
	}

	/**
	 * Returns the color of this style.
	 */
	@Nullable
	public TextColor getColor() {
		return this.color;
	}

	/**
	 * Returns whether the style has bold formatting.
	 * 
	 * @see Formatting#BOLD
	 */
	public boolean isBold() {
		return this.bold == Boolean.TRUE;
	}

	/**
	 * Returns whether the style has italic formatting.
	 * 
	 * @see Formatting#ITALIC
	 */
	public boolean isItalic() {
		return this.italic == Boolean.TRUE;
	}

	/**
	 * Returns whether the style has strikethrough formatting.
	 * 
	 * @see Formatting#STRIKETHROUGH
	 */
	public boolean isStrikethrough() {
		return this.strikethrough == Boolean.TRUE;
	}

	/**
	 * Returns whether the style has underline formatting.
	 * 
	 * @see Formatting#UNDERLINE
	 */
	public boolean isUnderlined() {
		return this.underlined == Boolean.TRUE;
	}

	/**
	 * Returns whether the style has obfuscated formatting.
	 * 
	 * @see Formatting#OBFUSCATED
	 */
	public boolean isObfuscated() {
		return this.obfuscated == Boolean.TRUE;
	}

	/**
	 * Returns if this is the empty style.
	 * 
	 * @see #EMPTY
	 */
	public boolean isEmpty() {
		return this == EMPTY;
	}

	/**
	 * Returns the click event of this style.
	 */
	@Nullable
	public ClickEvent getClickEvent() {
		return this.clickEvent;
	}

	/**
	 * Returns the hover event of this style.
	 */
	@Nullable
	public HoverEvent getHoverEvent() {
		return this.hoverEvent;
	}

	/**
	 * Returns the insertion text of the style.
	 * 
	 * <p>An insertion is inserted when a piece of text clicked while shift key
	 * is down in the chat HUD.
	 */
	@Nullable
	public String getInsertion() {
		return this.insertion;
	}

	/**
	 * Returns the font of this style.
	 */
	public Identifier getFont() {
		return this.font != null ? this.font : DEFAULT_FONT_ID;
	}

	private static <T> Style with(Style newStyle, @Nullable T oldAttribute, @Nullable T newAttribute) {
		return oldAttribute != null && newAttribute == null && newStyle.equals(EMPTY) ? EMPTY : newStyle;
	}

	/**
	 * Returns a new style with the color provided and all other attributes of
	 * this style.
	 * 
	 * @param color the new color
	 */
	public Style withColor(@Nullable TextColor color) {
		return Objects.equals(this.color, color)
			? this
			: with(
				new Style(color, this.bold, this.italic, this.underlined, this.strikethrough, this.obfuscated, this.clickEvent, this.hoverEvent, this.insertion, this.font),
				this.color,
				color
			);
	}

	/**
	 * Returns a new style with the color provided and all other attributes of
	 * this style.
	 * 
	 * @param color the new color
	 */
	public Style withColor(@Nullable Formatting color) {
		return this.withColor(color != null ? TextColor.fromFormatting(color) : null);
	}

	public Style withColor(int rgbColor) {
		return this.withColor(TextColor.fromRgb(rgbColor));
	}

	/**
	 * Returns a new style with the bold attribute provided and all other
	 * attributes of this style.
	 * 
	 * @param bold the new bold property
	 */
	public Style withBold(@Nullable Boolean bold) {
		return Objects.equals(this.bold, bold)
			? this
			: with(
				new Style(this.color, bold, this.italic, this.underlined, this.strikethrough, this.obfuscated, this.clickEvent, this.hoverEvent, this.insertion, this.font),
				this.bold,
				bold
			);
	}

	/**
	 * Returns a new style with the italic attribute provided and all other
	 * attributes of this style.
	 * 
	 * @param italic the new italic property
	 */
	public Style withItalic(@Nullable Boolean italic) {
		return Objects.equals(this.italic, italic)
			? this
			: with(
				new Style(this.color, this.bold, italic, this.underlined, this.strikethrough, this.obfuscated, this.clickEvent, this.hoverEvent, this.insertion, this.font),
				this.italic,
				italic
			);
	}

	/**
	 * Returns a new style with the underline attribute provided and all other
	 * attributes of this style.
	 */
	public Style withUnderline(@Nullable Boolean underline) {
		return Objects.equals(this.underlined, underline)
			? this
			: with(
				new Style(this.color, this.bold, this.italic, underline, this.strikethrough, this.obfuscated, this.clickEvent, this.hoverEvent, this.insertion, this.font),
				this.underlined,
				underline
			);
	}

	public Style withStrikethrough(@Nullable Boolean strikethrough) {
		return Objects.equals(this.strikethrough, strikethrough)
			? this
			: with(
				new Style(this.color, this.bold, this.italic, this.underlined, strikethrough, this.obfuscated, this.clickEvent, this.hoverEvent, this.insertion, this.font),
				this.strikethrough,
				strikethrough
			);
	}

	public Style withObfuscated(@Nullable Boolean obfuscated) {
		return Objects.equals(this.obfuscated, obfuscated)
			? this
			: with(
				new Style(this.color, this.bold, this.italic, this.underlined, this.strikethrough, obfuscated, this.clickEvent, this.hoverEvent, this.insertion, this.font),
				this.obfuscated,
				obfuscated
			);
	}

	/**
	 * Returns a new style with the click event provided and all other
	 * attributes of this style.
	 * 
	 * @param clickEvent the new click event
	 */
	public Style withClickEvent(@Nullable ClickEvent clickEvent) {
		return Objects.equals(this.clickEvent, clickEvent)
			? this
			: with(
				new Style(this.color, this.bold, this.italic, this.underlined, this.strikethrough, this.obfuscated, clickEvent, this.hoverEvent, this.insertion, this.font),
				this.clickEvent,
				clickEvent
			);
	}

	/**
	 * Returns a new style with the hover event provided and all other
	 * attributes of this style.
	 * 
	 * @param hoverEvent the new hover event
	 */
	public Style withHoverEvent(@Nullable HoverEvent hoverEvent) {
		return Objects.equals(this.hoverEvent, hoverEvent)
			? this
			: with(
				new Style(this.color, this.bold, this.italic, this.underlined, this.strikethrough, this.obfuscated, this.clickEvent, hoverEvent, this.insertion, this.font),
				this.hoverEvent,
				hoverEvent
			);
	}

	/**
	 * Returns a new style with the insertion provided and all other
	 * attributes of this style.
	 * 
	 * @param insertion the new insertion string
	 */
	public Style withInsertion(@Nullable String insertion) {
		return Objects.equals(this.insertion, insertion)
			? this
			: with(
				new Style(this.color, this.bold, this.italic, this.underlined, this.strikethrough, this.obfuscated, this.clickEvent, this.hoverEvent, insertion, this.font),
				this.insertion,
				insertion
			);
	}

	/**
	 * Returns a new style with the font provided and all other
	 * attributes of this style.
	 * 
	 * @param font the new font
	 */
	public Style withFont(@Nullable Identifier font) {
		return Objects.equals(this.font, font)
			? this
			: with(
				new Style(this.color, this.bold, this.italic, this.underlined, this.strikethrough, this.obfuscated, this.clickEvent, this.hoverEvent, this.insertion, font),
				this.font,
				font
			);
	}

	/**
	 * Returns a new style with the formatting provided and all other
	 * attributes of this style.
	 * 
	 * @param formatting the new formatting
	 */
	public Style withFormatting(Formatting formatting) {
		TextColor textColor = this.color;
		Boolean boolean_ = this.bold;
		Boolean boolean2 = this.italic;
		Boolean boolean3 = this.strikethrough;
		Boolean boolean4 = this.underlined;
		Boolean boolean5 = this.obfuscated;
		switch (formatting) {
			case OBFUSCATED:
				boolean5 = true;
				break;
			case BOLD:
				boolean_ = true;
				break;
			case STRIKETHROUGH:
				boolean3 = true;
				break;
			case UNDERLINE:
				boolean4 = true;
				break;
			case ITALIC:
				boolean2 = true;
				break;
			case RESET:
				return EMPTY;
			default:
				textColor = TextColor.fromFormatting(formatting);
		}

		return new Style(textColor, boolean_, boolean2, boolean4, boolean3, boolean5, this.clickEvent, this.hoverEvent, this.insertion, this.font);
	}

	/**
	 * Returns a new style with the formatting provided and some applicable
	 * attributes of this style.
	 * 
	 * <p>When a color formatting is passed for {@code formatting}, the other
	 * formattings, including bold, italic, strikethrough, underlined, and
	 * obfuscated, are all removed.
	 * 
	 * @param formatting the new formatting
	 */
	public Style withExclusiveFormatting(Formatting formatting) {
		TextColor textColor = this.color;
		Boolean boolean_ = this.bold;
		Boolean boolean2 = this.italic;
		Boolean boolean3 = this.strikethrough;
		Boolean boolean4 = this.underlined;
		Boolean boolean5 = this.obfuscated;
		switch (formatting) {
			case OBFUSCATED:
				boolean5 = true;
				break;
			case BOLD:
				boolean_ = true;
				break;
			case STRIKETHROUGH:
				boolean3 = true;
				break;
			case UNDERLINE:
				boolean4 = true;
				break;
			case ITALIC:
				boolean2 = true;
				break;
			case RESET:
				return EMPTY;
			default:
				boolean5 = false;
				boolean_ = false;
				boolean3 = false;
				boolean4 = false;
				boolean2 = false;
				textColor = TextColor.fromFormatting(formatting);
		}

		return new Style(textColor, boolean_, boolean2, boolean4, boolean3, boolean5, this.clickEvent, this.hoverEvent, this.insertion, this.font);
	}

	/**
	 * Returns a new style with the formattings provided and all other
	 * attributes of this style.
	 * 
	 * @param formattings an array of new formattings
	 */
	public Style withFormatting(Formatting... formattings) {
		TextColor textColor = this.color;
		Boolean boolean_ = this.bold;
		Boolean boolean2 = this.italic;
		Boolean boolean3 = this.strikethrough;
		Boolean boolean4 = this.underlined;
		Boolean boolean5 = this.obfuscated;

		for (Formatting formatting : formattings) {
			switch (formatting) {
				case OBFUSCATED:
					boolean5 = true;
					break;
				case BOLD:
					boolean_ = true;
					break;
				case STRIKETHROUGH:
					boolean3 = true;
					break;
				case UNDERLINE:
					boolean4 = true;
					break;
				case ITALIC:
					boolean2 = true;
					break;
				case RESET:
					return EMPTY;
				default:
					textColor = TextColor.fromFormatting(formatting);
			}
		}

		return new Style(textColor, boolean_, boolean2, boolean4, boolean3, boolean5, this.clickEvent, this.hoverEvent, this.insertion, this.font);
	}

	/**
	 * Returns a new style with the undefined attributes of this style filled
	 * by the {@code parent} style.
	 * 
	 * @param parent the parent style
	 */
	public Style withParent(Style parent) {
		if (this == EMPTY) {
			return parent;
		} else {
			return parent == EMPTY
				? this
				: new Style(
					this.color != null ? this.color : parent.color,
					this.bold != null ? this.bold : parent.bold,
					this.italic != null ? this.italic : parent.italic,
					this.underlined != null ? this.underlined : parent.underlined,
					this.strikethrough != null ? this.strikethrough : parent.strikethrough,
					this.obfuscated != null ? this.obfuscated : parent.obfuscated,
					this.clickEvent != null ? this.clickEvent : parent.clickEvent,
					this.hoverEvent != null ? this.hoverEvent : parent.hoverEvent,
					this.insertion != null ? this.insertion : parent.insertion,
					this.font != null ? this.font : parent.font
				);
		}
	}

	public String toString() {
		final StringBuilder stringBuilder = new StringBuilder("{");

		class Writer {
			private boolean shouldAppendComma;

			private void appendComma() {
				if (this.shouldAppendComma) {
					stringBuilder.append(',');
				}

				this.shouldAppendComma = true;
			}

			void append(String key, @Nullable Boolean value) {
				if (value != null) {
					this.appendComma();
					if (!value) {
						stringBuilder.append('!');
					}

					stringBuilder.append(key);
				}
			}

			void append(String key, @Nullable Object value) {
				if (value != null) {
					this.appendComma();
					stringBuilder.append(key);
					stringBuilder.append('=');
					stringBuilder.append(value);
				}
			}
		}

		Writer writer = new Writer();
		writer.append("color", this.color);
		writer.append("bold", this.bold);
		writer.append("italic", this.italic);
		writer.append("underlined", this.underlined);
		writer.append("strikethrough", this.strikethrough);
		writer.append("obfuscated", this.obfuscated);
		writer.append("clickEvent", this.clickEvent);
		writer.append("hoverEvent", this.hoverEvent);
		writer.append("insertion", this.insertion);
		writer.append("font", this.font);
		stringBuilder.append("}");
		return stringBuilder.toString();
	}

	public boolean equals(Object o) {
		if (this == o) {
			return true;
		} else {
			return !(o instanceof Style style)
				? false
				: this.bold == style.bold
					&& Objects.equals(this.getColor(), style.getColor())
					&& this.italic == style.italic
					&& this.obfuscated == style.obfuscated
					&& this.strikethrough == style.strikethrough
					&& this.underlined == style.underlined
					&& Objects.equals(this.clickEvent, style.clickEvent)
					&& Objects.equals(this.hoverEvent, style.hoverEvent)
					&& Objects.equals(this.insertion, style.insertion)
					&& Objects.equals(this.font, style.font);
		}
	}

	public int hashCode() {
		return Objects.hash(
			new Object[]{this.color, this.bold, this.italic, this.underlined, this.strikethrough, this.obfuscated, this.clickEvent, this.hoverEvent, this.insertion}
		);
	}

	/**
	 * Contains codecs to serialize {@link Style}s.
	 */
	public static class Codecs {
		public static final MapCodec<Style> MAP_CODEC = RecordCodecBuilder.mapCodec(
			instance -> instance.group(
						net.minecraft.util.dynamic.Codecs.createStrictOptionalFieldCodec(TextColor.CODEC, "color").forGetter(style -> Optional.ofNullable(style.color)),
						net.minecraft.util.dynamic.Codecs.createStrictOptionalFieldCodec(Codec.BOOL, "bold").forGetter(style -> Optional.ofNullable(style.bold)),
						net.minecraft.util.dynamic.Codecs.createStrictOptionalFieldCodec(Codec.BOOL, "italic").forGetter(style -> Optional.ofNullable(style.italic)),
						net.minecraft.util.dynamic.Codecs.createStrictOptionalFieldCodec(Codec.BOOL, "underlined").forGetter(style -> Optional.ofNullable(style.underlined)),
						net.minecraft.util.dynamic.Codecs.createStrictOptionalFieldCodec(Codec.BOOL, "strikethrough")
							.forGetter(style -> Optional.ofNullable(style.strikethrough)),
						net.minecraft.util.dynamic.Codecs.createStrictOptionalFieldCodec(Codec.BOOL, "obfuscated").forGetter(style -> Optional.ofNullable(style.obfuscated)),
						net.minecraft.util.dynamic.Codecs.createStrictOptionalFieldCodec(ClickEvent.CODEC, "clickEvent")
							.forGetter(style -> Optional.ofNullable(style.clickEvent)),
						net.minecraft.util.dynamic.Codecs.createStrictOptionalFieldCodec(HoverEvent.CODEC, "hoverEvent")
							.forGetter(style -> Optional.ofNullable(style.hoverEvent)),
						net.minecraft.util.dynamic.Codecs.createStrictOptionalFieldCodec(Codec.STRING, "insertion").forGetter(style -> Optional.ofNullable(style.insertion)),
						net.minecraft.util.dynamic.Codecs.createStrictOptionalFieldCodec(Identifier.CODEC, "font").forGetter(style -> Optional.ofNullable(style.font))
					)
					.apply(instance, Style::of)
		);
		public static final Codec<Style> CODEC = MAP_CODEC.codec();
	}
}
