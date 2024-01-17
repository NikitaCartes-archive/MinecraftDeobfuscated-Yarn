package net.minecraft.enchantment;

import net.minecraft.entity.EquipmentSlot;
import net.minecraft.registry.tag.ItemTags;

public class SwiftSneakEnchantment extends Enchantment {
	public SwiftSneakEnchantment(Enchantment.Rarity rarity, EquipmentSlot... slots) {
		super(rarity, ItemTags.LEG_ARMOR_ENCHANTABLE, slots);
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
}
