package net.minecraft;

import com.google.common.collect.ImmutableSet;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import java.util.Set;

public class class_223 implements class_209 {
	private final class_2073 field_1298;

	public class_223(class_2073 arg) {
		this.field_1298 = arg;
	}

	@Override
	public Set<class_169<?>> method_293() {
		return ImmutableSet.of(class_181.field_1229);
	}

	public boolean method_946(class_47 arg) {
		class_1799 lv = arg.method_296(class_181.field_1229);
		return lv != null && this.field_1298.method_8970(lv);
	}

	public static class_209.class_210 method_945(class_2073.class_2074 arg) {
		return () -> new class_223(arg.method_8976());
	}

	public static class class_224 extends class_209.class_211<class_223> {
		protected class_224() {
			super(new class_2960("match_tool"), class_223.class);
		}

		public void method_948(JsonObject jsonObject, class_223 arg, JsonSerializationContext jsonSerializationContext) {
			jsonObject.add("predicate", arg.field_1298.method_8971());
		}

		public class_223 method_949(JsonObject jsonObject, JsonDeserializationContext jsonDeserializationContext) {
			class_2073 lv = class_2073.method_8969(jsonObject.get("predicate"));
			return new class_223(lv);
		}
	}
}
