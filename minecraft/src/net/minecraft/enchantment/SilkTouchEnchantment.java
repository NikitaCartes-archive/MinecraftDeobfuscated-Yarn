package net.minecraft.enchantment;

import net.minecraft.entity.EquipmentSlot;
import net.minecraft.registry.tag.ItemTags;

public class SilkTouchEnchantment extends Enchantment {
	protected SilkTouchEnchantment(Enchantment.Rarity weight, EquipmentSlot... slotTypes) {
		super(weight, ItemTags.MINING_LOOT_ENCHANTABLE, slotTypes);
	}

	@Override
	public int getMinPower(int level) {
		return 15;
	}

	@Override
	public int getMaxPower(int level) {
		return super.getMinPower(level) + 50;
	}

	@Override
	public boolean canAccept(Enchantment other) {
		return super.canAccept(other) && other != Enchantments.FORTUNE;
	}
}
