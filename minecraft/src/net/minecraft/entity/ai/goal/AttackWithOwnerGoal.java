package net.minecraft.entity.ai.goal;

import java.util.EnumSet;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.TargetPredicate;
import net.minecraft.entity.passive.TameableEntity;

public class AttackWithOwnerGoal extends TrackTargetGoal {
	private final TameableEntity owner;
	private LivingEntity attacking;
	private int lastAttackTime;

	public AttackWithOwnerGoal(TameableEntity tameableEntity) {
		super(tameableEntity, false);
		this.owner = tameableEntity;
		this.setControlBits(EnumSet.of(Goal.ControlBit.field_18408));
	}

	@Override
	public boolean canStart() {
		if (!this.owner.isTamed()) {
			return false;
		} else {
			LivingEntity livingEntity = this.owner.getOwner();
			if (livingEntity == null) {
				return false;
			} else {
				this.attacking = livingEntity.getAttacking();
				int i = livingEntity.getLastAttackTime();
				return i != this.lastAttackTime && this.canTrack(this.attacking, TargetPredicate.DEFAULT) && this.owner.method_6178(this.attacking, livingEntity);
			}
		}
	}

	@Override
	public void start() {
		this.entity.setTarget(this.attacking);
		LivingEntity livingEntity = this.owner.getOwner();
		if (livingEntity != null) {
			this.lastAttackTime = livingEntity.getLastAttackTime();
		}

		super.start();
	}
}
