package net.minecraft.enchantment;

import net.minecraft.entity.EquipmentSlot;

public class PiercingEnchantment extends Enchantment {
	public PiercingEnchantment(Enchantment.Weight weight, EquipmentSlot... equipmentSlots) {
		super(weight, EnchantmentTarget.CROSSBOW, equipmentSlots);
	}

	@Override
	public int getMinimumPower(int i) {
		return 1 + (i - 1) * 10;
	}

	@Override
	public int method_20742(int i) {
		return 50;
	}

	@Override
	public int getMaximumLevel() {
		return 4;
	}

	@Override
	public boolean differs(Enchantment enchantment) {
		return super.differs(enchantment) && enchantment != Enchantments.MULTISHOT;
	}
}
