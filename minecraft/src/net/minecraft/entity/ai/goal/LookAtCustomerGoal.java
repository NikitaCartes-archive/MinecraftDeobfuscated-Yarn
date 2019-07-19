package net.minecraft.entity.ai.goal;

import net.minecraft.entity.passive.AbstractTraderEntity;
import net.minecraft.entity.player.PlayerEntity;

public class LookAtCustomerGoal extends LookAtEntityGoal {
	private final AbstractTraderEntity trader;

	public LookAtCustomerGoal(AbstractTraderEntity trader) {
		super(trader, PlayerEntity.class, 8.0F);
		this.trader = trader;
	}

	@Override
	public boolean canStart() {
		if (this.trader.hasCustomer()) {
			this.target = this.trader.getCurrentCustomer();
			return true;
		} else {
			return false;
		}
	}
}
