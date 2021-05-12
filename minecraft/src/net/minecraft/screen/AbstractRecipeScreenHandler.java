package net.minecraft.screen;

import net.minecraft.inventory.Inventory;
import net.minecraft.recipe.InputSlotFiller;
import net.minecraft.recipe.Recipe;
import net.minecraft.recipe.RecipeMatcher;
import net.minecraft.recipe.book.RecipeBookCategory;
import net.minecraft.server.network.ServerPlayerEntity;

public abstract class AbstractRecipeScreenHandler<C extends Inventory> extends ScreenHandler {
	public AbstractRecipeScreenHandler(ScreenHandlerType<?> screenHandlerType, int i) {
		super(screenHandlerType, i);
	}

	public void fillInputSlots(boolean craftAll, Recipe<?> recipe, ServerPlayerEntity player) {
		new InputSlotFiller<>(this).fillInputSlots(player, (Recipe<C>)recipe, craftAll);
	}

	public abstract void populateRecipeFinder(RecipeMatcher finder);

	public abstract void clearCraftingSlots();

	public abstract boolean matches(Recipe<? super C> recipe);

	public abstract int getCraftingResultSlotIndex();

	public abstract int getCraftingWidth();

	public abstract int getCraftingHeight();

	public abstract int getCraftingSlotCount();

	public abstract RecipeBookCategory getCategory();

	public abstract boolean canInsertIntoSlot(int index);
}
