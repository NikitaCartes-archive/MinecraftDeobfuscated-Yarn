package net.minecraft;

import com.google.common.collect.Maps;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import java.lang.reflect.Type;
import java.util.Map;
import java.util.Map.Entry;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class class_799 {
	private final class_2960 field_4268;
	private final Map<class_2960, Float> field_4269;

	public class_799(class_2960 arg, Map<class_2960, Float> map) {
		this.field_4268 = arg;
		this.field_4269 = map;
	}

	public class_2960 method_3472() {
		return this.field_4268;
	}

	boolean method_3473(class_1799 arg, @Nullable class_1937 arg2, @Nullable class_1309 arg3) {
		class_1792 lv = arg.method_7909();

		for (Entry<class_2960, Float> entry : this.field_4269.entrySet()) {
			class_1800 lv2 = lv.method_7868((class_2960)entry.getKey());
			if (lv2 == null || lv2.call(arg, arg2, arg3) < (Float)entry.getValue()) {
				return false;
			}
		}

		return true;
	}

	@Environment(EnvType.CLIENT)
	public static class class_800 implements JsonDeserializer<class_799> {
		protected class_800() {
		}

		public class_799 method_3475(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
			JsonObject jsonObject = jsonElement.getAsJsonObject();
			class_2960 lv = new class_2960(class_3518.method_15265(jsonObject, "model"));
			Map<class_2960, Float> map = this.method_3474(jsonObject);
			return new class_799(lv, map);
		}

		protected Map<class_2960, Float> method_3474(JsonObject jsonObject) {
			Map<class_2960, Float> map = Maps.<class_2960, Float>newLinkedHashMap();
			JsonObject jsonObject2 = class_3518.method_15296(jsonObject, "predicate");

			for (Entry<String, JsonElement> entry : jsonObject2.entrySet()) {
				map.put(new class_2960((String)entry.getKey()), class_3518.method_15269((JsonElement)entry.getValue(), (String)entry.getKey()));
			}

			return map;
		}
	}
}
