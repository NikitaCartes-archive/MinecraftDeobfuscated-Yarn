package net.minecraft;

import com.google.common.collect.ImmutableSet;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import java.util.Random;
import java.util.Set;

public class class_201 implements class_209 {
	private static final class_201 field_1280 = new class_201();

	private class_201() {
	}

	@Override
	public Set<class_169<?>> method_293() {
		return ImmutableSet.of(class_181.field_1225);
	}

	public boolean method_869(class_47 arg) {
		Float float_ = arg.method_296(class_181.field_1225);
		if (float_ != null) {
			Random random = arg.method_294();
			float f = 1.0F / float_;
			return random.nextFloat() <= f;
		} else {
			return true;
		}
	}

	public static class_209.class_210 method_871() {
		return () -> field_1280;
	}

	public static class class_202 extends class_209.class_211<class_201> {
		protected class_202() {
			super(new class_2960("survives_explosion"), class_201.class);
		}

		public void method_874(JsonObject jsonObject, class_201 arg, JsonSerializationContext jsonSerializationContext) {
		}

		public class_201 method_873(JsonObject jsonObject, JsonDeserializationContext jsonDeserializationContext) {
			return class_201.field_1280;
		}
	}
}
