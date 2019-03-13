package net.minecraft.entity.ai.goal;

import net.minecraft.entity.passive.AbstractTraderEntity;
import net.minecraft.entity.player.PlayerEntity;

public class LookAtCustomerGoal extends LookAtEntityGoal {
	private final AbstractTraderEntity field_6495;

	public LookAtCustomerGoal(AbstractTraderEntity abstractTraderEntity) {
		super(abstractTraderEntity, PlayerEntity.class, 8.0F);
		this.field_6495 = abstractTraderEntity;
	}

	@Override
	public boolean canStart() {
		if (this.field_6495.hasCustomer()) {
			this.target = this.field_6495.getCurrentCustomer();
			return true;
		} else {
			return false;
		}
	}
}
