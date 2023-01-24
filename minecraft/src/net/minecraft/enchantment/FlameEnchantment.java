package net.minecraft.enchantment;

import net.minecraft.entity.EquipmentSlot;

public class FlameEnchantment extends Enchantment {
	public FlameEnchantment(Enchantment.Rarity weight, EquipmentSlot... slotTypes) {
		super(weight, EnchantmentTarget.BOW, slotTypes);
	}

	@Override
	public int getMinPower(int level) {
		return 20;
	}

	@Override
	public int getMaxPower(int level) {
		return 50;
	}
}
