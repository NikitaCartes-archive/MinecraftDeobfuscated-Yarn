package net.minecraft.enchantment;

import net.minecraft.entity.EquipmentSlot;

public class AquaAffinityEnchantment extends Enchantment {
	public AquaAffinityEnchantment(Enchantment.Rarity weight, EquipmentSlot... slotTypes) {
		super(weight, EnchantmentTarget.ARMOR_HEAD, slotTypes);
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
