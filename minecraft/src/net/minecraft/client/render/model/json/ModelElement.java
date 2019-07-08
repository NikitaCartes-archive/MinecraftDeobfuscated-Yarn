package net.minecraft.client.render.model.json;

import com.google.common.collect.Maps;
import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import java.lang.reflect.Type;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.util.math.Vector3f;
import net.minecraft.util.JsonHelper;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;

@Environment(EnvType.CLIENT)
public class ModelElement {
	public final Vector3f from;
	public final Vector3f to;
	public final Map<Direction, ModelElementFace> faces;
	public final ModelRotation rotation;
	public final boolean shade;

	public ModelElement(Vector3f vector3f, Vector3f vector3f2, Map<Direction, ModelElementFace> map, @Nullable ModelRotation modelRotation, boolean bl) {
		this.from = vector3f;
		this.to = vector3f2;
		this.faces = map;
		this.rotation = modelRotation;
		this.shade = bl;
		this.method_3402();
	}

	private void method_3402() {
		for (Entry<Direction, ModelElementFace> entry : this.faces.entrySet()) {
			float[] fs = this.method_3401((Direction)entry.getKey());
			((ModelElementFace)entry.getValue()).textureData.setUvs(fs);
		}
	}

	private float[] method_3401(Direction direction) {
		switch (direction) {
			case DOWN:
				return new float[]{this.from.getX(), 16.0F - this.to.getZ(), this.to.getX(), 16.0F - this.from.getZ()};
			case UP:
				return new float[]{this.from.getX(), this.from.getZ(), this.to.getX(), this.to.getZ()};
			case NORTH:
			default:
				return new float[]{16.0F - this.to.getX(), 16.0F - this.to.getY(), 16.0F - this.from.getX(), 16.0F - this.from.getY()};
			case SOUTH:
				return new float[]{this.from.getX(), 16.0F - this.to.getY(), this.to.getX(), 16.0F - this.from.getY()};
			case WEST:
				return new float[]{this.from.getZ(), 16.0F - this.to.getY(), this.to.getZ(), 16.0F - this.from.getY()};
			case EAST:
				return new float[]{16.0F - this.to.getZ(), 16.0F - this.to.getY(), 16.0F - this.from.getZ(), 16.0F - this.from.getY()};
		}
	}

	@Environment(EnvType.CLIENT)
	public static class Deserializer implements JsonDeserializer<ModelElement> {
		protected Deserializer() {
		}

		public ModelElement method_3406(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
			JsonObject jsonObject = jsonElement.getAsJsonObject();
			Vector3f vector3f = this.deserializeFrom(jsonObject);
			Vector3f vector3f2 = this.deserializeTo(jsonObject);
			ModelRotation modelRotation = this.deserializeRotation(jsonObject);
			Map<Direction, ModelElementFace> map = this.deserializeFacesValidating(jsonDeserializationContext, jsonObject);
			if (jsonObject.has("shade") && !JsonHelper.hasBoolean(jsonObject, "shade")) {
				throw new JsonParseException("Expected shade to be a Boolean");
			} else {
				boolean bl = JsonHelper.getBoolean(jsonObject, "shade", true);
				return new ModelElement(vector3f, vector3f2, map, modelRotation, bl);
			}
		}

		@Nullable
		private ModelRotation deserializeRotation(JsonObject jsonObject) {
			ModelRotation modelRotation = null;
			if (jsonObject.has("rotation")) {
				JsonObject jsonObject2 = JsonHelper.getObject(jsonObject, "rotation");
				Vector3f vector3f = this.deserializeVec3f(jsonObject2, "origin");
				vector3f.scale(0.0625F);
				Direction.Axis axis = this.deserializeAxis(jsonObject2);
				float f = this.deserializeRotationAngle(jsonObject2);
				boolean bl = JsonHelper.getBoolean(jsonObject2, "rescale", false);
				modelRotation = new ModelRotation(vector3f, axis, f, bl);
			}

			return modelRotation;
		}

		private float deserializeRotationAngle(JsonObject jsonObject) {
			float f = JsonHelper.getFloat(jsonObject, "angle");
			if (f != 0.0F && MathHelper.abs(f) != 22.5F && MathHelper.abs(f) != 45.0F) {
				throw new JsonParseException("Invalid rotation " + f + " found, only -45/-22.5/0/22.5/45 allowed");
			} else {
				return f;
			}
		}

		private Direction.Axis deserializeAxis(JsonObject jsonObject) {
			String string = JsonHelper.getString(jsonObject, "axis");
			Direction.Axis axis = Direction.Axis.fromName(string.toLowerCase(Locale.ROOT));
			if (axis == null) {
				throw new JsonParseException("Invalid rotation axis: " + string);
			} else {
				return axis;
			}
		}

		private Map<Direction, ModelElementFace> deserializeFacesValidating(JsonDeserializationContext jsonDeserializationContext, JsonObject jsonObject) {
			Map<Direction, ModelElementFace> map = this.deserializeFaces(jsonDeserializationContext, jsonObject);
			if (map.isEmpty()) {
				throw new JsonParseException("Expected between 1 and 6 unique faces, got 0");
			} else {
				return map;
			}
		}

		private Map<Direction, ModelElementFace> deserializeFaces(JsonDeserializationContext jsonDeserializationContext, JsonObject jsonObject) {
			Map<Direction, ModelElementFace> map = Maps.newEnumMap(Direction.class);
			JsonObject jsonObject2 = JsonHelper.getObject(jsonObject, "faces");

			for (Entry<String, JsonElement> entry : jsonObject2.entrySet()) {
				Direction direction = this.getDirection((String)entry.getKey());
				map.put(direction, jsonDeserializationContext.deserialize((JsonElement)entry.getValue(), ModelElementFace.class));
			}

			return map;
		}

		private Direction getDirection(String string) {
			Direction direction = Direction.byName(string);
			if (direction == null) {
				throw new JsonParseException("Unknown facing: " + string);
			} else {
				return direction;
			}
		}

		private Vector3f deserializeTo(JsonObject jsonObject) {
			Vector3f vector3f = this.deserializeVec3f(jsonObject, "to");
			if (!(vector3f.getX() < -16.0F)
				&& !(vector3f.getY() < -16.0F)
				&& !(vector3f.getZ() < -16.0F)
				&& !(vector3f.getX() > 32.0F)
				&& !(vector3f.getY() > 32.0F)
				&& !(vector3f.getZ() > 32.0F)) {
				return vector3f;
			} else {
				throw new JsonParseException("'to' specifier exceeds the allowed boundaries: " + vector3f);
			}
		}

		private Vector3f deserializeFrom(JsonObject jsonObject) {
			Vector3f vector3f = this.deserializeVec3f(jsonObject, "from");
			if (!(vector3f.getX() < -16.0F)
				&& !(vector3f.getY() < -16.0F)
				&& !(vector3f.getZ() < -16.0F)
				&& !(vector3f.getX() > 32.0F)
				&& !(vector3f.getY() > 32.0F)
				&& !(vector3f.getZ() > 32.0F)) {
				return vector3f;
			} else {
				throw new JsonParseException("'from' specifier exceeds the allowed boundaries: " + vector3f);
			}
		}

		private Vector3f deserializeVec3f(JsonObject jsonObject, String string) {
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
