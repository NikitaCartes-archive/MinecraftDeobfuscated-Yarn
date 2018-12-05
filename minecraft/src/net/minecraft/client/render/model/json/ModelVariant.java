package net.minecraft.client.render.model.json;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import java.lang.reflect.Type;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.model.ModelRotation;
import net.minecraft.client.render.model.ModelRotationContainer;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;

@Environment(EnvType.CLIENT)
public class ModelVariant implements ModelRotationContainer {
	private final Identifier location;
	private final ModelRotation rotation;
	private final boolean uvLock;
	private final int weight;

	public ModelVariant(Identifier identifier, ModelRotation modelRotation, boolean bl, int i) {
		this.location = identifier;
		this.rotation = modelRotation;
		this.uvLock = bl;
		this.weight = i;
	}

	public Identifier getLocation() {
		return this.location;
	}

	@Override
	public ModelRotation getRotation() {
		return this.rotation;
	}

	@Override
	public boolean method_3512() {
		return this.uvLock;
	}

	public int getWeight() {
		return this.weight;
	}

	public String toString() {
		return "Variant{modelLocation=" + this.location + ", rotation=" + this.rotation + ", uvLock=" + this.uvLock + ", weight=" + this.weight + '}';
	}

	public boolean equals(Object object) {
		if (this == object) {
			return true;
		} else if (!(object instanceof ModelVariant)) {
			return false;
		} else {
			ModelVariant modelVariant = (ModelVariant)object;
			return this.location.equals(modelVariant.location)
				&& this.rotation == modelVariant.rotation
				&& this.uvLock == modelVariant.uvLock
				&& this.weight == modelVariant.weight;
		}
	}

	public int hashCode() {
		int i = this.location.hashCode();
		i = 31 * i + this.rotation.hashCode();
		i = 31 * i + Boolean.valueOf(this.uvLock).hashCode();
		return 31 * i + this.weight;
	}

	@Environment(EnvType.CLIENT)
	public static class class_814 implements JsonDeserializer<ModelVariant> {
		public ModelVariant method_3513(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
			JsonObject jsonObject = jsonElement.getAsJsonObject();
			Identifier identifier = this.method_3514(jsonObject);
			ModelRotation modelRotation = this.method_3515(jsonObject);
			boolean bl = this.method_3516(jsonObject);
			int i = this.method_3517(jsonObject);
			return new ModelVariant(identifier, modelRotation, bl, i);
		}

		private boolean method_3516(JsonObject jsonObject) {
			return JsonHelper.getBoolean(jsonObject, "uvlock", false);
		}

		protected ModelRotation method_3515(JsonObject jsonObject) {
			int i = JsonHelper.getInt(jsonObject, "x", 0);
			int j = JsonHelper.getInt(jsonObject, "y", 0);
			ModelRotation modelRotation = ModelRotation.get(i, j);
			if (modelRotation == null) {
				throw new JsonParseException("Invalid BlockModelRotation x: " + i + ", y: " + j);
			} else {
				return modelRotation;
			}
		}

		protected Identifier method_3514(JsonObject jsonObject) {
			return new Identifier(JsonHelper.getString(jsonObject, "model"));
		}

		protected int method_3517(JsonObject jsonObject) {
			int i = JsonHelper.getInt(jsonObject, "weight", 1);
			if (i < 1) {
				throw new JsonParseException("Invalid weight " + i + " found, expected integer >= 1");
			} else {
				return i;
			}
		}
	}
}
