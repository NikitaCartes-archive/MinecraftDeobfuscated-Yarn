package net.minecraft.enchantment;

import net.minecraft.entity.EquipmentSlot;

public class VanishingCurseEnchantment extends Enchantment {
	public VanishingCurseEnchantment(Enchantment.Weight weight, EquipmentSlot... equipmentSlots) {
		super(weight, EnchantmentTarget.ALL, equipmentSlots);
	}

	@Override
	public int getMinimumPower(int i) {
		return 25;
	}

	@Override
	public int getMaximumPower(int i) {
		return 50;
	}

	@Override
	public int getMaximumLevel() {
		return 1;
	}

	@Override
	public boolean isLootOnly() {
		return true;
	}

	@Override
	public boolean isCursed() {
		return true;
	}
}
