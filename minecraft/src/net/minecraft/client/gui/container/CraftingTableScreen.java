package net.minecraft.client.gui.container;

import com.mojang.blaze3d.platform.GlStateManager;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.ContainerScreen;
import net.minecraft.client.gui.ingame.RecipeBookProvider;
import net.minecraft.client.gui.recipebook.RecipeBookGui;
import net.minecraft.client.gui.widget.RecipeBookButtonWidget;
import net.minecraft.container.CraftingTableContainer;
import net.minecraft.container.Slot;
import net.minecraft.container.SlotActionType;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.text.TextComponent;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
public class CraftingTableScreen extends ContainerScreen<CraftingTableContainer> implements RecipeBookProvider {
	private static final Identifier field_2878 = new Identifier("textures/gui/container/crafting_table.png");
	private static final Identifier field_2881 = new Identifier("textures/gui/recipe_button.png");
	private final RecipeBookGui field_2880 = new RecipeBookGui();
	private boolean isNarrow;

	public CraftingTableScreen(CraftingTableContainer craftingTableContainer, PlayerInventory playerInventory, TextComponent textComponent) {
		super(craftingTableContainer, playerInventory, textComponent);
	}

	@Override
	protected void onInitialized() {
		super.onInitialized();
		this.isNarrow = this.screenWidth < 379;
		this.field_2880.initialize(this.screenWidth, this.screenHeight, this.client, this.isNarrow, this.container);
		this.left = this.field_2880.findLeftEdge(this.isNarrow, this.screenWidth, this.width);
		this.listeners.add(this.field_2880);
		this.method_18624(this.field_2880);
		this.addButton(
			new RecipeBookButtonWidget(this.left + 5, this.screenHeight / 2 - 49, 20, 18, 0, 0, 19, field_2881) {
				@Override
				public void method_1826() {
					CraftingTableScreen.this.field_2880.reset(CraftingTableScreen.this.isNarrow);
					CraftingTableScreen.this.field_2880.toggleOpen();
					CraftingTableScreen.this.left = CraftingTableScreen.this.field_2880
						.findLeftEdge(CraftingTableScreen.this.isNarrow, CraftingTableScreen.this.screenWidth, CraftingTableScreen.this.width);
					this.setPos(CraftingTableScreen.this.left + 5, CraftingTableScreen.this.screenHeight / 2 - 49);
				}
			}
		);
	}

	@Override
	public void update() {
		super.update();
		this.field_2880.update();
	}

	@Override
	public void draw(int i, int j, float f) {
		this.drawBackground();
		if (this.field_2880.isOpen() && this.isNarrow) {
			this.drawBackground(f, i, j);
			this.field_2880.draw(i, j, f);
		} else {
			this.field_2880.draw(i, j, f);
			super.draw(i, j, f);
			this.field_2880.drawGhostSlots(this.left, this.top, true, f);
		}

		this.drawMouseoverTooltip(i, j);
		this.field_2880.drawTooltip(this.left, this.top, i, j);
		this.method_18624(this.field_2880);
	}

	@Override
	protected void drawForeground(int i, int j) {
		this.fontRenderer.draw(this.field_17411.getFormattedText(), 28.0F, 6.0F, 4210752);
		this.fontRenderer.draw(this.playerInventory.method_5476().getFormattedText(), 8.0F, (float)(this.height - 96 + 2), 4210752);
	}

	@Override
	protected void drawBackground(float f, int i, int j) {
		GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
		this.client.method_1531().method_4618(field_2878);
		int k = this.left;
		int l = (this.screenHeight - this.height) / 2;
		this.drawTexturedRect(k, l, 0, 0, this.width, this.height);
	}

	@Override
	protected boolean isPointWithinBounds(int i, int j, int k, int l, double d, double e) {
		return (!this.isNarrow || !this.field_2880.isOpen()) && super.isPointWithinBounds(i, j, k, l, d, e);
	}

	@Override
	public boolean mouseClicked(double d, double e, int i) {
		if (this.field_2880.mouseClicked(d, e, i)) {
			return true;
		} else {
			return this.isNarrow && this.field_2880.isOpen() ? true : super.mouseClicked(d, e, i);
		}
	}

	@Override
	protected boolean isClickOutsideBounds(double d, double e, int i, int j, int k) {
		boolean bl = d < (double)i || e < (double)j || d >= (double)(i + this.width) || e >= (double)(j + this.height);
		return this.field_2880.isClickOutsideBounds(d, e, this.left, this.top, this.width, this.height, k) && bl;
	}

	@Override
	protected void onMouseClick(Slot slot, int i, int j, SlotActionType slotActionType) {
		super.onMouseClick(slot, i, j, slotActionType);
		this.field_2880.slotClicked(slot);
	}

	@Override
	public void refreshRecipeBook() {
		this.field_2880.refresh();
	}

	@Override
	public void onClosed() {
		this.field_2880.close();
		super.onClosed();
	}

	@Override
	public RecipeBookGui getRecipeBookGui() {
		return this.field_2880;
	}
}
