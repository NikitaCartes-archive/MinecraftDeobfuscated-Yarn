package net.minecraft.entity;

import net.minecraft.world.World;

public abstract class AbstractLightningEntity extends Entity {
	public AbstractLightningEntity(EntityType<?> entityType, World world) {
		super(entityType, world);
	}
}
