package net.minecraft.client.render.model.json;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.util.math.Direction;
import org.joml.Vector3f;

@Environment(EnvType.CLIENT)
public record ModelRotation(Vector3f origin, Direction.Axis axis, float angle, boolean rescale) {
}
