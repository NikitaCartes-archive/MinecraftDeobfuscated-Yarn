package net.minecraft.enchantment;

import net.minecraft.entity.EquipmentSlot;

public class LoyaltyEnchantment extends Enchantment {
	public LoyaltyEnchantment(Enchantment.Rarity weight, EquipmentSlot... slotTypes) {
		super(weight, EnchantmentTarget.TRIDENT, slotTypes);
	}

	@Override
	public int getMinimumPower(int level) {
		return 5 + level * 7;
	}

	@Override
	public int getMaximumPower(int level) {
		return 50;
	}

	@Override
	public int getMaximumLevel() {
		return 3;
	}

	@Override
	public boolean canAccept(Enchantment other) {
		return super.canAccept(other);
	}
}
