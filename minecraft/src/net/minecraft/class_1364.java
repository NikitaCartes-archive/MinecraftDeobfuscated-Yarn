package net.minecraft;

import net.minecraft.entity.ai.goal.LookAtEntityGoal;
import net.minecraft.entity.passive.AbstractVillagerEntity;
import net.minecraft.entity.player.PlayerEntity;

public class class_1364 extends LookAtEntityGoal {
	private final AbstractVillagerEntity field_6495;

	public class_1364(AbstractVillagerEntity abstractVillagerEntity) {
		super(abstractVillagerEntity, PlayerEntity.class, 8.0F);
		this.field_6495 = abstractVillagerEntity;
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
