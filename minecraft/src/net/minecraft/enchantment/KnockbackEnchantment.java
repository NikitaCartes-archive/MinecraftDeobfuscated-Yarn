package net.minecraft.enchantment;

import net.minecraft.entity.EquipmentSlot;

public class KnockbackEnchantment extends Enchantment {
	protected KnockbackEnchantment(Enchantment.Rarity weight, EquipmentSlot... slot) {
		super(weight, EnchantmentTarget.WEAPON, slot);
	}

	@Override
	public int getMinimumPower(int level) {
		return 5 + 20 * (level - 1);
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
