package net.minecraft.recipe;

import java.util.Iterator;
import net.minecraft.util.math.MathHelper;

public interface RecipeGridAligner<T> {
	default void alignRecipeToGrid(int i, int j, int k, Recipe<?> recipe, Iterator<T> iterator, int l) {
		int m = i;
		int n = j;
		if (recipe instanceof ShapedRecipe) {
			ShapedRecipe shapedRecipe = (ShapedRecipe)recipe;
			m = shapedRecipe.getWidth();
			n = shapedRecipe.getHeight();
		}

		int o = 0;

		for (int p = 0; p < j; p++) {
			if (o == k) {
				o++;
			}

			boolean bl = (float)n < (float)j / 2.0F;
			int q = MathHelper.floor((float)j / 2.0F - (float)n / 2.0F);
			if (bl && q > p) {
				o += i;
				p++;
			}

			for (int r = 0; r < i; r++) {
				if (!iterator.hasNext()) {
					return;
				}

				bl = (float)m < (float)i / 2.0F;
				q = MathHelper.floor((float)i / 2.0F - (float)m / 2.0F);
				int s = m;
				boolean bl2 = r < m;
				if (bl) {
					s = q + m;
					bl2 = q <= r && r < q + m;
				}

				if (bl2) {
					this.acceptAlignedInput(iterator, o, l, p, r);
				} else if (s == r) {
					o += i - r;
					break;
				}

				o++;
			}
		}
	}

	void acceptAlignedInput(Iterator<T> iterator, int i, int j, int k, int l);
}
