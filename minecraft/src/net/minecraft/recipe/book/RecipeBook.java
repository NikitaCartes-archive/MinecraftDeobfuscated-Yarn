package net.minecraft.recipe.book;

import com.google.common.collect.Sets;
import java.util.Set;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.container.CraftingContainer;
import net.minecraft.container.FurnaceContainer;
import net.minecraft.recipe.Recipe;
import net.minecraft.util.Identifier;

public class RecipeBook {
	protected final Set<Identifier> recipes = Sets.<Identifier>newHashSet();
	protected final Set<Identifier> toBeDisplayed = Sets.<Identifier>newHashSet();
	protected boolean guiOpen;
	protected boolean filteringCraftable;
	protected boolean furnaceGuiOpen;
	protected boolean furnaceFilteringCraftable;

	public void copyFrom(RecipeBook recipeBook) {
		this.recipes.clear();
		this.toBeDisplayed.clear();
		this.recipes.addAll(recipeBook.recipes);
		this.toBeDisplayed.addAll(recipeBook.toBeDisplayed);
	}

	public void method_14876(Recipe recipe) {
		if (!recipe.isIgnoredInRecipeBook()) {
			this.method_14881(recipe.getId());
		}
	}

	protected void method_14881(Identifier identifier) {
		this.recipes.add(identifier);
	}

	public boolean method_14878(@Nullable Recipe recipe) {
		return recipe == null ? false : this.recipes.contains(recipe.getId());
	}

	@Environment(EnvType.CLIENT)
	public void method_14893(Recipe recipe) {
		this.method_14879(recipe.getId());
	}

	protected void method_14879(Identifier identifier) {
		this.recipes.remove(identifier);
		this.toBeDisplayed.remove(identifier);
	}

	@Environment(EnvType.CLIENT)
	public boolean method_14883(Recipe recipe) {
		return this.toBeDisplayed.contains(recipe.getId());
	}

	public void method_14886(Recipe recipe) {
		this.toBeDisplayed.remove(recipe.getId());
	}

	public void method_14885(Recipe recipe) {
		this.method_14877(recipe.getId());
	}

	protected void method_14877(Identifier identifier) {
		this.toBeDisplayed.add(identifier);
	}

	@Environment(EnvType.CLIENT)
	public boolean isGuiOpen() {
		return this.guiOpen;
	}

	public void setGuiOpen(boolean bl) {
		this.guiOpen = bl;
	}

	@Environment(EnvType.CLIENT)
	public boolean isFilteringCraftable(CraftingContainer craftingContainer) {
		return craftingContainer instanceof FurnaceContainer ? this.furnaceFilteringCraftable : this.filteringCraftable;
	}

	@Environment(EnvType.CLIENT)
	public boolean isFilteringCraftable() {
		return this.filteringCraftable;
	}

	public void setFilteringCraftable(boolean bl) {
		this.filteringCraftable = bl;
	}

	@Environment(EnvType.CLIENT)
	public boolean isFurnaceGuiOpen() {
		return this.furnaceGuiOpen;
	}

	public void setFurnaceGuiOpen(boolean bl) {
		this.furnaceGuiOpen = bl;
	}

	@Environment(EnvType.CLIENT)
	public boolean isFurnaceFilteringCraftable() {
		return this.furnaceFilteringCraftable;
	}

	public void setFurnaceFilteringCraftable(boolean bl) {
		this.furnaceFilteringCraftable = bl;
	}
}
