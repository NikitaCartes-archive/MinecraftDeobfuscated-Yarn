package net.minecraft;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;

public class class_219 implements class_209 {
	private final float field_1296;

	private class_219(float f) {
		this.field_1296 = f;
	}

	public boolean method_934(class_47 arg) {
		return arg.method_294().nextFloat() < this.field_1296;
	}

	public static class_209.class_210 method_932(float f) {
		return () -> new class_219(f);
	}

	public static class class_220 extends class_209.class_211<class_219> {
		protected class_220() {
			super(new class_2960("random_chance"), class_219.class);
		}

		public void method_936(JsonObject jsonObject, class_219 arg, JsonSerializationContext jsonSerializationContext) {
			jsonObject.addProperty("chance", arg.field_1296);
		}

		public class_219 method_937(JsonObject jsonObject, JsonDeserializationContext jsonDeserializationContext) {
			return new class_219(class_3518.method_15259(jsonObject, "chance"));
		}
	}
}
