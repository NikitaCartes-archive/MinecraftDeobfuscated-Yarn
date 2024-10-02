package net.minecraft.client.gui.screen.recipebook;

import java.util.List;
import java.util.Objects;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.screen.ButtonTextures;
import net.minecraft.client.recipebook.RecipeBookType;
import net.minecraft.item.Items;
import net.minecraft.recipe.RecipeFinder;
import net.minecraft.recipe.RecipeGridAligner;
import net.minecraft.recipe.book.RecipeBookGroup;
import net.minecraft.recipe.display.RecipeDisplay;
import net.minecraft.recipe.display.ShapedCraftingRecipeDisplay;
import net.minecraft.recipe.display.ShapelessCraftingRecipeDisplay;
import net.minecraft.recipe.display.SlotDisplay;
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
	private static final List<RecipeBookWidget.Tab> TABS = List.of(
		new RecipeBookWidget.Tab(RecipeBookType.CRAFTING),
		new RecipeBookWidget.Tab(Items.IRON_AXE, Items.GOLDEN_SWORD, RecipeBookGroup.CRAFTING_EQUIPMENT),
		new RecipeBookWidget.Tab(Items.BRICKS, RecipeBookGroup.CRAFTING_BUILDING_BLOCKS),
		new RecipeBookWidget.Tab(Items.LAVA_BUCKET, Items.APPLE, RecipeBookGroup.CRAFTING_MISC),
		new RecipeBookWidget.Tab(Items.REDSTONE, RecipeBookGroup.CRAFTING_REDSTONE)
	);

	public AbstractCraftingRecipeBookWidget(AbstractCraftingScreenHandler screenHandler) {
		super(screenHandler, TABS);
	}

	@Override
	protected boolean isValid(Slot slot) {
		return this.craftingScreenHandler.getOutputSlot() == slot || this.craftingScreenHandler.getInputSlots().contains(slot);
	}

	private boolean canDisplay(RecipeDisplay display) {
		int i = this.craftingScreenHandler.getWidth();
		int j = this.craftingScreenHandler.getHeight();
		Objects.requireNonNull(display);

		return switch (display) {
			case ShapedCraftingRecipeDisplay shapedCraftingRecipeDisplay -> i >= shapedCraftingRecipeDisplay.width() && j >= shapedCraftingRecipeDisplay.height();
			case ShapelessCraftingRecipeDisplay shapelessCraftingRecipeDisplay -> i * j >= shapelessCraftingRecipeDisplay.ingredients().size();
			default -> false;
		};
	}

	@Override
	protected void showGhostRecipe(GhostRecipe ghostRecipe, RecipeDisplay display, SlotDisplay.Context context) {
		ghostRecipe.addResults(this.craftingScreenHandler.getOutputSlot(), context, display.result());
		Objects.requireNonNull(display);
		switch (display) {
			case ShapedCraftingRecipeDisplay shapedCraftingRecipeDisplay:
				List<Slot> list = this.craftingScreenHandler.getInputSlots();
				RecipeGridAligner.alignRecipeToGrid(
					this.craftingScreenHandler.getWidth(),
					this.craftingScreenHandler.getHeight(),
					shapedCraftingRecipeDisplay.width(),
					shapedCraftingRecipeDisplay.height(),
					shapedCraftingRecipeDisplay.ingredients(),
					(slot, index, x, y) -> {
						Slot slot2 = (Slot)list.get(index);
						ghostRecipe.addInputs(slot2, context, slot);
					}
				);
				break;
			case ShapelessCraftingRecipeDisplay shapelessCraftingRecipeDisplay:
				label15: {
					List<Slot> list2 = this.craftingScreenHandler.getInputSlots();
					int i = Math.min(shapelessCraftingRecipeDisplay.ingredients().size(), list2.size());

					for (int j = 0; j < i; j++) {
						ghostRecipe.addInputs((Slot)list2.get(j), context, (SlotDisplay)shapelessCraftingRecipeDisplay.ingredients().get(j));
					}
					break label15;
				}
		}
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
	protected void populateRecipes(RecipeResultCollection recipeResultCollection, RecipeFinder recipeFinder) {
		recipeResultCollection.populateRecipes(recipeFinder, this::canDisplay);
	}
}
