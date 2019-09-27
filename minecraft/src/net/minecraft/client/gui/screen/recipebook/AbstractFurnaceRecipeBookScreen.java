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
import net.minecraft.container.Slot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.Recipe;
import net.minecraft.util.DefaultedList;

@Environment(EnvType.CLIENT)
public abstract class AbstractFurnaceRecipeBookScreen extends RecipeBookWidget {
	private Iterator<Item> field_3153;
	private Set<Item> field_3149;
	private Slot outputSlot;
	private Item field_3152;
	private float field_3151;

	@Override
	protected boolean toggleFilteringCraftable() {
		boolean bl = !this.isFilteringCraftable();
		this.setFilteringCraftable(bl);
		return bl;
	}

	protected abstract boolean isFilteringCraftable();

	protected abstract void setFilteringCraftable(boolean bl);

	@Override
	public boolean isOpen() {
		return this.isGuiOpen();
	}

	protected abstract boolean isGuiOpen();

	@Override
	protected void setOpen(boolean bl) {
		this.setGuiOpen(bl);
		if (!bl) {
			this.recipesArea.hideAlternates();
		}

		this.sendBookDataPacket();
	}

	protected abstract void setGuiOpen(boolean bl);

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
		if (slot != null && slot.id < this.craftingContainer.getCraftingSlotCount()) {
			this.outputSlot = null;
		}
	}

	@Override
	public void showGhostRecipe(Recipe<?> recipe, List<Slot> list) {
		ItemStack itemStack = recipe.getOutput();
		this.ghostSlots.setRecipe(recipe);
		this.ghostSlots.addSlot(Ingredient.ofStacks(itemStack), ((Slot)list.get(2)).xPosition, ((Slot)list.get(2)).yPosition);
		DefaultedList<Ingredient> defaultedList = recipe.getPreviewInputs();
		this.outputSlot = (Slot)list.get(1);
		if (this.field_3149 == null) {
			this.field_3149 = this.getAllowedFuels();
		}

		this.field_3153 = this.field_3149.iterator();
		this.field_3152 = null;
		Iterator<Ingredient> iterator = defaultedList.iterator();

		for (int i = 0; i < 2; i++) {
			if (!iterator.hasNext()) {
				return;
			}

			Ingredient ingredient = (Ingredient)iterator.next();
			if (!ingredient.isEmpty()) {
				Slot slot = (Slot)list.get(i);
				this.ghostSlots.addSlot(ingredient, slot.xPosition, slot.yPosition);
			}
		}
	}

	protected abstract Set<Item> getAllowedFuels();

	@Override
	public void drawGhostSlots(int i, int j, boolean bl, float f) {
		super.drawGhostSlots(i, j, bl, f);
		if (this.outputSlot != null) {
			if (!Screen.hasControlDown()) {
				this.field_3151 += f;
			}

			int k = this.outputSlot.xPosition + i;
			int l = this.outputSlot.yPosition + j;
			DrawableHelper.fill(k, l, k + 16, l + 16, 822018048);
			this.client.getItemRenderer().renderGuiItem(this.client.player, this.method_2658().getStackForRender(), k, l);
			RenderSystem.depthFunc(516);
			DrawableHelper.fill(k, l, k + 16, l + 16, 822083583);
			RenderSystem.depthFunc(515);
		}
	}

	private Item method_2658() {
		if (this.field_3152 == null || this.field_3151 > 30.0F) {
			this.field_3151 = 0.0F;
			if (this.field_3153 == null || !this.field_3153.hasNext()) {
				if (this.field_3149 == null) {
					this.field_3149 = this.getAllowedFuels();
				}

				this.field_3153 = this.field_3149.iterator();
			}

			this.field_3152 = (Item)this.field_3153.next();
		}

		return this.field_3152;
	}
}
