/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.recipe;

import net.minecraft.block.entity.BannerBlockEntity;
import net.minecraft.inventory.CraftingInventory;
import net.minecraft.item.BannerItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.recipe.SpecialCraftingRecipe;
import net.minecraft.util.DyeColor;
import net.minecraft.util.Identifier;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.world.World;

public class BannerDuplicateRecipe
extends SpecialCraftingRecipe {
    public BannerDuplicateRecipe(Identifier identifier) {
        super(identifier);
    }

    @Override
    public boolean matches(CraftingInventory craftingInventory, World world) {
        DyeColor dyeColor = null;
        ItemStack itemStack = null;
        ItemStack itemStack2 = null;
        for (int i = 0; i < craftingInventory.size(); ++i) {
            ItemStack itemStack3 = craftingInventory.getStack(i);
            Item item = itemStack3.getItem();
            if (!(item instanceof BannerItem)) continue;
            BannerItem bannerItem = (BannerItem)item;
            if (dyeColor == null) {
                dyeColor = bannerItem.getColor();
            } else if (dyeColor != bannerItem.getColor()) {
                return false;
            }
            int j = BannerBlockEntity.getPatternCount(itemStack3);
            if (j > 6) {
                return false;
            }
            if (j > 0) {
                if (itemStack == null) {
                    itemStack = itemStack3;
                    continue;
                }
                return false;
            }
            if (itemStack2 == null) {
                itemStack2 = itemStack3;
                continue;
            }
            return false;
        }
        return itemStack != null && itemStack2 != null;
    }

    @Override
    public ItemStack craft(CraftingInventory craftingInventory) {
        for (int i = 0; i < craftingInventory.size(); ++i) {
            int j;
            ItemStack itemStack = craftingInventory.getStack(i);
            if (itemStack.isEmpty() || (j = BannerBlockEntity.getPatternCount(itemStack)) <= 0 || j > 6) continue;
            ItemStack itemStack2 = itemStack.copy();
            itemStack2.setCount(1);
            return itemStack2;
        }
        return ItemStack.EMPTY;
    }

    @Override
    public DefaultedList<ItemStack> getRemainingStacks(CraftingInventory craftingInventory) {
        DefaultedList<ItemStack> defaultedList = DefaultedList.ofSize(craftingInventory.size(), ItemStack.EMPTY);
        for (int i = 0; i < defaultedList.size(); ++i) {
            ItemStack itemStack = craftingInventory.getStack(i);
            if (itemStack.isEmpty()) continue;
            if (itemStack.getItem().hasRecipeRemainder()) {
                defaultedList.set(i, new ItemStack(itemStack.getItem().getRecipeRemainder()));
                continue;
            }
            if (!itemStack.hasTag() || BannerBlockEntity.getPatternCount(itemStack) <= 0) continue;
            ItemStack itemStack2 = itemStack.copy();
            itemStack2.setCount(1);
            defaultedList.set(i, itemStack2);
        }
        return defaultedList;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return RecipeSerializer.BANNER_DUPLICATE;
    }

    @Override
    public boolean fits(int width, int height) {
        return width * height >= 2;
    }
}

