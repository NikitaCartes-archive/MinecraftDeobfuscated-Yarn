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
import net.minecraft.sortme.Vector3f;
import net.minecraft.util.JsonHelper;

@Environment(EnvType.CLIENT)
public class ModelTransformation {
	public static final ModelTransformation ORIGIN = new ModelTransformation(new Vector3f(), new Vector3f(), new Vector3f(1.0F, 1.0F, 1.0F));
	public final Vector3f field_4287;
	public final Vector3f field_4286;
	public final Vector3f field_4285;

	public ModelTransformation(Vector3f vector3f, Vector3f vector3f2, Vector3f vector3f3) {
		this.field_4287 = new Vector3f(vector3f);
		this.field_4286 = new Vector3f(vector3f2);
		this.field_4285 = new Vector3f(vector3f3);
	}

	public boolean equals(Object object) {
		if (this == object) {
			return true;
		} else if (this.getClass() != object.getClass()) {
			return false;
		} else {
			ModelTransformation modelTransformation = (ModelTransformation)object;
			return this.field_4287.equals(modelTransformation.field_4287)
				&& this.field_4285.equals(modelTransformation.field_4285)
				&& this.field_4286.equals(modelTransformation.field_4286);
		}
	}

	public int hashCode() {
		int i = this.field_4287.hashCode();
		i = 31 * i + this.field_4286.hashCode();
		return 31 * i + this.field_4285.hashCode();
	}

	@Environment(EnvType.CLIENT)
	static class class_805 implements JsonDeserializer<ModelTransformation> {
		private static final Vector3f field_4288 = new Vector3f(0.0F, 0.0F, 0.0F);
		private static final Vector3f field_4290 = new Vector3f(0.0F, 0.0F, 0.0F);
		private static final Vector3f field_4289 = new Vector3f(1.0F, 1.0F, 1.0F);

		public ModelTransformation method_3494(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
			JsonObject jsonObject = jsonElement.getAsJsonObject();
			Vector3f vector3f = this.method_3493(jsonObject, "rotation", field_4288);
			Vector3f vector3f2 = this.method_3493(jsonObject, "translation", field_4290);
			vector3f2.scale(0.0625F);
			vector3f2.clamp(-5.0F, 5.0F);
			Vector3f vector3f3 = this.method_3493(jsonObject, "scale", field_4289);
			vector3f3.clamp(-4.0F, 4.0F);
			return new ModelTransformation(vector3f, vector3f2, vector3f3);
		}

		private Vector3f method_3493(JsonObject jsonObject, String string, Vector3f vector3f) {
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
