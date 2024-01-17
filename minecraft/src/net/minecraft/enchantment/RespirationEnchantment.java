package net.minecraft.enchantment;

import net.minecraft.entity.EquipmentSlot;
import net.minecraft.registry.tag.ItemTags;

public class RespirationEnchantment extends Enchantment {
	public RespirationEnchantment(Enchantment.Rarity weight, EquipmentSlot... slotTypes) {
		super(weight, ItemTags.HEAD_ARMOR_ENCHANTABLE, slotTypes);
	}

	@Override
	public int getMinPower(int level) {
		return 10 * level;
	}

	@Override
	public int getMaxPower(int level) {
		return this.getMinPower(level) + 30;
	}

	@Override
	public int getMaxLevel() {
		return 3;
	}
}
