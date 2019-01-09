package net.minecraft;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import java.lang.reflect.Type;
import java.util.Random;

public class class_61 implements class_59 {
	private final float field_977;
	private final float field_976;

	public class_61(float f, float g) {
		this.field_977 = f;
		this.field_976 = g;
	}

	public class_61(float f) {
		this.field_977 = f;
		this.field_976 = f;
	}

	public static class_61 method_377(float f, float g) {
		return new class_61(f, g);
	}

	public float method_378() {
		return this.field_977;
	}

	public float method_380() {
		return this.field_976;
	}

	@Override
	public int method_366(Random random) {
		return class_3532.method_15395(random, class_3532.method_15375(this.field_977), class_3532.method_15375(this.field_976));
	}

	public float method_374(Random random) {
		return class_3532.method_15344(random, this.field_977, this.field_976);
	}

	public boolean method_376(int i) {
		return (float)i <= this.field_976 && (float)i >= this.field_977;
	}

	@Override
	public class_2960 method_365() {
		return field_967;
	}

	public static class class_62 implements JsonDeserializer<class_61>, JsonSerializer<class_61> {
		public class_61 method_381(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
			if (class_3518.method_15275(jsonElement)) {
				return new class_61(class_3518.method_15269(jsonElement, "value"));
			} else {
				JsonObject jsonObject = class_3518.method_15295(jsonElement, "value");
				float f = class_3518.method_15259(jsonObject, "min");
				float g = class_3518.method_15259(jsonObject, "max");
				return new class_61(f, g);
			}
		}

		public JsonElement method_382(class_61 arg, Type type, JsonSerializationContext jsonSerializationContext) {
			if (arg.field_977 == arg.field_976) {
				return new JsonPrimitive(arg.field_977);
			} else {
				JsonObject jsonObject = new JsonObject();
				jsonObject.addProperty("min", arg.field_977);
				jsonObject.addProperty("max", arg.field_976);
				return jsonObject;
			}
		}
	}
}
