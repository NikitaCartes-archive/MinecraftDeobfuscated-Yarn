package net.minecraft.screen;

import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.recipe.RecipeEntry;
import net.minecraft.recipe.RecipeFinder;
import net.minecraft.recipe.book.RecipeBookType;
import net.minecraft.server.world.ServerWorld;

public abstract class AbstractRecipeScreenHandler extends ScreenHandler {
	public AbstractRecipeScreenHandler(ScreenHandlerType<?> screenHandlerType, int i) {
		super(screenHandlerType, i);
	}

	public abstract AbstractRecipeScreenHandler.PostFillAction fillInputSlots(
		boolean craftAll, boolean creative, RecipeEntry<?> recipe, ServerWorld world, PlayerInventory inventory
	);

	public abstract void populateRecipeFinder(RecipeFinder finder);

	public abstract RecipeBookType getCategory();

	public static enum PostFillAction {
		NOTHING,
		PLACE_GHOST_RECIPE;
	}
}
