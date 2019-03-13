package net.minecraft.entity.boss.dragon;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityPose;
import net.minecraft.entity.EntitySize;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.Packet;

public class EnderDragonPart extends Entity {
	public final EnderDragonEntity field_7007;
	public final String name;
	private final EntitySize field_18119;

	public EnderDragonPart(EnderDragonEntity enderDragonEntity, String string, float f, float g) {
		super(enderDragonEntity.method_5864(), enderDragonEntity.field_6002);
		this.field_18119 = EntitySize.resizeable(f, g);
		this.refreshSize();
		this.field_7007 = enderDragonEntity;
		this.name = string;
	}

	@Override
	protected void initDataTracker() {
	}

	@Override
	protected void method_5749(CompoundTag compoundTag) {
	}

	@Override
	protected void method_5652(CompoundTag compoundTag) {
	}

	@Override
	public boolean doesCollide() {
		return true;
	}

	@Override
	public boolean damage(DamageSource damageSource, float f) {
		return this.isInvulnerableTo(damageSource) ? false : this.field_7007.damagePart(this, damageSource, f);
	}

	@Override
	public boolean isPartOf(Entity entity) {
		return this == entity || this.field_7007 == entity;
	}

	@Override
	public Packet<?> method_18002() {
		throw new UnsupportedOperationException();
	}

	@Override
	public EntitySize method_18377(EntityPose entityPose) {
		return this.field_18119;
	}
}
