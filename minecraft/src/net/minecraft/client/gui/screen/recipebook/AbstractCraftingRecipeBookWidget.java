package net.minecraft.client.gui.screen.recipebook;

import java.util.List;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.screen.ButtonTextures;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.RecipeEntry;
import net.minecraft.recipe.RecipeFinder;
import net.minecraft.recipe.RecipeGridAligner;
import net.minecraft.recipe.book.RecipeBook;
import net.minecraft.screen.AbstractCraftingScreenHandler;
import net.minecraft.screen.slot.Slot;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
public class AbstractCraftingRecipeBookWidget extends RecipeBookWidget<AbstractCraftingScreenHandler> {
	private static final ButtonTextures TEXTURES = new ButtonTextures(
		Identifier.ofVanilla("recipe_book/filter_enabled"),
		Identifier.ofVanilla("recipe_book/filter_disabled"),
		Identifier.ofVanilla("recipe_book/filter_enabled_highlighted"),
		Identifier.ofVanilla("recipe_book/filter_disabled_highlighted")
	);
	private static final Text TOGGLE_CRAFTABLE_TEXT = Text.translatable("gui.recipebook.toggleRecipes.craftable");

	public AbstractCraftingRecipeBookWidget(AbstractCraftingScreenHandler screenHandler) {
		super(screenHandler);
	}

	@Override
	protected boolean isValid(Slot slot) {
		return this.craftingScreenHandler.getOutputSlot() == slot || this.craftingScreenHandler.getInputSlots().contains(slot);
	}

	@Override
	protected void showGhostRecipe(GhostRecipe ghostRecipe, RecipeEntry<?> entry) {
		ItemStack itemStack = entry.value().getResult(this.client.world.getRegistryManager());
		Slot slot = this.craftingScreenHandler.getOutputSlot();
		ghostRecipe.put(itemStack, slot);
		List<Slot> list = this.craftingScreenHandler.getInputSlots();
		RecipeGridAligner.alignRecipeToGrid(
			this.craftingScreenHandler.getWidth(),
			this.craftingScreenHandler.getHeight(),
			entry,
			entry.value().getIngredientPlacement().getPlacementSlots(),
			(slotx, index, x, y) -> slotx.ifPresent(slot2 -> {
					Slot slotxx = (Slot)list.get(index);
					ghostRecipe.put(slot2.possibleItems(), slotxx);
				})
		);
	}

	@Override
	protected void setBookButtonTexture() {
		this.toggleCraftableButton.setTextures(TEXTURES);
	}

	@Override
	protected Text getToggleCraftableButtonText() {
		return TOGGLE_CRAFTABLE_TEXT;
	}

	@Override
	protected void populateRecipes(RecipeResultCollection recipeResultCollection, RecipeFinder recipeFinder, RecipeBook recipeBook) {
		recipeResultCollection.populateRecipes(recipeFinder, this.craftingScreenHandler.getWidth(), this.craftingScreenHandler.getHeight(), recipeBook);
	}
}
