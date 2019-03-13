package net.minecraft.entity.passive;

import net.minecraft.entity.EntityType;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.world.World;

public abstract class ParrotBaseEntity extends TameableEntity {
	private int field_6864;

	protected ParrotBaseEntity(EntityType<? extends ParrotBaseEntity> entityType, World world) {
		super(entityType, world);
	}

	public boolean method_6627(ServerPlayerEntity serverPlayerEntity) {
		CompoundTag compoundTag = new CompoundTag();
		compoundTag.putString("id", this.getSavedEntityId());
		this.method_5647(compoundTag);
		if (serverPlayerEntity.method_7298(compoundTag)) {
			this.invalidate();
			return true;
		} else {
			return false;
		}
	}

	@Override
	public void update() {
		this.field_6864++;
		super.update();
	}

	public boolean method_6626() {
		return this.field_6864 > 100;
	}
}
