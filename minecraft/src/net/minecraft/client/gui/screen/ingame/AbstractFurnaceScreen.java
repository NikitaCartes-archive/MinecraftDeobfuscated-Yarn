package net.minecraft.client.gui.screen.ingame;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.recipebook.AbstractFurnaceRecipeBookScreen;
import net.minecraft.client.gui.screen.recipebook.RecipeBookProvider;
import net.minecraft.client.gui.screen.recipebook.RecipeBookWidget;
import net.minecraft.client.gui.widget.TexturedButtonWidget;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.screen.AbstractFurnaceScreenHandler;
import net.minecraft.screen.slot.Slot;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
public abstract class AbstractFurnaceScreen<T extends AbstractFurnaceScreenHandler> extends HandledScreen<T> implements RecipeBookProvider {
	public final AbstractFurnaceRecipeBookScreen recipeBook;
	private boolean narrow;
	private final Identifier background;
	private final Identifier litProgressTexture;
	private final Identifier burnProgressTexture;

	public AbstractFurnaceScreen(
		T handler,
		AbstractFurnaceRecipeBookScreen recipeBook,
		PlayerInventory inventory,
		Text title,
		Identifier background,
		Identifier litProgressTexture,
		Identifier burnProgressTexture
	) {
		super(handler, inventory, title);
		this.recipeBook = recipeBook;
		this.background = background;
		this.litProgressTexture = litProgressTexture;
		this.burnProgressTexture = burnProgressTexture;
	}

	@Override
	public void init() {
		super.init();
		this.narrow = this.width < 379;
		this.recipeBook.initialize(this.width, this.height, this.client, this.narrow, this.handler);
		this.x = this.recipeBook.findLeftEdge(this.width, this.backgroundWidth);
		this.addDrawableChild(new TexturedButtonWidget(this.x + 20, this.height / 2 - 49, 20, 18, RecipeBookWidget.BUTTON_TEXTURES, button -> {
			this.recipeBook.toggleOpen();
			this.x = this.recipeBook.findLeftEdge(this.width, this.backgroundWidth);
			button.setPosition(this.x + 20, this.height / 2 - 49);
		}));
		this.titleX = (this.backgroundWidth - this.textRenderer.getWidth(this.title)) / 2;
	}

	@Override
	public void handledScreenTick() {
		super.handledScreenTick();
		this.recipeBook.update();
	}

	@Override
	public void render(DrawContext context, int mouseX, int mouseY, float delta) {
		if (this.recipeBook.isOpen() && this.narrow) {
			this.renderBackground(context, mouseX, mouseY, delta);
			this.recipeBook.render(context, mouseX, mouseY, delta);
		} else {
			super.render(context, mouseX, mouseY, delta);
			this.recipeBook.render(context, mouseX, mouseY, delta);
			this.recipeBook.drawGhostSlots(context, this.x, this.y, true, delta);
		}

		this.drawMouseoverTooltip(context, mouseX, mouseY);
		this.recipeBook.drawTooltip(context, this.x, this.y, mouseX, mouseY);
	}

	@Override
	protected void drawBackground(DrawContext context, float delta, int mouseX, int mouseY) {
		int i = this.x;
		int j = this.y;
		context.drawTexture(this.background, i, j, 0, 0, this.backgroundWidth, this.backgroundHeight);
		if (this.handler.isBurning()) {
			int k = this.handler.getFuelProgress();
			context.drawGuiTexture(this.litProgressTexture, 14, 14, 0, 12 - k, i + 56, j + 36 + 12 - k, 14, k + 1);
		}

		int k = this.handler.getCookProgress();
		context.drawGuiTexture(this.burnProgressTexture, 24, 16, 0, 0, i + 79, j + 34, k + 1, 16);
	}

	@Override
	public boolean mouseClicked(double mouseX, double mouseY, int button) {
		if (this.recipeBook.mouseClicked(mouseX, mouseY, button)) {
			return true;
		} else {
			return this.narrow && this.recipeBook.isOpen() ? true : super.mouseClicked(mouseX, mouseY, button);
		}
	}

	@Override
	protected void onMouseClick(Slot slot, int slotId, int button, SlotActionType actionType) {
		super.onMouseClick(slot, slotId, button, actionType);
		this.recipeBook.slotClicked(slot);
	}

	@Override
	public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
		return this.recipeBook.keyPressed(keyCode, scanCode, modifiers) ? false : super.keyPressed(keyCode, scanCode, modifiers);
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
	public boolean charTyped(char chr, int modifiers) {
		return this.recipeBook.charTyped(chr, modifiers) ? true : super.charTyped(chr, modifiers);
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
