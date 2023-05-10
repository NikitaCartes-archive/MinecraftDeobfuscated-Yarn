package net.minecraft.recipe;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import java.util.List;
import java.util.Map;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.inventory.RecipeInputInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.book.CraftingRecipeCategory;
import net.minecraft.registry.DynamicRegistryManager;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;

public class RepairItemRecipe extends SpecialCraftingRecipe {
	public RepairItemRecipe(Identifier identifier, CraftingRecipeCategory craftingRecipeCategory) {
		super(identifier, craftingRecipeCategory);
	}

	public boolean matches(RecipeInputInventory recipeInputInventory, World world) {
		List<ItemStack> list = Lists.<ItemStack>newArrayList();

		for (int i = 0; i < recipeInputInventory.size(); i++) {
			ItemStack itemStack = recipeInputInventory.getStack(i);
			if (!itemStack.isEmpty()) {
				list.add(itemStack);
				if (list.size() > 1) {
					ItemStack itemStack2 = (ItemStack)list.get(0);
					if (!itemStack.isOf(itemStack2.getItem()) || itemStack2.getCount() != 1 || itemStack.getCount() != 1 || !itemStack2.getItem().isDamageable()) {
						return false;
					}
				}
			}
		}

		return list.size() == 2;
	}

	public ItemStack craft(RecipeInputInventory recipeInputInventory, DynamicRegistryManager dynamicRegistryManager) {
		List<ItemStack> list = Lists.<ItemStack>newArrayList();

		for (int i = 0; i < recipeInputInventory.size(); i++) {
			ItemStack itemStack = recipeInputInventory.getStack(i);
			if (!itemStack.isEmpty()) {
				list.add(itemStack);
				if (list.size() > 1) {
					ItemStack itemStack2 = (ItemStack)list.get(0);
					if (!itemStack.isOf(itemStack2.getItem()) || itemStack2.getCount() != 1 || itemStack.getCount() != 1 || !itemStack2.getItem().isDamageable()) {
						return ItemStack.EMPTY;
					}
				}
			}
		}

		if (list.size() == 2) {
			ItemStack itemStack3 = (ItemStack)list.get(0);
			ItemStack itemStack = (ItemStack)list.get(1);
			if (itemStack3.isOf(itemStack.getItem()) && itemStack3.getCount() == 1 && itemStack.getCount() == 1 && itemStack3.getItem().isDamageable()) {
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
				Map<Enchantment, Integer> map = Maps.<Enchantment, Integer>newHashMap();
				Map<Enchantment, Integer> map2 = EnchantmentHelper.get(itemStack3);
				Map<Enchantment, Integer> map3 = EnchantmentHelper.get(itemStack);
				Registries.ENCHANTMENT.stream().filter(Enchantment::isCursed).forEach(enchantment -> {
					int ix = Math.max((Integer)map2.getOrDefault(enchantment, 0), (Integer)map3.getOrDefault(enchantment, 0));
					if (ix > 0) {
						map.put(enchantment, ix);
					}
				});
				if (!map.isEmpty()) {
					EnchantmentHelper.set(map, itemStack4);
				}

				return itemStack4;
			}
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
