package net.minecraft.enchantment;

import net.minecraft.entity.EquipmentSlot;
import net.minecraft.registry.tag.ItemTags;

public class SoulSpeedEnchantment extends Enchantment {
	public SoulSpeedEnchantment(Enchantment.Rarity weight, EquipmentSlot... slotTypes) {
		super(weight, ItemTags.FOOT_ARMOR_ENCHANTABLE, slotTypes);
	}

	@Override
	public int getMinPower(int level) {
		return level * 10;
	}

	@Override
	public int getMaxPower(int level) {
		return this.getMinPower(level) + 15;
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
