package net.minecraft.enchantment;

import net.minecraft.entity.EquipmentSlot;

public class QuickChargeEnchantment extends Enchantment {
	public QuickChargeEnchantment(Enchantment.Rarity weight, EquipmentSlot... slot) {
		super(weight, EnchantmentTarget.CROSSBOW, slot);
	}

	@Override
	public int getMinPower(int level) {
		return 12 + (level - 1) * 20;
	}

	@Override
	public int getMaxPower(int level) {
		return 50;
	}

	@Override
	public int getMaxLevel() {
		return 3;
	}
}
