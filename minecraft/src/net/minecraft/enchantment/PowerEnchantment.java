package net.minecraft.enchantment;

import net.minecraft.entity.EquipmentSlot;

public class PowerEnchantment extends Enchantment {
	public PowerEnchantment(Enchantment.Weight weight, EquipmentSlot... equipmentSlots) {
		super(weight, EnchantmentTarget.BOW, equipmentSlots);
	}

	@Override
	public int getMinimumPower(int i) {
		return 1 + (i - 1) * 10;
	}

	@Override
	public int method_20742(int i) {
		return this.getMinimumPower(i) + 15;
	}

	@Override
	public int getMaximumLevel() {
		return 5;
	}
}
