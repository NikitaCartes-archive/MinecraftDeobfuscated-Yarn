package net.minecraft.enchantment;

import net.minecraft.entity.EquipmentSlot;

public class SwiftSneakEnchantment extends Enchantment {
	public SwiftSneakEnchantment(Enchantment.Rarity rarity, EquipmentSlot... slots) {
		super(rarity, EnchantmentTarget.ARMOR_FEET, slots);
	}

	@Override
	public int getMinPower(int level) {
		return level * 25;
	}

	@Override
	public int getMaxPower(int level) {
		return this.getMinPower(level) + 50;
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
	public int getMaxLevel() {
		return 3;
	}

	@Override
	public boolean canAccept(Enchantment other) {
		return super.canAccept(other) && other != Enchantments.DEPTH_STRIDER && other != Enchantments.SOUL_SPEED && other != Enchantments.FROST_WALKER;
	}
}
