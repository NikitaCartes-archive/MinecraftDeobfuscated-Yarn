package net.minecraft.enchantment;

import net.minecraft.entity.EquipmentSlot;
import net.minecraft.registry.tag.ItemTags;

public class RiptideEnchantment extends Enchantment {
	public RiptideEnchantment(Enchantment.Rarity weight, EquipmentSlot... slotTypes) {
		super(weight, ItemTags.TRIDENT_ENCHANTABLE, slotTypes);
	}

	@Override
	public int getMinPower(int level) {
		return 10 + level * 7;
	}

	@Override
	public int getMaxPower(int level) {
		return 50;
	}

	@Override
	public int getMaxLevel() {
		return 3;
	}

	@Override
	public boolean canAccept(Enchantment other) {
		return super.canAccept(other) && other != Enchantments.LOYALTY && other != Enchantments.CHANNELING;
	}
}
