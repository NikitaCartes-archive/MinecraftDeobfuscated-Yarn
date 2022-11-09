package net.minecraft.client.recipebook;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Table;
import com.google.common.collect.ImmutableList.Builder;
import com.mojang.logging.LogUtils;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.screen.recipebook.RecipeResultCollection;
import net.minecraft.recipe.AbstractCookingRecipe;
import net.minecraft.recipe.CraftingRecipe;
import net.minecraft.recipe.Recipe;
import net.minecraft.recipe.RecipeType;
import net.minecraft.recipe.book.CookingRecipeCategory;
import net.minecraft.recipe.book.RecipeBook;
import net.minecraft.registry.Registries;
import org.slf4j.Logger;

@Environment(EnvType.CLIENT)
public class ClientRecipeBook extends RecipeBook {
	private static final Logger LOGGER = LogUtils.getLogger();
	private Map<RecipeBookGroup, List<RecipeResultCollection>> resultsByGroup = ImmutableMap.of();
	private List<RecipeResultCollection> orderedResults = ImmutableList.of();

	public void reload(Iterable<Recipe<?>> recipes) {
		Map<RecipeBookGroup, List<List<Recipe<?>>>> map = toGroupedMap(recipes);
		Map<RecipeBookGroup, List<RecipeResultCollection>> map2 = Maps.<RecipeBookGroup, List<RecipeResultCollection>>newHashMap();
		Builder<RecipeResultCollection> builder = ImmutableList.builder();
		map.forEach(
			(group, recipesx) -> map2.put(group, (List)recipesx.stream().map(RecipeResultCollection::new).peek(builder::add).collect(ImmutableList.toImmutableList()))
		);
		RecipeBookGroup.SEARCH_MAP
			.forEach(
				(group, searchGroups) -> map2.put(
						group,
						(List)searchGroups.stream()
							.flatMap(searchGroup -> ((List)map2.getOrDefault(searchGroup, ImmutableList.of())).stream())
							.collect(ImmutableList.toImmutableList())
					)
			);
		this.resultsByGroup = ImmutableMap.copyOf(map2);
		this.orderedResults = builder.build();
	}

	private static Map<RecipeBookGroup, List<List<Recipe<?>>>> toGroupedMap(Iterable<Recipe<?>> recipes) {
		Map<RecipeBookGroup, List<List<Recipe<?>>>> map = Maps.<RecipeBookGroup, List<List<Recipe<?>>>>newHashMap();
		Table<RecipeBookGroup, String, List<Recipe<?>>> table = HashBasedTable.create();

		for (Recipe<?> recipe : recipes) {
			if (!recipe.isIgnoredInRecipeBook() && !recipe.isEmpty()) {
				RecipeBookGroup recipeBookGroup = getGroupForRecipe(recipe);
				String string = recipe.getGroup();
				if (string.isEmpty()) {
					((List)map.computeIfAbsent(recipeBookGroup, group -> Lists.newArrayList())).add(ImmutableList.of(recipe));
				} else {
					List<Recipe<?>> list = table.get(recipeBookGroup, string);
					if (list == null) {
						list = Lists.<Recipe<?>>newArrayList();
						table.put(recipeBookGroup, string, list);
						((List)map.computeIfAbsent(recipeBookGroup, group -> Lists.newArrayList())).add(list);
					}

					list.add(recipe);
				}
			}
		}

		return map;
	}

	private static RecipeBookGroup getGroupForRecipe(Recipe<?> recipe) {
		if (recipe instanceof CraftingRecipe craftingRecipe) {
			return switch (craftingRecipe.getCategory()) {
				case BUILDING -> RecipeBookGroup.CRAFTING_BUILDING_BLOCKS;
				case EQUIPMENT -> RecipeBookGroup.CRAFTING_EQUIPMENT;
				case REDSTONE -> RecipeBookGroup.CRAFTING_REDSTONE;
				case MISC -> RecipeBookGroup.CRAFTING_MISC;
			};
		} else {
			RecipeType<?> recipeType = recipe.getType();
			if (recipe instanceof AbstractCookingRecipe abstractCookingRecipe) {
				CookingRecipeCategory cookingRecipeCategory = abstractCookingRecipe.getCategory();
				if (recipeType == RecipeType.SMELTING) {
					return switch (cookingRecipeCategory) {
						case BLOCKS -> RecipeBookGroup.FURNACE_BLOCKS;
						case FOOD -> RecipeBookGroup.FURNACE_FOOD;
						case MISC -> RecipeBookGroup.FURNACE_MISC;
					};
				}

				if (recipeType == RecipeType.BLASTING) {
					return cookingRecipeCategory == CookingRecipeCategory.BLOCKS ? RecipeBookGroup.BLAST_FURNACE_BLOCKS : RecipeBookGroup.BLAST_FURNACE_MISC;
				}

				if (recipeType == RecipeType.SMOKING) {
					return RecipeBookGroup.SMOKER_FOOD;
				}

				if (recipeType == RecipeType.CAMPFIRE_COOKING) {
					return RecipeBookGroup.CAMPFIRE;
				}
			}

			if (recipeType == RecipeType.STONECUTTING) {
				return RecipeBookGroup.STONECUTTER;
			} else if (recipeType == RecipeType.SMITHING) {
				return RecipeBookGroup.SMITHING;
			} else {
				LOGGER.warn("Unknown recipe category: {}/{}", LogUtils.defer(() -> Registries.RECIPE_TYPE.getId(recipe.getType())), LogUtils.defer(recipe::getId));
				return RecipeBookGroup.UNKNOWN;
			}
		}
	}

	public List<RecipeResultCollection> getOrderedResults() {
		return this.orderedResults;
	}

	public List<RecipeResultCollection> getResultsForGroup(RecipeBookGroup category) {
		return (List<RecipeResultCollection>)this.resultsByGroup.getOrDefault(category, Collections.emptyList());
	}
}
