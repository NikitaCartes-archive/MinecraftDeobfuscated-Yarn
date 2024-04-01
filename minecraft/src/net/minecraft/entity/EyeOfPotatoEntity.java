package net.minecraft.entity;

import net.minecraft.world.World;

public class EyeOfPotatoEntity extends EyeOfEnderEntity {
	public EyeOfPotatoEntity(EntityType<? extends EyeOfPotatoEntity> entityType, World world) {
		super(entityType, world);
	}

	public EyeOfPotatoEntity(World world, double d, double e, double f) {
		this(EntityType.EYE_OF_POTATO, world);
		this.setPosition(d, e, f);
	}
}
