package net.minecraft.enchantment;

import net.minecraft.entity.EquipmentSlot;

public class FireAspectEnchantment extends Enchantment {
	protected FireAspectEnchantment(Enchantment.Rarity weight, EquipmentSlot... slotTypes) {
		super(weight, EnchantmentTarget.WEAPON, slotTypes);
	}

	@Override
	public int getMinimumPower(int level) {
		return 10 + 20 * (level - 1);
	}

	@Override
	public int getMaximumPower(int level) {
		return super.getMinimumPower(level) + 50;
	}

	@Override
	public int getMaximumLevel() {
		return 2;
	}
}
