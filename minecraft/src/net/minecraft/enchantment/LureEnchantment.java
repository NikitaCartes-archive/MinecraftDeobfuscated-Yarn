package net.minecraft.enchantment;

import net.minecraft.entity.EquipmentSlot;

public class LureEnchantment extends Enchantment {
	protected LureEnchantment(Enchantment.Weight weight, EnchantmentTarget enchantmentTarget, EquipmentSlot... equipmentSlots) {
		super(weight, enchantmentTarget, equipmentSlots);
	}

	@Override
	public int getMinimumPower(int level) {
		return 15 + (level - 1) * 9;
	}

	@Override
	public int getMaximumPower(int level) {
		return super.getMinimumPower(level) + 50;
	}

	@Override
	public int getMaximumLevel() {
		return 3;
	}
}
