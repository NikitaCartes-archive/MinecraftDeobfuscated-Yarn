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
import net.minecraft.class_789;
import net.minecraft.sortme.Vector3f;
import net.minecraft.util.JsonHelper;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;

@Environment(EnvType.CLIENT)
public class ModelElement {
	public final Vector3f field_4228;
	public final Vector3f field_4231;
	public final Map<Direction, ModelElementFace> faces;
	public final class_789 rotation;
	public final boolean shade;

	public ModelElement(Vector3f vector3f, Vector3f vector3f2, Map<Direction, ModelElementFace> map, @Nullable class_789 arg, boolean bl) {
		this.field_4228 = vector3f;
		this.field_4231 = vector3f2;
		this.faces = map;
		this.rotation = arg;
		this.shade = bl;
		this.method_3402();
	}

	private void method_3402() {
		for (Entry<Direction, ModelElementFace> entry : this.faces.entrySet()) {
			float[] fs = this.method_3401((Direction)entry.getKey());
			((ModelElementFace)entry.getValue()).field_4227.setUvs(fs);
		}
	}

	private float[] method_3401(Direction direction) {
		switch (direction) {
			case DOWN:
				return new float[]{this.field_4228.x(), 16.0F - this.field_4231.z(), this.field_4231.x(), 16.0F - this.field_4228.z()};
			case UP:
				return new float[]{this.field_4228.x(), this.field_4228.z(), this.field_4231.x(), this.field_4231.z()};
			case NORTH:
			default:
				return new float[]{16.0F - this.field_4231.x(), 16.0F - this.field_4231.y(), 16.0F - this.field_4228.x(), 16.0F - this.field_4228.y()};
			case SOUTH:
				return new float[]{this.field_4228.x(), 16.0F - this.field_4231.y(), this.field_4231.x(), 16.0F - this.field_4228.y()};
			case WEST:
				return new float[]{this.field_4228.z(), 16.0F - this.field_4231.y(), this.field_4231.z(), 16.0F - this.field_4228.y()};
			case EAST:
				return new float[]{16.0F - this.field_4231.z(), 16.0F - this.field_4231.y(), 16.0F - this.field_4228.z(), 16.0F - this.field_4228.y()};
		}
	}

	@Environment(EnvType.CLIENT)
	static class class_786 implements JsonDeserializer<ModelElement> {
		public ModelElement method_3406(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
			JsonObject jsonObject = jsonElement.getAsJsonObject();
			Vector3f vector3f = this.method_3407(jsonObject);
			Vector3f vector3f2 = this.method_3405(jsonObject);
			class_789 lv = this.method_3410(jsonObject);
			Map<Direction, ModelElementFace> map = this.method_3412(jsonDeserializationContext, jsonObject);
			if (jsonObject.has("shade") && !JsonHelper.isBoolean(jsonObject, "shade")) {
				throw new JsonParseException("Expected shade to be a Boolean");
			} else {
				boolean bl = JsonHelper.getBoolean(jsonObject, "shade", true);
				return new ModelElement(vector3f, vector3f2, map, lv, bl);
			}
		}

		@Nullable
		private class_789 method_3410(JsonObject jsonObject) {
			class_789 lv = null;
			if (jsonObject.has("rotation")) {
				JsonObject jsonObject2 = JsonHelper.getObject(jsonObject, "rotation");
				Vector3f vector3f = this.method_3409(jsonObject2, "origin");
				vector3f.scale(0.0625F);
				Direction.Axis axis = this.method_3411(jsonObject2);
				float f = this.method_3403(jsonObject2);
				boolean bl = JsonHelper.getBoolean(jsonObject2, "rescale", false);
				lv = new class_789(vector3f, axis, f, bl);
			}

			return lv;
		}

		private float method_3403(JsonObject jsonObject) {
			float f = JsonHelper.getFloat(jsonObject, "angle");
			if (f != 0.0F && MathHelper.abs(f) != 22.5F && MathHelper.abs(f) != 45.0F) {
				throw new JsonParseException("Invalid rotation " + f + " found, only -45/-22.5/0/22.5/45 allowed");
			} else {
				return f;
			}
		}

		private Direction.Axis method_3411(JsonObject jsonObject) {
			String string = JsonHelper.getString(jsonObject, "axis");
			Direction.Axis axis = Direction.Axis.fromName(string.toLowerCase(Locale.ROOT));
			if (axis == null) {
				throw new JsonParseException("Invalid rotation axis: " + string);
			} else {
				return axis;
			}
		}

		private Map<Direction, ModelElementFace> method_3412(JsonDeserializationContext jsonDeserializationContext, JsonObject jsonObject) {
			Map<Direction, ModelElementFace> map = this.method_3404(jsonDeserializationContext, jsonObject);
			if (map.isEmpty()) {
				throw new JsonParseException("Expected between 1 and 6 unique faces, got 0");
			} else {
				return map;
			}
		}

		private Map<Direction, ModelElementFace> method_3404(JsonDeserializationContext jsonDeserializationContext, JsonObject jsonObject) {
			Map<Direction, ModelElementFace> map = Maps.newEnumMap(Direction.class);
			JsonObject jsonObject2 = JsonHelper.getObject(jsonObject, "faces");

			for (Entry<String, JsonElement> entry : jsonObject2.entrySet()) {
				Direction direction = this.method_3408((String)entry.getKey());
				map.put(direction, jsonDeserializationContext.deserialize((JsonElement)entry.getValue(), ModelElementFace.class));
			}

			return map;
		}

		private Direction method_3408(String string) {
			Direction direction = Direction.byName(string);
			if (direction == null) {
				throw new JsonParseException("Unknown facing: " + string);
			} else {
				return direction;
			}
		}

		private Vector3f method_3405(JsonObject jsonObject) {
			Vector3f vector3f = this.method_3409(jsonObject, "to");
			if (!(vector3f.x() < -16.0F)
				&& !(vector3f.y() < -16.0F)
				&& !(vector3f.z() < -16.0F)
				&& !(vector3f.x() > 32.0F)
				&& !(vector3f.y() > 32.0F)
				&& !(vector3f.z() > 32.0F)) {
				return vector3f;
			} else {
				throw new JsonParseException("'to' specifier exceeds the allowed boundaries: " + vector3f);
			}
		}

		private Vector3f method_3407(JsonObject jsonObject) {
			Vector3f vector3f = this.method_3409(jsonObject, "from");
			if (!(vector3f.x() < -16.0F)
				&& !(vector3f.y() < -16.0F)
				&& !(vector3f.z() < -16.0F)
				&& !(vector3f.x() > 32.0F)
				&& !(vector3f.y() > 32.0F)
				&& !(vector3f.z() > 32.0F)) {
				return vector3f;
			} else {
				throw new JsonParseException("'from' specifier exceeds the allowed boundaries: " + vector3f);
			}
		}

		private Vector3f method_3409(JsonObject jsonObject, String string) {
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
