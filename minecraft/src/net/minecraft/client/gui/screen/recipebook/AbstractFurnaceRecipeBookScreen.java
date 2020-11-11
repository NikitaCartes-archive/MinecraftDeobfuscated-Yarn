package net.minecraft.client.gui.screen.recipebook;

import java.util.Iterator;
import java.util.List;
import java.util.Set;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.Recipe;
import net.minecraft.screen.slot.Slot;
import net.minecraft.util.collection.DefaultedList;

@Environment(EnvType.CLIENT)
public abstract class AbstractFurnaceRecipeBookScreen extends RecipeBookWidget {
	@Nullable
	private Ingredient fuels;

	@Override
	protected void setBookButtonTexture() {
		this.toggleCraftableButton.setTextureUV(152, 182, 28, 18, TEXTURE);
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
		ItemStack itemStack = recipe.getOutput();
		this.ghostSlots.setRecipe(recipe);
		this.ghostSlots.addSlot(Ingredient.ofStacks(itemStack), ((Slot)slots.get(2)).x, ((Slot)slots.get(2)).y);
		DefaultedList<Ingredient> defaultedList = recipe.getPreviewInputs();
		Slot slot = (Slot)slots.get(1);
		if (slot.getStack().isEmpty()) {
			if (this.fuels == null) {
				this.fuels = Ingredient.ofStacks(this.getAllowedFuels().stream().map(ItemStack::new));
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
