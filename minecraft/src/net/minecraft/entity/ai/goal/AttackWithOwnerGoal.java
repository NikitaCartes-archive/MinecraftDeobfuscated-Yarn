package net.minecraft.entity.ai.goal;

import java.util.EnumSet;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.TargetPredicate;
import net.minecraft.entity.passive.TameableEntity;

public class AttackWithOwnerGoal extends TrackTargetGoal {
	private final TameableEntity tameable;
	private LivingEntity attacking;
	private int lastAttackTime;

	public AttackWithOwnerGoal(TameableEntity tameableEntity) {
		super(tameableEntity, false);
		this.tameable = tameableEntity;
		this.setControls(EnumSet.of(Goal.Control.field_18408));
	}

	@Override
	public boolean canStart() {
		if (this.tameable.isTamed() && !this.tameable.isSitting()) {
			LivingEntity livingEntity = this.tameable.getOwner();
			if (livingEntity == null) {
				return false;
			} else {
				this.attacking = livingEntity.getAttacking();
				int i = livingEntity.getLastAttackTime();
				return i != this.lastAttackTime && this.canTrack(this.attacking, TargetPredicate.DEFAULT) && this.tameable.canAttackWithOwner(this.attacking, livingEntity);
			}
		} else {
			return false;
		}
	}

	@Override
	public void start() {
		this.mob.setTarget(this.attacking);
		LivingEntity livingEntity = this.tameable.getOwner();
		if (livingEntity != null) {
			this.lastAttackTime = livingEntity.getLastAttackTime();
		}

		super.start();
	}
}
