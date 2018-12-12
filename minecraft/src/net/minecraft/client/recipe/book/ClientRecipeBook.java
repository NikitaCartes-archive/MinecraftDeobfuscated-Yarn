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
import net.minecraft.container.BlastFurnaceContainer;
import net.minecraft.container.Container;
import net.minecraft.container.CraftingTableContainer;
import net.minecraft.container.FurnaceContainer;
import net.minecraft.container.PlayerContainer;
import net.minecraft.container.SmokerContainer;
import net.minecraft.item.FoodItem;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.item.block.BlockItem;
import net.minecraft.recipe.Recipe;
import net.minecraft.recipe.RecipeManager;
import net.minecraft.recipe.book.RecipeBook;
import net.minecraft.recipe.smelting.BlastingRecipe;
import net.minecraft.recipe.smelting.SmeltingRecipe;
import net.minecraft.recipe.smelting.SmokingRecipe;

@Environment(EnvType.CLIENT)
public class ClientRecipeBook extends RecipeBook {
	private final RecipeManager manager;
	private final Map<RecipeBookGroup, List<RecipeResultCollection>> resultsByGroup = Maps.<RecipeBookGroup, List<RecipeResultCollection>>newHashMap();
	private final List<RecipeResultCollection> orderedResults = Lists.<RecipeResultCollection>newArrayList();

	public ClientRecipeBook(RecipeManager recipeManager) {
		this.manager = recipeManager;
	}

	public void reload() {
		this.orderedResults.clear();
		this.resultsByGroup.clear();
		Table<RecipeBookGroup, String, RecipeResultCollection> table = HashBasedTable.create();

		for (Recipe recipe : this.manager.values()) {
			if (!recipe.isIgnoredInRecipeBook()) {
				RecipeBookGroup recipeBookGroup = getGroupForRecipe(recipe);
				String string = recipe.getGroup();
				RecipeResultCollection recipeResultCollection;
				if (string.isEmpty()) {
					recipeResultCollection = this.addGroup(recipeBookGroup);
				} else {
					recipeResultCollection = table.get(recipeBookGroup, string);
					if (recipeResultCollection == null) {
						recipeResultCollection = this.addGroup(recipeBookGroup);
						table.put(recipeBookGroup, string, recipeResultCollection);
					}
				}

				recipeResultCollection.addRecipe(recipe);
			}
		}
	}

	private RecipeResultCollection addGroup(RecipeBookGroup recipeBookGroup) {
		RecipeResultCollection recipeResultCollection = new RecipeResultCollection();
		this.orderedResults.add(recipeResultCollection);
		((List)this.resultsByGroup.computeIfAbsent(recipeBookGroup, recipeBookGroupx -> Lists.newArrayList())).add(recipeResultCollection);
		if (recipeBookGroup == RecipeBookGroup.field_1811 || recipeBookGroup == RecipeBookGroup.field_1808 || recipeBookGroup == RecipeBookGroup.field_1812) {
			((List)this.resultsByGroup.computeIfAbsent(RecipeBookGroup.field_1804, recipeBookGroupx -> Lists.newArrayList())).add(recipeResultCollection);
		} else if (recipeBookGroup == RecipeBookGroup.field_17111 || recipeBookGroup == RecipeBookGroup.field_17112) {
			((List)this.resultsByGroup.computeIfAbsent(RecipeBookGroup.field_17110, recipeBookGroupx -> Lists.newArrayList())).add(recipeResultCollection);
		} else if (recipeBookGroup == RecipeBookGroup.field_17114) {
			((List)this.resultsByGroup.computeIfAbsent(RecipeBookGroup.field_17113, recipeBookGroupx -> Lists.newArrayList())).add(recipeResultCollection);
		} else {
			((List)this.resultsByGroup.computeIfAbsent(RecipeBookGroup.field_1809, recipeBookGroupx -> Lists.newArrayList())).add(recipeResultCollection);
		}

		return recipeResultCollection;
	}

	private static RecipeBookGroup getGroupForRecipe(Recipe recipe) {
		if (recipe instanceof SmeltingRecipe) {
			if (recipe.getOutput().getItem() instanceof FoodItem) {
				return RecipeBookGroup.field_1808;
			} else {
				return recipe.getOutput().getItem() instanceof BlockItem ? RecipeBookGroup.field_1811 : RecipeBookGroup.field_1812;
			}
		} else if (recipe instanceof BlastingRecipe) {
			return recipe.getOutput().getItem() instanceof BlockItem ? RecipeBookGroup.field_17111 : RecipeBookGroup.field_17112;
		} else if (recipe instanceof SmokingRecipe) {
			return RecipeBookGroup.field_17114;
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

	public static List<RecipeBookGroup> getGroupsForContainer(Container container) {
		if (container instanceof CraftingTableContainer || container instanceof PlayerContainer) {
			return Lists.<RecipeBookGroup>newArrayList(
				RecipeBookGroup.field_1809, RecipeBookGroup.field_1813, RecipeBookGroup.field_1806, RecipeBookGroup.field_1810, RecipeBookGroup.field_1803
			);
		} else if (container instanceof FurnaceContainer) {
			return Lists.<RecipeBookGroup>newArrayList(RecipeBookGroup.field_1804, RecipeBookGroup.field_1808, RecipeBookGroup.field_1811, RecipeBookGroup.field_1812);
		} else if (container instanceof BlastFurnaceContainer) {
			return Lists.<RecipeBookGroup>newArrayList(RecipeBookGroup.field_17110, RecipeBookGroup.field_17111, RecipeBookGroup.field_17112);
		} else {
			return container instanceof SmokerContainer
				? Lists.<RecipeBookGroup>newArrayList(RecipeBookGroup.field_17113, RecipeBookGroup.field_17114)
				: Lists.<RecipeBookGroup>newArrayList();
		}
	}

	public List<RecipeResultCollection> getOrderedResults() {
		return this.orderedResults;
	}

	public List<RecipeResultCollection> getResultsForGroup(RecipeBookGroup recipeBookGroup) {
		return (List<RecipeResultCollection>)this.resultsByGroup.getOrDefault(recipeBookGroup, Collections.emptyList());
	}
}
