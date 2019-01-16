package net.minecraft;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.TrackTargetGoal;
import net.minecraft.entity.passive.TameableEntity;

public class class_1403 extends TrackTargetGoal {
	private final TameableEntity field_6654;
	private LivingEntity field_6655;
	private int field_6653;

	public class_1403(TameableEntity tameableEntity) {
		super(tameableEntity, false);
		this.field_6654 = tameableEntity;
		this.setControlBits(1);
	}

	@Override
	public boolean canStart() {
		if (!this.field_6654.isTamed()) {
			return false;
		} else {
			LivingEntity livingEntity = this.field_6654.getOwner();
			if (livingEntity == null) {
				return false;
			} else {
				this.field_6655 = livingEntity.getAttacker();
				int i = livingEntity.getLastAttackedTime();
				return i != this.field_6653 && this.canTrack(this.field_6655, false) && this.field_6654.method_6178(this.field_6655, livingEntity);
			}
		}
	}

	@Override
	public void start() {
		this.entity.setTarget(this.field_6655);
		LivingEntity livingEntity = this.field_6654.getOwner();
		if (livingEntity != null) {
			this.field_6653 = livingEntity.getLastAttackedTime();
		}

		super.start();
	}
}
