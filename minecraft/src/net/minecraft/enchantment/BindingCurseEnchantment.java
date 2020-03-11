package net.minecraft.enchantment;

import net.minecraft.entity.EquipmentSlot;

public class BindingCurseEnchantment extends Enchantment {
	public BindingCurseEnchantment(Enchantment.Rarity weight, EquipmentSlot... slotTypes) {
		super(weight, EnchantmentTarget.WEARABLE, slotTypes);
	}

	@Override
	public int getMinimumPower(int level) {
		return 25;
	}

	@Override
	public int getMaximumPower(int level) {
		return 50;
	}

	@Override
	public int getMaximumLevel() {
		return 1;
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
