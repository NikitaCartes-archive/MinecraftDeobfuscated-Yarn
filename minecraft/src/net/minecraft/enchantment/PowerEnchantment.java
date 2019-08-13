package net.minecraft.enchantment;

import net.minecraft.entity.EquipmentSlot;

public class PowerEnchantment extends Enchantment {
	public PowerEnchantment(Enchantment.Weight weight, EquipmentSlot... equipmentSlots) {
		super(weight, EnchantmentTarget.field_9070, equipmentSlots);
	}

	@Override
	public int getMinimumPower(int i) {
		return 1 + (i - 1) * 10;
	}

	@Override
	public int getMaximumPower(int i) {
		return this.getMinimumPower(i) + 15;
	}

	@Override
	public int getMaximumLevel() {
		return 5;
	}
}
