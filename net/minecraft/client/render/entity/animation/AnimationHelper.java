/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.render.entity.animation;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.entity.animation.Animation;
import net.minecraft.client.render.entity.animation.Keyframe;
import net.minecraft.client.render.entity.animation.Transformation;
import net.minecraft.client.render.entity.model.SinglePartEntityModel;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3f;

@Environment(value=EnvType.CLIENT)
public class AnimationHelper {
    public static void animate(SinglePartEntityModel<?> model, Animation animation, long runningTime, float f, Vec3f vec3f) {
        float g = AnimationHelper.getRunningSeconds(animation, runningTime);
        for (Map.Entry<String, List<Transformation>> entry : animation.boneAnimations().entrySet()) {
            Optional<ModelPart> optional = model.getChild(entry.getKey());
            List<Transformation> list = entry.getValue();
            optional.ifPresent(part -> list.forEach(transformation -> {
                Keyframe[] keyframes = transformation.keyframes();
                int i2 = Math.max(0, MathHelper.binarySearch(0, keyframes.length, i -> g <= keyframes[i].timestamp()) - 1);
                int j = Math.min(keyframes.length - 1, i2 + 1);
                Keyframe keyframe = keyframes[i2];
                Keyframe keyframe2 = keyframes[j];
                float h = g - keyframe.timestamp();
                float k = MathHelper.clamp(h / (keyframe2.timestamp() - keyframe.timestamp()), 0.0f, 1.0f);
                keyframe2.interpolation().apply(vec3f, k, keyframes, i2, j, f);
                transformation.target().apply((ModelPart)part, vec3f);
            }));
        }
    }

    private static float getRunningSeconds(Animation animation, long runningTime) {
        float f = (float)runningTime / 1000.0f;
        return animation.looping() ? f % animation.lengthInSeconds() : f;
    }

    public static Vec3f method_41823(float f, float g, float h) {
        return new Vec3f(f, -g, h);
    }

    public static Vec3f method_41829(float f, float g, float h) {
        return new Vec3f(f * ((float)Math.PI / 180), g * ((float)Math.PI / 180), h * ((float)Math.PI / 180));
    }

    public static Vec3f method_41822(double d, double e, double f) {
        return new Vec3f((float)(d - 1.0), (float)(e - 1.0), (float)(f - 1.0));
    }
}

