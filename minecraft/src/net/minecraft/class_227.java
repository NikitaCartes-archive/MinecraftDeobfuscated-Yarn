package net.minecraft;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import javax.annotation.Nullable;

public class class_227 implements class_209 {
	@Nullable
	private final Boolean field_1302;
	@Nullable
	private final Boolean field_1301;

	private class_227(@Nullable Boolean boolean_, @Nullable Boolean boolean2) {
		this.field_1302 = boolean_;
		this.field_1301 = boolean2;
	}

	public boolean method_957(class_47 arg) {
		class_3218 lv = arg.method_299();
		return this.field_1302 != null && this.field_1302 != lv.method_8419() ? false : this.field_1301 == null || this.field_1301 == lv.method_8546();
	}

	public static class class_228 extends class_209.class_211<class_227> {
		public class_228() {
			super(new class_2960("weather_check"), class_227.class);
		}

		public void method_960(JsonObject jsonObject, class_227 arg, JsonSerializationContext jsonSerializationContext) {
			jsonObject.addProperty("raining", arg.field_1302);
			jsonObject.addProperty("thundering", arg.field_1301);
		}

		public class_227 method_961(JsonObject jsonObject, JsonDeserializationContext jsonDeserializationContext) {
			Boolean boolean_ = jsonObject.has("raining") ? class_3518.method_15270(jsonObject, "raining") : null;
			Boolean boolean2 = jsonObject.has("thundering") ? class_3518.method_15270(jsonObject, "thundering") : null;
			return new class_227(boolean_, boolean2);
		}
	}
}
