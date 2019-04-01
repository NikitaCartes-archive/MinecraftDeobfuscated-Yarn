package net.minecraft;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import java.lang.reflect.Type;
import java.util.Random;

public final class class_40 implements class_59 {
	private final int field_918;
	private final float field_917;

	public class_40(int i, float f) {
		this.field_918 = i;
		this.field_917 = f;
	}

	@Override
	public int method_366(Random random) {
		int i = 0;

		for (int j = 0; j < this.field_918; j++) {
			if (random.nextFloat() < this.field_917) {
				i++;
			}
		}

		return i;
	}

	public static class_40 method_273(int i, float f) {
		return new class_40(i, f);
	}

	@Override
	public class_2960 method_365() {
		return field_969;
	}

	public static class class_41 implements JsonDeserializer<class_40>, JsonSerializer<class_40> {
		public class_40 method_275(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
			JsonObject jsonObject = class_3518.method_15295(jsonElement, "value");
			int i = class_3518.method_15260(jsonObject, "n");
			float f = class_3518.method_15259(jsonObject, "p");
			return new class_40(i, f);
		}

		public JsonElement method_276(class_40 arg, Type type, JsonSerializationContext jsonSerializationContext) {
			JsonObject jsonObject = new JsonObject();
			jsonObject.addProperty("n", arg.field_918);
			jsonObject.addProperty("p", arg.field_917);
			return jsonObject;
		}
	}
}
