package net.minecraft.recipe.book;

import com.google.common.collect.Sets;
import java.util.Set;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.container.BlastFurnaceContainer;
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
	protected boolean blastFurnaceGuiOpen;
	protected boolean blastFurnaceFilteringCraftable;
	protected boolean smokerGuiOpen;
	protected boolean smokerFilteringCraftable;

	public void copyFrom(RecipeBook recipeBook) {
		this.recipes.clear();
		this.toBeDisplayed.clear();
		this.recipes.addAll(recipeBook.recipes);
		this.toBeDisplayed.addAll(recipeBook.toBeDisplayed);
	}

	public void add(Recipe<?> recipe) {
		if (!recipe.isIgnoredInRecipeBook()) {
			this.add(recipe.getId());
		}
	}

	protected void add(Identifier identifier) {
		this.recipes.add(identifier);
	}

	public boolean contains(@Nullable Recipe<?> recipe) {
		return recipe == null ? false : this.recipes.contains(recipe.getId());
	}

	@Environment(EnvType.CLIENT)
	public void remove(Recipe<?> recipe) {
		this.remove(recipe.getId());
	}

	protected void remove(Identifier identifier) {
		this.recipes.remove(identifier);
		this.toBeDisplayed.remove(identifier);
	}

	@Environment(EnvType.CLIENT)
	public boolean shouldDisplay(Recipe<?> recipe) {
		return this.toBeDisplayed.contains(recipe.getId());
	}

	public void onRecipeDisplayed(Recipe<?> recipe) {
		this.toBeDisplayed.remove(recipe.getId());
	}

	public void display(Recipe<?> recipe) {
		this.display(recipe.getId());
	}

	protected void display(Identifier identifier) {
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
	public boolean isFilteringCraftable(CraftingContainer<?> craftingContainer) {
		if (craftingContainer instanceof FurnaceContainer) {
			return this.furnaceFilteringCraftable;
		} else {
			return craftingContainer instanceof BlastFurnaceContainer ? this.blastFurnaceFilteringCraftable : this.filteringCraftable;
		}
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

	@Environment(EnvType.CLIENT)
	public boolean isBlastFurnaceGuiOpen() {
		return this.blastFurnaceGuiOpen;
	}

	public void setBlastFurnaceGuiOpen(boolean bl) {
		this.blastFurnaceGuiOpen = bl;
	}

	@Environment(EnvType.CLIENT)
	public boolean isBlastFurnaceFilteringCraftable() {
		return this.blastFurnaceFilteringCraftable;
	}

	public void setBlastFurnaceFilteringCraftable(boolean bl) {
		this.blastFurnaceFilteringCraftable = bl;
	}

	@Environment(EnvType.CLIENT)
	public boolean isSmokerGuiOpen() {
		return this.smokerGuiOpen;
	}

	public void setSmokerGuiOpen(boolean bl) {
		this.smokerGuiOpen = bl;
	}

	@Environment(EnvType.CLIENT)
	public boolean isSmokerFilteringCraftable() {
		return this.smokerFilteringCraftable;
	}

	public void setSmokerFilteringCraftable(boolean bl) {
		this.smokerFilteringCraftable = bl;
	}
}
