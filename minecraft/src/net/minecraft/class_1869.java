package net.minecraft;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSyntaxException;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

public class class_1869 implements class_3955 {
	private final int field_9055;
	private final int field_9054;
	private final class_2371<class_1856> field_9052;
	private final class_1799 field_9053;
	private final class_2960 field_9051;
	private final String field_9056;

	public class_1869(class_2960 arg, String string, int i, int j, class_2371<class_1856> arg2, class_1799 arg3) {
		this.field_9051 = arg;
		this.field_9056 = string;
		this.field_9055 = i;
		this.field_9054 = j;
		this.field_9052 = arg2;
		this.field_9053 = arg3;
	}

	@Override
	public class_2960 method_8114() {
		return this.field_9051;
	}

	@Override
	public class_1865<?> method_8119() {
		return class_1865.field_9035;
	}

	@Environment(EnvType.CLIENT)
	@Override
	public String method_8112() {
		return this.field_9056;
	}

	@Override
	public class_1799 method_8110() {
		return this.field_9053;
	}

	@Override
	public class_2371<class_1856> method_8117() {
		return this.field_9052;
	}

	@Environment(EnvType.CLIENT)
	@Override
	public boolean method_8113(int i, int j) {
		return i >= this.field_9055 && j >= this.field_9054;
	}

	public boolean method_17728(class_1715 arg, class_1937 arg2) {
		for (int i = 0; i <= arg.method_17398() - this.field_9055; i++) {
			for (int j = 0; j <= arg.method_17397() - this.field_9054; j++) {
				if (this.method_8161(arg, i, j, true)) {
					return true;
				}

				if (this.method_8161(arg, i, j, false)) {
					return true;
				}
			}
		}

		return false;
	}

	private boolean method_8161(class_1715 arg, int i, int j, boolean bl) {
		for (int k = 0; k < arg.method_17398(); k++) {
			for (int l = 0; l < arg.method_17397(); l++) {
				int m = k - i;
				int n = l - j;
				class_1856 lv = class_1856.field_9017;
				if (m >= 0 && n >= 0 && m < this.field_9055 && n < this.field_9054) {
					if (bl) {
						lv = this.field_9052.get(this.field_9055 - m - 1 + n * this.field_9055);
					} else {
						lv = this.field_9052.get(m + n * this.field_9055);
					}
				}

				if (!lv.method_8093(arg.method_5438(k + l * arg.method_17398()))) {
					return false;
				}
			}
		}

		return true;
	}

	public class_1799 method_17727(class_1715 arg) {
		return this.method_8110().method_7972();
	}

	public int method_8150() {
		return this.field_9055;
	}

	public int method_8158() {
		return this.field_9054;
	}

	private static class_2371<class_1856> method_8148(String[] strings, Map<String, class_1856> map, int i, int j) {
		class_2371<class_1856> lv = class_2371.method_10213(i * j, class_1856.field_9017);
		Set<String> set = Sets.<String>newHashSet(map.keySet());
		set.remove(" ");

		for (int k = 0; k < strings.length; k++) {
			for (int l = 0; l < strings[k].length(); l++) {
				String string = strings[k].substring(l, l + 1);
				class_1856 lv2 = (class_1856)map.get(string);
				if (lv2 == null) {
					throw new JsonSyntaxException("Pattern references symbol '" + string + "' but it's not defined in the key");
				}

				set.remove(string);
				lv.set(l + i * k, lv2);
			}
		}

		if (!set.isEmpty()) {
			throw new JsonSyntaxException("Key defines symbols that aren't used in pattern: " + set);
		} else {
			return lv;
		}
	}

	@VisibleForTesting
	static String[] method_8146(String... strings) {
		int i = Integer.MAX_VALUE;
		int j = 0;
		int k = 0;
		int l = 0;

		for (int m = 0; m < strings.length; m++) {
			String string = strings[m];
			i = Math.min(i, method_8151(string));
			int n = method_8153(string);
			j = Math.max(j, n);
			if (n < 0) {
				if (k == m) {
					k++;
				}

				l++;
			} else {
				l = 0;
			}
		}

		if (strings.length == l) {
			return new String[0];
		} else {
			String[] strings2 = new String[strings.length - l - k];

			for (int o = 0; o < strings2.length; o++) {
				strings2[o] = strings[o + k].substring(i, j + 1);
			}

			return strings2;
		}
	}

