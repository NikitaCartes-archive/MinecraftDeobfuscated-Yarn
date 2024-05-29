package net.minecraft.entity.mob;

import net.minecraft.entity.EntityType;
import net.minecraft.world.World;

public abstract class AmbientEntity extends MobEntity {
	protected AmbientEntity(EntityType<? extends AmbientEntity> entityType, World world) {
		super(entityType, world);
	}

	@Override
	public boolean canBeLeashed() {
		return false;
	}
}
