/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.recipe;

import java.util.Iterator;
import net.minecraft.recipe.Recipe;
import net.minecraft.recipe.ShapedRecipe;
import net.minecraft.util.math.MathHelper;

public interface RecipeGridAligner<T> {
    default public void alignRecipeToGrid(int gridWidth, int gridHeight, int gridOutputSlot, Recipe<?> recipe, Iterator<T> inputs, int amount) {
        int i = gridWidth;
        int j = gridHeight;
        if (recipe instanceof ShapedRecipe) {
            ShapedRecipe shapedRecipe = (ShapedRecipe)recipe;
            i = shapedRecipe.getWidth();
            j = shapedRecipe.getHeight();
        }
        int k = 0;
        block0: for (int l = 0; l < gridHeight; ++l) {
            if (k == gridOutputSlot) {
                ++k;
            }
            boolean bl = (float)j < (float)gridHeight / 2.0f;
            int m = MathHelper.floor((float)gridHeight / 2.0f - (float)j / 2.0f);
            if (bl && m > l) {
                k += gridWidth;
                ++l;
            }
            for (int n = 0; n < gridWidth; ++n) {
                boolean bl2;
                if (!inputs.hasNext()) {
                    return;
                }
                bl = (float)i < (float)gridWidth / 2.0f;
                m = MathHelper.floor((float)gridWidth / 2.0f - (float)i / 2.0f);
                int o = i;
                boolean bl3 = bl2 = n < i;
                if (bl) {
                    o = m + i;
                    boolean bl4 = bl2 = m <= n && n < m + i;
                }
                if (bl2) {
                    this.acceptAlignedInput(inputs, k, amount, l, n);
                } else if (o == n) {
                    k += gridWidth - n;
                    continue block0;
                }
                ++k;
            }
        }
    }

    public void acceptAlignedInput(Iterator<T> var1, int var2, int var3, int var4, int var5);
}

