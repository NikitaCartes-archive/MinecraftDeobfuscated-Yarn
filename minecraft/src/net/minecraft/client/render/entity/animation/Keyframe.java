package net.minecraft.client.render.entity.animation;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.util.math.Vec3f;

@Environment(EnvType.CLIENT)
public record Keyframe(float timestamp, Vec3f target, Transformation.Interpolation interpolation) {
}
