package net.minecraft.enchantment;

import net.minecraft.entity.EquipmentSlot;

public class FireAspectEnchantment extends Enchantment {
	protected FireAspectEnchantment(Enchantment.Weight weight, EquipmentSlot... equipmentSlots) {
		super(weight, EnchantmentTarget.field_9074, equipmentSlots);
	}

	@Override
	public int getMinimumPower(int i) {
		return 10 + 20 * (i - 1);
	}

	@Override
	public int getMaximumPower(int i) {
		return super.getMinimumPower(i) + 50;
	}

	@Override
	public int getMaximumLevel() {
		return 2;
	}
}
