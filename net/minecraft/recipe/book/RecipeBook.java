/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.recipe.book;

import com.google.common.collect.Sets;
import java.util.Set;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.recipe.Recipe;
import net.minecraft.recipe.book.RecipeBookCategory;
import net.minecraft.recipe.book.RecipeBookOptions;
import net.minecraft.screen.AbstractRecipeScreenHandler;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;

public class RecipeBook {
    protected final Set<Identifier> recipes = Sets.newHashSet();
    protected final Set<Identifier> toBeDisplayed = Sets.newHashSet();
    private final RecipeBookOptions options = new RecipeBookOptions();

    public void copyFrom(RecipeBook book) {
        this.recipes.clear();
        this.toBeDisplayed.clear();
        this.options.copyFrom(book.options);
        this.recipes.addAll(book.recipes);
        this.toBeDisplayed.addAll(book.toBeDisplayed);
    }

    public void add(Recipe<?> recipe) {
        if (!recipe.isIgnoredInRecipeBook()) {
            this.add(recipe.getId());
        }
    }

    protected void add(Identifier id) {
        this.recipes.add(id);
    }

    public boolean contains(@Nullable Recipe<?> recipe) {
        if (recipe == null) {
            return false;
        }
        return this.recipes.contains(recipe.getId());
    }

    public boolean contains(Identifier id) {
        return this.recipes.contains(id);
    }

    @Environment(value=EnvType.CLIENT)
    public void remove(Recipe<?> recipe) {
        this.remove(recipe.getId());
    }

    protected void remove(Identifier id) {
        this.recipes.remove(id);
        this.toBeDisplayed.remove(id);
    }

    @Environment(value=EnvType.CLIENT)
    public boolean shouldDisplay(Recipe<?> recipe) {
        return this.toBeDisplayed.contains(recipe.getId());
    }

    public void onRecipeDisplayed(Recipe<?> recipe) {
        this.toBeDisplayed.remove(recipe.getId());
    }

    public void display(Recipe<?> recipe) {
        this.display(recipe.getId());
    }

    protected void display(Identifier id) {
        this.toBeDisplayed.add(id);
    }

    @Environment(value=EnvType.CLIENT)
    public boolean isGuiOpen(RecipeBookCategory category) {
        return this.options.isGuiOpen(category);
    }

    @Environment(value=EnvType.CLIENT)
    public void setGuiOpen(RecipeBookCategory category, boolean open) {
        this.options.setGuiOpen(category, open);
    }

    @Environment(value=EnvType.CLIENT)
    public boolean isFilteringCraftable(AbstractRecipeScreenHandler<?> handler) {
        return this.isFilteringCraftable(handler.getCategory());
    }

    @Environment(value=EnvType.CLIENT)
    public boolean isFilteringCraftable(RecipeBookCategory category) {
        return this.options.isFilteringCraftable(category);
    }

    @Environment(value=EnvType.CLIENT)
    public void setFilteringCraftable(RecipeBookCategory category, boolean filteringCraftable) {
        this.options.setFilteringCraftable(category, filteringCraftable);
    }

    public void setOptions(RecipeBookOptions options) {
        this.options.copyFrom(options);
    }

    public RecipeBookOptions getOptions() {
        return this.options.copy();
    }

    public void setCategoryOptions(RecipeBookCategory category, boolean guiOpen, boolean filteringCraftable) {
        this.options.setGuiOpen(category, guiOpen);
        this.options.setFilteringCraftable(category, filteringCraftable);
    }
}

