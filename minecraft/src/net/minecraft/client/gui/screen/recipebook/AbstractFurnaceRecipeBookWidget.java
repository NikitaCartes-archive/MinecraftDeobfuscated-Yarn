package net.minecraft.client.gui.screen.recipebook;

import java.util.List;
import java.util.Optional;
import java.util.SequencedSet;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.screen.ButtonTextures;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.item.FuelRegistry;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.IngredientPlacement;
import net.minecraft.recipe.RecipeEntry;
import net.minecraft.recipe.RecipeFinder;
import net.minecraft.recipe.book.RecipeBook;
import net.minecraft.screen.AbstractFurnaceScreenHandler;
import net.minecraft.screen.slot.Slot;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
public class AbstractFurnaceRecipeBookWidget extends RecipeBookWidget<AbstractFurnaceScreenHandler> {
	private static final ButtonTextures TEXTURES = new ButtonTextures(
		Identifier.ofVanilla("recipe_book/furnace_filter_enabled"),
		Identifier.ofVanilla("recipe_book/furnace_filter_disabled"),
		Identifier.ofVanilla("recipe_book/furnace_filter_enabled_highlighted"),
		Identifier.ofVanilla("recipe_book/furnace_filter_disabled_highlighted")
	);
	private final Text toggleCraftableButtonText;
	@Nullable
	private List<ItemStack> fuels;

	public AbstractFurnaceRecipeBookWidget(AbstractFurnaceScreenHandler screenHandler, Text toggleCraftableButtonText) {
		super(screenHandler);
		this.toggleCraftableButtonText = toggleCraftableButtonText;
	}

	@Override
	protected void setBookButtonTexture() {
		this.toggleCraftableButton.setTextures(TEXTURES);
	}

	@Override
	protected boolean isValid(Slot slot) {
		return switch (slot.id) {
			case 0, 1, 2 -> true;
			default -> false;
		};
	}

	@Override
	protected void showGhostRecipe(GhostRecipe ghostRecipe, RecipeEntry<?> entry) {
		ClientWorld clientWorld = this.client.world;
		ItemStack itemStack = entry.value().getResult(clientWorld.getRegistryManager());
		Slot slot = this.craftingScreenHandler.getOutputSlot();
		ghostRecipe.put(itemStack, slot);
		List<Optional<IngredientPlacement.PlacementSlot>> list = entry.value().getIngredientPlacement().getPlacementSlots();
		if (!list.isEmpty()) {
			((Optional)list.getFirst()).ifPresent(placementSlot -> {
				Slot slotx = this.craftingScreenHandler.slots.get(0);
				ghostRecipe.put(placementSlot.possibleItems(), slotx);
			});
		}

		Slot slot2 = this.craftingScreenHandler.slots.get(1);
		if (slot2.getStack().isEmpty()) {
			if (list.size() > 1) {
				((Optional)list.get(1)).ifPresent(placementSlot -> ghostRecipe.put(placementSlot.possibleItems(), slot2));
			} else {
				if (this.fuels == null) {
					this.fuels = this.getAllowedFuels(clientWorld.getFuelRegistry()).stream().map(ItemStack::new).toList();
				}

				ghostRecipe.put(this.fuels, slot2);
			}
		}
	}

	private SequencedSet<Item> getAllowedFuels(FuelRegistry fuelRegistry) {
		return fuelRegistry.getFuelItems();
	}

	@Override
	protected Text getToggleCraftableButtonText() {
		return this.toggleCraftableButtonText;
	}

	@Override
	protected void populateRecipes(RecipeResultCollection recipeResultCollection, RecipeFinder recipeFinder, RecipeBook recipeBook) {
		recipeResultCollection.populateRecipes(recipeFinder, 1, 1, recipeBook);
	}
}
