package net.minecraft.util.hit;

import net.minecraft.util.math.Vec3d;

public abstract class HitResult {
	protected final Vec3d field_1329;

	protected HitResult(Vec3d vec3d) {
		this.field_1329 = vec3d;
	}

	public abstract HitResult.Type getType();

	public Vec3d method_17784() {
		return this.field_1329;
	}

	public static enum Type {
		NONE,
		BLOCK,
		ENTITY;
	}
}
