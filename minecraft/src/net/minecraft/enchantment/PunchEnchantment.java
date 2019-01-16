package net.minecraft.enchantment;

import net.minecraft.entity.EquipmentSlot;

public class PunchEnchantment extends Enchantment {
	public PunchEnchantment(Enchantment.Weight weight, EquipmentSlot... equipmentSlots) {
		super(weight, EnchantmentTarget.BOW, equipmentSlots);
	}

	@Override
	public int getMinimumPower(int i) {
		return 12 + (i - 1) * 20;
	}

	@Override
	public int getMaximumLevel() {
		return 2;
	}
}
