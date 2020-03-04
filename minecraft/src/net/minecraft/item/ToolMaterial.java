package net.minecraft.item;

import net.minecraft.recipe.Ingredient;

public interface ToolMaterial {
	int getDurability();

	float getMiningSpeedMultiplier();

	float getAttackDamage();

	int getMiningLevel();

	int getEnchantability();

	Ingredient getRepairIngredient();
}
