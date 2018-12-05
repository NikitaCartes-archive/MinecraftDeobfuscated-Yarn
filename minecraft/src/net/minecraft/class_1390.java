package net.minecraft;

import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.passive.VillagerEntity;
import net.minecraft.entity.player.PlayerEntity;

public class class_1390 extends Goal {
	private final VillagerEntity field_6610;

	public class_1390(VillagerEntity villagerEntity) {
		this.field_6610 = villagerEntity;
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
		} else if (this.field_6610.field_6037) {
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
		this.field_6610.getNavigation().method_6340();
	}

	@Override
	public void onRemove() {
		this.field_6610.setCurrentCustomer(null);
	}
}
