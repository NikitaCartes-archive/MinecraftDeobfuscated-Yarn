package net.minecraft.enchantment;

import net.minecraft.entity.EquipmentSlot;

public class RespirationEnchantment extends Enchantment {
	public RespirationEnchantment(Enchantment.Weight weight, EquipmentSlot... equipmentSlots) {
		super(weight, EnchantmentTarget.field_9080, equipmentSlots);
	}

	@Override
	public int getMinimumPower(int i) {
		return 10 * i;
	}

	@Override
	public int getMaximumPower(int i) {
		return this.getMinimumPower(i) + 30;
	}

	@Override
	public int getMaximumLevel() {
		return 3;
	}
}
