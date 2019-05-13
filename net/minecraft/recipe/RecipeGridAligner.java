/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.recipe;

import java.util.Iterator;
import net.minecraft.recipe.Recipe;
import net.minecraft.recipe.ShapedRecipe;
import net.minecraft.util.math.MathHelper;

public interface RecipeGridAligner<T> {
    default public void alignRecipeToGrid(int i, int j, int k, Recipe<?> recipe, Iterator<T> iterator, int l) {
        int m = i;
        int n = j;
        if (recipe instanceof ShapedRecipe) {
            ShapedRecipe shapedRecipe = (ShapedRecipe)recipe;
            m = shapedRecipe.getWidth();
            n = shapedRecipe.getHeight();
        }
        int o = 0;
        block0: for (int p = 0; p < j; ++p) {
            if (o == k) {
                ++o;
            }
            boolean bl = (float)n < (float)j / 2.0f;
            int q = MathHelper.floor((float)j / 2.0f - (float)n / 2.0f);
            if (bl && q > p) {
                o += i;
                ++p;
            }
            for (int r = 0; r < i; ++r) {
                boolean bl2;
                if (!iterator.hasNext()) {
                    return;
                }
                bl = (float)m < (float)i / 2.0f;
                q = MathHelper.floor((float)i / 2.0f - (float)m / 2.0f);
                int s = m;
                boolean bl3 = bl2 = r < m;
                if (bl) {
                    s = q + m;
                    boolean bl4 = bl2 = q <= r && r < q + m;
                }
                if (bl2) {
                    this.acceptAlignedInput(iterator, o, l, p, r);
                } else if (s == r) {
                    o += i - r;
                    continue block0;
                }
                ++o;
            }
        }
    }

    public void acceptAlignedInput(Iterator<T> var1, int var2, int var3, int var4, int var5);
}

