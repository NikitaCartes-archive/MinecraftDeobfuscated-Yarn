package net.minecraft.enchantment;

import net.minecraft.entity.EquipmentSlot;

public class RespirationEnchantment extends Enchantment {
	public RespirationEnchantment(Enchantment.Weight weight, EquipmentSlot... equipmentSlots) {
		super(weight, EnchantmentTarget.ARMOR_HEAD, equipmentSlots);
	}

	@Override
	public int getMinimumPower(int i) {
		return 10 * i;
	}

	@Override
	public int method_20742(int i) {
		return this.getMinimumPower(i) + 30;
	}

	@Override
	public int getMaximumLevel() {
		return 3;
	}
}
