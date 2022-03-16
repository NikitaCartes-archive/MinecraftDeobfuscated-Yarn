/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.render.entity.animation;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.entity.animation.Keyframe;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3f;

@Environment(value=EnvType.CLIENT)
public record Transformation(Target target, Keyframe[] keyframes) {

    @Environment(value=EnvType.CLIENT)
    public static interface Target {
        public void apply(ModelPart var1, Vec3f var2);
    }

    @Environment(value=EnvType.CLIENT)
    public static class Interpolations {
        public static final Interpolation field_37884 = (vec3f, delta, keyframes, start, end, f) -> {
            Vec3f vec3f2 = keyframes[start].target();
            Vec3f vec3f3 = keyframes[end].target();
            vec3f.set(MathHelper.lerp(delta, vec3f2.getX(), vec3f3.getX()) * f, MathHelper.lerp(delta, vec3f2.getY(), vec3f3.getY()) * f, MathHelper.lerp(delta, vec3f2.getZ(), vec3f3.getZ()) * f);
            return vec3f;
        };
        public static final Interpolation field_37885 = (vec3f, delta, keyframes, start, end, f) -> {
            Vec3f vec3f2 = keyframes[Math.max(0, start - 1)].target();
            Vec3f vec3f3 = keyframes[start].target();
            Vec3f vec3f4 = keyframes[end].target();
            Vec3f vec3f5 = keyframes[Math.min(keyframes.length - 1, end + 1)].target();
            vec3f.set(MathHelper.method_41303(delta, vec3f2.getX(), vec3f3.getX(), vec3f4.getX(), vec3f5.getX()) * f, MathHelper.method_41303(delta, vec3f2.getY(), vec3f3.getY(), vec3f4.getY(), vec3f5.getY()) * f, MathHelper.method_41303(delta, vec3f2.getZ(), vec3f3.getZ(), vec3f4.getZ(), vec3f5.getZ()) * f);
            return vec3f;
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
        public Vec3f apply(Vec3f var1, float var2, Keyframe[] var3, int var4, int var5, float var6);
    }
}

