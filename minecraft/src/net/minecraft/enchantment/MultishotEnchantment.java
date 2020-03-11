package net.minecraft.enchantment;

import net.minecraft.entity.EquipmentSlot;

public class MultishotEnchantment extends Enchantment {
	public MultishotEnchantment(Enchantment.Rarity weight, EquipmentSlot... slotTypes) {
		super(weight, EnchantmentTarget.CROSSBOW, slotTypes);
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
		return super.canAccept(other) && other != Enchantments.PIERCING;
	}
}
