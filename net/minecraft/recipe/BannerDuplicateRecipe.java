/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.recipe;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.entity.BannerBlockEntity;
import net.minecraft.inventory.CraftingInventory;
import net.minecraft.item.BannerItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.recipe.SpecialCraftingRecipe;
import net.minecraft.util.DefaultedList;
import net.minecraft.util.DyeColor;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;

public class BannerDuplicateRecipe
extends SpecialCraftingRecipe {
    public BannerDuplicateRecipe(Identifier identifier) {
        super(identifier);
    }

    public boolean method_17703(CraftingInventory craftingInventory, World world) {
        DyeColor dyeColor = null;
        ItemStack itemStack = null;
        ItemStack itemStack2 = null;
        for (int i = 0; i < craftingInventory.getInvSize(); ++i) {
            ItemStack itemStack3 = craftingInventory.getInvStack(i);
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

    public ItemStack method_17702(CraftingInventory craftingInventory) {
        for (int i = 0; i < craftingInventory.getInvSize(); ++i) {
            int j;
            ItemStack itemStack = craftingInventory.getInvStack(i);
            if (itemStack.isEmpty() || (j = BannerBlockEntity.getPatternCount(itemStack)) <= 0 || j > 6) continue;
            ItemStack itemStack2 = itemStack.copy();
            itemStack2.setAmount(1);
            return itemStack2;
        }
        return ItemStack.EMPTY;
    }

    public DefaultedList<ItemStack> method_17704(CraftingInventory craftingInventory) {
        DefaultedList<ItemStack> defaultedList = DefaultedList.create(craftingInventory.getInvSize(), ItemStack.EMPTY);
        for (int i = 0; i < defaultedList.size(); ++i) {
            ItemStack itemStack = craftingInventory.getInvStack(i);
            if (itemStack.isEmpty()) continue;
            if (itemStack.getItem().hasRecipeRemainder()) {
                defaultedList.set(i, new ItemStack(itemStack.getItem().getRecipeRemainder()));
                continue;
            }
            if (!itemStack.hasTag() || BannerBlockEntity.getPatternCount(itemStack) <= 0) continue;
            ItemStack itemStack2 = itemStack.copy();
            itemStack2.setAmount(1);
            defaultedList.set(i, itemStack2);
        }
        return defaultedList;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return RecipeSerializer.BANNER_DUPLICATE;
    }

    @Override
    @Environment(value=EnvType.CLIENT)
    public boolean fits(int i, int j) {
        return i * j >= 2;
    }
}

