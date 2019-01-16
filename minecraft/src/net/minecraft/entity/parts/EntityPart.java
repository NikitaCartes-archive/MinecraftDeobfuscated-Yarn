package net.minecraft.entity.parts;

import net.minecraft.entity.Entity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.nbt.CompoundTag;

public class EntityPart extends Entity {
	public final IEntityPartDamageDelegate damageDelegate;
	public final String name;

	public EntityPart(IEntityPartDamageDelegate iEntityPartDamageDelegate, String string, float f, float g) {
		super(iEntityPartDamageDelegate.getType(), iEntityPartDamageDelegate.getPartDamageWorld());
		this.setSize(f, g);
		this.damageDelegate = iEntityPartDamageDelegate;
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
		return this.isInvulnerableTo(damageSource) ? false : this.damageDelegate.damage(this, damageSource, f);
	}

	@Override
	public boolean isPartOf(Entity entity) {
		return this == entity || this.damageDelegate == entity;
	}
}
