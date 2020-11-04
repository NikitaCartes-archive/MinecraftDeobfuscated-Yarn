package net.minecraft.text;

import com.google.common.collect.ImmutableMap;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.util.Formatting;

/**
 * Represents an RGB color of a {@link Text}.
 * 
 * <p>This is immutable, and part of a {@link Style}.</p>
 * 
 * @see Style
 */
public final class TextColor {
	private static final Map<Formatting, TextColor> FORMATTING_TO_COLOR = (Map<Formatting, TextColor>)Stream.of(Formatting.values())
		.filter(Formatting::isColor)
		.collect(ImmutableMap.toImmutableMap(Function.identity(), formatting -> new TextColor(formatting.getColorValue(), formatting.getName())));
	private static final Map<String, TextColor> BY_NAME = (Map<String, TextColor>)FORMATTING_TO_COLOR.values()
		.stream()
		.collect(ImmutableMap.toImmutableMap(textColor -> textColor.name, Function.identity()));
	private final int rgb;
	@Nullable
	private final String name;

	private TextColor(int rgb, String name) {
		this.rgb = rgb;
		this.name = name;
	}

	private TextColor(int rgb) {
		this.rgb = rgb;
		this.name = null;
	}

	/**
	 * Gets the RGB value of this color.
	 * 
	 * <p>The red bits can be obtained by {@code (rgb >> 16) & 0xFF}, green bits
	 * by {@code (rgb >> 8) & 0xFF}, blue bits by {@code rgb & 0xFF}.</p>
	 */
	@Environment(EnvType.CLIENT)
	public int getRgb() {
		return this.rgb;
	}

	/**
	 * Gets the name of this color, used for converting the color to JSON format.
	 */
	public String getName() {
		return this.name != null ? this.name : this.getHexCode();
	}

	private String getHexCode() {
		return String.format("#%06X", this.rgb);
	}

	public boolean equals(Object object) {
		if (this == object) {
			return true;
		} else if (object != null && this.getClass() == object.getClass()) {
			TextColor textColor = (TextColor)object;
			return this.rgb == textColor.rgb;
		} else {
			return false;
		}
	}

	public int hashCode() {
		return Objects.hash(new Object[]{this.rgb, this.name});
	}

	public String toString() {
		return this.name != null ? this.name : this.getHexCode();
	}

	/**
	 * Obtains a text color from a formatting.
	 * 
	 * @param formatting the formatting
	 */
	@Nullable
	public static TextColor fromFormatting(Formatting formatting) {
		return (TextColor)FORMATTING_TO_COLOR.get(formatting);
	}

	/**
	 * Obtains a text color from an RGB value.
	 * 
	 * @param rgb the RGB color
	 */
	public static TextColor fromRgb(int rgb) {
		return new TextColor(rgb);
	}

	/**
	 * Parses a color by its name.
	 * 
	 * @param name the name
	 */
	@Nullable
	public static TextColor parse(String name) {
		if (name.startsWith("#")) {
			try {
				int i = Integer.parseInt(name.substring(1), 16);
				return fromRgb(i);
			} catch (NumberFormatException var2) {
				return null;
			}
		} else {
			return (TextColor)BY_NAME.get(name);
		}
	}
}
