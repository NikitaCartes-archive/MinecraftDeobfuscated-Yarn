package net.minecraft.enchantment;

import net.minecraft.entity.EquipmentSlot;
import net.minecraft.registry.tag.ItemTags;

public class InfinityEnchantment extends Enchantment {
	public InfinityEnchantment(Enchantment.Rarity weight, EquipmentSlot... slotTypes) {
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

	@Override
	public boolean canAccept(Enchantment other) {
		return other instanceof MendingEnchantment ? false : super.canAccept(other);
	}
}
