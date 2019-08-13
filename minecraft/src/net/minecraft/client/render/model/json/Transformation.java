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
import net.minecraft.client.util.math.Vector3f;
import net.minecraft.util.JsonHelper;

@Environment(EnvType.CLIENT)
public class Transformation {
	public static final Transformation NONE = new Transformation(new Vector3f(), new Vector3f(), new Vector3f(1.0F, 1.0F, 1.0F));
	public final Vector3f rotation;
	public final Vector3f translation;
	public final Vector3f scale;

	public Transformation(Vector3f vector3f, Vector3f vector3f2, Vector3f vector3f3) {
		this.rotation = new Vector3f(vector3f);
		this.translation = new Vector3f(vector3f2);
		this.scale = new Vector3f(vector3f3);
	}

	public boolean equals(Object object) {
		if (this == object) {
			return true;
		} else if (this.getClass() != object.getClass()) {
			return false;
		} else {
			Transformation transformation = (Transformation)object;
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

		private Vector3f parseVector3f(JsonObject jsonObject, String string, Vector3f vector3f) {
			if (!jsonObject.has(string)) {
				return vector3f;
			} else {
				JsonArray jsonArray = JsonHelper.getArray(jsonObject, string);
				if (jsonArray.size() != 3) {
					throw new JsonParseException("Expected 3 " + string + " values, found: " + jsonArray.size());
				} else {
					float[] fs = new float[3];

					for (int i = 0; i < fs.length; i++) {
						fs[i] = JsonHelper.asFloat(jsonArray.get(i), string + "[" + i + "]");
					}

					return new Vector3f(fs[0], fs[1], fs[2]);
				}
			}
		}
	}
}
