package net.minecraft.enchantment;

import net.minecraft.entity.EquipmentSlot;

public class ChannelingEnchantment extends Enchantment {
	public ChannelingEnchantment(Enchantment.Rarity weight, EquipmentSlot... slotTypes) {
		super(weight, EnchantmentTarget.TRIDENT, slotTypes);
	}

	@Override
	public int getMinimumPower(int level) {
		return 25;
	}

	@Override
	public int getMaximumPower(int level) {
		return 50;
	}

	@Override
	public int getMaximumLevel() {
		return 1;
	}

	@Override
	public boolean canAccept(Enchantment other) {
		return super.canAccept(other);
	}
}
