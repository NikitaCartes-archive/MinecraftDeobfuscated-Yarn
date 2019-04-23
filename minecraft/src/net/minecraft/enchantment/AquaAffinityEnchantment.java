package net.minecraft.enchantment;

import net.minecraft.entity.EquipmentSlot;

public class AquaAffinityEnchantment extends Enchantment {
	public AquaAffinityEnchantment(Enchantment.Weight weight, EquipmentSlot... equipmentSlots) {
		super(weight, EnchantmentTarget.ARMOR_HEAD, equipmentSlots);
	}

	@Override
	public int getMinimumPower(int i) {
		return 1;
	}

	@Override
	public int getMaximumLevel() {
		return 1;
	}
}
