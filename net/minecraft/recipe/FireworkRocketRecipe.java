/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.recipe;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.inventory.CraftingInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.recipe.SpecialCraftingRecipe;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;

public class FireworkRocketRecipe
extends SpecialCraftingRecipe {
    private static final Ingredient PAPER = Ingredient.ofItems(Items.PAPER);
    private static final Ingredient DURATION_MODIFIER = Ingredient.ofItems(Items.GUNPOWDER);
    private static final Ingredient FIREWORK_STAR = Ingredient.ofItems(Items.FIREWORK_STAR);

    public FireworkRocketRecipe(Identifier identifier) {
        super(identifier);
    }

    @Override
    public boolean matches(CraftingInventory craftingInventory, World world) {
        boolean bl = false;
        int i = 0;
        for (int j = 0; j < craftingInventory.size(); ++j) {
            ItemStack itemStack = craftingInventory.getStack(j);
            if (itemStack.isEmpty()) continue;
            if (PAPER.test(itemStack)) {
                if (bl) {
                    return false;
                }
                bl = true;
                continue;
            }
            if (!(DURATION_MODIFIER.test(itemStack) ? ++i > 3 : !FIREWORK_STAR.test(itemStack))) continue;
            return false;
        }
        return bl && i >= 1;
    }

    @Override
    public ItemStack craft(CraftingInventory craftingInventory) {
        ItemStack itemStack = new ItemStack(Items.FIREWORK_ROCKET, 3);
        NbtCompound nbtCompound = itemStack.getOrCreateSubTag("Fireworks");
        NbtList nbtList = new NbtList();
        int i = 0;
        for (int j = 0; j < craftingInventory.size(); ++j) {
            NbtCompound nbtCompound2;
            ItemStack itemStack2 = craftingInventory.getStack(j);
            if (itemStack2.isEmpty()) continue;
            if (DURATION_MODIFIER.test(itemStack2)) {
                ++i;
                continue;
            }
            if (!FIREWORK_STAR.test(itemStack2) || (nbtCompound2 = itemStack2.getSubTag("Explosion")) == null) continue;
            nbtList.add(nbtCompound2);
        }
        nbtCompound.putByte("Flight", (byte)i);
        if (!nbtList.isEmpty()) {
            nbtCompound.put("Explosions", nbtList);
        }
        return itemStack;
    }

    @Override
    @Environment(value=EnvType.CLIENT)
    public boolean fits(int width, int height) {
        return width * height >= 2;
    }

    @Override
    public ItemStack getOutput() {
        return new ItemStack(Items.FIREWORK_ROCKET);
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return RecipeSerializer.FIREWORK_ROCKET;
    }
}

