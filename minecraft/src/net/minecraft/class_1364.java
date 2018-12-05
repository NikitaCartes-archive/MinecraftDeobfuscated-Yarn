package net.minecraft;

import net.minecraft.entity.passive.VillagerEntity;
import net.minecraft.entity.player.PlayerEntity;

public class class_1364 extends class_1361 {
	private final VillagerEntity field_6495;

	public class_1364(VillagerEntity villagerEntity) {
		super(villagerEntity, PlayerEntity.class, 8.0F);
		this.field_6495 = villagerEntity;
	}

	@Override
	public boolean canStart() {
		if (this.field_6495.method_7235()) {
			this.field_6484 = this.field_6495.getCurrentCustomer();
			return true;
		} else {
			return false;
		}
	}
}
