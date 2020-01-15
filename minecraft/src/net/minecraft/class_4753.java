package net.minecraft;

import net.minecraft.enchantment.DamageEnchantment;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentTarget;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;

public class class_4753 extends Enchantment {
	public class_4753(Enchantment.Weight weight, EquipmentSlot... equipmentSlots) {
		super(weight, EnchantmentTarget.AXE, equipmentSlots);
	}

	@Override
	public int getMinimumPower(int level) {
		return 5 + (level - 1) * 20;
	}

	@Override
	public int getMaximumPower(int level) {
		return this.getMinimumPower(level) + 20;
	}

	@Override
	public int getMaximumLevel() {
		return 3;
	}

	@Override
	public float getAttackDamage(int level, LivingEntity livingEntity) {
		return (float)level;
	}

	@Override
	public boolean differs(Enchantment other) {
		return !(other instanceof DamageEnchantment) && super.differs(other);
	}
}
