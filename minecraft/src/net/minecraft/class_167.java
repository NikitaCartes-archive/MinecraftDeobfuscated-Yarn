package net.minecraft;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

public class class_167 implements Comparable<class_167> {
	private final Map<String, class_178> field_1160 = Maps.<String, class_178>newHashMap();
	private String[][] field_1161 = new String[0][];

	public void method_727(Map<String, class_175> map, String[][] strings) {
		Set<String> set = map.keySet();
		this.field_1160.entrySet().removeIf(entry -> !set.contains(entry.getKey()));

		for (String string : set) {
			if (!this.field_1160.containsKey(string)) {
				this.field_1160.put(string, new class_178());
			}
		}

		this.field_1161 = strings;
	}

	public boolean method_740() {
		if (this.field_1161.length == 0) {
			return false;
		} else {
			for (String[] strings : this.field_1161) {
				boolean bl = false;

				for (String string : strings) {
					class_178 lv = this.method_737(string);
					if (lv != null && lv.method_784()) {
						bl = true;
						break;
					}
				}

				if (!bl) {
					return false;
				}
			}

			return true;
		}
	}

	public boolean method_742() {
		for (class_178 lv : this.field_1160.values()) {
			if (lv.method_784()) {
				return true;
			}
		}

		return false;
	}

	public boolean method_743(String string) {
		class_178 lv = (class_178)this.field_1160.get(string);
		if (lv != null && !lv.method_784()) {
			lv.method_789();
			return true;
		} else {
			return false;
		}
	}

	public boolean method_729(String string) {
		class_178 lv = (class_178)this.field_1160.get(string);
		if (lv != null && lv.method_784()) {
			lv.method_790();
			return true;
		} else {
			return false;
		}
	}

	public String toString() {
		return "AdvancementProgress{criteria=" + this.field_1160 + ", requirements=" + Arrays.deepToString(this.field_1161) + '}';
	}

	public void method_733(class_2540 arg) {
		arg.method_10804(this.field_1160.size());

		for (Entry<String, class_178> entry : this.field_1160.entrySet()) {
			arg.method_10814((String)entry.getKey());
			((class_178)entry.getValue()).method_787(arg);
		}
	}

	public static class_167 method_732(class_2540 arg) {
		class_167 lv = new class_167();
		int i = arg.method_10816();

		for (int j = 0; j < i; j++) {
			lv.field_1160.put(arg.method_10800(32767), class_178.method_785(arg));
		}

		return lv;
	}

	@Nullable
	public class_178 method_737(String string) {
		return (class_178)this.field_1160.get(string);
	}

	@Environment(EnvType.CLIENT)
	public float method_735() {
		if (this.field_1160.isEmpty()) {
			return 0.0F;
		} else {
			float f = (float)this.field_1161.length;
			float g = (float)this.method_736();
			return g / f;
		}
	}

	@Nullable
	@Environment(EnvType.CLIENT)
	public String method_728() {
		if (this.field_1160.isEmpty()) {
			return null;
		} else {
			int i = this.field_1161.length;
			if (i <= 1) {
				return null;
			} else {
				int j = this.method_736();
				return j + "/" + i;
			}
		}
	}

	@Environment(EnvType.CLIENT)
	private int method_736() {
		int i = 0;

		for (String[] strings : this.field_1161) {
			boolean bl = false;

			for (String string : strings) {
				class_178 lv = this.method_737(string);
				if (lv != null && lv.method_784()) {
					bl = true;
					break;
				}
			}

			if (bl) {
				i++;
			}
		}

		return i;
	}

	public Iterable<String> method_731() {
		List<String> list = Lists.<String>newArrayList();

		for (Entry<String, class_178> entry : this.field_1160.entrySet()) {
			if (!((class_178)entry.getValue()).method_784()) {
				list.add(entry.getKey());
			}
		}

		return list;
	}

	public Iterable<String> method_734() {
		List<String> list = Lists.<String>newArrayList();

		for (Entry<String, class_178> entry : this.field_1160.entrySet()) {
			if (((class_178)entry.getValue()).method_784()) {
				list.add(entry.getKey());
			}
		}

		return list;
	}

	@Nullable
	public Date method_741() {
		Date date = null;

		for (class_178 lv : this.field_1160.values()) {
			if (lv.method_784() && (date == null || lv.method_786().before(date))) {
				date = lv.method_786();
			}
		}

		return date;
	}

	public int method_738(class_167 arg) {
		Date date = this.method_741();
		Date date2 = arg.method_741();
		if (date == null && date2 != null) {
			return 1;
		} else if (date != null && date2 == null) {
			return -1;
		} else {
			return date == null && date2 == null ? 0 : date.compareTo(date2);
		}
	}

	public static class class_168 implements JsonDeserializer<class_167>, JsonSerializer<class_167> {
		public JsonElement method_744(class_167 arg, Type type, JsonSerializationContext jsonSerializationContext) {
			JsonObject jsonObject = new JsonObject();
			JsonObject jsonObject2 = new JsonObject();

			for (Entry<String, class_178> entry : arg.field_1160.entrySet()) {
				class_178 lv = (class_178)entry.getValue();
				if (lv.method_784()) {
					jsonObject2.add((String)entry.getKey(), lv.method_783());
				}
			}

			if (!jsonObject2.entrySet().isEmpty()) {
				jsonObject.add("criteria", jsonObject2);
			}

			jsonObject.addProperty("done", arg.method_740());
			return jsonObject;
		}

		public class_167 method_745(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
			JsonObject jsonObject = class_3518.method_15295(jsonElement, "advancement");
			JsonObject jsonObject2 = class_3518.method_15281(jsonObject, "criteria", new JsonObject());
			class_167 lv = new class_167();

			for (Entry<String, JsonElement> entry : jsonObject2.entrySet()) {
				String string = (String)entry.getKey();
				lv.field_1160.put(string, class_178.method_788(class_3518.method_15287((JsonElement)entry.getValue(), string)));
			}

			return lv;
		}
	}
}
