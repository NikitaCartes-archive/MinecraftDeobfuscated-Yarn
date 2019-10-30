package net.minecraft.client.render.model.json;

import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import java.lang.reflect.Type;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.util.math.Vector3f;
import net.minecraft.util.JsonHelper;
import net.minecraft.util.math.Quaternion;

@Environment(EnvType.CLIENT)
public class Transformation {
	public static final Transformation NONE = new Transformation(new Vector3f(), new Vector3f(), new Vector3f(1.0F, 1.0F, 1.0F));
	public final Vector3f rotation;
	public final Vector3f translation;
	public final Vector3f scale;

	public Transformation(Vector3f rotation, Vector3f translation, Vector3f scale) {
		this.rotation = new Vector3f(rotation);
		this.translation = new Vector3f(translation);
		this.scale = new Vector3f(scale);
	}

	public void method_23075(boolean bl, MatrixStack matrixStack) {
		if (this != NONE) {
			float f = this.rotation.getX();
			float g = this.rotation.getY();
			float h = this.rotation.getZ();
			if (bl) {
				g = -g;
				h = -h;
			}

			int i = bl ? -1 : 1;
			matrixStack.translate((double)((float)i * this.translation.getX()), (double)this.translation.getY(), (double)this.translation.getZ());
			matrixStack.multiply(new Quaternion(f, g, h, true));
			matrixStack.scale(this.scale.getX(), this.scale.getY(), this.scale.getZ());
		}
	}

	public boolean equals(Object o) {
		if (this == o) {
			return true;
		} else if (this.getClass() != o.getClass()) {
			return false;
		} else {
			Transformation transformation = (Transformation)o;
			return this.rotation.equals(transformation.rotation) && this.scale.equals(transformation.scale) && this.translation.equals(transformation.translation);
		}
	}

	public int hashCode() {
		int i = this.rotation.hashCode();
		i = 31 * i + this.translation.hashCode();
		return 31 * i + this.scale.hashCode();
	}

	@Environment(EnvType.CLIENT)
	public static class Deserializer implements JsonDeserializer<Transformation> {
		private static final Vector3f DEFAULT_ROATATION = new Vector3f(0.0F, 0.0F, 0.0F);
		private static final Vector3f DEFAULT_TRANSLATION = new Vector3f(0.0F, 0.0F, 0.0F);
		private static final Vector3f DEFAULT_SCALE = new Vector3f(1.0F, 1.0F, 1.0F);

		protected Deserializer() {
		}

		public Transformation method_3494(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
			JsonObject jsonObject = jsonElement.getAsJsonObject();
			Vector3f vector3f = this.parseVector3f(jsonObject, "rotation", DEFAULT_ROATATION);
			Vector3f vector3f2 = this.parseVector3f(jsonObject, "translation", DEFAULT_TRANSLATION);
			vector3f2.scale(0.0625F);
			vector3f2.clamp(-5.0F, 5.0F);
			Vector3f vector3f3 = this.parseVector3f(jsonObject, "scale", DEFAULT_SCALE);
			vector3f3.clamp(-4.0F, 4.0F);
			return new Transformation(vector3f, vector3f2, vector3f3);
		}

		private Vector3f parseVector3f(JsonObject json, String key, Vector3f default_) {
			if (!json.has(key)) {
				return default_;
			} else {
				JsonArray jsonArray = JsonHelper.getArray(json, key);
				if (jsonArray.size() != 3) {
					throw new JsonParseException("Expected 3 " + key + " values, found: " + jsonArray.size());
				} else {
					float[] fs = new float[3];

					for (int i = 0; i < fs.length; i++) {
						fs[i] = JsonHelper.asFloat(jsonArray.get(i), key + "[" + i + "]");
					}

					return new Vector3f(fs[0], fs[1], fs[2]);
				}
			}
		}
	}
}
