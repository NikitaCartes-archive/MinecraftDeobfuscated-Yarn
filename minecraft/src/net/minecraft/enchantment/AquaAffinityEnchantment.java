package net.minecraft.enchantment;

import net.minecraft.entity.EquipmentSlot;
import net.minecraft.registry.tag.ItemTags;

public class AquaAffinityEnchantment extends Enchantment {
	public AquaAffinityEnchantment(Enchantment.Rarity weight, EquipmentSlot... slotTypes) {
		super(weight, ItemTags.HEAD_ARMOR_ENCHANTABLE, slotTypes);
	}

	@Override
	public int getMinPower(int level) {
		return 1;
	}

	@Override
	public int getMaxPower(int level) {
		return this.getMinPower(level) + 40;
	}
}
