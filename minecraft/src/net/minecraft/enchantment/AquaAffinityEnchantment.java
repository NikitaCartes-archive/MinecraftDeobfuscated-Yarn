package net.minecraft.enchantment;

import net.minecraft.entity.EquipmentSlot;

public class AquaAffinityEnchantment extends Enchantment {
	public AquaAffinityEnchantment(Enchantment.Weight weight, EquipmentSlot... equipmentSlots) {
		super(weight, EnchantmentTarget.field_9080, equipmentSlots);
	}

	@Override
	public int getMinimumPower(int i) {
		return 1;
	}

	@Override
	public int getMaximumPower(int i) {
		return this.getMinimumPower(i) + 40;
	}

	@Override
	public int getMaximumLevel() {
		return 1;
	}
}
