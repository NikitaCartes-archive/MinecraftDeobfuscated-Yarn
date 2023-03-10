/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.render.entity.animation;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.entity.animation.Keyframe;
import net.minecraft.util.math.MathHelper;
import org.joml.Vector3f;

@Environment(value=EnvType.CLIENT)
public record Transformation(Target target, Keyframe[] keyframes) {

    @Environment(value=EnvType.CLIENT)
    public static interface Target {
        public void apply(ModelPart var1, Vector3f var2);
    }

    @Environment(value=EnvType.CLIENT)
    public static class Interpolations {
        public static final Interpolation LINEAR = (vector3f, delta, keyframes, start, end, f) -> {
            Vector3f vector3f2 = keyframes[start].target();
            Vector3f vector3f3 = keyframes[end].target();
            return vector3f2.lerp(vector3f3, delta, vector3f).mul(f);
        };
        public static final Interpolation CUBIC = (vector3f, delta, keyframes, start, end, f) -> {
            Vector3f vector3f2 = keyframes[Math.max(0, start - 1)].target();
            Vector3f vector3f3 = keyframes[start].target();
            Vector3f vector3f4 = keyframes[end].target();
            Vector3f vector3f5 = keyframes[Math.min(keyframes.length - 1, end + 1)].target();
            vector3f.set(MathHelper.catmullRom(delta, vector3f2.x(), vector3f3.x(), vector3f4.x(), vector3f5.x()) * f, MathHelper.catmullRom(delta, vector3f2.y(), vector3f3.y(), vector3f4.y(), vector3f5.y()) * f, MathHelper.catmullRom(delta, vector3f2.z(), vector3f3.z(), vector3f4.z(), vector3f5.z()) * f);
            return vector3f;
        };
    }

    @Environment(value=EnvType.CLIENT)
    public static class Targets {
        public static final Target TRANSLATE = ModelPart::translate;
        public static final Target ROTATE = ModelPart::rotate;
        public static final Target SCALE = ModelPart::scale;
    }

    @Environment(value=EnvType.CLIENT)
    public static interface Interpolation {
        public Vector3f apply(Vector3f var1, float var2, Keyframe[] var3, int var4, int var5, float var6);
    }
}

