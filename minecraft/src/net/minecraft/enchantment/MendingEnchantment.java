package net.minecraft.enchantment;

import net.minecraft.entity.EquipmentSlot;

public class MendingEnchantment extends Enchantment {
	public MendingEnchantment(Enchantment.Weight weight, EquipmentSlot... equipmentSlots) {
		super(weight, EnchantmentTarget.TOOL, equipmentSlots);
	}

	@Override
	public int getMinimumPower(int i) {
		return i * 25;
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
