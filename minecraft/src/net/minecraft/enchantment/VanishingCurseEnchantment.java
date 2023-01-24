package net.minecraft.enchantment;

import net.minecraft.entity.EquipmentSlot;

public class VanishingCurseEnchantment extends Enchantment {
	public VanishingCurseEnchantment(Enchantment.Rarity weight, EquipmentSlot... slotTypes) {
		super(weight, EnchantmentTarget.VANISHABLE, slotTypes);
	}

	@Override
	public int getMinPower(int level) {
		return 25;
	}

	@Override
	public int getMaxPower(int level) {
		return 50;
	}

	@Override
	public boolean isTreasure() {
		return true;
	}

	@Override
	public boolean isCursed() {
		return true;
	}
}
