package net.minecraft.item;

import net.minecraft.recipe.Ingredient;

public interface ToolMaterial {
	int getDurability();

	float getMiningSpeed();

	float getAttackDamage();

	int getMiningLevel();

	int getEnchantability();

	Ingredient getRepairIngredient();
}
