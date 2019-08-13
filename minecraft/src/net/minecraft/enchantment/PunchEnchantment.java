package net.minecraft.enchantment;

import net.minecraft.entity.EquipmentSlot;

public class PunchEnchantment extends Enchantment {
	public PunchEnchantment(Enchantment.Weight weight, EquipmentSlot... equipmentSlots) {
		super(weight, EnchantmentTarget.field_9070, equipmentSlots);
	}

	@Override
	public int getMinimumPower(int i) {
		return 12 + (i - 1) * 20;
	}

	@Override
	public int getMaximumPower(int i) {
		return this.getMinimumPower(i) + 25;
	}

	@Override
	public int getMaximumLevel() {
		return 2;
	}
}
