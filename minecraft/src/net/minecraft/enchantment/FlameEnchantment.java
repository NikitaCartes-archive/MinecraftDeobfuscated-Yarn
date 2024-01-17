package net.minecraft.enchantment;

import net.minecraft.entity.EquipmentSlot;
import net.minecraft.registry.tag.ItemTags;

public class FlameEnchantment extends Enchantment {
	public FlameEnchantment(Enchantment.Rarity weight, EquipmentSlot... slotTypes) {
		super(weight, ItemTags.BOW_ENCHANTABLE, slotTypes);
	}

	@Override
	public int getMinPower(int level) {
		return 20;
	}

	@Override
	public int getMaxPower(int level) {
		return 50;
	}
}
