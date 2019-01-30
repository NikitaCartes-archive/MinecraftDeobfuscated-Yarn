package net.minecraft;

import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.passive.AbstractVillagerEntity;
import net.minecraft.entity.player.PlayerEntity;

public class class_1390 extends Goal {
	private final AbstractVillagerEntity owner;

	public class_1390(AbstractVillagerEntity abstractVillagerEntity) {
		this.owner = abstractVillagerEntity;
		this.setControlBits(5);
	}

	@Override
	public boolean canStart() {
		if (!this.owner.isValid()) {
			return false;
		} else if (this.owner.isInsideWater()) {
			return false;
		} else if (!this.owner.onGround) {
			return false;
		} else if (this.owner.velocityModified) {
			return false;
		} else {
			PlayerEntity playerEntity = this.owner.getCurrentCustomer();
			if (playerEntity == null) {
				return false;
			} else {
				return this.owner.squaredDistanceTo(playerEntity) > 16.0 ? false : playerEntity.container != null;
			}
		}
	}

	@Override
	public void start() {
		this.owner.getNavigation().stop();
	}

	@Override
	public void onRemove() {
		this.owner.setCurrentCustomer(null);
	}
}
