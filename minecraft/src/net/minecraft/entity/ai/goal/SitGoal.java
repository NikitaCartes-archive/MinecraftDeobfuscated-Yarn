package net.minecraft.entity.ai.goal;

import java.util.EnumSet;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.passive.TameableEntity;

public class SitGoal extends Goal {
	private final TameableEntity tameable;
	private boolean enabledWithOwner;

	public SitGoal(TameableEntity tameableEntity) {
		this.tameable = tameableEntity;
		this.setControls(EnumSet.of(Goal.Control.field_18407, Goal.Control.field_18405));
	}

	@Override
	public boolean shouldContinue() {
		return this.enabledWithOwner;
	}

	@Override
	public boolean canStart() {
		if (!this.tameable.isTamed()) {
			return false;
		} else if (this.tameable.isInsideWaterOrBubbleColumn()) {
			return false;
		} else if (!this.tameable.onGround) {
			return false;
		} else {
			LivingEntity livingEntity = this.tameable.getOwner();
			if (livingEntity == null) {
				return true;
			} else {
				return this.tameable.squaredDistanceTo(livingEntity) < 144.0 && livingEntity.getAttacker() != null ? false : this.enabledWithOwner;
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

	public void setEnabledWithOwner(boolean bl) {
		this.enabledWithOwner = bl;
	}
}
