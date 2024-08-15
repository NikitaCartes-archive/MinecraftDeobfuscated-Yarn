package net.minecraft.recipe;

import java.util.Iterator;
import net.minecraft.util.math.MathHelper;

public interface RecipeGridAligner {
	static <T> void alignRecipeToGrid(int width, int height, RecipeEntry<?> recipe, Iterable<T> inputs, RecipeGridAligner.Filler<T> filler) {
		int i = width;
		int j = height;
		if (recipe.value() instanceof ShapedRecipe shapedRecipe) {
			i = shapedRecipe.getWidth();
			j = shapedRecipe.getHeight();
		}

		Iterator<T> iterator = inputs.iterator();
		int k = 0;

		for (int l = 0; l < height; l++) {
			boolean bl = (float)j < (float)height / 2.0F;
			int m = MathHelper.floor((float)height / 2.0F - (float)j / 2.0F);
			if (bl && m > l) {
				k += width;
				l++;
			}

			for (int n = 0; n < width; n++) {
				if (!iterator.hasNext()) {
					return;
				}

				bl = (float)i < (float)width / 2.0F;
				m = MathHelper.floor((float)width / 2.0F - (float)i / 2.0F);
				int o = i;
				boolean bl2 = n < i;
				if (bl) {
					o = m + i;
					bl2 = m <= n && n < m + i;
				}

				if (bl2) {
					filler.addItemToSlot((T)iterator.next(), k, n, l);
				} else if (o == n) {
					k += width - n;
					break;
				}

				k++;
			}
		}
	}

	@FunctionalInterface
	public interface Filler<T> {
		void addItemToSlot(T slot, int index, int x, int y);
	}
}
