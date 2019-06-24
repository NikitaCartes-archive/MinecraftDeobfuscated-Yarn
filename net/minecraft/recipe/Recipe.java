/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.recipe;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.Blocks;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.recipe.RecipeType;
import net.minecraft.util.DefaultedList;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;

public interface Recipe<C extends Inventory> {
    public boolean matches(C var1, World var2);

    public ItemStack craft(C var1);

    @Environment(value=EnvType.CLIENT)
    public boolean fits(int var1, int var2);

    public ItemStack getOutput();

    default public DefaultedList<ItemStack> getRemainingStacks(C inventory) {
        DefaultedList<ItemStack> defaultedList = DefaultedList.ofSize(inventory.getInvSize(), ItemStack.EMPTY);
        for (int i = 0; i < defaultedList.size(); ++i) {
            Item item = inventory.getInvStack(i).getItem();
            if (!item.hasRecipeRemainder()) continue;
            defaultedList.set(i, new ItemStack(item.getRecipeRemainder()));
        }
        return defaultedList;
    }

    default public DefaultedList<Ingredient> getPreviewInputs() {
        return DefaultedList.of();
    }

    default public boolean isIgnoredInRecipeBook() {
        return false;
    }

    @Environment(value=EnvType.CLIENT)
    default public String getGroup() {
        return "";
    }

    @Environment(value=EnvType.CLIENT)
    default public ItemStack getRecipeKindIcon() {
        return new ItemStack(Blocks.CRAFTING_TABLE);
    }

    public Identifier getId();

    public RecipeSerializer<?> getSerializer();

    public RecipeType<?> getType();
}

