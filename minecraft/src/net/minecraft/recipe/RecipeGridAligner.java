package net.minecraft.recipe;

import java.util.Iterator;
import net.minecraft.util.math.MathHelper;

public interface RecipeGridAligner {
	static <T> void alignRecipeToGrid(int width, int height, Recipe<?> recipe, Iterable<T> slots, RecipeGridAligner.Filler<T> filter) {
		if (recipe instanceof ShapedRecipe shapedRecipe) {
			alignRecipeToGrid(width, height, shapedRecipe.getWidth(), shapedRecipe.getHeight(), slots, filter);
		} else {
			alignRecipeToGrid(width, height, width, height, slots, filter);
		}
	}

	static <T> void alignRecipeToGrid(int width, int height, int recipeWidth, int recipeHeight, Iterable<T> slots, RecipeGridAligner.Filler<T> filter) {
		Iterator<T> iterator = slots.iterator();
		int i = 0;

		for (int j = 0; j < height; j++) {
			boolean bl = (float)recipeHeight < (float)height / 2.0F;
			int k = MathHelper.floor((float)height / 2.0F - (float)recipeHeight / 2.0F);
			if (bl && k > j) {
				i += width;
				j++;
			}

			for (int l = 0; l < width; l++) {
				if (!iterator.hasNext()) {
					return;
				}

				bl = (float)recipeWidth < (float)width / 2.0F;
				k = MathHelper.floor((float)width / 2.0F - (float)recipeWidth / 2.0F);
				int m = recipeWidth;
				boolean bl2 = l < recipeWidth;
				if (bl) {
					m = k + recipeWidth;
					bl2 = k <= l && l < k + recipeWidth;
				}

				if (bl2) {
					filter.addItemToSlot((T)iterator.next(), i, l, j);
				} else if (m == l) {
					i += width - l;
					break;
				}

				i++;
			}
		}
	}

	@FunctionalInterface
	public interface Filler<T> {
		void addItemToSlot(T slot, int index, int x, int y);
	}
}
