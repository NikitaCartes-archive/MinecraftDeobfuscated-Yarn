/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.render.entity.animation;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.entity.animation.Transformation;
import net.minecraft.util.math.Vec3f;

@Environment(value=EnvType.CLIENT)
public record Keyframe(float timestamp, Vec3f target, Transformation.Interpolation interpolation) {
}

