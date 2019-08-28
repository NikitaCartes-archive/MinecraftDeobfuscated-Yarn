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
    private final List<Recipe<?>> recipes = Lists.newArrayList();
    private final Set<Recipe<?>> craftableRecipes = Sets.newHashSet();
    private final Set<Recipe<?>> fittingRecipes = Sets.newHashSet();
    private final Set<Recipe<?>> unlockedRecipes = Sets.newHashSet();
    private boolean singleOutput = true;

    public boolean isInitialized() {
        return !this.unlockedRecipes.isEmpty();
    }

    public void initialize(RecipeBook recipeBook) {
        for (Recipe<?> recipe : this.recipes) {
            if (!recipeBook.contains(recipe)) continue;
            this.unlockedRecipes.add(recipe);
        }
    }

    public void computeCraftables(RecipeFinder recipeFinder, int i, int j, RecipeBook recipeBook) {
        for (int k = 0; k < this.recipes.size(); ++k) {
            boolean bl;
            Recipe<?> recipe = this.recipes.get(k);
            boolean bl2 = bl = recipe.fits(i, j) && recipeBook.contains(recipe);
            if (bl) {
                this.fittingRecipes.add(recipe);
            } else {
                this.fittingRecipes.remove(recipe);
            }
            if (bl && recipeFinder.findRecipe(recipe, null)) {
                this.craftableRecipes.add(recipe);
                continue;
            }
            this.craftableRecipes.remove(recipe);
        }
    }

    public boolean isCraftable(Recipe<?> recipe) {
        return this.craftableRecipes.contains(recipe);
    }

    public boolean hasCraftableRecipes() {
        return !this.craftableRecipes.isEmpty();
    }

    public boolean hasFittingRecipes() {
        return !this.fittingRecipes.isEmpty();
    }

    public List<Recipe<?>> getAllRecipes() {
        return this.recipes;
    }

    public List<Recipe<?>> getResults(boolean bl) {
        ArrayList<Recipe<?>> list = Lists.newArrayList();
        Set<Recipe<?>> set = bl ? this.craftableRecipes : this.fittingRecipes;
        for (Recipe<?> recipe : this.recipes) {
            if (!set.contains(recipe)) continue;
            list.add(recipe);
        }
        return list;
    }

    public List<Recipe<?>> getRecipes(boolean bl) {
        ArrayList<Recipe<?>> list = Lists.newArrayList();
        for (Recipe<?> recipe : this.recipes) {
            if (!this.fittingRecipes.contains(recipe) || this.craftableRecipes.contains(recipe) != bl) continue;
            list.add(recipe);
        }
        return list;
    }

    public void addRecipe(Recipe<?> recipe) {
        this.recipes.add(recipe);
        if (this.singleOutput) {
            ItemStack itemStack2;
            ItemStack itemStack = this.recipes.get(0).getOutput();
            this.singleOutput = ItemStack.areItemsEqualIgnoreDamage(itemStack, itemStack2 = recipe.getOutput()) && ItemStack.areTagsEqual(itemStack, itemStack2);
        }
    }

    public boolean hasSingleOutput() {
        return this.singleOutput;
    }
}

