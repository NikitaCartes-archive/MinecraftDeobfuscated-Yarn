package net.minecraft.client.gui.screen.ingame;

import com.mojang.blaze3d.platform.GlStateManager;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.screen.recipebook.AbstractFurnaceRecipeBookScreen;
import net.minecraft.client.gui.screen.recipebook.RecipeBookProvider;
import net.minecraft.client.gui.screen.recipebook.RecipeBookWidget;
import net.minecraft.client.gui.widget.TexturedButtonWidget;
import net.minecraft.container.AbstractFurnaceContainer;
import net.minecraft.container.Slot;
import net.minecraft.container.SlotActionType;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
public abstract class AbstractFurnaceScreen<T extends AbstractFurnaceContainer> extends AbstractContainerScreen<T> implements RecipeBookProvider {
	private static final Identifier RECIPE_BUTTON_TEXTURE = new Identifier("textures/gui/recipe_button.png");
	public final AbstractFurnaceRecipeBookScreen field_2924;
	private boolean narrow;
	private final Identifier field_18975;

	public AbstractFurnaceScreen(
		T abstractFurnaceContainer,
		AbstractFurnaceRecipeBookScreen abstractFurnaceRecipeBookScreen,
		PlayerInventory playerInventory,
		Text text,
		Identifier identifier
	) {
		super(abstractFurnaceContainer, playerInventory, text);
		this.field_2924 = abstractFurnaceRecipeBookScreen;
		this.field_18975 = identifier;
	}

	@Override
	public void init() {
		super.init();
		this.narrow = this.width < 379;
		this.field_2924.initialize(this.width, this.height, this.minecraft, this.narrow, this.container);
		this.left = this.field_2924.findLeftEdge(this.narrow, this.width, this.containerWidth);
		this.addButton(new TexturedButtonWidget(this.left + 20, this.height / 2 - 49, 20, 18, 0, 0, 19, RECIPE_BUTTON_TEXTURE, buttonWidget -> {
			this.field_2924.reset(this.narrow);
			this.field_2924.toggleOpen();
			this.left = this.field_2924.findLeftEdge(this.narrow, this.width, this.containerWidth);
			((TexturedButtonWidget)buttonWidget).setPos(this.left + 20, this.height / 2 - 49);
		}));
	}

	@Override
	public void tick() {
		super.tick();
		this.field_2924.update();
	}

	@Override
	public void render(int i, int j, float f) {
		this.renderBackground();
		if (this.field_2924.isOpen() && this.narrow) {
			this.drawBackground(f, i, j);
			this.field_2924.render(i, j, f);
		} else {
			this.field_2924.render(i, j, f);
			super.render(i, j, f);
			this.field_2924.drawGhostSlots(this.left, this.top, true, f);
		}

		this.drawMouseoverTooltip(i, j);
		this.field_2924.drawTooltip(this.left, this.top, i, j);
	}

	@Override
	protected void drawForeground(int i, int j) {
		String string = this.title.asFormattedString();
		this.font.draw(string, (float)(this.containerWidth / 2 - this.font.getStringWidth(string) / 2), 6.0F, 4210752);
		this.font.draw(this.playerInventory.getDisplayName().asFormattedString(), 8.0F, (float)(this.containerHeight - 96 + 2), 4210752);
	}

	@Override
	protected void drawBackground(float f, int i, int j) {
		GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
		this.minecraft.method_1531().bindTexture(this.field_18975);
		int k = this.left;
		int l = this.top;
		this.blit(k, l, 0, 0, this.containerWidth, this.containerHeight);
		if (this.container.isBurning()) {
			int m = this.container.getFuelProgress();
			this.blit(k + 56, l + 36 + 12 - m, 176, 12 - m, 14, m + 1);
		}

		int m = this.container.getCookProgress();
		this.blit(k + 79, l + 34, 176, 14, m + 1, 16);
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
		boolean bl = d < (double)i || e < (double)j || d >= (double)(i + this.containerWidth) || e >= (double)(j + this.containerHeight);
		return this.field_2924.isClickOutsideBounds(d, e, this.left, this.top, this.containerWidth, this.containerHeight, k) && bl;
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
	public RecipeBookWidget getRecipeBookGui() {
		return this.field_2924;
	}

	@Override
	public void removed() {
		this.field_2924.close();
		super.removed();
	}
}
