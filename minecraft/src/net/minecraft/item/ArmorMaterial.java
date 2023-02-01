package net.minecraft.item;

import net.minecraft.recipe.Ingredient;
import net.minecraft.sound.SoundEvent;

public interface ArmorMaterial {
	int getDurability(ArmorItem.Type type);

	int getProtection(ArmorItem.Type type);

	int getEnchantability();

	SoundEvent getEquipSound();

	Ingredient getRepairIngredient();

	String getName();

	float getToughness();

	float getKnockbackResistance();
}
