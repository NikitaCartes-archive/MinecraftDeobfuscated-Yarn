package net.minecraft;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;

public class class_114 extends class_120 {
	private final class_42 field_1044;

	private class_114(class_209[] args, class_42 arg) {
		super(args);
		this.field_1044 = arg;
	}

	@Override
	public class_1799 method_522(class_1799 arg, class_47 arg2) {
		int i = this.field_1044.applyAsInt(arg.method_7947());
		arg.method_7939(i);
		return arg;
	}

	public static class_120.class_121<?> method_506(class_42 arg) {
		return method_520(args -> new class_114(args, arg));
	}

	public static class class_115 extends class_120.class_123<class_114> {
		protected class_115() {
			super(new class_2960("limit_count"), class_114.class);
		}

		public void method_510(JsonObject jsonObject, class_114 arg, JsonSerializationContext jsonSerializationContext) {
			super.method_529(jsonObject, arg, jsonSerializationContext);
			jsonObject.add("limit", jsonSerializationContext.serialize(arg.field_1044));
		}

		public class_114 method_509(JsonObject jsonObject, JsonDeserializationContext jsonDeserializationContext, class_209[] args) {
			class_42 lv = class_3518.method_15272(jsonObject, "limit", jsonDeserializationContext, class_42.class);
			return new class_114(args, lv);
		}
	}
}
