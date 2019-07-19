package net.minecraft.enchantment;

import net.minecraft.entity.EquipmentSlot;

public class PiercingEnchantment extends Enchantment {
	public PiercingEnchantment(Enchantment.Weight weight, EquipmentSlot... slotTypes) {
		super(weight, EnchantmentTarget.CROSSBOW, slotTypes);
	}

	@Override
	public int getMinimumPower(int level) {
		return 1 + (level - 1) * 10;
	}

	@Override
	public int getMaximumPower(int level) {
		return 50;
	}

	@Override
	public int getMaximumLevel() {
		return 4;
	}

	@Override
	public boolean differs(Enchantment other) {
		return super.differs(other) && other != Enchantments.MULTISHOT;
	}
}
