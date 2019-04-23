package net.minecraft.util.hit;

import net.minecraft.entity.Entity;
import net.minecraft.util.math.Vec3d;

public class EntityHitResult extends HitResult {
	private final Entity entity;

	public EntityHitResult(Entity entity) {
		this(entity, new Vec3d(entity.x, entity.y, entity.z));
	}

	public EntityHitResult(Entity entity, Vec3d vec3d) {
		super(vec3d);
		this.entity = entity;
	}

	public Entity getEntity() {
		return this.entity;
	}

	@Override
	public HitResult.Type getType() {
		return HitResult.Type.field_1331;
	}
}
