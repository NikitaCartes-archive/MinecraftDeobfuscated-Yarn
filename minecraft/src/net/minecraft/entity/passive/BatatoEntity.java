package net.minecraft.entity.passive;

import net.minecraft.entity.EntityType;
import net.minecraft.world.World;

public class BatatoEntity extends BatEntity {
	public BatatoEntity(EntityType<? extends BatEntity> entityType, World world) {
		super(entityType, world);
	}

	@Override
	public boolean isPotato() {
		return true;
	}
}
