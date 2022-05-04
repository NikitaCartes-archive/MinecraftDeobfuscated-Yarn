package net.minecraft.util;

import com.google.common.collect.Lists;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import javax.annotation.Nullable;

/**
 * An enum holding formattings.
 * 
 * <p>There are two types of formattings, color and modifier. Color formattings
 * are associated with a specific color, while modifier formattings modify the
 * style, such as by bolding the text. {@link #RESET} is a special formatting
 * and is not classified as either of these two.
 */
public enum Formatting implements StringIdentifiable {
	BLACK("BLACK", '0', 0, 0),
	DARK_BLUE("DARK_BLUE", '1', 1, 170),
	DARK_GREEN("DARK_GREEN", '2', 2, 43520),
	DARK_AQUA("DARK_AQUA", '3', 3, 43690),
	DARK_RED("DARK_RED", '4', 4, 11141120),
	DARK_PURPLE("DARK_PURPLE", '5', 5, 11141290),
	GOLD("GOLD", '6', 6, 16755200),
	GRAY("GRAY", '7', 7, 11184810),
	DARK_GRAY("DARK_GRAY", '8', 8, 5592405),
	BLUE("BLUE", '9', 9, 5592575),
	GREEN("GREEN", 'a', 10, 5635925),
	AQUA("AQUA", 'b', 11, 5636095),
	RED("RED", 'c', 12, 16733525),
	LIGHT_PURPLE("LIGHT_PURPLE", 'd', 13, 16733695),
	YELLOW("YELLOW", 'e', 14, 16777045),
	WHITE("WHITE", 'f', 15, 16777215),
	OBFUSCATED("OBFUSCATED", 'k', true),
	BOLD("BOLD", 'l', true),
	STRIKETHROUGH("STRIKETHROUGH", 'm', true),
	UNDERLINE("UNDERLINE", 'n', true),
	ITALIC("ITALIC", 'o', true),
	RESET("RESET", 'r', -1, null);

	public static final com.mojang.serialization.Codec<Formatting> CODEC = StringIdentifiable.createCodec(Formatting::values);
	public static final char FORMATTING_CODE_PREFIX = '§';
	private static final Map<String, Formatting> BY_NAME = (Map<String, Formatting>)Arrays.stream(values())
		.collect(Collectors.toMap(f -> sanitize(f.name), f -> f));
	private static final Pattern FORMATTING_CODE_PATTERN = Pattern.compile("(?i)§[0-9A-FK-OR]");
	private final String name;
	private final char code;
	private final boolean modifier;
	private final String stringValue;
	private final int colorIndex;
	@Nullable
	private final Integer colorValue;

	private static String sanitize(String name) {
		return name.toLowerCase(Locale.ROOT).replaceAll("[^a-z]", "");
	}

	private Formatting(String name, char code, int colorIndex, @Nullable Integer colorValue) {
		this(name, code, false, colorIndex, colorValue);
	}

	private Formatting(String name, char code, boolean modifier) {
		this(name, code, modifier, -1, null);
	}

	private Formatting(String name, char code, boolean modifier, int colorIndex, @Nullable Integer colorValue) {
		this.name = name;
		this.code = code;
		this.modifier = modifier;
		this.colorIndex = colorIndex;
		this.colorValue = colorValue;
		this.stringValue = "§" + code;
	}

	/**
	 * {@return the code to be placed after the {@value FORMATTING_CODE_PREFIX} when this format is converted to a string}
	 */
	public char getCode() {
		return this.code;
	}

	/**
	 * {@return the color index for the formatting, or {@code -1} to indicate no color}
	 * 
	 * @apiNote This is also used to calculate scoreboard team display slot IDs.
	 */
	public int getColorIndex() {
		return this.colorIndex;
	}

	/**
	 * {@return true if the formatting is a modifier, false otherwise}
	 */
	public boolean isModifier() {
		return this.modifier;
	}

	/**
	 * {@return true if the formatting is associated with a color, false otherwise}
	 */
	public boolean isColor() {
		return !this.modifier && this != RESET;
	}

	/**
	 * {@return the color of the formatted text, or {@code null} if the formatting
	 * has no associated color}
	 */
	@Nullable
	public Integer getColorValue() {
		return this.colorValue;
	}

	/**
	 * {@return the name of the formatting}
	 */
	public String getName() {
		return this.name().toLowerCase(Locale.ROOT);
	}

	public String toString() {
		return this.stringValue;
	}

	/**
	 * {@return the {@code text} with all formatting codes removed}
	 * 
	 * @see StringHelper#stripTextFormat
	 */
	@Nullable
	public static String strip(@Nullable String string) {
		return string == null ? null : FORMATTING_CODE_PATTERN.matcher(string).replaceAll("");
	}

	/**
	 * {@return the formatting with the name {@code name}, or {@code null} if there is none}
	 */
	@Nullable
	public static Formatting byName(@Nullable String name) {
		return name == null ? null : (Formatting)BY_NAME.get(sanitize(name));
	}

	/**
	 * {@return the formatting with the color index {@code colorIndex},
	 * or {@code null} if there is none}
	 */
	@Nullable
	public static Formatting byColorIndex(int colorIndex) {
		if (colorIndex < 0) {
			return RESET;
		} else {
			for (Formatting formatting : values()) {
				if (formatting.getColorIndex() == colorIndex) {
					return formatting;
				}
			}

			return null;
		}
	}

	/**
	 * {@return the formatting with the code {@code code}, or {@code null} if there is none}
	 */
	@Nullable
	public static Formatting byCode(char code) {
		char c = Character.toString(code).toLowerCase(Locale.ROOT).charAt(0);

		for (Formatting formatting : values()) {
			if (formatting.code == c) {
				return formatting;
			}
		}

		return null;
	}

	/**
	 * {@return the list of formattings matching the given condition}
	 * 
	 * @param colors whether or not to include color formattings
	 * @param modifiers whether or not to include modifier formattings
	 */
	public static Collection<String> getNames(boolean colors, boolean modifiers) {
		List<String> list = Lists.<String>newArrayList();

		for (Formatting formatting : values()) {
			if ((!formatting.isColor() || colors) && (!formatting.isModifier() || modifiers)) {
				list.add(formatting.getName());
			}
		}

		return list;
	}

	@Override
	public String asString() {
		return this.getName();
	}
}
