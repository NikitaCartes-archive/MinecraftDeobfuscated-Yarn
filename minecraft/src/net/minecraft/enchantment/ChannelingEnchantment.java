package net.minecraft.enchantment;

import net.minecraft.entity.EquipmentSlot;

public class ChannelingEnchantment extends Enchantment {
	public ChannelingEnchantment(Enchantment.Weight weight, EquipmentSlot... equipmentSlots) {
		super(weight, EnchantmentTarget.field_9073, equipmentSlots);
	}

	@Override
	public int getMinimumPower(int i) {
		return 25;
	}

	@Override
	public int getMaximumPower(int i) {
		return 50;
	}

	@Override
	public int getMaximumLevel() {
		return 1;
	}

	@Override
	public boolean differs(Enchantment enchantment) {
		return super.differs(enchantment);
	}
}
