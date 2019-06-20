package net.minecraft;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import java.util.Map;
import javax.annotation.Nullable;

public class class_2035 {
	public static final class_2035 field_9571 = new class_2035();
	private final class_1887 field_9569;
	private final class_2096.class_2100 field_9570;

	public class_2035() {
		this.field_9569 = null;
		this.field_9570 = class_2096.class_2100.field_9708;
	}

	public class_2035(@Nullable class_1887 arg, class_2096.class_2100 arg2) {
		this.field_9569 = arg;
		this.field_9570 = arg2;
	}

	public boolean method_8880(Map<class_1887, Integer> map) {
		if (this.field_9569 != null) {
			if (!map.containsKey(this.field_9569)) {
				return false;
			}

			int i = (Integer)map.get(this.field_9569);
			if (this.field_9570 != null && !this.field_9570.method_9054(i)) {
				return false;
			}
		} else if (this.field_9570 != null) {
			for (Integer integer : map.values()) {
				if (this.field_9570.method_9054(integer)) {
					return true;
				}
			}

			return false;
		}

		return true;
	}

	public JsonElement method_8881() {
		if (this == field_9571) {
			return JsonNull.INSTANCE;
		} else {
			JsonObject jsonObject = new JsonObject();
			if (this.field_9569 != null) {
				jsonObject.addProperty("enchantment", class_2378.field_11160.method_10221(this.field_9569).toString());
			}

			jsonObject.add("levels", this.field_9570.method_9036());
			return jsonObject;
		}
	}

	public static class_2035 method_8882(@Nullable JsonElement jsonElement) {
		if (jsonElement != null && !jsonElement.isJsonNull()) {
			JsonObject jsonObject = class_3518.method_15295(jsonElement, "enchantment");
			class_1887 lv = null;
			if (jsonObject.has("enchantment")) {
				class_2960 lv2 = new class_2960(class_3518.method_15265(jsonObject, "enchantment"));
				lv = (class_1887)class_2378.field_11160.method_17966(lv2).orElseThrow(() -> new JsonSyntaxException("Unknown enchantment '" + lv2 + "'"));
			}

			class_2096.class_2100 lv3 = class_2096.class_2100.method_9056(jsonObject.get("levels"));
			return new class_2035(lv, lv3);
		} else {
			return field_9571;
		}
	}

	public static class_2035[] method_8879(@Nullable JsonElement jsonElement) {
		if (jsonElement != null && !jsonElement.isJsonNull()) {
			JsonArray jsonArray = class_3518.method_15252(jsonElement, "enchantments");
			class_2035[] lvs = new class_2035[jsonArray.size()];

			for (int i = 0; i < lvs.length; i++) {
				lvs[i] = method_8882(jsonArray.get(i));
			}

			return lvs;
		} else {
			return new class_2035[0];
		}
	}
}
