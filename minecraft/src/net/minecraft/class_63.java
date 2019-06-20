package net.minecraft;

import com.google.common.collect.Maps;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import java.util.Map;

public class class_63 {
	private static final Map<class_2960, Class<? extends class_59>> field_978 = Maps.<class_2960, Class<? extends class_59>>newHashMap();

	public static class_59 method_383(JsonElement jsonElement, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
		if (jsonElement.isJsonPrimitive()) {
			return jsonDeserializationContext.deserialize(jsonElement, class_44.class);
		} else {
			JsonObject jsonObject = jsonElement.getAsJsonObject();
			String string = class_3518.method_15253(jsonObject, "type", class_59.field_967.toString());
			Class<? extends class_59> class_ = (Class<? extends class_59>)field_978.get(new class_2960(string));
			if (class_ == null) {
				throw new JsonParseException("Unknown generator: " + string);
			} else {
				return jsonDeserializationContext.deserialize(jsonObject, class_);
			}
		}
	}

	public static JsonElement method_384(class_59 arg, JsonSerializationContext jsonSerializationContext) {
		JsonElement jsonElement = jsonSerializationContext.serialize(arg);
		if (jsonElement.isJsonObject()) {
			jsonElement.getAsJsonObject().addProperty("type", arg.method_365().toString());
		}

		return jsonElement;
	}

	static {
		field_978.put(class_59.field_967, class_61.class);
		field_978.put(class_59.field_969, class_40.class);
		field_978.put(class_59.field_968, class_44.class);
	}
}
