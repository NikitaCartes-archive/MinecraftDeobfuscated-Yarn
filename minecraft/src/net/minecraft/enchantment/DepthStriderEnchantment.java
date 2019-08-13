package net.minecraft.enchantment;

import net.minecraft.entity.EquipmentSlot;

public class DepthStriderEnchantment extends Enchantment {
	public DepthStriderEnchantment(Enchantment.Weight weight, EquipmentSlot... equipmentSlots) {
		super(weight, EnchantmentTarget.field_9079, equipmentSlots);
	}

	@Override
	public int getMinimumPower(int i) {
		return i * 10;
	}

	@Override
	public int getMaximumPower(int i) {
		return this.getMinimumPower(i) + 15;
	}

	@Override
	public int getMaximumLevel() {
		return 3;
	}

	@Override
	public boolean differs(Enchantment enchantment) {
		return super.differs(enchantment) && enchantment != Enchantments.field_9122;
	}
}
