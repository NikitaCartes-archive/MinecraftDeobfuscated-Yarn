package net.minecraft.client.gui.screen.recipebook;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Sets;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.function.Predicate;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.RecipeEntry;
import net.minecraft.recipe.RecipeFinder;
import net.minecraft.recipe.book.RecipeBook;
import net.minecraft.registry.DynamicRegistryManager;

@Environment(EnvType.CLIENT)
public class RecipeResultCollection {
	private final DynamicRegistryManager registryManager;
	private final List<RecipeEntry<?>> recipes;
	private final boolean singleOutput;
	private final Set<RecipeEntry<?>> craftableRecipes = Sets.<RecipeEntry<?>>newHashSet();
	private final Set<RecipeEntry<?>> fittingRecipes = Sets.<RecipeEntry<?>>newHashSet();
	private final Set<RecipeEntry<?>> unlockedRecipes = Sets.<RecipeEntry<?>>newHashSet();

	public RecipeResultCollection(DynamicRegistryManager registryManager, List<RecipeEntry<?>> recipes) {
		this.registryManager = registryManager;
		this.recipes = ImmutableList.copyOf(recipes);
		if (recipes.size() <= 1) {
			this.singleOutput = true;
		} else {
			this.singleOutput = shouldHaveSingleOutput(registryManager, recipes);
		}
	}

	private static boolean shouldHaveSingleOutput(DynamicRegistryManager registryManager, List<RecipeEntry<?>> recipes) {
		int i = recipes.size();
		ItemStack itemStack = ((RecipeEntry)recipes.get(0)).value().getResult(registryManager);

		for (int j = 1; j < i; j++) {
			ItemStack itemStack2 = ((RecipeEntry)recipes.get(j)).value().getResult(registryManager);
			if (!ItemStack.areItemsAndComponentsEqual(itemStack, itemStack2)) {
				return false;
			}
		}

		return true;
	}

	public DynamicRegistryManager getRegistryManager() {
		return this.registryManager;
	}

	public boolean isInitialized() {
		return !this.unlockedRecipes.isEmpty();
	}

	public void initialize(RecipeBook recipeBook) {
		for (RecipeEntry<?> recipeEntry : this.recipes) {
			if (recipeBook.contains(recipeEntry)) {
				this.unlockedRecipes.add(recipeEntry);
			}
		}
	}

	public void populateRecipes(RecipeFinder finder, int width, int height, RecipeBook recipeBook) {
		for (RecipeEntry<?> recipeEntry : this.recipes) {
			boolean bl = recipeEntry.value().fits(width, height) && recipeBook.contains(recipeEntry);
			if (bl) {
				this.fittingRecipes.add(recipeEntry);
			} else {
				this.fittingRecipes.remove(recipeEntry);
			}

			if (bl && finder.isCraftable(recipeEntry.value(), null)) {
				this.craftableRecipes.add(recipeEntry);
			} else {
				this.craftableRecipes.remove(recipeEntry);
			}
		}
	}

	public boolean isCraftable(RecipeEntry<?> recipe) {
		return this.craftableRecipes.contains(recipe);
	}

	public boolean hasCraftableRecipes() {
		return !this.craftableRecipes.isEmpty();
	}

	public boolean hasFittingRecipes() {
		return !this.fittingRecipes.isEmpty();
	}

	public List<RecipeEntry<?>> getAllRecipes() {
		return this.recipes;
	}

	public List<RecipeEntry<?>> filter(RecipeResultCollection.RecipeFilterMode filterMode) {
		Predicate<RecipeEntry<?>> predicate = switch (filterMode) {
			case ANY -> this.fittingRecipes::contains;
			case CRAFTABLE -> this.craftableRecipes::contains;
			case NOT_CRAFTABLE -> recipe -> this.fittingRecipes.contains(recipe) && !this.craftableRecipes.contains(recipe);
		};
		List<RecipeEntry<?>> list = new ArrayList();

		for (RecipeEntry<?> recipeEntry : this.recipes) {
			if (predicate.test(recipeEntry)) {
				list.add(recipeEntry);
			}
		}

		return list;
	}

	public boolean hasSingleOutput() {
		return this.singleOutput;
	}

	@Environment(EnvType.CLIENT)
	public static enum RecipeFilterMode {
		ANY,
		CRAFTABLE,
		NOT_CRAFTABLE;
	}
}
