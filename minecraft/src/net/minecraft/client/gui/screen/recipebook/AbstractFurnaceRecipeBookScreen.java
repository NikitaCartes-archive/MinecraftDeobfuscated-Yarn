package net.minecraft.client.gui.screen.recipebook;

import com.mojang.blaze3d.systems.RenderSystem;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.Recipe;
import net.minecraft.screen.slot.Slot;
import net.minecraft.util.DefaultedList;

@Environment(EnvType.CLIENT)
public abstract class AbstractFurnaceRecipeBookScreen extends RecipeBookWidget {
	private Iterator<Item> fuelIterator;
	private Set<Item> fuels;
	private Slot outputSlot;
	private Item currentItem;
	private float frameTime;

	@Override
	protected boolean toggleFilteringCraftable() {
		boolean bl = !this.isFilteringCraftable();
		this.setFilteringCraftable(bl);
		return bl;
	}

	protected abstract boolean isFilteringCraftable();

	protected abstract void setFilteringCraftable(boolean filteringCraftable);

	@Override
	public boolean isOpen() {
		return this.isGuiOpen();
	}

	protected abstract boolean isGuiOpen();

	@Override
	protected void setOpen(boolean opened) {
		this.setGuiOpen(opened);
		if (!opened) {
			this.recipesArea.hideAlternates();
		}

		this.sendBookDataPacket();
	}

	protected abstract void setGuiOpen(boolean opened);

	@Override
	protected void setBookButtonTexture() {
		this.toggleCraftableButton.setTextureUV(152, 182, 28, 18, TEXTURE);
	}

	@Override
	protected String getCraftableButtonText() {
		return I18n.translate(this.toggleCraftableButton.isToggled() ? this.getToggleCraftableButtonText() : "gui.recipebook.toggleRecipes.all");
	}

	protected abstract String getToggleCraftableButtonText();

	@Override
	public void slotClicked(@Nullable Slot slot) {
		super.slotClicked(slot);
		if (slot != null && slot.id < this.craftingScreenHandler.getCraftingSlotCount()) {
			this.outputSlot = null;
		}
	}

	@Override
	public void showGhostRecipe(Recipe<?> recipe, List<Slot> slots) {
		ItemStack itemStack = recipe.getOutput();
		this.ghostSlots.setRecipe(recipe);
		this.ghostSlots.addSlot(Ingredient.ofStacks(itemStack), ((Slot)slots.get(2)).xPosition, ((Slot)slots.get(2)).yPosition);
		DefaultedList<Ingredient> defaultedList = recipe.getPreviewInputs();
		this.outputSlot = (Slot)slots.get(1);
		if (this.fuels == null) {
			this.fuels = this.getAllowedFuels();
		}

		this.fuelIterator = this.fuels.iterator();
		this.currentItem = null;
		Iterator<Ingredient> iterator = defaultedList.iterator();

		for (int i = 0; i < 2; i++) {
			if (!iterator.hasNext()) {
				return;
			}

			Ingredient ingredient = (Ingredient)iterator.next();
			if (!ingredient.isEmpty()) {
				Slot slot = (Slot)slots.get(i);
				this.ghostSlots.addSlot(ingredient, slot.xPosition, slot.yPosition);
			}
		}
	}

	protected abstract Set<Item> getAllowedFuels();

	@Override
	public void drawGhostSlots(int left, int top, boolean isBig, float lastFrameDuration) {
		super.drawGhostSlots(left, top, isBig, lastFrameDuration);
		if (this.outputSlot != null) {
			if (!Screen.hasControlDown()) {
				this.frameTime += lastFrameDuration;
			}

			int i = this.outputSlot.xPosition + left;
			int j = this.outputSlot.yPosition + top;
			DrawableHelper.fill(i, j, i + 16, j + 16, 822018048);
			this.client.getItemRenderer().renderGuiItem(this.client.player, this.getItem().getStackForRender(), i, j);
			RenderSystem.depthFunc(516);
			DrawableHelper.fill(i, j, i + 16, j + 16, 822083583);
			RenderSystem.depthFunc(515);
		}
	}

	private Item getItem() {
		if (this.currentItem == null || this.frameTime > 30.0F) {
			this.frameTime = 0.0F;
			if (this.fuelIterator == null || !this.fuelIterator.hasNext()) {
				if (this.fuels == null) {
					this.fuels = this.getAllowedFuels();
				}

				this.fuelIterator = this.fuels.iterator();
			}

			this.currentItem = (Item)this.fuelIterator.next();
		}

		return this.currentItem;
	}
}
