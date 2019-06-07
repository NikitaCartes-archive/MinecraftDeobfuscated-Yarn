package net.minecraft.enchantment;

import net.minecraft.entity.EquipmentSlot;

public class QuickChargeEnchantment extends Enchantment {
	public QuickChargeEnchantment(Enchantment.Weight weight, EquipmentSlot... equipmentSlots) {
		super(weight, EnchantmentTarget.CROSSBOW, equipmentSlots);
	}

	@Override
	public int getMinimumPower(int i) {
		return 12 + (i - 1) * 20;
	}

	@Override
	public int method_20742(int i) {
		return 50;
	}

	@Override
	public int getMaximumLevel() {
		return 3;
	}
}
