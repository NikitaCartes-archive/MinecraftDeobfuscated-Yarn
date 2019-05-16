package net.minecraft.client.gui.screen.ingame;

import com.mojang.blaze3d.platform.GlStateManager;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.screen.recipebook.RecipeBookProvider;
import net.minecraft.client.gui.screen.recipebook.RecipeBookScreen;
import net.minecraft.client.gui.widget.RecipeBookButtonWidget;
import net.minecraft.container.CraftingTableContainer;
import net.minecraft.container.Slot;
import net.minecraft.container.SlotActionType;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
public class CraftingTableScreen extends AbstractContainerScreen<CraftingTableContainer> implements RecipeBookProvider {
	private static final Identifier BG_TEX = new Identifier("textures/gui/container/crafting_table.png");
	private static final Identifier RECIPE_BUTTON_TEX = new Identifier("textures/gui/recipe_button.png");
	private final RecipeBookScreen recipeBookGui = new RecipeBookScreen();
	private boolean isNarrow;

	public CraftingTableScreen(CraftingTableContainer craftingTableContainer, PlayerInventory playerInventory, Component component) {
		super(craftingTableContainer, playerInventory, component);
	}

	@Override
	protected void init() {
		super.init();
		this.isNarrow = this.width < 379;
		this.recipeBookGui.initialize(this.width, this.height, this.minecraft, this.isNarrow, this.container);
		this.left = this.recipeBookGui.findLeftEdge(this.isNarrow, this.width, this.containerWidth);
		this.children.add(this.recipeBookGui);
		this.setInitialFocus(this.recipeBookGui);
		this.addButton(new RecipeBookButtonWidget(this.left + 5, this.height / 2 - 49, 20, 18, 0, 0, 19, RECIPE_BUTTON_TEX, buttonWidget -> {
			this.recipeBookGui.reset(this.isNarrow);
			this.recipeBookGui.toggleOpen();
			this.left = this.recipeBookGui.findLeftEdge(this.isNarrow, this.width, this.containerWidth);
			((RecipeBookButtonWidget)buttonWidget).setPos(this.left + 5, this.height / 2 - 49);
		}));
	}

	@Override
	public void tick() {
		super.tick();
		this.recipeBookGui.update();
	}

	@Override
	public void render(int i, int j, float f) {
		this.renderBackground();
		if (this.recipeBookGui.isOpen() && this.isNarrow) {
			this.drawBackground(f, i, j);
			this.recipeBookGui.render(i, j, f);
		} else {
			this.recipeBookGui.render(i, j, f);
			super.render(i, j, f);
			this.recipeBookGui.drawGhostSlots(this.left, this.top, true, f);
		}

		this.drawMouseoverTooltip(i, j);
		this.recipeBookGui.drawTooltip(this.left, this.top, i, j);
		this.focusOn(this.recipeBookGui);
	}

	@Override
	protected void drawForeground(int i, int j) {
		this.font.draw(this.title.getFormattedText(), 28.0F, 6.0F, 4210752);
		this.font.draw(this.playerInventory.getDisplayName().getFormattedText(), 8.0F, (float)(this.containerHeight - 96 + 2), 4210752);
	}

	@Override
	protected void drawBackground(float f, int i, int j) {
		GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
		this.minecraft.getTextureManager().bindTexture(BG_TEX);
		int k = this.left;
		int l = (this.height - this.containerHeight) / 2;
		this.blit(k, l, 0, 0, this.containerWidth, this.containerHeight);
	}

	@Override
	protected boolean isPointWithinBounds(int i, int j, int k, int l, double d, double e) {
		return (!this.isNarrow || !this.recipeBookGui.isOpen()) && super.isPointWithinBounds(i, j, k, l, d, e);
	}

	@Override
	public boolean mouseClicked(double d, double e, int i) {
		if (this.recipeBookGui.mouseClicked(d, e, i)) {
			return true;
		} else {
			return this.isNarrow && this.recipeBookGui.isOpen() ? true : super.mouseClicked(d, e, i);
		}
	}

	@Override
	protected boolean isClickOutsideBounds(double d, double e, int i, int j, int k) {
		boolean bl = d < (double)i || e < (double)j || d >= (double)(i + this.containerWidth) || e >= (double)(j + this.containerHeight);
		return this.recipeBookGui.isClickOutsideBounds(d, e, this.left, this.top, this.containerWidth, this.containerHeight, k) && bl;
	}

	@Override
	protected void onMouseClick(Slot slot, int i, int j, SlotActionType slotActionType) {
		super.onMouseClick(slot, i, j, slotActionType);
		this.recipeBookGui.slotClicked(slot);
	}

	@Override
	public void refreshRecipeBook() {
		this.recipeBookGui.refresh();
	}

	@Override
	public void removed() {
		this.recipeBookGui.close();
		super.removed();
	}

	@Override
	public RecipeBookScreen getRecipeBookGui() {
		return this.recipeBookGui;
	}
}
