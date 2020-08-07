package net.minecraft.entity.passive;

import net.minecraft.entity.EntityType;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.world.World;

public abstract class TameableShoulderEntity extends TameableEntity {
	private int ticks;

	protected TameableShoulderEntity(EntityType<? extends TameableShoulderEntity> entityType, World world) {
		super(entityType, world);
	}

	public boolean mountOnto(ServerPlayerEntity player) {
		CompoundTag compoundTag = new CompoundTag();
		compoundTag.putString("id", this.getSavedEntityId());
		this.toTag(compoundTag);
		if (player.addShoulderEntity(compoundTag)) {
			this.remove();
			return true;
		} else {
			return false;
		}
	}

	@Override
	public void tick() {
		this.ticks++;
		super.tick();
	}

	public boolean isReadyToSitOnPlayer() {
		return this.ticks > 100;
	}
}
