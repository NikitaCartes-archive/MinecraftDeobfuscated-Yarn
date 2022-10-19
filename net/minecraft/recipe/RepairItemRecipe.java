/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.recipe;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.inventory.CraftingInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.recipe.SpecialCraftingRecipe;
import net.minecraft.recipe.book.CraftingRecipeCategory;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;

public class RepairItemRecipe
extends SpecialCraftingRecipe {
    public RepairItemRecipe(Identifier identifier, CraftingRecipeCategory craftingRecipeCategory) {
        super(identifier, craftingRecipeCategory);
    }

    @Override
    public boolean matches(CraftingInventory craftingInventory, World world) {
        ArrayList<ItemStack> list = Lists.newArrayList();
        for (int i = 0; i < craftingInventory.size(); ++i) {
            ItemStack itemStack2;
            ItemStack itemStack = craftingInventory.getStack(i);
            if (itemStack.isEmpty()) continue;
            list.add(itemStack);
            if (list.size() <= 1 || itemStack.isOf((itemStack2 = (ItemStack)list.get(0)).getItem()) && itemStack2.getCount() == 1 && itemStack.getCount() == 1 && itemStack2.getItem().isDamageable()) continue;
            return false;
        }
        return list.size() == 2;
    }

    @Override
    public ItemStack craft(CraftingInventory craftingInventory) {
        ItemStack itemStack3;
        ItemStack itemStack;
        ArrayList<ItemStack> list = Lists.newArrayList();
        for (int i = 0; i < craftingInventory.size(); ++i) {
            ItemStack itemStack2;
            itemStack = craftingInventory.getStack(i);
            if (itemStack.isEmpty()) continue;
            list.add(itemStack);
            if (list.size() <= 1 || itemStack.isOf((itemStack2 = (ItemStack)list.get(0)).getItem()) && itemStack2.getCount() == 1 && itemStack.getCount() == 1 && itemStack2.getItem().isDamageable()) continue;
            return ItemStack.EMPTY;
        }
        if (list.size() == 2 && (itemStack3 = (ItemStack)list.get(0)).isOf((itemStack = (ItemStack)list.get(1)).getItem()) && itemStack3.getCount() == 1 && itemStack.getCount() == 1 && itemStack3.getItem().isDamageable()) {
            Item item = itemStack3.getItem();
            int j = item.getMaxDamage() - itemStack3.getDamage();
            int k = item.getMaxDamage() - itemStack.getDamage();
            int l = j + k + item.getMaxDamage() * 5 / 100;
            int m = item.getMaxDamage() - l;
            if (m < 0) {
                m = 0;
            }
            ItemStack itemStack4 = new ItemStack(itemStack3.getItem());
            itemStack4.setDamage(m);
            HashMap<Enchantment, Integer> map = Maps.newHashMap();
            Map<Enchantment, Integer> map2 = EnchantmentHelper.get(itemStack3);
            Map<Enchantment, Integer> map3 = EnchantmentHelper.get(itemStack);
            Registry.ENCHANTMENT.stream().filter(Enchantment::isCursed).forEach(enchantment -> {
                int i = Math.max(map2.getOrDefault(enchantment, 0), map3.getOrDefault(enchantment, 0));
                if (i > 0) {
                    map.put((Enchantment)enchantment, i);
                }
            });
            if (!map.isEmpty()) {
                EnchantmentHelper.set(map, itemStack4);
            }
            return itemStack4;
        }
        return ItemStack.EMPTY;
    }

    @Override
    public boolean fits(int width, int height) {
        return width * height >= 2;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return RecipeSerializer.REPAIR_ITEM;
    }
}

