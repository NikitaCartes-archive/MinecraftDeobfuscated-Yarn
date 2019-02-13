package net.minecraft.client.gui.container;

import com.mojang.blaze3d.platform.GlStateManager;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.ContainerScreen;
import net.minecraft.client.gui.ingame.RecipeBookProvider;
import net.minecraft.client.gui.recipebook.RecipeBookGui;
import net.minecraft.client.gui.widget.RecipeBookButtonWidget;
import net.minecraft.container.AbstractFurnaceContainer;
import net.minecraft.container.Slot;
import net.minecraft.container.SlotActionType;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.text.TextComponent;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
public abstract class AbstractFurnaceScreen<T extends AbstractFurnaceContainer> extends ContainerScreen<T> implements RecipeBookProvider {
	private static final Identifier RECIPE_BUTTON_TEXTURE = new Identifier("textures/gui/recipe_button.png");
	public final AbstractFurnaceRecipeBookScreen recipeBook;
	private boolean narrow;

	public AbstractFurnaceScreen(
		T abstractFurnaceContainer, AbstractFurnaceRecipeBookScreen abstractFurnaceRecipeBookScreen, PlayerInventory playerInventory, TextComponent textComponent
	) {
		super(abstractFurnaceContainer, playerInventory, textComponent);
		this.recipeBook = abstractFurnaceRecipeBookScreen;
	}

	@Override
	public void onInitialized() {
		super.onInitialized();
		this.narrow = this.width < 379;
		this.recipeBook.initialize(this.width, this.height, this.client, this.narrow, this.container);
		this.left = this.recipeBook.findLeftEdge(this.narrow, this.width, this.containerWidth);
		this.addButton(
			new RecipeBookButtonWidget(10, this.left + 20, this.height / 2 - 49, 20, 18, 0, 0, 19, RECIPE_BUTTON_TEXTURE) {
				@Override
				public void onPressed(double d, double e) {
					AbstractFurnaceScreen.this.recipeBook.reset(AbstractFurnaceScreen.this.narrow);
					AbstractFurnaceScreen.this.recipeBook.toggleOpen();
					AbstractFurnaceScreen.this.left = AbstractFurnaceScreen.this.recipeBook
						.findLeftEdge(AbstractFurnaceScreen.this.narrow, AbstractFurnaceScreen.this.width, AbstractFurnaceScreen.this.containerWidth);
					this.setPos(AbstractFurnaceScreen.this.left + 20, AbstractFurnaceScreen.this.height / 2 - 49);
				}
			}
		);
	}

	protected abstract Identifier getBackgroundTexture();

	@Override
	public void update() {
		super.update();
		this.recipeBook.update();
	}

	@Override
	public void draw(int i, int j, float f) {
		this.drawBackground();
		if (this.recipeBook.isOpen() && this.narrow) {
			this.drawBackground(f, i, j);
			this.recipeBook.draw(i, j, f);
		} else {
			this.recipeBook.draw(i, j, f);
			super.draw(i, j, f);
			this.recipeBook.drawGhostSlots(this.left, this.top, true, f);
		}

		this.drawMouseoverTooltip(i, j);
		this.recipeBook.drawTooltip(this.left, this.top, i, j);
	}

	@Override
	protected void drawForeground(int i, int j) {
		String string = this.name.getFormattedText();
		this.fontRenderer.draw(string, (float)(this.containerWidth / 2 - this.fontRenderer.getStringWidth(string) / 2), 6.0F, 4210752);
		this.fontRenderer.draw(this.playerInventory.getDisplayName().getFormattedText(), 8.0F, (float)(this.containerHeight - 96 + 2), 4210752);
	}

	@Override
	protected void drawBackground(float f, int i, int j) {
		GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
		this.client.getTextureManager().bindTexture(this.getBackgroundTexture());
		int k = this.left;
		int l = this.top;
		this.drawTexturedRect(k, l, 0, 0, this.containerWidth, this.containerHeight);
		if (this.container.isBurning()) {
			int m = this.container.getFuelProgress();
			this.drawTexturedRect(k + 56, l + 36 + 12 - m, 176, 12 - m, 14, m + 1);
		}

		int m = this.container.getCookProgress();
		this.drawTexturedRect(k + 79, l + 34, 176, 14, m + 1, 16);
	}

	@Override
	public boolean mouseClicked(double d, double e, int i) {
		if (this.recipeBook.mouseClicked(d, e, i)) {
			return true;
		} else {
			return this.narrow && this.recipeBook.isOpen() ? true : super.mouseClicked(d, e, i);
		}
	}

	@Override
	protected void onMouseClick(Slot slot, int i, int j, SlotActionType slotActionType) {
		super.onMouseClick(slot, i, j, slotActionType);
		this.recipeBook.slotClicked(slot);
	}

	@Override
	public boolean keyPressed(int i, int j, int k) {
		return this.recipeBook.keyPressed(i, j, k) ? false : super.keyPressed(i, j, k);
	}

	@Override
	protected boolean isClickOutsideBounds(double d, double e, int i, int j, int k) {
		boolean bl = d < (double)i || e < (double)j || d >= (double)(i + this.containerWidth) || e >= (double)(j + this.containerHeight);
		return this.recipeBook.isClickOutsideBounds(d, e, this.left, this.top, this.containerWidth, this.containerHeight, k) && bl;
	}

	@Override
	public boolean charTyped(char c, int i) {
		return this.recipeBook.charTyped(c, i) ? true : super.charTyped(c, i);
	}

	@Override
	public void refreshRecipeBook() {
		this.recipeBook.refresh();
	}

	@Override
	public RecipeBookGui getRecipeBookGui() {
		return this.recipeBook;
	}

	@Override
	public void onClosed() {
		this.recipeBook.close();
		super.onClosed();
	}
}
