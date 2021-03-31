package net.minecraft.item;

import net.minecraft.entity.EquipmentSlot;
import net.minecraft.recipe.Ingredient;
import net.minecraft.sound.SoundEvent;

public interface ArmorMaterial {
	int getDurability(EquipmentSlot slot);

	int getProtectionAmount(EquipmentSlot slot);

	int getEnchantability();

	SoundEvent getEquipSound();

	Ingredient getRepairIngredient();

	String getName();

	float getToughness();

	float getKnockbackResistance();
}
