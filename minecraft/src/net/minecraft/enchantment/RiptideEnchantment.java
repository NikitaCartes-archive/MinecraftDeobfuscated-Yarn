package net.minecraft.enchantment;

import net.minecraft.entity.EquipmentSlot;

public class RiptideEnchantment extends Enchantment {
	public RiptideEnchantment(Enchantment.Weight weight, EquipmentSlot... equipmentSlots) {
		super(weight, EnchantmentTarget.TRIDENT, equipmentSlots);
	}

	@Override
	public int getMinimumPower(int i) {
		return 10 + i * 7;
	}

	@Override
	public int method_20742(int i) {
		return 50;
	}

	@Override
	public int getMaximumLevel() {
		return 3;
	}

	@Override
	public boolean differs(Enchantment enchantment) {
		return super.differs(enchantment) && enchantment != Enchantments.field_9120 && enchantment != Enchantments.field_9117;
	}
}
