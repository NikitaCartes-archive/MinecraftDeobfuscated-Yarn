/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.gui.screen.recipebook;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.Recipe;
import net.minecraft.recipe.RecipeMatcher;
import net.minecraft.recipe.book.RecipeBook;

@Environment(value=EnvType.CLIENT)
public class RecipeResultCollection {
    private final List<Recipe<?>> recipes;
    private final boolean singleOutput;
    private final Set<Recipe<?>> craftableRecipes = Sets.newHashSet();
    private final Set<Recipe<?>> fittingRecipes = Sets.newHashSet();
    private final Set<Recipe<?>> unlockedRecipes = Sets.newHashSet();

    public RecipeResultCollection(List<Recipe<?>> recipes) {
        this.recipes = ImmutableList.copyOf(recipes);
        this.singleOutput = recipes.size() <= 1 ? true : RecipeResultCollection.shouldHaveSingleOutput(recipes);
    }

    private static boolean shouldHaveSingleOutput(List<Recipe<?>> recipes) {
        int i = recipes.size();
        ItemStack itemStack = recipes.get(0).getOutput();
        for (int j = 1; j < i; ++j) {
            ItemStack itemStack2 = recipes.get(j).getOutput();
            if (ItemStack.areItemsEqual(itemStack, itemStack2) && ItemStack.areNbtEqual(itemStack, itemStack2)) continue;
            return false;
        }
        return true;
    }

    public boolean isInitialized() {
        return !this.unlockedRecipes.isEmpty();
    }

    public void initialize(RecipeBook recipeBook) {
        for (Recipe<?> recipe : this.recipes) {
            if (!recipeBook.contains(recipe)) continue;
            this.unlockedRecipes.add(recipe);
        }
    }

    public void computeCraftables(RecipeMatcher recipeFinder, int gridWidth, int gridHeight, RecipeBook recipeBook) {
        for (Recipe<?> recipe : this.recipes) {
            boolean bl;
            boolean bl2 = bl = recipe.fits(gridWidth, gridHeight) && recipeBook.contains(recipe);
            if (bl) {
                this.fittingRecipes.add(recipe);
            } else {
                this.fittingRecipes.remove(recipe);
            }
            if (bl && recipeFinder.match(recipe, null)) {
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

    public List<Recipe<?>> getResults(boolean craftableOnly) {
        ArrayList<Recipe<?>> list = Lists.newArrayList();
        Set<Recipe<?>> set = craftableOnly ? this.craftableRecipes : this.fittingRecipes;
        for (Recipe<?> recipe : this.recipes) {
            if (!set.contains(recipe)) continue;
            list.add(recipe);
        }
        return list;
    }

    public List<Recipe<?>> getRecipes(boolean craftable) {
        ArrayList<Recipe<?>> list = Lists.newArrayList();
        for (Recipe<?> recipe : this.recipes) {
            if (!this.fittingRecipes.contains(recipe) || this.craftableRecipes.contains(recipe) != craftable) continue;
            list.add(recipe);
        }
        return list;
    }

    public boolean hasSingleOutput() {
        return this.singleOutput;
    }
}

