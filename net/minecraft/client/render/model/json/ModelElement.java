/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.render.model.json;

import com.google.common.collect.Maps;
import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import java.lang.reflect.Type;
import java.util.EnumMap;
import java.util.Locale;
import java.util.Map;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.model.json.ModelElementFace;
import net.minecraft.client.render.model.json.ModelRotation;
import net.minecraft.client.util.math.Vector3f;
import net.minecraft.util.JsonHelper;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import org.jetbrains.annotations.Nullable;

@Environment(value=EnvType.CLIENT)
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
        this.initTextures();
    }

    private void initTextures() {
        for (Map.Entry<Direction, ModelElementFace> entry : this.faces.entrySet()) {
            float[] fs = this.getRotatedMatrix(entry.getKey());
            entry.getValue().textureData.setUvs(fs);
        }
    }

    private float[] getRotatedMatrix(Direction direction) {
        switch (direction) {
            case DOWN: {
                return new float[]{this.from.getX(), 16.0f - this.to.getZ(), this.to.getX(), 16.0f - this.from.getZ()};
            }
            case UP: {
                return new float[]{this.from.getX(), this.from.getZ(), this.to.getX(), this.to.getZ()};
            }
            default: {
                return new float[]{16.0f - this.to.getX(), 16.0f - this.to.getY(), 16.0f - this.from.getX(), 16.0f - this.from.getY()};
            }
            case SOUTH: {
                return new float[]{this.from.getX(), 16.0f - this.to.getY(), this.to.getX(), 16.0f - this.from.getY()};
            }
            case WEST: {
                return new float[]{this.from.getZ(), 16.0f - this.to.getY(), this.to.getZ(), 16.0f - this.from.getY()};
            }
            case EAST: 
        }
        return new float[]{16.0f - this.to.getZ(), 16.0f - this.to.getY(), 16.0f - this.from.getZ(), 16.0f - this.from.getY()};
    }

    @Environment(value=EnvType.CLIENT)
    public static class Deserializer
    implements JsonDeserializer<ModelElement> {
        protected Deserializer() {
        }

        @Override
        public ModelElement deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
            JsonObject jsonObject = jsonElement.getAsJsonObject();
            Vector3f vector3f = this.deserializeFrom(jsonObject);
            Vector3f vector3f2 = this.deserializeTo(jsonObject);
            ModelRotation modelRotation = this.deserializeRotation(jsonObject);
            Map<Direction, ModelElementFace> map = this.deserializeFacesValidating(jsonDeserializationContext, jsonObject);
            if (jsonObject.has("shade") && !JsonHelper.hasBoolean(jsonObject, "shade")) {
                throw new JsonParseException("Expected shade to be a Boolean");
            }
            boolean bl = JsonHelper.getBoolean(jsonObject, "shade", true);
            return new ModelElement(vector3f, vector3f2, map, modelRotation, bl);
        }

        @Nullable
        private ModelRotation deserializeRotation(JsonObject jsonObject) {
            ModelRotation modelRotation = null;
            if (jsonObject.has("rotation")) {
                JsonObject jsonObject2 = JsonHelper.getObject(jsonObject, "rotation");
                Vector3f vector3f = this.deserializeVec3f(jsonObject2, "origin");
                vector3f.scale(0.0625f);
                Direction.Axis axis = this.deserializeAxis(jsonObject2);
                float f = this.deserializeRotationAngle(jsonObject2);
                boolean bl = JsonHelper.getBoolean(jsonObject2, "rescale", false);
                modelRotation = new ModelRotation(vector3f, axis, f, bl);
            }
            return modelRotation;
        }

        private float deserializeRotationAngle(JsonObject jsonObject) {
            float f = JsonHelper.getFloat(jsonObject, "angle");
            if (f != 0.0f && MathHelper.abs(f) != 22.5f && MathHelper.abs(f) != 45.0f) {
                throw new JsonParseException("Invalid rotation " + f + " found, only -45/-22.5/0/22.5/45 allowed");
            }
            return f;
        }

        private Direction.Axis deserializeAxis(JsonObject jsonObject) {
            String string = JsonHelper.getString(jsonObject, "axis");
            Direction.Axis axis = Direction.Axis.fromName(string.toLowerCase(Locale.ROOT));
            if (axis == null) {
                throw new JsonParseException("Invalid rotation axis: " + string);
            }
            return axis;
        }

        private Map<Direction, ModelElementFace> deserializeFacesValidating(JsonDeserializationContext jsonDeserializationContext, JsonObject jsonObject) {
            Map<Direction, ModelElementFace> map = this.deserializeFaces(jsonDeserializationContext, jsonObject);
            if (map.isEmpty()) {
                throw new JsonParseException("Expected between 1 and 6 unique faces, got 0");
            }
            return map;
        }

        private Map<Direction, ModelElementFace> deserializeFaces(JsonDeserializationContext jsonDeserializationContext, JsonObject jsonObject) {
            EnumMap<Direction, ModelElementFace> map = Maps.newEnumMap(Direction.class);
            JsonObject jsonObject2 = JsonHelper.getObject(jsonObject, "faces");
            for (Map.Entry<String, JsonElement> entry : jsonObject2.entrySet()) {
                Direction direction = this.getDirection(entry.getKey());
                map.put(direction, (ModelElementFace)jsonDeserializationContext.deserialize(entry.getValue(), (Type)((Object)ModelElementFace.class)));
            }
            return map;
        }

        private Direction getDirection(String string) {
            Direction direction = Direction.byName(string);
            if (direction == null) {
                throw new JsonParseException("Unknown facing: " + string);
            }
            return direction;
        }

        private Vector3f deserializeTo(JsonObject jsonObject) {
            Vector3f vector3f = this.deserializeVec3f(jsonObject, "to");
            if (vector3f.getX() < -16.0f || vector3f.getY() < -16.0f || vector3f.getZ() < -16.0f || vector3f.getX() > 32.0f || vector3f.getY() > 32.0f || vector3f.getZ() > 32.0f) {
                throw new JsonParseException("'to' specifier exceeds the allowed boundaries: " + vector3f);
            }
            return vector3f;
        }

        private Vector3f deserializeFrom(JsonObject jsonObject) {
            Vector3f vector3f = this.deserializeVec3f(jsonObject, "from");
            if (vector3f.getX() < -16.0f || vector3f.getY() < -16.0f || vector3f.getZ() < -16.0f || vector3f.getX() > 32.0f || vector3f.getY() > 32.0f || vector3f.getZ() > 32.0f) {
                throw new JsonParseException("'from' specifier exceeds the allowed boundaries: " + vector3f);
            }
            return vector3f;
        }

        private Vector3f deserializeVec3f(JsonObject jsonObject, String string) {
            JsonArray jsonArray = JsonHelper.getArray(jsonObject, string);
            if (jsonArray.size() != 3) {
                throw new JsonParseException("Expected 3 " + string + " values, found: " + jsonArray.size());
            }
            float[] fs = new float[3];
            for (int i = 0; i < fs.length; ++i) {
                fs[i] = JsonHelper.asFloat(jsonArray.get(i), string + "[" + i + "]");
            }
            return new Vector3f(fs[0], fs[1], fs[2]);
        }

        @Override
        public /* synthetic */ Object deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
            return this.deserialize(jsonElement, type, jsonDeserializationContext);
        }
    }
}

