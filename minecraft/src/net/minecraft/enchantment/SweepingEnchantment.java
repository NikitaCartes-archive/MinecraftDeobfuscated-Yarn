package net.minecraft.enchantment;

import net.minecraft.entity.EquipmentSlot;

public class SweepingEnchantment extends Enchantment {
	public SweepingEnchantment(Enchantment.Weight weight, EquipmentSlot... slotTypes) {
		super(weight, EnchantmentTarget.WEAPON, slotTypes);
	}

	@Override
	public int getMinimumPower(int level) {
		return 5 + (level - 1) * 9;
	}

	@Override
	public int getMaximumPower(int level) {
		return this.getMinimumPower(level) + 15;
	}

	@Override
	public int getMaximumLevel() {
		return 3;
	}

	public static float getMultiplier(int i) {
		return 1.0F - 1.0F / (float)(i + 1);
	}
}
