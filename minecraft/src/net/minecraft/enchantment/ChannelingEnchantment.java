package net.minecraft.enchantment;

import net.minecraft.entity.EquipmentSlot;
import net.minecraft.registry.tag.ItemTags;

public class ChannelingEnchantment extends Enchantment {
	public ChannelingEnchantment(Enchantment.Rarity weight, EquipmentSlot... slotTypes) {
		super(weight, ItemTags.TRIDENT_ENCHANTABLE, slotTypes);
	}

	@Override
	public int getMinPower(int level) {
		return 25;
	}

	@Override
	public int getMaxPower(int level) {
		return 50;
	}
}
