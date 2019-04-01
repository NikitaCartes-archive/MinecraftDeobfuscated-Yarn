package net.minecraft;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import java.lang.reflect.Type;
import java.util.Random;

public final class class_44 implements class_59 {
	private final int field_922;

	public class_44(int i) {
		this.field_922 = i;
	}

	@Override
	public int method_366(Random random) {
		return this.field_922;
	}

	@Override
	public class_2960 method_365() {
		return field_968;
	}

	public static class_44 method_289(int i) {
		return new class_44(i);
	}

	public static class class_45 implements JsonDeserializer<class_44>, JsonSerializer<class_44> {
		public class_44 method_291(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
			return new class_44(class_3518.method_15257(jsonElement, "value"));
		}

		public JsonElement method_290(class_44 arg, Type type, JsonSerializationContext jsonSerializationContext) {
			return new JsonPrimitive(arg.field_922);
		}
	}
}
