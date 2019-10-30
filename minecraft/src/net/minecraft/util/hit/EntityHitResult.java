package net.minecraft.util.hit;

import net.minecraft.entity.Entity;
import net.minecraft.util.math.Vec3d;

public class EntityHitResult extends HitResult {
	private final Entity entity;

	public EntityHitResult(Entity entity) {
		this(entity, entity.getPos());
	}

	public EntityHitResult(Entity entity, Vec3d pos) {
		super(pos);
		this.entity = entity;
	}

	public Entity getEntity() {
		return this.entity;
	}

	@Override
	public HitResult.Type getType() {
		return HitResult.Type.ENTITY;
	}
}
