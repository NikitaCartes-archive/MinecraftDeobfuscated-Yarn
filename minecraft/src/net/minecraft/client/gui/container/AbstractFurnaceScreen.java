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
	private static final Identifier field_2926 = new Identifier("textures/gui/recipe_button.png");
	public final AbstractFurnaceRecipeBookScreen field_2924;
	private boolean narrow;

	public AbstractFurnaceScreen(
		T abstractFurnaceContainer, AbstractFurnaceRecipeBookScreen abstractFurnaceRecipeBookScreen, PlayerInventory playerInventory, TextComponent textComponent
	) {
		super(abstractFurnaceContainer, playerInventory, textComponent);
		this.field_2924 = abstractFurnaceRecipeBookScreen;
	}

	@Override
	public void onInitialized() {
		super.onInitialized();
		this.narrow = this.screenWidth < 379;
		this.field_2924.initialize(this.screenWidth, this.screenHeight, this.client, this.narrow, this.container);
		this.left = this.field_2924.findLeftEdge(this.narrow, this.screenWidth, this.width);
		this.addButton(
			new RecipeBookButtonWidget(this.left + 20, this.screenHeight / 2 - 49, 20, 18, 0, 0, 19, field_2926) {
				@Override
				public void method_1826() {
					AbstractFurnaceScreen.this.field_2924.reset(AbstractFurnaceScreen.this.narrow);
					AbstractFurnaceScreen.this.field_2924.toggleOpen();
					AbstractFurnaceScreen.this.left = AbstractFurnaceScreen.this.field_2924
						.findLeftEdge(AbstractFurnaceScreen.this.narrow, AbstractFurnaceScreen.this.screenWidth, AbstractFurnaceScreen.this.width);
					this.setPos(AbstractFurnaceScreen.this.left + 20, AbstractFurnaceScreen.this.screenHeight / 2 - 49);
				}
			}
		);
	}

	protected abstract Identifier method_17045();

	@Override
	public void update() {
		super.update();
		this.field_2924.update();
	}

	@Override
	public void draw(int i, int j, float f) {
		this.drawBackground();
		if (this.field_2924.isOpen() && this.narrow) {
			this.drawBackground(f, i, j);
			this.field_2924.draw(i, j, f);
		} else {
			this.field_2924.draw(i, j, f);
			super.draw(i, j, f);
			this.field_2924.drawGhostSlots(this.left, this.top, true, f);
		}

		this.drawMouseoverTooltip(i, j);
		this.field_2924.drawTooltip(this.left, this.top, i, j);
	}

	@Override
	protected void drawForeground(int i, int j) {
		String string = this.field_17411.getFormattedText();
		this.fontRenderer.draw(string, (float)(this.width / 2 - this.fontRenderer.getStringWidth(string) / 2), 6.0F, 4210752);
		this.fontRenderer.draw(this.playerInventory.method_5476().getFormattedText(), 8.0F, (float)(this.height - 96 + 2), 4210752);
	}

	@Override
	protected void drawBackground(float f, int i, int j) {
		GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
		this.client.method_1531().method_4618(this.method_17045());
		int k = this.left;
		int l = this.top;
		this.drawTexturedRect(k, l, 0, 0, this.width, this.height);
		if (this.container.isBurning()) {
			int m = this.container.getFuelProgress();
			this.drawTexturedRect(k + 56, l + 36 + 12 - m, 176, 12 - m, 14, m + 1);
		}

		int m = this.container.getCookProgress();
		this.drawTexturedRect(k + 79, l + 34, 176, 14, m + 1, 16);
	}

	@Override
	public boolean mouseClicked(double d, double e, int i) {
		if (this.field_2924.mouseClicked(d, e, i)) {
			return true;
		} else {
			return this.narrow && this.field_2924.isOpen() ? true : super.mouseClicked(d, e, i);
		}
	}

	@Override
	protected void onMouseClick(Slot slot, int i, int j, SlotActionType slotActionType) {
		super.onMouseClick(slot, i, j, slotActionType);
		this.field_2924.slotClicked(slot);
	}

	@Override
	public boolean keyPressed(int i, int j, int k) {
		return this.field_2924.keyPressed(i, j, k) ? false : super.keyPressed(i, j, k);
	}

	@Override
	protected boolean isClickOutsideBounds(double d, double e, int i, int j, int k) {
		boolean bl = d < (double)i || e < (double)j || d >= (double)(i + this.width) || e >= (double)(j + this.height);
		return this.field_2924.isClickOutsideBounds(d, e, this.left, this.top, this.width, this.height, k) && bl;
	}

	@Override
	public boolean charTyped(char c, int i) {
		return this.field_2924.charTyped(c, i) ? true : super.charTyped(c, i);
	}

	@Override
	public void refreshRecipeBook() {
		this.field_2924.refresh();
	}

	@Override
	public RecipeBookGui getRecipeBookGui() {
		return this.field_2924;
	}

	@Override
	public void onClosed() {
		this.field_2924.close();
		super.onClosed();
	}
}
