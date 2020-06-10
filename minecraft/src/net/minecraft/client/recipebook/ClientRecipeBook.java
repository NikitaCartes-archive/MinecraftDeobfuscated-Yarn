package net.minecraft.client.recipebook;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Table;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.screen.recipebook.RecipeResultCollection;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.Recipe;
import net.minecraft.recipe.RecipeManager;
import net.minecraft.recipe.RecipeType;
import net.minecraft.recipe.book.RecipeBook;
import net.minecraft.screen.AbstractRecipeScreenHandler;
import net.minecraft.screen.BlastFurnaceScreenHandler;
import net.minecraft.screen.CraftingScreenHandler;
import net.minecraft.screen.FurnaceScreenHandler;
import net.minecraft.screen.PlayerScreenHandler;
import net.minecraft.screen.SmokerScreenHandler;
import net.minecraft.util.registry.Registry;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Environment(EnvType.CLIENT)
public class ClientRecipeBook extends RecipeBook {
	private static final Logger field_25622 = LogManager.getLogger();
	private final RecipeManager manager;
	private final Map<RecipeBookGroup, List<RecipeResultCollection>> resultsByGroup = Maps.<RecipeBookGroup, List<RecipeResultCollection>>newHashMap();
	private final List<RecipeResultCollection> orderedResults = Lists.<RecipeResultCollection>newArrayList();

	public ClientRecipeBook(RecipeManager manager) {
		this.manager = manager;
	}

	public void reload() {
		this.orderedResults.clear();
		this.resultsByGroup.clear();
		Table<RecipeBookGroup, String, RecipeResultCollection> table = HashBasedTable.create();

		for (Recipe<?> recipe : this.manager.values()) {
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

	private RecipeResultCollection addGroup(RecipeBookGroup group) {
		RecipeResultCollection recipeResultCollection = new RecipeResultCollection();
		this.orderedResults.add(recipeResultCollection);
		((List)this.resultsByGroup.computeIfAbsent(group, recipeBookGroup -> Lists.newArrayList())).add(recipeResultCollection);
		if (group == RecipeBookGroup.FURNACE_BLOCKS || group == RecipeBookGroup.FURNACE_FOOD || group == RecipeBookGroup.FURNACE_MISC) {
			this.addGroupResults(RecipeBookGroup.FURNACE_SEARCH, recipeResultCollection);
		} else if (group == RecipeBookGroup.BLAST_FURNACE_BLOCKS || group == RecipeBookGroup.BLAST_FURNACE_MISC) {
			this.addGroupResults(RecipeBookGroup.BLAST_FURNACE_SEARCH, recipeResultCollection);
		} else if (group == RecipeBookGroup.SMOKER_FOOD) {
			this.addGroupResults(RecipeBookGroup.SMOKER_SEARCH, recipeResultCollection);
		} else if (group == RecipeBookGroup.CRAFTING_BUILDING_BLOCKS
			|| group == RecipeBookGroup.CRAFTING_REDSTONE
			|| group == RecipeBookGroup.CRAFTING_EQUIPMENT
			|| group == RecipeBookGroup.CRAFTING_MISC) {
			this.addGroupResults(RecipeBookGroup.SEARCH, recipeResultCollection);
		}

		return recipeResultCollection;
	}

	private void addGroupResults(RecipeBookGroup group, RecipeResultCollection results) {
		((List)this.resultsByGroup.computeIfAbsent(group, recipeBookGroup -> Lists.newArrayList())).add(results);
	}

	private static RecipeBookGroup getGroupForRecipe(Recipe<?> recipe) {
		RecipeType<?> recipeType = recipe.getType();
		if (recipeType == RecipeType.CRAFTING) {
			ItemStack itemStack = recipe.getOutput();
			ItemGroup itemGroup = itemStack.getItem().getGroup();
			if (itemGroup == ItemGroup.BUILDING_BLOCKS) {
				return RecipeBookGroup.CRAFTING_BUILDING_BLOCKS;
			} else if (itemGroup == ItemGroup.TOOLS || itemGroup == ItemGroup.COMBAT) {
				return RecipeBookGroup.CRAFTING_EQUIPMENT;
			} else {
				return itemGroup == ItemGroup.REDSTONE ? RecipeBookGroup.CRAFTING_REDSTONE : RecipeBookGroup.CRAFTING_MISC;
			}
		} else if (recipeType == RecipeType.SMELTING) {
			if (recipe.getOutput().getItem().isFood()) {
				return RecipeBookGroup.FURNACE_FOOD;
			} else {
				return recipe.getOutput().getItem() instanceof BlockItem ? RecipeBookGroup.FURNACE_BLOCKS : RecipeBookGroup.FURNACE_MISC;
			}
		} else if (recipeType == RecipeType.BLASTING) {
			return recipe.getOutput().getItem() instanceof BlockItem ? RecipeBookGroup.BLAST_FURNACE_BLOCKS : RecipeBookGroup.BLAST_FURNACE_MISC;
		} else if (recipeType == RecipeType.SMOKING) {
			return RecipeBookGroup.SMOKER_FOOD;
		} else if (recipeType == RecipeType.STONECUTTING) {
			return RecipeBookGroup.STONECUTTER;
		} else if (recipeType == RecipeType.CAMPFIRE_COOKING) {
			return RecipeBookGroup.CAMPFIRE;
		} else if (recipeType == RecipeType.SMITHING) {
			return RecipeBookGroup.SMITHING;
		} else {
			field_25622.warn("Unknown recipe category: {}/{}", () -> Registry.RECIPE_TYPE.getId(recipe.getType()), recipe::getId);
			return RecipeBookGroup.UNKNOWN;
		}
	}

	public static List<RecipeBookGroup> getGroups(AbstractRecipeScreenHandler<?> handler) {
		if (handler instanceof CraftingScreenHandler || handler instanceof PlayerScreenHandler) {
			return Lists.<RecipeBookGroup>newArrayList(
				RecipeBookGroup.SEARCH,
				RecipeBookGroup.CRAFTING_EQUIPMENT,
				RecipeBookGroup.CRAFTING_BUILDING_BLOCKS,
				RecipeBookGroup.CRAFTING_MISC,
				RecipeBookGroup.CRAFTING_REDSTONE
			);
		} else if (handler instanceof FurnaceScreenHandler) {
			return Lists.<RecipeBookGroup>newArrayList(
				RecipeBookGroup.FURNACE_SEARCH, RecipeBookGroup.FURNACE_FOOD, RecipeBookGroup.FURNACE_BLOCKS, RecipeBookGroup.FURNACE_MISC
			);
		} else if (handler instanceof BlastFurnaceScreenHandler) {
			return Lists.<RecipeBookGroup>newArrayList(RecipeBookGroup.BLAST_FURNACE_SEARCH, RecipeBookGroup.BLAST_FURNACE_BLOCKS, RecipeBookGroup.BLAST_FURNACE_MISC);
		} else {
			return handler instanceof SmokerScreenHandler
				? Lists.<RecipeBookGroup>newArrayList(RecipeBookGroup.SMOKER_SEARCH, RecipeBookGroup.SMOKER_FOOD)
				: Lists.<RecipeBookGroup>newArrayList();
		}
	}

	public List<RecipeResultCollection> getOrderedResults() {
		return this.orderedResults;
	}

	public List<RecipeResultCollection> getResultsForGroup(RecipeBookGroup category) {
		return (List<RecipeResultCollection>)this.resultsByGroup.getOrDefault(category, Collections.emptyList());
	}
}
