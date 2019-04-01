package net.minecraft;

import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import java.lang.reflect.Type;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class class_804 {
	public static final class_804 field_4284 = new class_804(new class_1160(), new class_1160(), new class_1160(1.0F, 1.0F, 1.0F));
	public final class_1160 field_4287;
	public final class_1160 field_4286;
	public final class_1160 field_4285;

	public class_804(class_1160 arg, class_1160 arg2, class_1160 arg3) {
		this.field_4287 = new class_1160(arg);
		this.field_4286 = new class_1160(arg2);
		this.field_4285 = new class_1160(arg3);
	}

	public boolean equals(Object object) {
		if (this == object) {
			return true;
		} else if (this.getClass() != object.getClass()) {
			return false;
		} else {
			class_804 lv = (class_804)object;
			return this.field_4287.equals(lv.field_4287) && this.field_4285.equals(lv.field_4285) && this.field_4286.equals(lv.field_4286);
		}
	}

	public int hashCode() {
		int i = this.field_4287.hashCode();
		i = 31 * i + this.field_4286.hashCode();
		return 31 * i + this.field_4285.hashCode();
	}

	@Environment(EnvType.CLIENT)
	public static class class_805 implements JsonDeserializer<class_804> {
		private static final class_1160 field_4288 = new class_1160(0.0F, 0.0F, 0.0F);
		private static final class_1160 field_4290 = new class_1160(0.0F, 0.0F, 0.0F);
		private static final class_1160 field_4289 = new class_1160(1.0F, 1.0F, 1.0F);

		protected class_805() {
		}

		public class_804 method_3494(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
			JsonObject jsonObject = jsonElement.getAsJsonObject();
			class_1160 lv = this.method_3493(jsonObject, "rotation", field_4288);
			class_1160 lv2 = this.method_3493(jsonObject, "translation", field_4290);
			lv2.method_4942(0.0625F);
			lv2.method_4946(-5.0F, 5.0F);
			class_1160 lv3 = this.method_3493(jsonObject, "scale", field_4289);
			lv3.method_4946(-4.0F, 4.0F);
			return new class_804(lv, lv2, lv3);
		}

		private class_1160 method_3493(JsonObject jsonObject, String string, class_1160 arg) {
			if (!jsonObject.has(string)) {
				return arg;
			} else {
				JsonArray jsonArray = class_3518.method_15261(jsonObject, string);
				if (jsonArray.size() != 3) {
					throw new JsonParseException("Expected 3 " + string + " values, found: " + jsonArray.size());
				} else {
					float[] fs = new float[3];

					for (int i = 0; i < fs.length; i++) {
						fs[i] = class_3518.method_15269(jsonArray.get(i), string + "[" + i + "]");
					}

					return new class_1160(fs[0], fs[1], fs[2]);
				}
			}
		}
	}
}
