package net.minecraft.enchantment;

import net.minecraft.entity.EquipmentSlot;

public class InfinityEnchantment extends Enchantment {
	public InfinityEnchantment(Enchantment.Rarity weight, EquipmentSlot... slotTypes) {
		super(weight, EnchantmentTarget.BOW, slotTypes);
	}

	@Override
	public int getMinimumPower(int level) {
		return 20;
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
		return other instanceof MendingEnchantment ? false : super.canAccept(other);
	}
}
