package net.minecraft.enchantment;

import net.minecraft.entity.EquipmentSlot;

public class SweepingEnchantment extends Enchantment {
	public SweepingEnchantment(Enchantment.Weight weight, EquipmentSlot... equipmentSlots) {
		super(weight, EnchantmentTarget.WEAPON, equipmentSlots);
	}

	@Override
	public int getMinimumPower(int i) {
		return 5 + (i - 1) * 9;
	}

	@Override
	public int getMaximumLevel() {
		return 3;
	}

	public static float getMultiplier(int i) {
		return 1.0F - 1.0F / (float)(i + 1);
	}
}
