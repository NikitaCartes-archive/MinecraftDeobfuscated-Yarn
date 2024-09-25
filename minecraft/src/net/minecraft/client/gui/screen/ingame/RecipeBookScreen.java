package net.minecraft.client.gui.screen.ingame;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.ScreenPos;
import net.minecraft.client.gui.screen.recipebook.RecipeBookProvider;
import net.minecraft.client.gui.screen.recipebook.RecipeBookWidget;
import net.minecraft.client.gui.widget.TexturedButtonWidget;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.screen.AbstractRecipeScreenHandler;
import net.minecraft.screen.slot.Slot;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.text.Text;

@Environment(EnvType.CLIENT)
public abstract class RecipeBookScreen<T extends AbstractRecipeScreenHandler> extends HandledScreen<T> implements RecipeBookProvider {
	private final RecipeBookWidget<?> recipeBook;
	private boolean narrow;

	public RecipeBookScreen(T handler, RecipeBookWidget<?> recipeBook, PlayerInventory inventory, Text title) {
		super(handler, inventory, title);
		this.recipeBook = recipeBook;
	}

	@Override
	protected void init() {
		super.init();
		this.narrow = this.width < 379;
		this.recipeBook.initialize(this.width, this.height, this.client, this.narrow);
		this.x = this.recipeBook.findLeftEdge(this.width, this.backgroundWidth);
		this.addRecipeBook();
	}

	protected abstract ScreenPos getRecipeBookButtonPos();

	private void addRecipeBook() {
		ScreenPos screenPos = this.getRecipeBookButtonPos();
		this.addDrawableChild(new TexturedButtonWidget(screenPos.x(), screenPos.y(), 20, 18, RecipeBookWidget.BUTTON_TEXTURES, button -> {
			this.recipeBook.toggleOpen();
			this.x = this.recipeBook.findLeftEdge(this.width, this.backgroundWidth);
			ScreenPos screenPosx = this.getRecipeBookButtonPos();
			button.setPosition(screenPosx.x(), screenPosx.y());
			this.onRecipeBookToggled();
		}));
		this.addSelectableChild(this.recipeBook);
	}

	protected void onRecipeBookToggled() {
	}

	@Override
	public void render(DrawContext context, int mouseX, int mouseY, float delta) {
		if (this.recipeBook.isOpen() && this.narrow) {
			this.renderBackground(context, mouseX, mouseY, delta);
		} else {
			super.render(context, mouseX, mouseY, delta);
		}

		this.recipeBook.render(context, mouseX, mouseY, delta);
		this.drawMouseoverTooltip(context, mouseX, mouseY);
		this.recipeBook.drawTooltip(context, mouseX, mouseY, this.focusedSlot);
	}

	@Override
	protected void drawSlots(DrawContext context) {
		super.drawSlots(context);
		this.recipeBook.drawGhostSlots(context, this.shouldAddPaddingToGhostResult());
	}

	protected boolean shouldAddPaddingToGhostResult() {
		return true;
	}

	@Override
	public boolean charTyped(char chr, int modifiers) {
		return this.recipeBook.charTyped(chr, modifiers) ? true : super.charTyped(chr, modifiers);
	}

	@Override
	public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
		return this.recipeBook.keyPressed(keyCode, scanCode, modifiers) ? true : super.keyPressed(keyCode, scanCode, modifiers);
	}

	@Override
	public boolean mouseClicked(double mouseX, double mouseY, int button) {
		if (this.recipeBook.mouseClicked(mouseX, mouseY, button)) {
			this.setFocused(this.recipeBook);
			return true;
		} else {
			return this.narrow && this.recipeBook.isOpen() ? true : super.mouseClicked(mouseX, mouseY, button);
		}
	}

	@Override
	protected boolean isPointWithinBounds(int x, int y, int width, int height, double pointX, double pointY) {
		return (!this.narrow || !this.recipeBook.isOpen()) && super.isPointWithinBounds(x, y, width, height, pointX, pointY);
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
	protected void onMouseClick(Slot slot, int slotId, int button, SlotActionType actionType) {
		super.onMouseClick(slot, slotId, button, actionType);
		this.recipeBook.onMouseClick(slot);
	}

	@Override
	public void handledScreenTick() {
		super.handledScreenTick();
		this.recipeBook.update();
	}

	@Override
	public void refreshRecipeBook() {
		this.recipeBook.refresh();
	}

	@Override
	public RecipeBookWidget getRecipeBookWidget() {
		return this.recipeBook;
	}
}
