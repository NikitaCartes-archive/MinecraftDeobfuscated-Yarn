package net.minecraft;

import com.google.common.collect.Maps;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import java.util.Map;
import java.util.Map.Entry;
import javax.annotation.Nullable;

public class class_175 {
	private final class_184 field_1214;

	public class_175(class_184 arg) {
		this.field_1214 = arg;
	}

	public class_175() {
		this.field_1214 = null;
	}

	public void method_771(class_2540 arg) {
	}

	public static class_175 method_770(JsonObject jsonObject, JsonDeserializationContext jsonDeserializationContext) {
		class_2960 lv = new class_2960(class_3518.method_15265(jsonObject, "trigger"));
		class_179<?> lv2 = class_174.method_765(lv);
		if (lv2 == null) {
			throw new JsonSyntaxException("Invalid criterion trigger: " + lv);
		} else {
			class_184 lv3 = lv2.method_795(class_3518.method_15281(jsonObject, "conditions", new JsonObject()), jsonDeserializationContext);
			return new class_175(lv3);
		}
	}

	public static class_175 method_769(class_2540 arg) {
		return new class_175();
	}

	public static Map<String, class_175> method_772(JsonObject jsonObject, JsonDeserializationContext jsonDeserializationContext) {
		Map<String, class_175> map = Maps.<String, class_175>newHashMap();

		for (Entry<String, JsonElement> entry : jsonObject.entrySet()) {
			map.put(entry.getKey(), method_770(class_3518.method_15295((JsonElement)entry.getValue(), "criterion"), jsonDeserializationContext));
		}

		return map;
	}

	public static Map<String, class_175> method_768(class_2540 arg) {
		Map<String, class_175> map = Maps.<String, class_175>newHashMap();
		int i = arg.method_10816();

		for (int j = 0; j < i; j++) {
			map.put(arg.method_10800(32767), method_769(arg));
		}

		return map;
	}

	public static void method_775(Map<String, class_175> map, class_2540 arg) {
		arg.method_10804(map.size());

		for (Entry<String, class_175> entry : map.entrySet()) {
			arg.method_10814((String)entry.getKey());
			((class_175)entry.getValue()).method_771(arg);
		}
	}

	@Nullable
	public class_184 method_774() {
		return this.field_1214;
	}

	public JsonElement method_773() {
		JsonObject jsonObject = new JsonObject();
		jsonObject.addProperty("trigger", this.field_1214.method_806().toString());
		jsonObject.add("conditions", this.field_1214.method_807());
		return jsonObject;
	}
}
