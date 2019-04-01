package net.minecraft;

import com.google.common.collect.ImmutableSet;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import java.util.Set;

public class class_225 implements class_209 {
	private final float field_1300;
	private final float field_1299;

	private class_225(float f, float g) {
		this.field_1300 = f;
		this.field_1299 = g;
	}

	@Override
	public Set<class_169<?>> method_293() {
		return ImmutableSet.of(class_181.field_1230);
	}

	public boolean method_950(class_47 arg) {
		class_1297 lv = arg.method_296(class_181.field_1230);
		int i = 0;
		if (lv instanceof class_1309) {
			i = class_1890.method_8226((class_1309)lv);
		}

		return arg.method_294().nextFloat() < this.field_1300 + (float)i * this.field_1299;
	}

	public static class_209.class_210 method_953(float f, float g) {
		return () -> new class_225(f, g);
	}

	public static class class_226 extends class_209.class_211<class_225> {
		protected class_226() {
			super(new class_2960("random_chance_with_looting"), class_225.class);
		}

		public void method_955(JsonObject jsonObject, class_225 arg, JsonSerializationContext jsonSerializationContext) {
			jsonObject.addProperty("chance", arg.field_1300);
			jsonObject.addProperty("looting_multiplier", arg.field_1299);
		}

		public class_225 method_956(JsonObject jsonObject, JsonDeserializationContext jsonDeserializationContext) {
			return new class_225(class_3518.method_15259(jsonObject, "chance"), class_3518.method_15259(jsonObject, "looting_multiplier"));
		}
	}
}
