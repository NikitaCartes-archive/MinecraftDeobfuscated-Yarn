package net.minecraft.enchantment;

import net.minecraft.entity.EquipmentSlot;

public class LureEnchantment extends Enchantment {
	protected LureEnchantment(Enchantment.Weight weight, EnchantmentTarget enchantmentTarget, EquipmentSlot... equipmentSlots) {
		super(weight, enchantmentTarget, equipmentSlots);
	}

	@Override
	public int getMinimumPower(int i) {
		return 15 + (i - 1) * 9;
	}

	@Override
	public int getMaximumPower(int i) {
		return super.getMinimumPower(i) + 50;
	}

	@Override
	public int getMaximumLevel() {
		return 3;
	}
}
