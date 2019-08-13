package net.minecraft.enchantment;

import net.minecraft.entity.EquipmentSlot;

public class InfinityEnchantment extends Enchantment {
	public InfinityEnchantment(Enchantment.Weight weight, EquipmentSlot... equipmentSlots) {
		super(weight, EnchantmentTarget.field_9070, equipmentSlots);
	}

	@Override
	public int getMinimumPower(int i) {
		return 20;
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
		return enchantment instanceof MendingEnchantment ? false : super.differs(enchantment);
	}
}
