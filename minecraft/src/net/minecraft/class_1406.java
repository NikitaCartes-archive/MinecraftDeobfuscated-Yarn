package net.minecraft;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.TrackTargetGoal;
import net.minecraft.entity.passive.TameableEntity;

public class class_1406 extends TrackTargetGoal {
	private final TameableEntity field_6666;
	private LivingEntity field_6667;
	private int field_6665;

	public class_1406(TameableEntity tameableEntity) {
		super(tameableEntity, false);
		this.field_6666 = tameableEntity;
		this.setControlBits(1);
	}

	@Override
	public boolean canStart() {
		if (!this.field_6666.isTamed()) {
			return false;
		} else {
			LivingEntity livingEntity = this.field_6666.getOwner();
			if (livingEntity == null) {
				return false;
			} else {
				this.field_6667 = livingEntity.method_6052();
				int i = livingEntity.method_6083();
				return i != this.field_6665 && this.canTrack(this.field_6667, false) && this.field_6666.method_6178(this.field_6667, livingEntity);
			}
		}
	}

	@Override
	public void start() {
		this.entity.setTarget(this.field_6667);
		LivingEntity livingEntity = this.field_6666.getOwner();
		if (livingEntity != null) {
			this.field_6665 = livingEntity.method_6083();
		}

		super.start();
	}
}
