package net.minecraft.util.hit;

import net.minecraft.util.math.Vec3d;

public abstract class HitResult {
	protected final Vec3d pos;

	protected HitResult(Vec3d vec3d) {
		this.pos = vec3d;
	}

	public abstract HitResult.Type getType();

	public Vec3d getPos() {
		return this.pos;
	}

	public static enum Type {
		field_1333,
		field_1332,
		field_1331;
	}
}
