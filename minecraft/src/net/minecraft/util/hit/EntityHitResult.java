package net.minecraft.util.hit;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.Vec3d;

public class EntityHitResult extends HitResult {
	private final Entity entity;
	private final float field_26776;

	public EntityHitResult(Entity entity, float f) {
		this(entity, entity.getPos(), f);
	}

	public EntityHitResult(Entity entity, Vec3d pos, float f) {
		super(pos);
		this.entity = entity;
		this.field_26776 = f;
	}

	public Entity getEntity() {
		return this.entity;
	}

	@Override
	public HitResult.Type getType() {
		return HitResult.Type.field_1331;
	}

	@Environment(EnvType.CLIENT)
	public float method_31221() {
		return this.field_26776;
	}
}
