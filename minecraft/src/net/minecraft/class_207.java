package net.minecraft;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import java.util.Set;
import java.util.function.Function;

public class class_207 implements class_209 {
	private final class_209 field_1283;

	private class_207(class_209 arg) {
		this.field_1283 = arg;
	}

	public final boolean method_888(class_47 arg) {
		return !this.field_1283.test(arg);
	}

	@Override
	public Set<class_169<?>> method_293() {
		return this.field_1283.method_293();
	}

	@Override
	public void method_292(class_58 arg, Function<class_2960, class_52> function, Set<class_2960> set, class_176 arg2) {
		class_209.super.method_292(arg, function, set, arg2);
		this.field_1283.method_292(arg, function, set, arg2);
	}

	public static class_209.class_210 method_889(class_209.class_210 arg) {
		class_207 lv = new class_207(arg.build());
		return () -> lv;
	}

	public static class class_208 extends class_209.class_211<class_207> {
		public class_208() {
			super(new class_2960("inverted"), class_207.class);
		}

		public void method_892(JsonObject jsonObject, class_207 arg, JsonSerializationContext jsonSerializationContext) {
			jsonObject.add("term", jsonSerializationContext.serialize(arg.field_1283));
		}

		public class_207 method_891(JsonObject jsonObject, JsonDeserializationContext jsonDeserializationContext) {
			class_209 lv = class_3518.method_15272(jsonObject, "term", jsonDeserializationContext, class_209.class);
			return new class_207(lv);
		}
	}
}
