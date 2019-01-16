package net.minecraft.text;

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

public enum TextFormat {
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

	private static final Map<String, TextFormat> field_1052 = (Map<String, TextFormat>)Arrays.stream(values())
		.collect(Collectors.toMap(textFormat -> sanitizeName(textFormat.field_1057), textFormat -> textFormat));
	private static final Pattern FORMAT_PATTERN = Pattern.compile("(?i)§[0-9A-FK-OR]");
	private final String field_1057;
	private final char field_1059;
	private final boolean modifier;
	private final String code;
	private final int id;
	@Nullable
	private final Integer color;

	private static String sanitizeName(String string) {
		return string.toLowerCase(Locale.ROOT).replaceAll("[^a-z]", "");
	}

	private TextFormat(String string2, char c, int j, @Nullable Integer integer) {
		this(string2, c, false, j, integer);
	}

	private TextFormat(String string2, char c, boolean bl) {
		this(string2, c, bl, -1, null);
	}

	private TextFormat(String string2, char c, boolean bl, int j, @Nullable Integer integer) {
		this.field_1057 = string2;
		this.field_1059 = c;
		this.modifier = bl;
		this.id = j;
		this.color = integer;
		this.code = "§" + c;
	}

	@Environment(EnvType.CLIENT)
	public static String method_538(String string) {
		StringBuilder stringBuilder = new StringBuilder();
		int i = -1;
		int j = string.length();

		while ((i = string.indexOf(167, i + 1)) != -1) {
			if (i < j - 1) {
				TextFormat textFormat = method_544(string.charAt(i + 1));
				if (textFormat != null) {
					if (textFormat.method_545()) {
						stringBuilder.setLength(0);
					}

					if (textFormat != RESET) {
						stringBuilder.append(textFormat);
					}
				}
			}
		}

		return stringBuilder.toString();
	}

	public int getId() {
		return this.id;
	}

	public boolean isModifier() {
		return this.modifier;
	}

	public boolean isColor() {
		return !this.modifier && this != RESET;
	}

	@Nullable
	@Environment(EnvType.CLIENT)
	public Integer getColor() {
		return this.color;
	}

	@Environment(EnvType.CLIENT)
	public boolean method_545() {
		return !this.modifier;
	}

	public String getFormatName() {
		return this.name().toLowerCase(Locale.ROOT);
	}

	public String toString() {
		return this.code;
	}

	@Nullable
	public static String stripFormatting(@Nullable String string) {
		return string == null ? null : FORMAT_PATTERN.matcher(string).replaceAll("");
	}

	@Nullable
	public static TextFormat getFormatByName(@Nullable String string) {
		return string == null ? null : (TextFormat)field_1052.get(sanitizeName(string));
	}

	@Nullable
	public static TextFormat byId(int i) {
		if (i < 0) {
			return RESET;
		} else {
			for (TextFormat textFormat : values()) {
				if (textFormat.getId() == i) {
					return textFormat;
				}
			}

			return null;
		}
	}

	@Nullable
	@Environment(EnvType.CLIENT)
	public static TextFormat method_544(char c) {
		char d = Character.toString(c).toLowerCase(Locale.ROOT).charAt(0);

		for (TextFormat textFormat : values()) {
			if (textFormat.field_1059 == d) {
				return textFormat;
			}
		}

		return null;
	}

	public static Collection<String> method_540(boolean bl, boolean bl2) {
		List<String> list = Lists.<String>newArrayList();

		for (TextFormat textFormat : values()) {
			if ((!textFormat.isColor() || bl) && (!textFormat.isModifier() || bl2)) {
				list.add(textFormat.getFormatName());
			}
		}

		return list;
	}
}
