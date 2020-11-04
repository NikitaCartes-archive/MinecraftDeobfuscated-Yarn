package net.minecraft.client.render.model.json;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.util.math.Vector3f;
import net.minecraft.util.math.Direction;

@Environment(EnvType.CLIENT)
public class ModelRotation {
	public final Vector3f origin;
	public final Direction.Axis axis;
	public final float angle;
	public final boolean rescale;

	public ModelRotation(Vector3f origin, Direction.Axis axis, float angle, boolean rescale) {
		this.origin = origin;
		this.axis = axis;
		this.angle = angle;
		this.rescale = rescale;
	}
}
