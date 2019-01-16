package net.minecraft.enchantment;

import net.minecraft.entity.EquipmentSlot;

public class FireAspectEnchantment extends Enchantment {
	protected FireAspectEnchantment(Enchantment.Weight weight, EquipmentSlot... equipmentSlots) {
		super(weight, EnchantmentTarget.WEAPON, equipmentSlots);
	}

	@Override
	public int getMinimumPower(int i) {
		return 10 + 20 * (i - 1);
	}

	@Override
	public int getMaximumLevel() {
		return 2;
	}
}
