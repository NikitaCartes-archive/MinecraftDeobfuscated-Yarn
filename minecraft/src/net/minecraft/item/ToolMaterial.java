package net.minecraft.item;

import net.minecraft.recipe.Ingredient;

public interface ToolMaterial {
	int getDurability();

	float getBlockBreakingSpeed();

	float getAttackDamage();

	int getMiningLevel();

	int getEnchantability();

	Ingredient getRepairIngredient();
}
