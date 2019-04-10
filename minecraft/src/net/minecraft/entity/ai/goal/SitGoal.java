package net.minecraft.entity.ai.goal;

import java.util.EnumSet;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.passive.TameableEntity;

public class SitGoal extends Goal {
	private final TameableEntity entity;
	private boolean enabledWithOwner;

	public SitGoal(TameableEntity tameableEntity) {
		this.entity = tameableEntity;
		this.setControls(EnumSet.of(Goal.Control.field_18407, Goal.Control.field_18405));
	}

	@Override
	public boolean canStart() {
		if (!this.entity.isTamed()) {
			return false;
		} else if (this.entity.isInsideWaterOrBubbleColumn()) {
			return false;
		} else if (!this.entity.onGround) {
			return false;
		} else {
			LivingEntity livingEntity = this.entity.getOwner();
			if (livingEntity == null) {
				return true;
			} else {
				return this.entity.squaredDistanceTo(livingEntity) < 144.0 && livingEntity.getAttacker() != null ? false : this.enabledWithOwner;
			}
		}
	}

	@Override
	public void start() {
		this.entity.getNavigation().stop();
		this.entity.setSitting(true);
	}

	@Override
	public void stop() {
		this.entity.setSitting(false);
	}

	public void setEnabledWithOwner(boolean bl) {
		this.enabledWithOwner = bl;
	}
}
