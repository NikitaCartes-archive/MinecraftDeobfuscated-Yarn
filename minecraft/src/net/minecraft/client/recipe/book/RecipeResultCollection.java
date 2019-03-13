package net.minecraft.client.recipe.book;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import java.util.List;
import java.util.Set;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.Recipe;
import net.minecraft.recipe.RecipeFinder;
import net.minecraft.recipe.book.RecipeBook;

@Environment(EnvType.CLIENT)
public class RecipeResultCollection {
	private final List<Recipe<?>> allRecipes = Lists.<Recipe<?>>newArrayList();
	private final Set<Recipe<?>> craftableResults = Sets.<Recipe<?>>newHashSet();
	private final Set<Recipe<?>> fittableResults = Sets.<Recipe<?>>newHashSet();
	private final Set<Recipe<?>> allResults = Sets.<Recipe<?>>newHashSet();
	private boolean field_3148 = true;

	public boolean isInitialized() {
		return !this.allResults.isEmpty();
	}

	public void method_2647(RecipeBook recipeBook) {
		for (Recipe<?> recipe : this.allRecipes) {
			if (recipeBook.contains(recipe)) {
				this.allResults.add(recipe);
			}
		}
	}

	public void method_2649(RecipeFinder recipeFinder, int i, int j, RecipeBook recipeBook) {
		for (int k = 0; k < this.allRecipes.size(); k++) {
			Recipe<?> recipe = (Recipe<?>)this.allRecipes.get(k);
			boolean bl = recipe.fits(i, j) && recipeBook.contains(recipe);
			if (bl) {
				this.fittableResults.add(recipe);
			} else {
				this.fittableResults.remove(recipe);
			}

			if (bl && recipeFinder.method_7402(recipe, null)) {
				this.craftableResults.add(recipe);
			} else {
				this.craftableResults.remove(recipe);
			}
		}
	}

	public boolean isCraftable(Recipe<?> recipe) {
		return this.craftableResults.contains(recipe);
	}

	public boolean hasCraftableResults() {
		return !this.craftableResults.isEmpty();
	}

	public boolean hasFittableResults() {
		return !this.fittableResults.isEmpty();
	}

	public List<Recipe<?>> getAllRecipes() {
		return this.allRecipes;
	}

	public List<Recipe<?>> getResults(boolean bl) {
		List<Recipe<?>> list = Lists.<Recipe<?>>newArrayList();
		Set<Recipe<?>> set = bl ? this.craftableResults : this.fittableResults;

		for (Recipe<?> recipe : this.allRecipes) {
			if (set.contains(recipe)) {
				list.add(recipe);
			}
		}

		return list;
	}

	public List<Recipe<?>> getResultsExclusive(boolean bl) {
		List<Recipe<?>> list = Lists.<Recipe<?>>newArrayList();

		for (Recipe<?> recipe : this.allRecipes) {
			if (this.fittableResults.contains(recipe) && this.craftableResults.contains(recipe) == bl) {
				list.add(recipe);
			}
		}

		return list;
	}

	public void addRecipe(Recipe<?> recipe) {
		this.allRecipes.add(recipe);
		if (this.field_3148) {
			ItemStack itemStack = ((Recipe)this.allRecipes.get(0)).getOutput();
			ItemStack itemStack2 = recipe.getOutput();
			this.field_3148 = ItemStack.areEqualIgnoreTags(itemStack, itemStack2) && ItemStack.areTagsEqual(itemStack, itemStack2);
		}
	}

	public boolean method_2656() {
		return this.field_3148;
	}
}
