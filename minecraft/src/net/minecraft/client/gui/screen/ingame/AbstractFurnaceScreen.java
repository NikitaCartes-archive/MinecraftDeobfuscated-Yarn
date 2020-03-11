package net.minecraft.client.gui.screen.ingame;

import com.mojang.blaze3d.systems.RenderSystem;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
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
	private static final Identifier RECIPE_BUTTON_TEXTURE = new Identifier("textures/gui/recipe_button.png");
	public final AbstractFurnaceRecipeBookScreen recipeBook;
	private boolean narrow;
	private final Identifier background;

	public AbstractFurnaceScreen(T handler, AbstractFurnaceRecipeBookScreen recipeBook, PlayerInventory inventory, Text title, Identifier background) {
		super(handler, inventory, title);
		this.recipeBook = recipeBook;
		this.background = background;
	}

	@Override
	public void init() {
		super.init();
		this.narrow = this.width < 379;
		this.recipeBook.initialize(this.width, this.height, this.client, this.narrow, this.handler);
		this.x = this.recipeBook.findLeftEdge(this.narrow, this.width, this.backgroundWidth);
		this.addButton(new TexturedButtonWidget(this.x + 20, this.height / 2 - 49, 20, 18, 0, 0, 19, RECIPE_BUTTON_TEXTURE, buttonWidget -> {
			this.recipeBook.reset(this.narrow);
			this.recipeBook.toggleOpen();
			this.x = this.recipeBook.findLeftEdge(this.narrow, this.width, this.backgroundWidth);
			((TexturedButtonWidget)buttonWidget).setPos(this.x + 20, this.height / 2 - 49);
		}));
	}

	@Override
	public void tick() {
		super.tick();
		this.recipeBook.update();
	}

	@Override
	public void render(int mouseX, int mouseY, float delta) {
		this.renderBackground();
		if (this.recipeBook.isOpen() && this.narrow) {
			this.drawBackground(delta, mouseX, mouseY);
			this.recipeBook.render(mouseX, mouseY, delta);
		} else {
			this.recipeBook.render(mouseX, mouseY, delta);
			super.render(mouseX, mouseY, delta);
			this.recipeBook.drawGhostSlots(this.x, this.y, true, delta);
		}

		this.drawMouseoverTooltip(mouseX, mouseY);
		this.recipeBook.drawTooltip(this.x, this.y, mouseX, mouseY);
	}

	@Override
	protected void drawForeground(int mouseX, int mouseY) {
		String string = this.title.asFormattedString();
		this.textRenderer.draw(string, (float)(this.backgroundWidth / 2 - this.textRenderer.getStringWidth(string) / 2), 6.0F, 4210752);
		this.textRenderer.draw(this.playerInventory.getDisplayName().asFormattedString(), 8.0F, (float)(this.backgroundHeight - 96 + 2), 4210752);
	}

	@Override
	protected void drawBackground(float delta, int mouseX, int mouseY) {
		RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
		this.client.getTextureManager().bindTexture(this.background);
		int i = this.x;
		int j = this.y;
		this.drawTexture(i, j, 0, 0, this.backgroundWidth, this.backgroundHeight);
		if (this.handler.isBurning()) {
			int k = this.handler.getFuelProgress();
			this.drawTexture(i + 56, j + 36 + 12 - k, 176, 12 - k, 14, k + 1);
		}

		int k = this.handler.getCookProgress();
		this.drawTexture(i + 79, j + 34, 176, 14, k + 1, 16);
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
	protected void onMouseClick(Slot slot, int invSlot, int button, SlotActionType slotActionType) {
		super.onMouseClick(slot, invSlot, button, slotActionType);
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
	public boolean charTyped(char chr, int keyCode) {
		return this.recipeBook.charTyped(chr, keyCode) ? true : super.charTyped(chr, keyCode);
	}

	@Override
	public void refreshRecipeBook() {
		this.recipeBook.refresh();
	}

	@Override
	public RecipeBookWidget getRecipeBookWidget() {
		return this.recipeBook;
	}

	@Override
	public void removed() {
		this.recipeBook.close();
		super.removed();
	}
}
