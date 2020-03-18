package net.minecraft.recipe.book;

import com.google.common.collect.Sets;
import java.util.Set;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.recipe.Recipe;
import net.minecraft.screen.AbstractRecipeScreenHandler;
import net.minecraft.screen.BlastFurnaceScreenHandler;
import net.minecraft.screen.FurnaceScreenHandler;
import net.minecraft.screen.SmokerScreenHandler;
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

	public void copyFrom(RecipeBook book) {
		this.recipes.clear();
		this.toBeDisplayed.clear();
		this.guiOpen = book.guiOpen;
		this.filteringCraftable = book.filteringCraftable;
		this.furnaceGuiOpen = book.furnaceGuiOpen;
		this.furnaceFilteringCraftable = book.furnaceFilteringCraftable;
		this.blastFurnaceGuiOpen = book.blastFurnaceGuiOpen;
		this.blastFurnaceFilteringCraftable = book.blastFurnaceFilteringCraftable;
		this.smokerGuiOpen = book.smokerGuiOpen;
		this.smokerFilteringCraftable = book.smokerFilteringCraftable;
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
		return recipe == null ? false : this.recipes.contains(recipe.getId());
	}

	public boolean contains(Identifier id) {
		return this.recipes.contains(id);
	}

	@Environment(EnvType.CLIENT)
	public void remove(Recipe<?> recipe) {
		this.remove(recipe.getId());
	}

	protected void remove(Identifier id) {
		this.recipes.remove(id);
		this.toBeDisplayed.remove(id);
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

	protected void display(Identifier id) {
		this.toBeDisplayed.add(id);
	}

	@Environment(EnvType.CLIENT)
	public boolean isGuiOpen() {
		return this.guiOpen;
	}

	public void setGuiOpen(boolean guiOpen) {
		this.guiOpen = guiOpen;
	}

	@Environment(EnvType.CLIENT)
	public boolean isFilteringCraftable(AbstractRecipeScreenHandler<?> handler) {
		if (handler instanceof FurnaceScreenHandler) {
			return this.furnaceFilteringCraftable;
		} else if (handler instanceof BlastFurnaceScreenHandler) {
			return this.blastFurnaceFilteringCraftable;
		} else {
			return handler instanceof SmokerScreenHandler ? this.smokerFilteringCraftable : this.filteringCraftable;
		}
	}

	@Environment(EnvType.CLIENT)
	public boolean isFilteringCraftable() {
		return this.filteringCraftable;
	}

	public void setFilteringCraftable(boolean filteringCraftable) {
		this.filteringCraftable = filteringCraftable;
	}

	@Environment(EnvType.CLIENT)
	public boolean isFurnaceGuiOpen() {
		return this.furnaceGuiOpen;
	}

	public void setFurnaceGuiOpen(boolean furnaceGuiOpen) {
		this.furnaceGuiOpen = furnaceGuiOpen;
	}

	@Environment(EnvType.CLIENT)
	public boolean isFurnaceFilteringCraftable() {
		return this.furnaceFilteringCraftable;
	}

	public void setFurnaceFilteringCraftable(boolean furnaceFilteringCraftable) {
		this.furnaceFilteringCraftable = furnaceFilteringCraftable;
	}

	@Environment(EnvType.CLIENT)
	public boolean isBlastFurnaceGuiOpen() {
		return this.blastFurnaceGuiOpen;
	}

	public void setBlastFurnaceGuiOpen(boolean blastFurnaceGuiOpen) {
		this.blastFurnaceGuiOpen = blastFurnaceGuiOpen;
	}

	@Environment(EnvType.CLIENT)
	public boolean isBlastFurnaceFilteringCraftable() {
		return this.blastFurnaceFilteringCraftable;
	}

	public void setBlastFurnaceFilteringCraftable(boolean blastFurnaceFilteringCraftable) {
		this.blastFurnaceFilteringCraftable = blastFurnaceFilteringCraftable;
	}

	@Environment(EnvType.CLIENT)
	public boolean isSmokerGuiOpen() {
		return this.smokerGuiOpen;
	}

	public void setSmokerGuiOpen(boolean smokerGuiOpen) {
		this.smokerGuiOpen = smokerGuiOpen;
	}

	@Environment(EnvType.CLIENT)
	public boolean isSmokerFilteringCraftable() {
		return this.smokerFilteringCraftable;
	}

	public void setSmokerFilteringCraftable(boolean smokerFilteringCraftable) {
		this.smokerFilteringCraftable = smokerFilteringCraftable;
	}
}
