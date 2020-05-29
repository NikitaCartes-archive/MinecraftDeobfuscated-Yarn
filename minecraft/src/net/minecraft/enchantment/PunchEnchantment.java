package net.minecraft.enchantment;

import net.minecraft.entity.EquipmentSlot;

public class PunchEnchantment extends Enchantment {
	public PunchEnchantment(Enchantment.Rarity weight, EquipmentSlot... slotTypes) {
		super(weight, EnchantmentTarget.BOW, slotTypes);
	}

	@Override
	public int getMinPower(int level) {
		return 12 + (level - 1) * 20;
	}

	@Override
	public int getMaxPower(int level) {
		return this.getMinPower(level) + 25;
	}

	@Override
	public int getMaxLevel() {
		return 2;
	}
}
