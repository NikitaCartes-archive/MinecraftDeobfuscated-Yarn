package net.minecraft;

import com.google.common.collect.ImmutableSet;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import java.util.Set;

public class class_125 extends class_120 {
	private final class_61 field_1082;
	private final int field_1083;

	private class_125(class_209[] args, class_61 arg, int i) {
		super(args);
		this.field_1082 = arg;
		this.field_1083 = i;
	}

	@Override
	public Set<class_169<?>> method_293() {
		return ImmutableSet.of(class_181.field_1230);
	}

	private boolean method_549() {
		return this.field_1083 > 0;
	}

	@Override
	public class_1799 method_522(class_1799 arg, class_47 arg2) {
		class_1297 lv = arg2.method_296(class_181.field_1230);
		if (lv instanceof class_1309) {
			int i = class_1890.method_8226((class_1309)lv);
			if (i == 0) {
				return arg;
			}

			float f = (float)i * this.field_1082.method_374(arg2.method_294());
			arg.method_7933(Math.round(f));
			if (this.method_549() && arg.method_7947() > this.field_1083) {
				arg.method_7939(this.field_1083);
			}
		}

		return arg;
	}

	public static class_125.class_126 method_547(class_61 arg) {
		return new class_125.class_126(arg);
	}

	public static class class_126 extends class_120.class_121<class_125.class_126> {
		private final class_61 field_1084;
		private int field_1085 = 0;

		public class_126(class_61 arg) {
			this.field_1084 = arg;
		}

		protected class_125.class_126 method_552() {
			return this;
		}

		public class_125.class_126 method_551(int i) {
			this.field_1085 = i;
			return this;
		}

		@Override
		public class_117 method_515() {
			return new class_125(this.method_526(), this.field_1084, this.field_1085);
		}
	}

	public static class class_127 extends class_120.class_123<class_125> {
		protected class_127() {
			super(new class_2960("looting_enchant"), class_125.class);
		}

		public void method_553(JsonObject jsonObject, class_125 arg, JsonSerializationContext jsonSerializationContext) {
			super.method_529(jsonObject, arg, jsonSerializationContext);
			jsonObject.add("count", jsonSerializationContext.serialize(arg.field_1082));
			if (arg.method_549()) {
				jsonObject.add("limit", jsonSerializationContext.serialize(arg.field_1083));
			}
		}

		public class_125 method_554(JsonObject jsonObject, JsonDeserializationContext jsonDeserializationContext, class_209[] args) {
			int i = class_3518.method_15282(jsonObject, "limit", 0);
			return new class_125(args, class_3518.method_15272(jsonObject, "count", jsonDeserializationContext, class_61.class), i);
		}
	}
}
