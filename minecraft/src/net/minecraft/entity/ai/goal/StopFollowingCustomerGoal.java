package net.minecraft.entity.ai.goal;

import java.util.EnumSet;
import net.minecraft.entity.passive.MerchantEntity;
import net.minecraft.entity.player.PlayerEntity;

public class StopFollowingCustomerGoal extends Goal {
	private final MerchantEntity merchant;

	public StopFollowingCustomerGoal(MerchantEntity merchant) {
		this.merchant = merchant;
		this.setControls(EnumSet.of(Goal.Control.JUMP, Goal.Control.MOVE));
	}

	@Override
	public boolean canStart() {
		if (!this.merchant.isAlive()) {
			return false;
		} else if (this.merchant.isTouchingWater()) {
			return false;
		} else if (!this.merchant.isOnGround()) {
			return false;
		} else if (this.merchant.velocityModified) {
			return false;
		} else {
			PlayerEntity playerEntity = this.merchant.getCustomer();
			if (playerEntity == null) {
				return false;
			} else {
				return this.merchant.squaredDistanceTo(playerEntity) > 16.0 ? false : playerEntity.currentScreenHandler != null;
			}
		}
	}

	@Override
	public void start() {
		this.merchant.getNavigation().stop();
	}

	@Override
	public void stop() {
		this.merchant.setCustomer(null);
	}
}
