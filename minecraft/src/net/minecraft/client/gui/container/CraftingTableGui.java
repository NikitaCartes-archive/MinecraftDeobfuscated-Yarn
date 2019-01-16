package net.minecraft.client.gui.container;

import com.mojang.blaze3d.platform.GlStateManager;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.ContainerGui;
import net.minecraft.client.gui.GuiEventListener;
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
public class CraftingTableGui extends ContainerGui<CraftingTableContainer> implements RecipeBookProvider {
	private static final Identifier BG_TEX = new Identifier("textures/gui/container/crafting_table.png");
	private static final Identifier RECIPE_BUTTON_TEX = new Identifier("textures/gui/recipe_button.png");
	private final RecipeBookGui recipeBookGui = new RecipeBookGui();
	private boolean isNarrow;

	public CraftingTableGui(CraftingTableContainer craftingTableContainer, PlayerInventory playerInventory, TextComponent textComponent) {
		super(craftingTableContainer, playerInventory, textComponent);
	}

	@Override
	protected void onInitialized() {
		super.onInitialized();
		this.isNarrow = this.width < 379;
		this.recipeBookGui.initialize(this.width, this.height, this.client, this.isNarrow, this.container);
		this.left = this.recipeBookGui.findLeftEdge(this.isNarrow, this.width, this.containerWidth);
		this.listeners.add(this.recipeBookGui);
		this.addButton(
			new RecipeBookButtonWidget(10, this.left + 5, this.height / 2 - 49, 20, 18, 0, 0, 19, RECIPE_BUTTON_TEX) {
				@Override
				public void onPressed(double d, double e) {
					CraftingTableGui.this.recipeBookGui.reset(CraftingTableGui.this.isNarrow);
					CraftingTableGui.this.recipeBookGui.toggleOpen();
					CraftingTableGui.this.left = CraftingTableGui.this.recipeBookGui
						.findLeftEdge(CraftingTableGui.this.isNarrow, CraftingTableGui.this.width, CraftingTableGui.this.containerWidth);
					this.setPos(CraftingTableGui.this.left + 5, CraftingTableGui.this.height / 2 - 49);
				}
			}
		);
	}

	@Nullable
	@Override
	public GuiEventListener getFocused() {
		return this.recipeBookGui;
	}

	@Override
	public void update() {
		super.update();
		this.recipeBookGui.update();
	}

	@Override
	public void draw(int i, int j, float f) {
		this.drawBackground();
		if (this.recipeBookGui.isOpen() && this.isNarrow) {
			this.drawBackground(f, i, j);
			this.recipeBookGui.draw(i, j, f);
		} else {
			this.recipeBookGui.draw(i, j, f);
			super.draw(i, j, f);
			this.recipeBookGui.drawGhostSlots(this.left, this.top, true, f);
		}

		this.drawMousoverTooltip(i, j);
		this.recipeBookGui.drawTooltip(this.left, this.top, i, j);
	}

	@Override
	protected void drawForeground(int i, int j) {
		this.fontRenderer.draw(this.name.getFormattedText(), 28.0F, 6.0F, 4210752);
		this.fontRenderer.draw(this.playerInventory.getDisplayName().getFormattedText(), 8.0F, (float)(this.containerHeight - 96 + 2), 4210752);
	}

	@Override
	protected void drawBackground(float f, int i, int j) {
		GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
		this.client.getTextureManager().bindTexture(BG_TEX);
		int k = this.left;
		int l = (this.height - this.containerHeight) / 2;
		this.drawTexturedRect(k, l, 0, 0, this.containerWidth, this.containerHeight);
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
	public void onClosed() {
		this.recipeBookGui.close();
		super.onClosed();
	}

	@Override
	public RecipeBookGui getRecipeBookGui() {
		return this.recipeBookGui;
	}
}
