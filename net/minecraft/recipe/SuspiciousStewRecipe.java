/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.recipe;

import net.minecraft.block.Blocks;
import net.minecraft.block.SuspiciousStewIngredient;
import net.minecraft.inventory.CraftingInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.SuspiciousStewItem;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.recipe.SpecialCraftingRecipe;
import net.minecraft.recipe.book.CraftingRecipeCategory;
import net.minecraft.registry.DynamicRegistryManager;
import net.minecraft.registry.tag.ItemTags;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;

public class SuspiciousStewRecipe
extends SpecialCraftingRecipe {
    public SuspiciousStewRecipe(Identifier identifier, CraftingRecipeCategory craftingRecipeCategory) {
        super(identifier, craftingRecipeCategory);
    }

    @Override
    public boolean matches(CraftingInventory craftingInventory, World world) {
        boolean bl = false;
        boolean bl2 = false;
        boolean bl3 = false;
        boolean bl4 = false;
        for (int i = 0; i < craftingInventory.size(); ++i) {
            ItemStack itemStack = craftingInventory.getStack(i);
            if (itemStack.isEmpty()) continue;
            if (itemStack.isOf(Blocks.BROWN_MUSHROOM.asItem()) && !bl3) {
                bl3 = true;
                continue;
            }
            if (itemStack.isOf(Blocks.RED_MUSHROOM.asItem()) && !bl2) {
                bl2 = true;
                continue;
            }
            if (itemStack.isIn(ItemTags.SMALL_FLOWERS) && !bl) {
                bl = true;
                continue;
            }
            if (itemStack.isOf(Items.BOWL) && !bl4) {
                bl4 = true;
                continue;
            }
            return false;
        }
        return bl && bl3 && bl2 && bl4;
    }

    @Override
    public ItemStack craft(CraftingInventory craftingInventory, DynamicRegistryManager dynamicRegistryManager) {
        ItemStack itemStack = new ItemStack(Items.SUSPICIOUS_STEW, 1);
        for (int i = 0; i < craftingInventory.size(); ++i) {
            SuspiciousStewIngredient suspiciousStewIngredient;
            ItemStack itemStack2 = craftingInventory.getStack(i);
            if (itemStack2.isEmpty() || (suspiciousStewIngredient = SuspiciousStewIngredient.of(itemStack2.getItem())) == null) continue;
            SuspiciousStewItem.addEffectToStew(itemStack, suspiciousStewIngredient.getEffectInStew(), suspiciousStewIngredient.getEffectInStewDuration());
            break;
        }
        return itemStack;
    }

    @Override
    public boolean fits(int width, int height) {
        return width >= 2 && height >= 2;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return RecipeSerializer.SUSPICIOUS_STEW;
    }
}

