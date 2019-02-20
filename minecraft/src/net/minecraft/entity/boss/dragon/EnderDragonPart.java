package net.minecraft.entity.boss.dragon;

import net.minecraft.class_4048;
import net.minecraft.class_4050;
import net.minecraft.entity.Entity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.Packet;

public class EnderDragonPart extends Entity {
	public final EnderDragonEntity owner;
	public final String name;
	private final class_4048 field_18119;

	public EnderDragonPart(EnderDragonEntity enderDragonEntity, String string, float f, float g) {
		super(enderDragonEntity.getType(), enderDragonEntity.world);
		this.field_18119 = class_4048.method_18384(f, g);
		this.method_18382();
		this.owner = enderDragonEntity;
		this.name = string;
	}

	@Override
	protected void initDataTracker() {
	}

	@Override
	protected void readCustomDataFromTag(CompoundTag compoundTag) {
	}

	@Override
	protected void writeCustomDataToTag(CompoundTag compoundTag) {
	}

	@Override
	public boolean doesCollide() {
		return true;
	}

	@Override
	public boolean damage(DamageSource damageSource, float f) {
		return this.isInvulnerableTo(damageSource) ? false : this.owner.damagePart(this, damageSource, f);
	}

	@Override
	public boolean isPartOf(Entity entity) {
		return this == entity || this.owner == entity;
	}

	@Override
	public Packet<?> createSpawnPacket() {
		throw new UnsupportedOperationException();
	}

	@Override
	public class_4048 method_18377(class_4050 arg) {
		return this.field_18119;
	}
}
