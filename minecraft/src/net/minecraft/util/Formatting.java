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
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

public enum Formatting {
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

	public int getColorIndex() {
		return this.colorIndex;
	}

	public boolean isModifier() {
		return this.modifier;
	}

	public boolean isColor() {
		return !this.modifier && this != RESET;
	}

	@Nullable
	public Integer getColorValue() {
		return this.colorValue;
	}

	public String getName() {
		return this.name().toLowerCase(Locale.ROOT);
	}

	public String toString() {
		return this.stringValue;
	}

	@Nullable
	public static String strip(@Nullable String string) {
		return string == null ? null : FORMATTING_CODE_PATTERN.matcher(string).replaceAll("");
	}

	@Nullable
	public static Formatting byName(@Nullable String name) {
		return name == null ? null : (Formatting)BY_NAME.get(sanitize(name));
	}

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

	@Nullable
	@Environment(EnvType.CLIENT)
	public static Formatting byCode(char code) {
		char c = Character.toString(code).toLowerCase(Locale.ROOT).charAt(0);

		for (Formatting formatting : values()) {
			if (formatting.code == c) {
				return formatting;
			}
		}

		return null;
	}

	public static Collection<String> getNames(boolean colors, boolean modifiers) {
		List<String> list = Lists.<String>newArrayList();

		for (Formatting formatting : values()) {
			if ((!formatting.isColor() || colors) && (!formatting.isModifier() || modifiers)) {
				list.add(formatting.getName());
			}
		}

		return list;
	}
}
