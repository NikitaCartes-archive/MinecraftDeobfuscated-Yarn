/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.recipe;

import net.minecraft.inventory.CraftingInventory;
import net.minecraft.item.FilledMapItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.map.MapState;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.recipe.ShapedRecipe;
import net.minecraft.recipe.book.CraftingRecipeCategory;
import net.minecraft.registry.DynamicRegistryManager;
import net.minecraft.util.Identifier;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.world.World;

public class MapExtendingRecipe
extends ShapedRecipe {
    public MapExtendingRecipe(Identifier id, CraftingRecipeCategory category) {
        super(id, "", category, 3, 3, DefaultedList.copyOf(Ingredient.EMPTY, Ingredient.ofItems(Items.PAPER), Ingredient.ofItems(Items.PAPER), Ingredient.ofItems(Items.PAPER), Ingredient.ofItems(Items.PAPER), Ingredient.ofItems(Items.FILLED_MAP), Ingredient.ofItems(Items.PAPER), Ingredient.ofItems(Items.PAPER), Ingredient.ofItems(Items.PAPER), Ingredient.ofItems(Items.PAPER)), new ItemStack(Items.MAP));
    }

    @Override
    public boolean matches(CraftingInventory craftingInventory, World world) {
        if (!super.matches(craftingInventory, world)) {
            return false;
        }
        ItemStack itemStack = ItemStack.EMPTY;
        for (int i = 0; i < craftingInventory.size() && itemStack.isEmpty(); ++i) {
            ItemStack itemStack2 = craftingInventory.getStack(i);
            if (!itemStack2.isOf(Items.FILLED_MAP)) continue;
            itemStack = itemStack2;
        }
        if (itemStack.isEmpty()) {
            return false;
        }
        MapState mapState = FilledMapItem.getMapState(itemStack, world);
        if (mapState == null) {
            return false;
        }
        if (mapState.hasMonumentIcon()) {
            return false;
        }
        return mapState.scale < 4;
    }

    @Override
    public ItemStack craft(CraftingInventory craftingInventory, DynamicRegistryManager dynamicRegistryManager) {
        ItemStack itemStack = ItemStack.EMPTY;
        for (int i = 0; i < craftingInventory.size() && itemStack.isEmpty(); ++i) {
            ItemStack itemStack2 = craftingInventory.getStack(i);
            if (!itemStack2.isOf(Items.FILLED_MAP)) continue;
            itemStack = itemStack2;
        }
        itemStack = itemStack.copy();
        itemStack.setCount(1);
        itemStack.getOrCreateNbt().putInt("map_scale_direction", 1);
        return itemStack;
    }

    @Override
    public boolean isIgnoredInRecipeBook() {
        return true;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return RecipeSerializer.MAP_EXTENDING;
    }
}

