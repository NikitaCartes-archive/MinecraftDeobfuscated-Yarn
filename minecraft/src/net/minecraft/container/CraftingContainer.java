package net.minecraft.container;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.recipe.Recipe;
import net.minecraft.recipe.RecipeFinder;

public abstract class CraftingContainer extends Container {
	public abstract void populateRecipeFinder(RecipeFinder recipeFinder);

	public abstract void clearCraftingSlots();

	public abstract boolean matches(Recipe recipe);

	public abstract int getCraftingResultSlotIndex();

	public abstract int getCraftingWidth();

	public abstract int getCraftingHeight();

	@Environment(EnvType.CLIENT)
	public abstract int getCraftingSlotCount();
}
