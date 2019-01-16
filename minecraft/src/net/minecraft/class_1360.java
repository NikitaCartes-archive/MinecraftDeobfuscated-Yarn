package net.minecraft;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.passive.ParrotBaseEntity;
import net.minecraft.entity.player.PlayerEntity;

public class class_1360 extends Goal {
	private final ParrotBaseEntity field_6478;
	private PlayerEntity field_6479;
	private boolean field_6480;

	public class_1360(ParrotBaseEntity parrotBaseEntity) {
		this.field_6478 = parrotBaseEntity;
	}

	@Override
	public boolean canStart() {
		LivingEntity livingEntity = this.field_6478.getOwner();
		boolean bl = livingEntity != null
			&& !((PlayerEntity)livingEntity).isSpectator()
			&& !((PlayerEntity)livingEntity).abilities.flying
			&& !livingEntity.isInsideWater();
		return !this.field_6478.isSitting() && bl && this.field_6478.method_6626();
	}

	@Override
	public boolean canStop() {
		return !this.field_6480;
	}

	@Override
	public void start() {
		this.field_6479 = (PlayerEntity)this.field_6478.getOwner();
		this.field_6480 = false;
	}

	@Override
	public void tick() {
		if (!this.field_6480 && !this.field_6478.isSitting() && !this.field_6478.isLeashed()) {
			if (this.field_6478.getBoundingBox().intersects(this.field_6479.getBoundingBox())) {
				this.field_6480 = this.field_6478.method_6627(this.field_6479);
			}
		}
	}
}
