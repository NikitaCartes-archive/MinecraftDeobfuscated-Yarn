package net.minecraft;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import java.util.Random;

public class class_106 extends class_120 {
	private final class_59 field_1026;
	private final boolean field_1027;

	private class_106(class_209[] args, class_59 arg, boolean bl) {
		super(args);
		this.field_1026 = arg;
		this.field_1027 = bl;
	}

	@Override
	public class_1799 method_522(class_1799 arg, class_47 arg2) {
		Random random = arg2.method_294();
		return class_1890.method_8233(random, arg, this.field_1026.method_366(random), this.field_1027);
	}

	public static class_106.class_107 method_481(class_59 arg) {
		return new class_106.class_107(arg);
	}

	public static class class_107 extends class_120.class_121<class_106.class_107> {
		private final class_59 field_1028;
		private boolean field_1029;

		public class_107(class_59 arg) {
			this.field_1028 = arg;
		}

		protected class_106.class_107 method_483() {
			return this;
		}

		public class_106.class_107 method_484() {
			this.field_1029 = true;
			return this;
		}

		@Override
		public class_117 method_515() {
			return new class_106(this.method_526(), this.field_1028, this.field_1029);
		}
	}

	public static class class_108 extends class_120.class_123<class_106> {
		public class_108() {
			super(new class_2960("enchant_with_levels"), class_106.class);
		}

		public void method_485(JsonObject jsonObject, class_106 arg, JsonSerializationContext jsonSerializationContext) {
			super.method_529(jsonObject, arg, jsonSerializationContext);
			jsonObject.add("levels", class_63.method_384(arg.field_1026, jsonSerializationContext));
			jsonObject.addProperty("treasure", arg.field_1027);
		}

		public class_106 method_486(JsonObject jsonObject, JsonDeserializationContext jsonDeserializationContext, class_209[] args) {
			class_59 lv = class_63.method_383(jsonObject.get("levels"), jsonDeserializationContext);
			boolean bl = class_3518.method_15258(jsonObject, "treasure", false);
			return new class_106(args, lv, bl);
		}
	}
}
