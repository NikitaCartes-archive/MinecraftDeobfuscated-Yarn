package net.minecraft;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;

public class class_141 extends class_120 {
	private final class_59 field_1114;

	private class_141(class_209[] args, class_59 arg) {
		super(args);
		this.field_1114 = arg;
	}

	@Override
	public class_1799 method_522(class_1799 arg, class_47 arg2) {
		arg.method_7939(this.field_1114.method_366(arg2.method_294()));
		return arg;
	}

	public static class_120.class_121<?> method_621(class_59 arg) {
		return method_520(args -> new class_141(args, arg));
	}

	public static class class_142 extends class_120.class_123<class_141> {
		protected class_142() {
			super(new class_2960("set_count"), class_141.class);
		}

		public void method_623(JsonObject jsonObject, class_141 arg, JsonSerializationContext jsonSerializationContext) {
			super.method_529(jsonObject, arg, jsonSerializationContext);
			jsonObject.add("count", class_63.method_384(arg.field_1114, jsonSerializationContext));
		}

		public class_141 method_622(JsonObject jsonObject, JsonDeserializationContext jsonDeserializationContext, class_209[] args) {
			class_59 lv = class_63.method_383(jsonObject.get("count"), jsonDeserializationContext);
			return new class_141(args, lv);
		}
	}
}
