package net.minecraft.client.render.entity.animation;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import org.joml.Vector3f;

@Environment(EnvType.CLIENT)
public record Keyframe(float timestamp, Vector3f target, Transformation.Interpolation interpolation) {
}
