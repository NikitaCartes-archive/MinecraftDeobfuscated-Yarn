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
	field_1058("DARK_BLUE", '1', 1, 170),
	field_1077("DARK_GREEN", '2', 2, 43520),
	field_1062("DARK_AQUA", '3', 3, 43690),
	field_1079("DARK_RED", '4', 4, 11141120),
	field_1064("DARK_PURPLE", '5', 5, 11141290),
	field_1065("GOLD", '6', 6, 16755200),
	field_1080("GRAY", '7', 7, 11184810),
	field_1063("DARK_GRAY", '8', 8, 5592405),
	field_1078("BLUE", '9', 9, 5592575),
	field_1060("GREEN", 'a', 10, 5635925),
	field_1075("AQUA", 'b', 11, 5636095),
	field_1061("RED", 'c', 12, 16733525),
	field_1076("LIGHT_PURPLE", 'd', 13, 16733695),
	field_1054("YELLOW", 'e', 14, 16777045),
	field_1068("WHITE", 'f', 15, 16777215),
	field_1051("OBFUSCATED", 'k', true),
	field_1067("BOLD", 'l', true),
	field_1055("STRIKETHROUGH", 'm', true),
	field_1073("UNDERLINE", 'n', true),
	field_1056("ITALIC", 'o', true),
	RESET("RESET", 'r', -1, null);

	private static final Map<String, TextFormat> field_1052 = (Map<String, TextFormat>)Arrays.stream(values())
		.collect(Collectors.toMap(textFormat -> sanitizeName(textFormat.field_1057), textFormat -> textFormat));
	private static final Pattern FORMAT_PATTERN = Pattern.compile("(?i)§[0-9A-FK-OR]");
	private final String field_1057;
	private final char sectionSignCode;
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
		this.sectionSignCode = c;
		this.modifier = bl;
		this.id = j;
		this.color = integer;
		this.code = "§" + c;
	}

	@Environment(EnvType.CLIENT)
	public static String getFormatAtEnd(String string) {
		StringBuilder stringBuilder = new StringBuilder();
		int i = -1;
		int j = string.length();

		while ((i = string.indexOf(167, i + 1)) != -1) {
			if (i < j - 1) {
				TextFormat textFormat = bySectionSignCode(string.charAt(i + 1));
				if (textFormat != null) {
					if (textFormat.affectsGlyphWidth()) {
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
	public boolean affectsGlyphWidth() {
		return !this.modifier;
	}

	public String getName() {
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
	public static TextFormat bySectionSignCode(char c) {
		char d = Character.toString(c).toLowerCase(Locale.ROOT).charAt(0);

		for (TextFormat textFormat : values()) {
			if (textFormat.sectionSignCode == d) {
				return textFormat;
			}
		}

		return null;
	}

	public static Collection<String> getNames(boolean bl, boolean bl2) {
		List<String> list = Lists.<String>newArrayList();

		for (TextFormat textFormat : values()) {
			if ((!textFormat.isColor() || bl) && (!textFormat.isModifier() || bl2)) {
				list.add(textFormat.getName());
			}
		}

		return list;
	}
}
