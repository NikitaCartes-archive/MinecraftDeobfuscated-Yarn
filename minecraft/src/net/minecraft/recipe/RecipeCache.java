package net.minecraft.recipe;

import java.lang.ref.WeakReference;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import javax.annotation.Nullable;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.input.CraftingRecipeInput;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.world.World;

public class RecipeCache {
	private final RecipeCache.CachedRecipe[] cache;
	private WeakReference<RecipeManager> recipeManagerRef = new WeakReference(null);

	public RecipeCache(int size) {
		this.cache = new RecipeCache.CachedRecipe[size];
	}

	public Optional<RecipeEntry<CraftingRecipe>> getRecipe(World world, CraftingRecipeInput input) {
		if (input.isEmpty()) {
			return Optional.empty();
		} else {
			this.validateRecipeManager(world);

			for (int i = 0; i < this.cache.length; i++) {
				RecipeCache.CachedRecipe cachedRecipe = this.cache[i];
				if (cachedRecipe != null && cachedRecipe.matches(input.getStacks())) {
					this.sendToFront(i);
					return Optional.ofNullable(cachedRecipe.value());
				}
			}

			return this.getAndCacheRecipe(input, world);
		}
	}

	private void validateRecipeManager(World world) {
		RecipeManager recipeManager = world.getRecipeManager();
		if (recipeManager != this.recipeManagerRef.get()) {
			this.recipeManagerRef = new WeakReference(recipeManager);
			Arrays.fill(this.cache, null);
		}
	}

	private Optional<RecipeEntry<CraftingRecipe>> getAndCacheRecipe(CraftingRecipeInput input, World world) {
		Optional<RecipeEntry<CraftingRecipe>> optional = world.getRecipeManager().getFirstMatch(RecipeType.CRAFTING, input, world);
		this.cache(input.getStacks(), (RecipeEntry<CraftingRecipe>)optional.orElse(null));
		return optional;
	}

	private void sendToFront(int index) {
		if (index > 0) {
			RecipeCache.CachedRecipe cachedRecipe = this.cache[index];
			System.arraycopy(this.cache, 0, this.cache, 1, index);
			this.cache[0] = cachedRecipe;
		}
	}

	private void cache(List<ItemStack> inputStacks, @Nullable RecipeEntry<CraftingRecipe> recipe) {
		DefaultedList<ItemStack> defaultedList = DefaultedList.ofSize(inputStacks.size(), ItemStack.EMPTY);

		for (int i = 0; i < inputStacks.size(); i++) {
			defaultedList.set(i, ((ItemStack)inputStacks.get(i)).copyWithCount(1));
		}

		System.arraycopy(this.cache, 0, this.cache, 1, this.cache.length - 1);
		this.cache[0] = new RecipeCache.CachedRecipe(defaultedList, recipe);
	}

	static record CachedRecipe(DefaultedList<ItemStack> key, @Nullable RecipeEntry<CraftingRecipe> value) {
		public boolean matches(List<ItemStack> inputs) {
			if (this.key.size() != inputs.size()) {
				return false;
			} else {
				for (int i = 0; i < this.key.size(); i++) {
					if (!ItemStack.areItemsAndComponentsEqual(this.key.get(i), (ItemStack)inputs.get(i))) {
						return false;
					}
				}

				return true;
			}
		}
	}
}
