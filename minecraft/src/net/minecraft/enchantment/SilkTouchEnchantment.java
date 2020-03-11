package net.minecraft.enchantment;

import net.minecraft.entity.EquipmentSlot;

public class SilkTouchEnchantment extends Enchantment {
	protected SilkTouchEnchantment(Enchantment.Rarity weight, EquipmentSlot... slotTypes) {
		super(weight, EnchantmentTarget.DIGGER, slotTypes);
	}

	@Override
	public int getMinimumPower(int level) {
		return 15;
	}

	@Override
	public int getMaximumPower(int level) {
		return super.getMinimumPower(level) + 50;
	}

	@Override
	public int getMaximumLevel() {
		return 1;
	}

	@Override
	public boolean canAccept(Enchantment other) {
		return super.canAccept(other) && other != Enchantments.FORTUNE;
	}
}
