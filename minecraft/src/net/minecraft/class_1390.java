package net.minecraft;

import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.passive.AbstractTraderEntity;
import net.minecraft.entity.player.PlayerEntity;

public class class_1390 extends Goal {
	private final AbstractTraderEntity field_6610;

	public class_1390(AbstractTraderEntity abstractTraderEntity) {
		this.field_6610 = abstractTraderEntity;
		this.setControlBits(5);
	}

	@Override
	public boolean canStart() {
		if (!this.field_6610.isValid()) {
			return false;
		} else if (this.field_6610.isInsideWater()) {
			return false;
		} else if (!this.field_6610.onGround) {
			return false;
		} else if (this.field_6610.velocityModified) {
			return false;
		} else {
			PlayerEntity playerEntity = this.field_6610.getCurrentCustomer();
			if (playerEntity == null) {
				return false;
			} else {
				return this.field_6610.squaredDistanceTo(playerEntity) > 16.0 ? false : playerEntity.container != null;
			}
		}
	}

	@Override
	public void start() {
		this.field_6610.getNavigation().stop();
	}

	@Override
	public void onRemove() {
		this.field_6610.setCurrentCustomer(null);
	}
}
