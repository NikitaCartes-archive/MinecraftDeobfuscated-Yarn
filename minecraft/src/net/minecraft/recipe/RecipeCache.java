package net.minecraft.recipe;

import java.lang.ref.WeakReference;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import javax.annotation.Nullable;
import net.minecraft.inventory.RecipeInputInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.world.World;

public class RecipeCache {
	private final RecipeCache.CachedRecipe[] cache;
	private WeakReference<RecipeManager> recipeManagerRef = new WeakReference(null);

	public RecipeCache(int size) {
		this.cache = new RecipeCache.CachedRecipe[size];
	}

	public Optional<CraftingRecipe> getRecipe(World world, RecipeInputInventory inputInventory) {
		if (inputInventory.isEmpty()) {
			return Optional.empty();
		} else {
			this.validateRecipeManager(world);

			for (int i = 0; i < this.cache.length; i++) {
				RecipeCache.CachedRecipe cachedRecipe = this.cache[i];
				if (cachedRecipe != null && cachedRecipe.matches(inputInventory.getHeldStacks())) {
					this.sendToFront(i);
					return Optional.ofNullable(cachedRecipe.value());
				}
			}

			return this.getAndCacheRecipe(inputInventory, world);
		}
	}

	private void validateRecipeManager(World world) {
		RecipeManager recipeManager = world.getRecipeManager();
		if (recipeManager != this.recipeManagerRef.get()) {
			this.recipeManagerRef = new WeakReference(recipeManager);
			Arrays.fill(this.cache, null);
		}
	}

	private Optional<CraftingRecipe> getAndCacheRecipe(RecipeInputInventory inputInventory, World world) {
		Optional<RecipeEntry<CraftingRecipe>> optional = world.getRecipeManager().getFirstMatch(RecipeType.CRAFTING, inputInventory, world);
		this.cache(inputInventory.getHeldStacks(), (CraftingRecipe)optional.map(RecipeEntry::value).orElse(null));
		return optional.map(RecipeEntry::value);
	}

	private void sendToFront(int index) {
		if (index > 0) {
			RecipeCache.CachedRecipe cachedRecipe = this.cache[index];
			System.arraycopy(this.cache, 0, this.cache, 1, index);
			this.cache[0] = cachedRecipe;
		}
	}

	private void cache(List<ItemStack> inputStacks, @Nullable CraftingRecipe recipe) {
		DefaultedList<ItemStack> defaultedList = DefaultedList.ofSize(inputStacks.size(), ItemStack.EMPTY);

		for (int i = 0; i < inputStacks.size(); i++) {
			defaultedList.set(i, ((ItemStack)inputStacks.get(i)).copyWithCount(1));
		}

		System.arraycopy(this.cache, 0, this.cache, 1, this.cache.length - 1);
		this.cache[0] = new RecipeCache.CachedRecipe(defaultedList, recipe);
	}

	static record CachedRecipe(DefaultedList<ItemStack> key, @Nullable CraftingRecipe value) {
		public boolean matches(List<ItemStack> inputs) {
			if (this.key.size() != inputs.size()) {
				return false;
			} else {
				for (int i = 0; i < this.key.size(); i++) {
					if (!ItemStack.areItemsAndNbtEqual(this.key.get(i), (ItemStack)inputs.get(i))) {
						return false;
					}
				}

				return true;
			}
		}
	}
}
