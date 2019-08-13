package net.minecraft.enchantment;

import net.minecraft.entity.EquipmentSlot;

public class MultishotEnchantment extends Enchantment {
	public MultishotEnchantment(Enchantment.Weight weight, EquipmentSlot... equipmentSlots) {
		super(weight, EnchantmentTarget.field_9081, equipmentSlots);
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
		return super.differs(enchantment) && enchantment != Enchantments.field_9132;
	}
}
