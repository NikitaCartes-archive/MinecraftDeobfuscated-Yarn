/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.item;

import net.minecraft.recipe.Ingredient;

public interface ToolMaterial {
    public int getDurability();

    public float getMiningSpeed();

    public float getAttackDamage();

    public int getMiningLevel();

    public int getEnchantability();

    public Ingredient getRepairIngredient();
}

