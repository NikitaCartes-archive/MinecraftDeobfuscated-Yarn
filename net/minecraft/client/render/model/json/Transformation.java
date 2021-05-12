/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
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
import net.minecraft.util.JsonHelper;
import net.minecraft.util.math.Quaternion;
import net.minecraft.util.math.Vec3f;

@Environment(value=EnvType.CLIENT)
public class Transformation {
    public static final Transformation IDENTITY = new Transformation(new Vec3f(), new Vec3f(), new Vec3f(1.0f, 1.0f, 1.0f));
    public final Vec3f rotation;
    public final Vec3f translation;
    public final Vec3f scale;

    public Transformation(Vec3f rotation, Vec3f translation, Vec3f scale) {
        this.rotation = rotation.copy();
        this.translation = translation.copy();
        this.scale = scale.copy();
    }

    public void apply(boolean leftHanded, MatrixStack matrices) {
        if (this == IDENTITY) {
            return;
        }
        float f = this.rotation.getX();
        float g = this.rotation.getY();
        float h = this.rotation.getZ();
        if (leftHanded) {
            g = -g;
            h = -h;
        }
        int i = leftHanded ? -1 : 1;
        matrices.translate((float)i * this.translation.getX(), this.translation.getY(), this.translation.getZ());
        matrices.multiply(new Quaternion(f, g, h, true));
        matrices.scale(this.scale.getX(), this.scale.getY(), this.scale.getZ());
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (this.getClass() == o.getClass()) {
            Transformation transformation = (Transformation)o;
            return this.rotation.equals(transformation.rotation) && this.scale.equals(transformation.scale) && this.translation.equals(transformation.translation);
        }
        return false;
    }

    public int hashCode() {
        int i = this.rotation.hashCode();
        i = 31 * i + this.translation.hashCode();
        i = 31 * i + this.scale.hashCode();
        return i;
    }

    @Environment(value=EnvType.CLIENT)
    protected static class Deserializer
    implements JsonDeserializer<Transformation> {
        private static final Vec3f DEFAULT_ROTATION = new Vec3f(0.0f, 0.0f, 0.0f);
        private static final Vec3f DEFAULT_TRANSLATION = new Vec3f(0.0f, 0.0f, 0.0f);
        private static final Vec3f DEFAULT_SCALE = new Vec3f(1.0f, 1.0f, 1.0f);
        public static final float field_32808 = 5.0f;
        public static final float field_32809 = 4.0f;

        protected Deserializer() {
        }

        @Override
        public Transformation deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
            JsonObject jsonObject = jsonElement.getAsJsonObject();
            Vec3f vec3f = this.parseVector3f(jsonObject, "rotation", DEFAULT_ROTATION);
            Vec3f vec3f2 = this.parseVector3f(jsonObject, "translation", DEFAULT_TRANSLATION);
            vec3f2.scale(0.0625f);
            vec3f2.clamp(-5.0f, 5.0f);
            Vec3f vec3f3 = this.parseVector3f(jsonObject, "scale", DEFAULT_SCALE);
            vec3f3.clamp(-4.0f, 4.0f);
            return new Transformation(vec3f, vec3f2, vec3f3);
        }

        private Vec3f parseVector3f(JsonObject json, String key, Vec3f fallback) {
            if (!json.has(key)) {
                return fallback;
            }
            JsonArray jsonArray = JsonHelper.getArray(json, key);
            if (jsonArray.size() != 3) {
                throw new JsonParseException("Expected 3 " + key + " values, found: " + jsonArray.size());
            }
            float[] fs = new float[3];
            for (int i = 0; i < fs.length; ++i) {
                fs[i] = JsonHelper.asFloat(jsonArray.get(i), key + "[" + i + "]");
            }
            return new Vec3f(fs[0], fs[1], fs[2]);
        }

        @Override
        public /* synthetic */ Object deserialize(JsonElement functionJson, Type unused, JsonDeserializationContext context) throws JsonParseException {
            return this.deserialize(functionJson, unused, context);
        }
    }
}

