package net.minecraft.entity.mob;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.entity.EntityGroup;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.raid.RaiderEntity;
import net.minecraft.world.World;

public abstract class IllagerEntity extends RaiderEntity {
	protected IllagerEntity(EntityType<? extends IllagerEntity> entityType, World world) {
		super(entityType, world);
	}

	@Override
	public EntityGroup getGroup() {
		return EntityGroup.ILLAGER;
	}

	@Environment(EnvType.CLIENT)
	public IllagerEntity.State method_6990() {
		return IllagerEntity.State.field_7207;
	}

	@Environment(EnvType.CLIENT)
	public static enum State {
		field_7207,
		field_7211,
		field_7212,
		field_7208,
		field_7213,
		field_7210;
	}
}
