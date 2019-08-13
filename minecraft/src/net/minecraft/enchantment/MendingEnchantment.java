package net.minecraft.enchantment;

import net.minecraft.entity.EquipmentSlot;

public class MendingEnchantment extends Enchantment {
	public MendingEnchantment(Enchantment.Weight weight, EquipmentSlot... equipmentSlots) {
		super(weight, EnchantmentTarget.field_9082, equipmentSlots);
	}

	@Override
	public int getMinimumPower(int i) {
		return i * 25;
	}

	@Override
	public int getMaximumPower(int i) {
		return this.getMinimumPower(i) + 50;
	}

	@Override
	public boolean isTreasure() {
		return true;
	}

	@Override
	public int getMaximumLevel() {
		return 1;
	}
}
