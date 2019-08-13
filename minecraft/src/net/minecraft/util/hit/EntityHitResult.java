package net.minecraft.util.hit;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.Vec3d;

public class EntityHitResult extends HitResult {
	private final Entity entity;
	private final float field_20351;

	public EntityHitResult(Entity entity, float f) {
		this(entity, new Vec3d(entity.x, entity.y, entity.z), f);
	}

	public EntityHitResult(Entity entity, Vec3d vec3d, float f) {
		super(vec3d);
		this.entity = entity;
		this.field_20351 = f;
	}

	public Entity getEntity() {
		return this.entity;
	}

	@Override
	public HitResult.Type getType() {
		return HitResult.Type.field_1331;
	}

	@Environment(EnvType.CLIENT)
	public float method_21763() {
		return this.field_20351;
	}
}