	private static int method_8151(String string) {
		int i = 0;

		while (i < string.length() && string.charAt(i) == ' ') {
			i++;
		}

		return i;
	}

	private static int method_8153(String string) {
		int i = string.length() - 1;

		while (i >= 0 && string.charAt(i) == ' ') {
			i--;
		}

		return i;
	}

	private static String[] method_8145(JsonArray jsonArray) {
		String[] strings = new String[jsonArray.size()];
		if (strings.length > 3) {
			throw new JsonSyntaxException("Invalid pattern: too many rows, 3 is maximum");
		} else if (strings.length == 0) {
			throw new JsonSyntaxException("Invalid pattern: empty pattern not allowed");
		} else {
			for (int i = 0; i < strings.length; i++) {
				String string = class_3518.method_15287(jsonArray.get(i), "pattern[" + i + "]");
				if (string.length() > 3) {
					throw new JsonSyntaxException("Invalid pattern: too many columns, 3 is maximum");
				}

				if (i > 0 && strings[0].length() != string.length()) {
					throw new JsonSyntaxException("Invalid pattern: each row must be the same width");
				}

				strings[i] = string;
			}

			return strings;
		}
	}

	private static Map<String, class_1856> method_8157(JsonObject jsonObject) {
		Map<String, class_1856> map = Maps.<String, class_1856>newHashMap();

		for (Entry<String, JsonElement> entry : jsonObject.entrySet()) {
			if (((String)entry.getKey()).length() != 1) {
				throw new JsonSyntaxException("Invalid key entry: '" + (String)entry.getKey() + "' is an invalid symbol (must be 1 character only).");
			}

			if (" ".equals(entry.getKey())) {
				throw new JsonSyntaxException("Invalid key entry: ' ' is a reserved symbol.");
			}

			map.put(entry.getKey(), class_1856.method_8102((JsonElement)entry.getValue()));
		}

		map.put(" ", class_1856.field_9017);
		return map;
	}

	public static class_1799 method_8155(JsonObject jsonObject) {
		String string = class_3518.method_15265(jsonObject, "item");
		class_1792 lv = (class_1792)class_2378.field_11142
			.method_17966(new class_2960(string))
			.orElseThrow(() -> new JsonSyntaxException("Unknown item '" + string + "'"));
		if (jsonObject.has("data")) {
			throw new JsonParseException("Disallowed data tag found");
		} else {
			int i = class_3518.method_15282(jsonObject, "count", 1);
			return new class_1799(lv, i);
		}
	}

	public static class class_1870 implements class_1865<class_1869> {
		public class_1869 method_8164(class_2960 arg, JsonObject jsonObject) {
			String string = class_3518.method_15253(jsonObject, "group", "");
			Map<String, class_1856> map = class_1869.method_8157(class_3518.method_15296(jsonObject, "key"));
			String[] strings = class_1869.method_8146(class_1869.method_8145(class_3518.method_15261(jsonObject, "pattern")));
			int i = strings[0].length();
			int j = strings.length;
			class_2371<class_1856> lv = class_1869.method_8148(strings, map, i, j);
			class_1799 lv2 = class_1869.method_8155(class_3518.method_15296(jsonObject, "result"));
			return new class_1869(arg, string, i, j, lv, lv2);
		}

		public class_1869 method_8163(class_2960 arg, class_2540 arg2) {
			int i = arg2.method_10816();
			int j = arg2.method_10816();
			String string = arg2.method_10800(32767);
			class_2371<class_1856> lv = class_2371.method_10213(i * j, class_1856.field_9017);

			for (int k = 0; k < lv.size(); k++) {
				lv.set(k, class_1856.method_8086(arg2));
			}

			class_1799 lv2 = arg2.method_10819();
			return new class_1869(arg, string, i, j, lv, lv2);
		}

		public void method_8165(class_2540 arg, class_1869 arg2) {
			arg.method_10804(arg2.field_9055);
			arg.method_10804(arg2.field_9054);
			arg.method_10814(arg2.field_9056);

			for (class_1856 lv : arg2.field_9052) {
				lv.method_8088(arg);
			}

			arg.method_10793(arg2.field_9053);
		}
	}
}
