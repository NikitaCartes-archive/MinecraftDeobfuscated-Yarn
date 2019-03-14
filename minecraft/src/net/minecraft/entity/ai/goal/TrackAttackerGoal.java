package net.minecraft.entity.ai.goal;

import java.util.EnumSet;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.TargetPredicate;
import net.minecraft.entity.passive.TameableEntity;

public class TrackAttackerGoal extends TrackTargetGoal {
	private final TameableEntity field_6654;
	private LivingEntity field_6655;
	private int field_6653;

	public TrackAttackerGoal(TameableEntity tameableEntity) {
		super(tameableEntity, false);
		this.field_6654 = tameableEntity;
		this.setControlBits(EnumSet.of(Goal.ControlBit.field_18408));
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
				return i != this.field_6653 && this.canTrack(this.field_6655, TargetPredicate.DEFAULT) && this.field_6654.method_6178(this.field_6655, livingEntity);
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
