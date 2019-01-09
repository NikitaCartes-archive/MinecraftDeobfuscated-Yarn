package net.minecraft;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Iterables;
import com.google.common.collect.Maps;
import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSyntaxException;
import java.util.Map;
import java.util.Random;
import java.util.Map.Entry;

public class class_152 extends class_120 {
	private final Map<class_1291, class_61> field_1122;

	private class_152(class_209[] args, Map<class_1291, class_61> map) {
		super(args);
		this.field_1122 = ImmutableMap.copyOf(map);
	}

	@Override
	public class_1799 method_522(class_1799 arg, class_47 arg2) {
		if (arg.method_7909() == class_1802.field_8766 && !this.field_1122.isEmpty()) {
			Random random = arg2.method_294();
			int i = random.nextInt(this.field_1122.size());
			Entry<class_1291, class_61> entry = Iterables.get(this.field_1122.entrySet(), i);
			class_1291 lv = (class_1291)entry.getKey();
			int j = ((class_61)entry.getValue()).method_366(random) * 20;
			class_1830.method_8021(arg, lv, j);
			return arg;
		} else {
			return arg;
		}
	}

	public static class_152.class_153 method_637() {
		return new class_152.class_153();
	}

	public static class class_153 extends class_120.class_121<class_152.class_153> {
		private final Map<class_1291, class_61> field_1123 = Maps.<class_1291, class_61>newHashMap();

		protected class_152.class_153 method_639() {
			return this;
		}

		public class_152.class_153 method_640(class_1291 arg, class_61 arg2) {
			this.field_1123.put(arg, arg2);
			return this;
		}

		@Override
		public class_117 method_515() {
			return new class_152(this.method_526(), this.field_1123);
		}
	}

	public static class class_154 extends class_120.class_123<class_152> {
		public class_154() {
			super(new class_2960("set_stew_effect"), class_152.class);
		}

		public void method_642(JsonObject jsonObject, class_152 arg, JsonSerializationContext jsonSerializationContext) {
			super.method_529(jsonObject, arg, jsonSerializationContext);
			if (!arg.field_1122.isEmpty()) {
				JsonArray jsonArray = new JsonArray();

				for (class_1291 lv : arg.field_1122.keySet()) {
					JsonObject jsonObject2 = new JsonObject();
					class_2960 lv2 = class_2378.field_11159.method_10221(lv);
					if (lv2 == null) {
						throw new IllegalArgumentException("Don't know how to serialize mob effect " + lv);
					}

					jsonObject2.add("type", new JsonPrimitive(lv2.toString()));
					jsonObject2.add("duration", jsonSerializationContext.serialize(arg.field_1122.get(lv)));
					jsonArray.add(jsonObject2);
				}

				jsonObject.add("effects", jsonArray);
			}
		}

		public class_152 method_641(JsonObject jsonObject, JsonDeserializationContext jsonDeserializationContext, class_209[] args) {
			Map<class_1291, class_61> map = Maps.<class_1291, class_61>newHashMap();
			if (jsonObject.has("effects")) {
				for (JsonElement jsonElement : class_3518.method_15261(jsonObject, "effects")) {
					String string = class_3518.method_15265(jsonElement.getAsJsonObject(), "type");
					class_1291 lv = class_2378.field_11159.method_10223(new class_2960(string));
					if (lv == null) {
						throw new JsonSyntaxException("Unknown mob effect '" + string + "'");
					}

					class_61 lv2 = class_3518.method_15272(jsonElement.getAsJsonObject(), "duration", jsonDeserializationContext, class_61.class);
					map.put(lv, lv2);
				}
			}

			return new class_152(args, map);
		}
	}
}
