package net.minecraft.enchantment;

import net.minecraft.entity.EquipmentSlot;
import net.minecraft.registry.tag.ItemTags;

public class MultishotEnchantment extends Enchantment {
	public MultishotEnchantment(Enchantment.Rarity weight, EquipmentSlot... slotTypes) {
		super(weight, ItemTags.CROSSBOW_ENCHANTABLE, slotTypes);
	}

	@Override
	public int getMinPower(int level) {
		return 20;
	}

	@Override
	public int getMaxPower(int level) {
		return 50;
	}

	@Override
	public boolean canAccept(Enchantment other) {
		return super.canAccept(other) && other != Enchantments.PIERCING;
	}
}
