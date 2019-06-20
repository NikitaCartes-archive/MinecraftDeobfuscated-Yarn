package net.minecraft;

import com.google.common.collect.ImmutableSet;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import java.util.Set;

public class class_182 implements class_209 {
	private final class_1887 field_1234;
	private final float[] field_1235;

	private class_182(class_1887 arg, float[] fs) {
		this.field_1234 = arg;
		this.field_1235 = fs;
	}

	@Override
	public Set<class_169<?>> method_293() {
		return ImmutableSet.of(class_181.field_1229);
	}

	public boolean method_799(class_47 arg) {
		class_1799 lv = arg.method_296(class_181.field_1229);
		int i = lv != null ? class_1890.method_8225(this.field_1234, lv) : 0;
		float f = this.field_1235[Math.min(i, this.field_1235.length - 1)];
		return arg.method_294().nextFloat() < f;
	}

	public static class_209.class_210 method_800(class_1887 arg, float... fs) {
		return () -> new class_182(arg, fs);
	}

	public static class class_183 extends class_209.class_211<class_182> {
		public class_183() {
			super(new class_2960("table_bonus"), class_182.class);
		}

		public void method_805(JsonObject jsonObject, class_182 arg, JsonSerializationContext jsonSerializationContext) {
			jsonObject.addProperty("enchantment", class_2378.field_11160.method_10221(arg.field_1234).toString());
			jsonObject.add("chances", jsonSerializationContext.serialize(arg.field_1235));
		}

		public class_182 method_804(JsonObject jsonObject, JsonDeserializationContext jsonDeserializationContext) {
			class_2960 lv = new class_2960(class_3518.method_15265(jsonObject, "enchantment"));
			class_1887 lv2 = (class_1887)class_2378.field_11160.method_17966(lv).orElseThrow(() -> new JsonParseException("Invalid enchantment id: " + lv));
			float[] fs = class_3518.method_15272(jsonObject, "chances", jsonDeserializationContext, float[].class);
			return new class_182(lv2, fs);
		}
	}
}
