/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.gui.screen.recipebook;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.Recipe;
import net.minecraft.recipe.RecipeFinder;
import net.minecraft.recipe.book.RecipeBook;

@Environment(value=EnvType.CLIENT)
public class RecipeResultCollection {
    private final List<Recipe<?>> allRecipes = Lists.newArrayList();
    private final Set<Recipe<?>> craftableResults = Sets.newHashSet();
    private final Set<Recipe<?>> fittableResults = Sets.newHashSet();
    private final Set<Recipe<?>> allResults = Sets.newHashSet();
    private boolean field_3148 = true;

    public boolean isInitialized() {
        return !this.allResults.isEmpty();
    }

    public void initialize(RecipeBook recipeBook) {
        for (Recipe<?> recipe : this.allRecipes) {
            if (!recipeBook.contains(recipe)) continue;
            this.allResults.add(recipe);
        }
    }

    public void computeCraftables(RecipeFinder recipeFinder, int i, int j, RecipeBook recipeBook) {
        for (int k = 0; k < this.allRecipes.size(); ++k) {
            boolean bl;
            Recipe<?> recipe = this.allRecipes.get(k);
            boolean bl2 = bl = recipe.fits(i, j) && recipeBook.contains(recipe);
            if (bl) {
                this.fittableResults.add(recipe);
            } else {
                this.fittableResults.remove(recipe);
            }
            if (bl && recipeFinder.findRecipe(recipe, null)) {
                this.craftableResults.add(recipe);
                continue;
            }
            this.craftableResults.remove(recipe);
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
        ArrayList<Recipe<?>> list = Lists.newArrayList();
        Set<Recipe<?>> set = bl ? this.craftableResults : this.fittableResults;
        for (Recipe<?> recipe : this.allRecipes) {
            if (!set.contains(recipe)) continue;
            list.add(recipe);
        }
        return list;
    }

    public List<Recipe<?>> getResultsExclusive(boolean bl) {
        ArrayList<Recipe<?>> list = Lists.newArrayList();
        for (Recipe<?> recipe : this.allRecipes) {
            if (!this.fittableResults.contains(recipe) || this.craftableResults.contains(recipe) != bl) continue;
            list.add(recipe);
        }
        return list;
    }

    public void addRecipe(Recipe<?> recipe) {
        this.allRecipes.add(recipe);
        if (this.field_3148) {
            ItemStack itemStack2;
            ItemStack itemStack = this.allRecipes.get(0).getOutput();
            this.field_3148 = ItemStack.areItemsEqualIgnoreDamage(itemStack, itemStack2 = recipe.getOutput()) && ItemStack.areTagsEqual(itemStack, itemStack2);
        }
    }

    public boolean method_2656() {
        return this.field_3148;
    }
}

