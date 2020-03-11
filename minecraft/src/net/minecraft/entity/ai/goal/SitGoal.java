package net.minecraft.entity.ai.goal;

import java.util.EnumSet;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.passive.TameableEntity;

public class SitGoal extends Goal {
	private final TameableEntity tameable;

	public SitGoal(TameableEntity tameable) {
		this.tameable = tameable;
		this.setControls(EnumSet.of(Goal.Control.JUMP, Goal.Control.MOVE));
	}

	@Override
	public boolean shouldContinue() {
		return this.tameable.method_24345();
	}

	@Override
	public boolean canStart() {
		if (!this.tameable.isTamed()) {
			return false;
		} else if (this.tameable.isInsideWaterOrBubbleColumn()) {
			return false;
		} else if (!this.tameable.isOnGround()) {
			return false;
		} else {
			LivingEntity livingEntity = this.tameable.getOwner();
			if (livingEntity == null) {
				return true;
			} else {
				return this.tameable.squaredDistanceTo(livingEntity) < 144.0 && livingEntity.getAttacker() != null ? false : this.tameable.method_24345();
			}
		}
	}

	@Override
	public void start() {
		this.tameable.getNavigation().stop();
		this.tameable.setSitting(true);
	}

	@Override
	public void stop() {
		this.tameable.setSitting(false);
	}
}
