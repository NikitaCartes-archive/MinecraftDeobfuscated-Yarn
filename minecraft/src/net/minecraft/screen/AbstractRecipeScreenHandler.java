package net.minecraft.screen;

import net.minecraft.recipe.InputSlotFiller;
import net.minecraft.recipe.Recipe;
import net.minecraft.recipe.RecipeEntry;
import net.minecraft.recipe.RecipeMatcher;
import net.minecraft.recipe.book.RecipeBookCategory;
import net.minecraft.recipe.input.RecipeInput;
import net.minecraft.server.network.ServerPlayerEntity;

public abstract class AbstractRecipeScreenHandler<I extends RecipeInput, R extends Recipe<I>> extends ScreenHandler {
	public AbstractRecipeScreenHandler(ScreenHandlerType<?> screenHandlerType, int i) {
		super(screenHandlerType, i);
	}

	public void fillInputSlots(boolean craftAll, RecipeEntry<?> recipe, ServerPlayerEntity player) {
		RecipeEntry<R> recipeEntry = (RecipeEntry<R>)recipe;
		this.onInputSlotFillStart();

		try {
			new InputSlotFiller<>(this).fillInputSlots(player, recipeEntry, craftAll);
		} finally {
			this.onInputSlotFillFinish((RecipeEntry<R>)recipe);
		}
	}

	protected void onInputSlotFillStart() {
	}

	protected void onInputSlotFillFinish(RecipeEntry<R> recipe) {
	}

	public abstract void populateRecipeFinder(RecipeMatcher finder);

	public abstract void clearCraftingSlots();

	public abstract boolean matches(RecipeEntry<R> recipe);

	public abstract int getCraftingResultSlotIndex();

	public abstract int getCraftingWidth();

	public abstract int getCraftingHeight();

	public abstract int getCraftingSlotCount();

	public abstract RecipeBookCategory getCategory();

	public abstract boolean canInsertIntoSlot(int index);
}
