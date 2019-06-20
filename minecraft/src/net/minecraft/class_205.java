package net.minecraft;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;

public class class_205 implements class_209 {
	private final class_2090 field_1282;

	private class_205(class_2090 arg) {
		this.field_1282 = arg;
	}

	public boolean method_881(class_47 arg) {
		class_2338 lv = arg.method_296(class_181.field_1232);
		return lv != null && this.field_1282.method_9020(arg.method_299(), (float)lv.method_10263(), (float)lv.method_10264(), (float)lv.method_10260());
	}

	public static class_209.class_210 method_884(class_2090.class_2091 arg) {
		return () -> new class_205(arg.method_9023());
	}

	public static class class_206 extends class_209.class_211<class_205> {
		public class_206() {
			super(new class_2960("location_check"), class_205.class);
		}

		public void method_886(JsonObject jsonObject, class_205 arg, JsonSerializationContext jsonSerializationContext) {
			jsonObject.add("predicate", arg.field_1282.method_9019());
		}

		public class_205 method_885(JsonObject jsonObject, JsonDeserializationContext jsonDeserializationContext) {
			class_2090 lv = class_2090.method_9021(jsonObject.get("predicate"));
			return new class_205(lv);
		}
	}
}
