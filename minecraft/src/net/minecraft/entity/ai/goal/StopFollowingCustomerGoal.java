package net.minecraft.entity.ai.goal;

import java.util.EnumSet;
import net.minecraft.entity.passive.AbstractTraderEntity;
import net.minecraft.entity.player.PlayerEntity;

public class StopFollowingCustomerGoal extends Goal {
	private final AbstractTraderEntity trader;

	public StopFollowingCustomerGoal(AbstractTraderEntity abstractTraderEntity) {
		this.trader = abstractTraderEntity;
		this.setControls(EnumSet.of(Goal.Control.field_18407, Goal.Control.field_18405));
	}

	@Override
	public boolean canStart() {
		if (!this.trader.isAlive()) {
			return false;
		} else if (this.trader.isInsideWater()) {
			return false;
		} else if (!this.trader.onGround) {
			return false;
		} else if (this.trader.velocityModified) {
			return false;
		} else {
			PlayerEntity playerEntity = this.trader.getCurrentCustomer();
			if (playerEntity == null) {
				return false;
			} else {
				return this.trader.squaredDistanceTo(playerEntity) > 16.0 ? false : playerEntity.container != null;
			}
		}
	}

	@Override
	public void start() {
		this.trader.getNavigation().stop();
	}

	@Override
	public void stop() {
		this.trader.setCurrentCustomer(null);
	}
}
