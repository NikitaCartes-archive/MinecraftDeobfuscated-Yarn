package net.minecraft.enchantment;

import net.minecraft.entity.EntityGroup;
import net.minecraft.entity.EquipmentSlot;

public class ChoppingEnchantment extends Enchantment {
	public ChoppingEnchantment(Enchantment.Weight weight, EquipmentSlot... equipmentSlots) {
		super(weight, EnchantmentTarget.field_20355, equipmentSlots);
	}

	@Override
	public int getMinimumPower(int i) {
		return 5 + (i - 1) * 20;
	}

	@Override
	public int getMaximumPower(int i) {
		return this.getMinimumPower(i) + 20;
	}

	@Override
	public int getMaximumLevel() {
		return 3;
	}

	@Override
	public float getAttackDamage(int i, EntityGroup entityGroup) {
		return (float)i;
	}

	@Override
	public boolean differs(Enchantment enchantment) {
		return !(enchantment instanceof DamageEnchantment) && super.differs(enchantment);
	}
}
