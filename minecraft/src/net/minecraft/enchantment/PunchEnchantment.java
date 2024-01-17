package net.minecraft.enchantment;

import net.minecraft.entity.EquipmentSlot;
import net.minecraft.registry.tag.ItemTags;

public class PunchEnchantment extends Enchantment {
	public PunchEnchantment(Enchantment.Rarity weight, EquipmentSlot... slotTypes) {
		super(weight, ItemTags.BOW_ENCHANTABLE, slotTypes);
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
