package net.minecraft.enchantment;

import net.minecraft.entity.EquipmentSlot;
import net.minecraft.registry.tag.ItemTags;

public class MendingEnchantment extends Enchantment {
	public MendingEnchantment(Enchantment.Rarity weight, EquipmentSlot... slotTypes) {
		super(weight, ItemTags.DURABILITY_ENCHANTABLE, slotTypes);
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
}
