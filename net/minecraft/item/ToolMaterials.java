/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.item;

import java.util.function.Supplier;
import net.minecraft.block.Blocks;
import net.minecraft.item.Items;
import net.minecraft.item.ToolMaterial;
import net.minecraft.recipe.Ingredient;
import net.minecraft.tag.ItemTags;
import net.minecraft.util.Lazy;

public enum ToolMaterials implements ToolMaterial
{
    WOOD(0, 59, 2.0f, 0.0f, 15, () -> Ingredient.fromTag(ItemTags.PLANKS)),
    STONE(1, 131, 4.0f, 1.0f, 5, () -> Ingredient.ofItems(Blocks.COBBLESTONE)),
    IRON(2, 250, 6.0f, 2.0f, 14, () -> Ingredient.ofItems(Items.IRON_INGOT)),
    DIAMOND(3, 1561, 8.0f, 3.0f, 10, () -> Ingredient.ofItems(Items.DIAMOND)),
    GOLD(0, 32, 12.0f, 0.0f, 22, () -> Ingredient.ofItems(Items.GOLD_INGOT));

    private final int miningLevel;
    private final int durability;
    private final float blockBreakSpeed;
    private final float attackDamage;
    private final int enchantability;
    private final Lazy<Ingredient> repairIngredient;

    private ToolMaterials(int j, int k, float f, float g, int l, Supplier<Ingredient> supplier) {
        this.miningLevel = j;
        this.durability = k;
        this.blockBreakSpeed = f;
        this.attackDamage = g;
        this.enchantability = l;
        this.repairIngredient = new Lazy<Ingredient>(supplier);
    }

    @Override
    public int getDurability() {
        return this.durability;
    }

    @Override
    public float getBlockBreakingSpeed() {
        return this.blockBreakSpeed;
    }

    @Override
    public float getAttackDamage() {
        return this.attackDamage;
    }

    @Override
    public int getMiningLevel() {
        return this.miningLevel;
    }

    @Override
    public int getEnchantability() {
        return this.enchantability;
    }

    @Override
    public Ingredient getRepairIngredient() {
        return this.repairIngredient.get();
    }
}

