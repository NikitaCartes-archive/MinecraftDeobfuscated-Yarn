package net.minecraft.enchantment;

import net.minecraft.entity.EquipmentSlot;

public class SoulSpeedEnchantment extends Enchantment {
	public SoulSpeedEnchantment(Enchantment.Rarity weight, EquipmentSlot... slotTypes) {
		super(weight, EnchantmentTarget.ARMOR_FEET, slotTypes);
	}

	@Override
	public int getMinimumPower(int level) {
		return level * 10;
	}

	@Override
	public int getMaximumPower(int level) {
		return this.getMinimumPower(level) + 15;
	}

	@Override
	public boolean isTreasure() {
		return true;
	}

	@Override
	public boolean isAvailableForEnchantedBookOffer() {
		return false;
	}

	@Override
	public boolean isAvailableForRandomSelection() {
		return false;
	}

	@Override
	public int getMaximumLevel() {
		return 3;
	}
}
