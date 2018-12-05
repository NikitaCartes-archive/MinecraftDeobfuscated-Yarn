package net.minecraft.client.render.model.json;

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
import net.minecraft.util.JsonHelper;

@Environment(EnvType.CLIENT)
public class ModelElementTexture {
	public float[] uvs;
	public final int rotation;

	public ModelElementTexture(@Nullable float[] fs, int i) {
		this.uvs = fs;
		this.rotation = i;
	}

	public float getU(int i) {
		if (this.uvs == null) {
			throw new NullPointerException("uvs");
		} else {
			int j = this.method_3413(i);
			return this.uvs[j != 0 && j != 1 ? 2 : 0];
		}
	}

	public float getV(int i) {
		if (this.uvs == null) {
			throw new NullPointerException("uvs");
		} else {
			int j = this.method_3413(i);
			return this.uvs[j != 0 && j != 3 ? 3 : 1];
		}
	}

	private int method_3413(int i) {
		return (i + this.rotation / 90) % 4;
	}

	public int method_3414(int i) {
		return (i + 4 - this.rotation / 90) % 4;
	}

	public void setUvs(float[] fs) {
		if (this.uvs == null) {
			this.uvs = fs;
		}
	}

	@Environment(EnvType.CLIENT)
	static class class_788 implements JsonDeserializer<ModelElementTexture> {
		public ModelElementTexture method_3418(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
			JsonObject jsonObject = jsonElement.getAsJsonObject();
			float[] fs = this.method_3419(jsonObject);
			int i = this.method_3420(jsonObject);
			return new ModelElementTexture(fs, i);
		}

		protected int method_3420(JsonObject jsonObject) {
			int i = JsonHelper.getInt(jsonObject, "rotation", 0);
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
				JsonArray jsonArray = JsonHelper.getArray(jsonObject, "uv");
				if (jsonArray.size() != 4) {
					throw new JsonParseException("Expected 4 uv values, found: " + jsonArray.size());
				} else {
					float[] fs = new float[4];

					for (int i = 0; i < fs.length; i++) {
						fs[i] = JsonHelper.asFloat(jsonArray.get(i), "uv[" + i + "]");
					}

					return fs;
				}
			}
		}
	}
}
