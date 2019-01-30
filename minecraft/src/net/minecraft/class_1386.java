package net.minecraft;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.passive.TameableEntity;

public class class_1386 extends Goal {
	private final TameableEntity owner;
	private boolean field_6598;

	public class_1386(TameableEntity tameableEntity) {
		this.owner = tameableEntity;
		this.setControlBits(5);
	}

	@Override
	public boolean canStart() {
		if (!this.owner.isTamed()) {
			return false;
		} else if (this.owner.isInsideWaterOrBubbleColumn()) {
			return false;
		} else if (!this.owner.onGround) {
			return false;
		} else {
			LivingEntity livingEntity = this.owner.getOwner();
			if (livingEntity == null) {
				return true;
			} else {
				return this.owner.squaredDistanceTo(livingEntity) < 144.0 && livingEntity.getAttacker() != null ? false : this.field_6598;
			}
		}
	}

	@Override
	public void start() {
		this.owner.getNavigation().stop();
		this.owner.setSitting(true);
	}

	@Override
	public void onRemove() {
		this.owner.setSitting(false);
	}

	public void method_6311(boolean bl) {
		this.field_6598 = bl;
	}
}
