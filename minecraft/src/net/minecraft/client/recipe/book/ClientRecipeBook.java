package net.minecraft.client.recipe.book;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Table;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_516;
import net.minecraft.container.Container;
import net.minecraft.container.CraftingTableContainer;
import net.minecraft.container.FurnaceContainer;
import net.minecraft.container.PlayerContainer;
import net.minecraft.item.FoodItem;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.item.block.BlockItem;
import net.minecraft.recipe.Recipe;
import net.minecraft.recipe.RecipeManager;
import net.minecraft.recipe.book.RecipeBook;
import net.minecraft.recipe.smelting.SmeltingRecipe;

@Environment(EnvType.CLIENT)
public class ClientRecipeBook extends RecipeBook {
	private final RecipeManager manager;
	private final Map<RecipeBookGroup, List<class_516>> field_1638 = Maps.<RecipeBookGroup, List<class_516>>newHashMap();
	private final List<class_516> field_1637 = Lists.<class_516>newArrayList();

	public ClientRecipeBook(RecipeManager recipeManager) {
		this.manager = recipeManager;
	}

	public void method_1401() {
		this.field_1637.clear();
		this.field_1638.clear();
		Table<RecipeBookGroup, String, class_516> table = HashBasedTable.create();

		for (Recipe recipe : this.manager.values()) {
			if (!recipe.isIgnoredInRecipeBook()) {
				RecipeBookGroup recipeBookGroup = method_1400(recipe);
				String string = recipe.getGroup();
				class_516 lv;
				if (string.isEmpty()) {
					lv = this.method_1394(recipeBookGroup);
				} else {
					lv = table.get(recipeBookGroup, string);
					if (lv == null) {
						lv = this.method_1394(recipeBookGroup);
						table.put(recipeBookGroup, string, lv);
					}
				}

				lv.method_2654(recipe);
			}
		}
	}

	private class_516 method_1394(RecipeBookGroup recipeBookGroup) {
		class_516 lv = new class_516();
		this.field_1637.add(lv);
		((List)this.field_1638.computeIfAbsent(recipeBookGroup, recipeBookGroupx -> Lists.newArrayList())).add(lv);
		if (recipeBookGroup != RecipeBookGroup.field_1811 && recipeBookGroup != RecipeBookGroup.field_1808 && recipeBookGroup != RecipeBookGroup.field_1812) {
			((List)this.field_1638.computeIfAbsent(RecipeBookGroup.field_1809, recipeBookGroupx -> Lists.newArrayList())).add(lv);
		} else {
			((List)this.field_1638.computeIfAbsent(RecipeBookGroup.field_1804, recipeBookGroupx -> Lists.newArrayList())).add(lv);
		}

		return lv;
	}

	private static RecipeBookGroup method_1400(Recipe recipe) {
		if (recipe instanceof SmeltingRecipe) {
			if (recipe.getOutput().getItem() instanceof FoodItem) {
				return RecipeBookGroup.field_1808;
			} else {
				return recipe.getOutput().getItem() instanceof BlockItem ? RecipeBookGroup.field_1811 : RecipeBookGroup.field_1812;
			}
		} else {
			ItemStack itemStack = recipe.getOutput();
			ItemGroup itemGroup = itemStack.getItem().getItemGroup();
			if (itemGroup == ItemGroup.BUILDING_BLOCKS) {
				return RecipeBookGroup.field_1806;
			} else if (itemGroup == ItemGroup.TOOLS || itemGroup == ItemGroup.COMBAT) {
				return RecipeBookGroup.field_1813;
			} else {
				return itemGroup == ItemGroup.REDSTONE ? RecipeBookGroup.field_1803 : RecipeBookGroup.field_1810;
			}
		}
	}

	public static List<RecipeBookGroup> method_1395(Container container) {
		if (container instanceof CraftingTableContainer || container instanceof PlayerContainer) {
			return Lists.<RecipeBookGroup>newArrayList(
				RecipeBookGroup.field_1809, RecipeBookGroup.field_1813, RecipeBookGroup.field_1806, RecipeBookGroup.field_1810, RecipeBookGroup.field_1803
			);
		} else {
			return container instanceof FurnaceContainer
				? Lists.<RecipeBookGroup>newArrayList(RecipeBookGroup.field_1804, RecipeBookGroup.field_1808, RecipeBookGroup.field_1811, RecipeBookGroup.field_1812)
				: Lists.<RecipeBookGroup>newArrayList();
		}
	}

	public List<class_516> method_1393() {
		return this.field_1637;
	}

	public List<class_516> method_1396(RecipeBookGroup recipeBookGroup) {
		return (List<class_516>)this.field_1638.getOrDefault(recipeBookGroup, Collections.emptyList());
	}
}
