package net.minecraft;

import net.minecraft.enchantment.DamageEnchantment;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentTarget;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;

public class class_5509 extends Enchantment {
	public class_5509(Enchantment.Rarity rarity, EquipmentSlot... equipmentSlots) {
		super(rarity, EnchantmentTarget.field_26774, equipmentSlots);
	}

	@Override
	public int getMinPower(int level) {
		return 5 + (level - 1) * 20;
	}

	@Override
	public int getMaxPower(int level) {
		return this.getMinPower(level) + 20;
	}

	@Override
	public int getMaxLevel() {
		return 3;
	}

	@Override
	public float getAttackDamage(int level, LivingEntity livingEntity) {
		return (float)level;
	}

	@Override
	public boolean canAccept(Enchantment other) {
		return !(other instanceof DamageEnchantment) && super.canAccept(other);
	}
}
