package net.minecraft;

import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import java.lang.reflect.Type;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class class_787 {
	public float[] field_4235;
	public final int field_4234;

	public class_787(@Nullable float[] fs, int i) {
		this.field_4235 = fs;
		this.field_4234 = i;
	}

	public float method_3415(int i) {
		if (this.field_4235 == null) {
			throw new NullPointerException("uvs");
		} else {
			int j = this.method_3413(i);
			return this.field_4235[j != 0 && j != 1 ? 2 : 0];
		}
	}

	public float method_3416(int i) {
		if (this.field_4235 == null) {
			throw new NullPointerException("uvs");
		} else {
			int j = this.method_3413(i);
			return this.field_4235[j != 0 && j != 3 ? 3 : 1];
		}
	}

	private int method_3413(int i) {
		return (i + this.field_4234 / 90) % 4;
	}

	public int method_3414(int i) {
		return (i + 4 - this.field_4234 / 90) % 4;
	}

	public void method_3417(float[] fs) {
		if (this.field_4235 == null) {
			this.field_4235 = fs;
		}
	}

	@Environment(EnvType.CLIENT)
	public static class class_788 implements JsonDeserializer<class_787> {
		protected class_788() {
		}

		public class_787 method_3418(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
			JsonObject jsonObject = jsonElement.getAsJsonObject();
			float[] fs = this.method_3419(jsonObject);
			int i = this.method_3420(jsonObject);
			return new class_787(fs, i);
		}

		protected int method_3420(JsonObject jsonObject) {
			int i = class_3518.method_15282(jsonObject, "rotation", 0);
			if (i >= 0 && i % 90 == 0 && i / 90 <= 3) {
				return i;
			} else {
				throw new JsonParseException("Invalid rotation " + i + " found, only 0/90/180/270 allowed");
			}
		}

		@Nullable
		private float[] method_3419(JsonObject jsonObject) {
			if (!jsonObject.has("uv")) {
				return null;
			} else {
				JsonArray jsonArray = class_3518.method_15261(jsonObject, "uv");
				if (jsonArray.size() != 4) {
					throw new JsonParseException("Expected 4 uv values, found: " + jsonArray.size());
				} else {
					float[] fs = new float[4];

					for (int i = 0; i < fs.length; i++) {
						fs[i] = class_3518.method_15269(jsonArray.get(i), "uv[" + i + "]");
					}

					return fs;
				}
			}
		}
	}
}
