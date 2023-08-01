package net.minecraft.client.gui.screen.recipebook;

import java.util.Iterator;
import java.util.List;
import java.util.Set;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.screen.ButtonTextures;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.Recipe;
import net.minecraft.screen.slot.Slot;
import net.minecraft.util.Identifier;
import net.minecraft.util.collection.DefaultedList;

@Environment(EnvType.CLIENT)
public abstract class AbstractFurnaceRecipeBookScreen extends RecipeBookWidget {
	private static final ButtonTextures TEXTURES = new ButtonTextures(
		new Identifier("recipe_book/furnace_filter_enabled"),
		new Identifier("recipe_book/furnace_filter_disabled"),
		new Identifier("recipe_book/furnace_filter_enabled_highlighted"),
		new Identifier("recipe_book/furnace_filter_disabled_highlighted")
	);
	@Nullable
	private Ingredient fuels;

	@Override
	protected void setBookButtonTexture() {
		this.toggleCraftableButton.setTextures(TEXTURES);
	}

	@Override
	public void slotClicked(@Nullable Slot slot) {
		super.slotClicked(slot);
		if (slot != null && slot.id < this.craftingScreenHandler.getCraftingSlotCount()) {
			this.ghostSlots.reset();
		}
	}

	@Override
	public void showGhostRecipe(Recipe<?> recipe, List<Slot> slots) {
		ItemStack itemStack = recipe.getOutput(this.client.world.getRegistryManager());
		this.ghostSlots.setRecipe(recipe);
		this.ghostSlots.addSlot(Ingredient.ofStacks(itemStack), ((Slot)slots.get(2)).x, ((Slot)slots.get(2)).y);
		DefaultedList<Ingredient> defaultedList = recipe.getIngredients();
		Slot slot = (Slot)slots.get(1);
		if (slot.getStack().isEmpty()) {
			if (this.fuels == null) {
				this.fuels = Ingredient.ofStacks(this.getAllowedFuels().stream().filter(item -> item.isEnabled(this.client.world.getEnabledFeatures())).map(ItemStack::new));
			}

			this.ghostSlots.addSlot(this.fuels, slot.x, slot.y);
		}

		Iterator<Ingredient> iterator = defaultedList.iterator();

		for (int i = 0; i < 2; i++) {
			if (!iterator.hasNext()) {
				return;
			}

			Ingredient ingredient = (Ingredient)iterator.next();
			if (!ingredient.isEmpty()) {
				Slot slot2 = (Slot)slots.get(i);
				this.ghostSlots.addSlot(ingredient, slot2.x, slot2.y);
			}
		}
	}

	protected abstract Set<Item> getAllowedFuels();
}
