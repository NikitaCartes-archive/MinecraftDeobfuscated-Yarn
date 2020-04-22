package net.minecraft.client.gui.screen.ingame;

import com.mojang.blaze3d.systems.RenderSystem;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.screen.recipebook.RecipeBookProvider;
import net.minecraft.client.gui.screen.recipebook.RecipeBookWidget;
import net.minecraft.client.gui.widget.TexturedButtonWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.screen.CraftingScreenHandler;
import net.minecraft.screen.slot.Slot;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
public class CraftingScreen extends HandledScreen<CraftingScreenHandler> implements RecipeBookProvider {
	private static final Identifier TEXTURE = new Identifier("textures/gui/container/crafting_table.png");
	private static final Identifier RECIPE_BUTTON_TEXTURE = new Identifier("textures/gui/recipe_button.png");
	private final RecipeBookWidget recipeBook = new RecipeBookWidget();
	private boolean isNarrow;

	public CraftingScreen(CraftingScreenHandler handler, PlayerInventory inventory, Text title) {
		super(handler, inventory, title);
	}

	@Override
	protected void init() {
		super.init();
		this.isNarrow = this.width < 379;
		this.recipeBook.initialize(this.width, this.height, this.client, this.isNarrow, this.handler);
		this.x = this.recipeBook.findLeftEdge(this.isNarrow, this.width, this.backgroundWidth);
		this.children.add(this.recipeBook);
		this.setInitialFocus(this.recipeBook);
		this.addButton(new TexturedButtonWidget(this.x + 5, this.height / 2 - 49, 20, 18, 0, 0, 19, RECIPE_BUTTON_TEXTURE, buttonWidget -> {
			this.recipeBook.reset(this.isNarrow);
			this.recipeBook.toggleOpen();
			this.x = this.recipeBook.findLeftEdge(this.isNarrow, this.width, this.backgroundWidth);
			((TexturedButtonWidget)buttonWidget).setPos(this.x + 5, this.height / 2 - 49);
		}));
	}

	@Override
	public void tick() {
		super.tick();
		this.recipeBook.update();
	}

	@Override
	public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
		this.renderBackground(matrices);
		if (this.recipeBook.isOpen() && this.isNarrow) {
			this.drawBackground(matrices, delta, mouseX, mouseY);
			this.recipeBook.render(matrices, mouseX, mouseY, delta);
		} else {
			this.recipeBook.render(matrices, mouseX, mouseY, delta);
			super.render(matrices, mouseX, mouseY, delta);
			this.recipeBook.drawGhostSlots(matrices, this.x, this.y, true, delta);
		}

		this.drawMouseoverTooltip(matrices, mouseX, mouseY);
		this.recipeBook.drawTooltip(matrices, this.x, this.y, mouseX, mouseY);
	}

	@Override
	protected void drawForeground(MatrixStack matrixStack, int i, int j) {
		this.textRenderer.draw(matrixStack, this.title, 28.0F, 6.0F, 4210752);
		this.textRenderer.draw(matrixStack, this.playerInventory.getDisplayName(), 8.0F, (float)(this.backgroundHeight - 96 + 2), 4210752);
	}

	@Override
	protected void drawBackground(MatrixStack matrixStack, float f, int mouseY, int i) {
		RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
		this.client.getTextureManager().bindTexture(TEXTURE);
		int j = this.x;
		int k = (this.height - this.backgroundHeight) / 2;
		this.drawTexture(matrixStack, j, k, 0, 0, this.backgroundWidth, this.backgroundHeight);
	}

	@Override
	protected boolean isPointWithinBounds(int xPosition, int yPosition, int width, int height, double pointX, double pointY) {
		return (!this.isNarrow || !this.recipeBook.isOpen()) && super.isPointWithinBounds(xPosition, yPosition, width, height, pointX, pointY);
	}

	@Override
	public boolean mouseClicked(double mouseX, double mouseY, int button) {
		if (this.recipeBook.mouseClicked(mouseX, mouseY, button)) {
			return true;
		} else {
			return this.isNarrow && this.recipeBook.isOpen() ? true : super.mouseClicked(mouseX, mouseY, button);
		}
	}

	@Override
	protected boolean isClickOutsideBounds(double mouseX, double mouseY, int left, int top, int button) {
		boolean bl = mouseX < (double)left
			|| mouseY < (double)top
			|| mouseX >= (double)(left + this.backgroundWidth)
			|| mouseY >= (double)(top + this.backgroundHeight);
		return this.recipeBook.isClickOutsideBounds(mouseX, mouseY, this.x, this.y, this.backgroundWidth, this.backgroundHeight, button) && bl;
	}

	@Override
	protected void onMouseClick(Slot slot, int invSlot, int clickData, SlotActionType actionType) {
		super.onMouseClick(slot, invSlot, clickData, actionType);
		this.recipeBook.slotClicked(slot);
	}

	@Override
	public void refreshRecipeBook() {
		this.recipeBook.refresh();
	}

	@Override
	public void removed() {
		this.recipeBook.close();
		super.removed();
	}

	@Override
	public RecipeBookWidget getRecipeBookWidget() {
		return this.recipeBook;
	}
}
