/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.recipe;

import com.google.common.collect.Lists;
import java.util.ArrayList;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.inventory.CraftingInventory;
import net.minecraft.item.DyeItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.recipe.SpecialCraftingRecipe;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;

public class FireworkStarFadeRecipe
extends SpecialCraftingRecipe {
    private static final Ingredient field_9015 = Ingredient.ofItems(Items.FIREWORK_STAR);

    public FireworkStarFadeRecipe(Identifier identifier) {
        super(identifier);
    }

    public boolean method_17711(CraftingInventory craftingInventory, World world) {
        boolean bl = false;
        boolean bl2 = false;
        for (int i = 0; i < craftingInventory.getInvSize(); ++i) {
            ItemStack itemStack = craftingInventory.getInvStack(i);
            if (itemStack.isEmpty()) continue;
            if (itemStack.getItem() instanceof DyeItem) {
                bl = true;
                continue;
            }
            if (field_9015.method_8093(itemStack)) {
                if (bl2) {
                    return false;
                }
                bl2 = true;
                continue;
            }
            return false;
        }
        return bl2 && bl;
    }

    public ItemStack method_17710(CraftingInventory craftingInventory) {
        ArrayList<Integer> list = Lists.newArrayList();
        ItemStack itemStack = null;
        for (int i = 0; i < craftingInventory.getInvSize(); ++i) {
            ItemStack itemStack2 = craftingInventory.getInvStack(i);
            Item item = itemStack2.getItem();
            if (item instanceof DyeItem) {
                list.add(((DyeItem)item).getColor().getFireworkColor());
                continue;
            }
            if (!field_9015.method_8093(itemStack2)) continue;
            itemStack = itemStack2.copy();
            itemStack.setCount(1);
        }
        if (itemStack == null || list.isEmpty()) {
            return ItemStack.EMPTY;
        }
        itemStack.getOrCreateSubTag("Explosion").putIntArray("FadeColors", list);
        return itemStack;
    }

    @Override
    @Environment(value=EnvType.CLIENT)
    public boolean fits(int i, int j) {
        return i * j >= 2;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return RecipeSerializer.FIREWORK_STAR_FADE;
    }
}

