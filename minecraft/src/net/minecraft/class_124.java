package net.minecraft;

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

public enum class_124 {
	field_1074("BLACK", '0', 0, 0),
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
	field_1070("RESET", 'r', -1, null);

	private static final Map<String, class_124> field_1052 = (Map<String, class_124>)Arrays.stream(values())
		.collect(Collectors.toMap(arg -> method_535(arg.field_1057), arg -> arg));
	private static final Pattern field_1066 = Pattern.compile("(?i)§[0-9A-FK-OR]");
	private final String field_1057;
	private final char field_1059;
	private final boolean field_1081;
	private final String field_1069;
	private final int field_1071;
	@Nullable
	private final Integer field_1053;

	private static String method_535(String string) {
		return string.toLowerCase(Locale.ROOT).replaceAll("[^a-z]", "");
	}

	private class_124(String string2, char c, int j, @Nullable Integer integer) {
		this(string2, c, false, j, integer);
	}

	private class_124(String string2, char c, boolean bl) {
		this(string2, c, bl, -1, null);
	}

	private class_124(String string2, char c, boolean bl, int j, @Nullable Integer integer) {
		this.field_1057 = string2;
		this.field_1059 = c;
		this.field_1081 = bl;
		this.field_1071 = j;
		this.field_1053 = integer;
		this.field_1069 = "§" + c;
	}

	@Environment(EnvType.CLIENT)
	public static String method_538(String string) {
		StringBuilder stringBuilder = new StringBuilder();
		int i = -1;
		int j = string.length();

		while ((i = string.indexOf(167, i + 1)) != -1) {
			if (i < j - 1) {
				class_124 lv = method_544(string.charAt(i + 1));
				if (lv != null) {
					if (lv.method_545()) {
						stringBuilder.setLength(0);
					}

					if (lv != field_1070) {
						stringBuilder.append(lv);
					}
				}
			}
		}

		return stringBuilder.toString();
	}

	public int method_536() {
		return this.field_1071;
	}

	public boolean method_542() {
		return this.field_1081;
	}

	public boolean method_543() {
		return !this.field_1081 && this != field_1070;
	}

	@Nullable
	@Environment(EnvType.CLIENT)
	public Integer method_532() {
		return this.field_1053;
	}

	@Environment(EnvType.CLIENT)
	public boolean method_545() {
		return !this.field_1081;
	}

	public String method_537() {
		return this.name().toLowerCase(Locale.ROOT);
	}

	public String toString() {
		return this.field_1069;
	}

	@Nullable
	public static String method_539(@Nullable String string) {
		return string == null ? null : field_1066.matcher(string).replaceAll("");
	}

	@Nullable
	public static class_124 method_533(@Nullable String string) {
		return string == null ? null : (class_124)field_1052.get(method_535(string));
	}

	@Nullable
	public static class_124 method_534(int i) {
		if (i < 0) {
			return field_1070;
		} else {
			for (class_124 lv : values()) {
				if (lv.method_536() == i) {
					return lv;
				}
			}

			return null;
		}
	}

	@Nullable
	@Environment(EnvType.CLIENT)
	public static class_124 method_544(char c) {
		char d = Character.toString(c).toLowerCase(Locale.ROOT).charAt(0);

		for (class_124 lv : values()) {
			if (lv.field_1059 == d) {
				return lv;
			}
		}

		return null;
	}

	public static Collection<String> method_540(boolean bl, boolean bl2) {
		List<String> list = Lists.<String>newArrayList();

		for (class_124 lv : values()) {
			if ((!lv.method_543() || bl) && (!lv.method_542() || bl2)) {
				list.add(lv.method_537());
			}
		}

		return list;
	}
}
