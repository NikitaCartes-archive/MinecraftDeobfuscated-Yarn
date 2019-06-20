package net.minecraft;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import java.lang.reflect.Type;
import java.util.function.IntUnaryOperator;
import javax.annotation.Nullable;

public class class_42 implements IntUnaryOperator {
	private final Integer field_921;
	private final Integer field_920;
	private final IntUnaryOperator field_919;

	private class_42(@Nullable Integer integer, @Nullable Integer integer2) {
		this.field_921 = integer;
		this.field_920 = integer2;
		if (integer == null) {
			if (integer2 == null) {
				this.field_919 = ix -> ix;
			} else {
				int i = integer2;
				this.field_919 = jx -> Math.min(i, jx);
			}
		} else {
			int i = integer;
			if (integer2 == null) {
				this.field_919 = jx -> Math.max(i, jx);
			} else {
				int j = integer2;
				this.field_919 = k -> class_3532.method_15340(k, i, j);
			}
		}
	}

	public static class_42 method_282(int i, int j) {
		return new class_42(i, j);
	}

	public static class_42 method_280(int i) {
		return new class_42(i, null);
	}

	public static class_42 method_277(int i) {
		return new class_42(null, i);
	}

	public int applyAsInt(int i) {
		return this.field_919.applyAsInt(i);
	}

	public static class class_43 implements JsonDeserializer<class_42>, JsonSerializer<class_42> {
		public class_42 method_286(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
			JsonObject jsonObject = class_3518.method_15295(jsonElement, "value");
			Integer integer = jsonObject.has("min") ? class_3518.method_15260(jsonObject, "min") : null;
			Integer integer2 = jsonObject.has("max") ? class_3518.method_15260(jsonObject, "max") : null;
			return new class_42(integer, integer2);
		}

		public JsonElement method_287(class_42 arg, Type type, JsonSerializationContext jsonSerializationContext) {
			JsonObject jsonObject = new JsonObject();
			if (arg.field_920 != null) {
				jsonObject.addProperty("max", arg.field_920);
			}

			if (arg.field_921 != null) {
				jsonObject.addProperty("min", arg.field_921);
			}

			return jsonObject;
		}
	}
}
