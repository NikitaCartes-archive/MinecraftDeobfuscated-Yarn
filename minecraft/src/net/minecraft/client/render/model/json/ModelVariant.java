package net.minecraft.client.render.model.json;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import java.lang.reflect.Type;
import java.util.Objects;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.model.ModelBakeSettings;
import net.minecraft.client.util.math.AffineTransformation;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;

@Environment(EnvType.CLIENT)
public class ModelVariant implements ModelBakeSettings {
	private final Identifier location;
	private final AffineTransformation rotation;
	private final boolean uvLock;
	private final int weight;

	public ModelVariant(Identifier location, AffineTransformation affineTransformation, boolean uvLock, int weight) {
		this.location = location;
		this.rotation = affineTransformation;
		this.uvLock = uvLock;
		this.weight = weight;
	}

	public Identifier getLocation() {
		return this.location;
	}

	@Override
	public AffineTransformation getRotation() {
		return this.rotation;
	}

	@Override
	public boolean isShaded() {
		return this.uvLock;
	}

	public int getWeight() {
		return this.weight;
	}

	public String toString() {
		return "Variant{modelLocation=" + this.location + ", rotation=" + this.rotation + ", uvLock=" + this.uvLock + ", weight=" + this.weight + '}';
	}

	public boolean equals(Object o) {
		if (this == o) {
			return true;
		} else if (!(o instanceof ModelVariant)) {
			return false;
		} else {
			ModelVariant modelVariant = (ModelVariant)o;
			return this.location.equals(modelVariant.location)
				&& Objects.equals(this.rotation, modelVariant.rotation)
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
	public static class Deserializer implements JsonDeserializer<ModelVariant> {
		public ModelVariant deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
			JsonObject jsonObject = jsonElement.getAsJsonObject();
			Identifier identifier = this.deserializeModel(jsonObject);
			net.minecraft.client.render.model.ModelRotation modelRotation = this.deserializeRotation(jsonObject);
			boolean bl = this.deserializeUvLock(jsonObject);
			int i = this.deserializeWeight(jsonObject);
			return new ModelVariant(identifier, modelRotation.getRotation(), bl, i);
		}

		private boolean deserializeUvLock(JsonObject object) {
			return JsonHelper.getBoolean(object, "uvlock", false);
		}

		protected net.minecraft.client.render.model.ModelRotation deserializeRotation(JsonObject object) {
			int i = JsonHelper.getInt(object, "x", 0);
			int j = JsonHelper.getInt(object, "y", 0);
			net.minecraft.client.render.model.ModelRotation modelRotation = net.minecraft.client.render.model.ModelRotation.get(i, j);
			if (modelRotation == null) {
				throw new JsonParseException("Invalid BlockModelRotation x: " + i + ", y: " + j);
			} else {
				return modelRotation;
			}
		}

		protected Identifier deserializeModel(JsonObject object) {
			return new Identifier(JsonHelper.getString(object, "model"));
		}

		protected int deserializeWeight(JsonObject object) {
			int i = JsonHelper.getInt(object, "weight", 1);
			if (i < 1) {
				throw new JsonParseException("Invalid weight " + i + " found, expected integer >= 1");
			} else {
				return i;
			}
		}
	}
}
