package net.minecraft.client.gui.screen.ingame;

import com.mojang.blaze3d.systems.RenderSystem;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.screen.recipebook.RecipeBookProvider;
import net.minecraft.client.gui.screen.recipebook.RecipeBookWidget;
import net.minecraft.client.gui.widget.TexturedButtonWidget;
import net.minecraft.container.CraftingTableContainer;
import net.minecraft.container.Slot;
import net.minecraft.container.SlotActionType;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
public class CraftingTableScreen extends ContainerScreen<CraftingTableContainer> implements RecipeBookProvider {
	private static final Identifier BG_TEX = new Identifier("textures/gui/container/crafting_table.png");
	private static final Identifier RECIPE_BUTTON_TEX = new Identifier("textures/gui/recipe_button.png");
	private final RecipeBookWidget recipeBookGui = new RecipeBookWidget();
	private boolean isNarrow;

	public CraftingTableScreen(CraftingTableContainer container, PlayerInventory inventory, Text title) {
		super(container, inventory, title);
	}

	@Override
	protected void init() {
		super.init();
		this.isNarrow = this.width < 379;
		this.recipeBookGui.initialize(this.width, this.height, this.minecraft, this.isNarrow, this.container);
		this.x = this.recipeBookGui.findLeftEdge(this.isNarrow, this.width, this.containerWidth);
		this.children.add(this.recipeBookGui);
		this.setInitialFocus(this.recipeBookGui);
		this.addButton(new TexturedButtonWidget(this.x + 5, this.height / 2 - 49, 20, 18, 0, 0, 19, RECIPE_BUTTON_TEX, buttonWidget -> {
			this.recipeBookGui.reset(this.isNarrow);
			this.recipeBookGui.toggleOpen();
			this.x = this.recipeBookGui.findLeftEdge(this.isNarrow, this.width, this.containerWidth);
			((TexturedButtonWidget)buttonWidget).setPos(this.x + 5, this.height / 2 - 49);
		}));
	}

	@Override
	public void tick() {
		super.tick();
		this.recipeBookGui.update();
	}

	@Override
	public void render(int mouseX, int mouseY, float delta) {
		this.renderBackground();
		if (this.recipeBookGui.isOpen() && this.isNarrow) {
			this.drawBackground(delta, mouseX, mouseY);
			this.recipeBookGui.render(mouseX, mouseY, delta);
		} else {
			this.recipeBookGui.render(mouseX, mouseY, delta);
			super.render(mouseX, mouseY, delta);
			this.recipeBookGui.drawGhostSlots(this.x, this.y, true, delta);
		}

		this.drawMouseoverTooltip(mouseX, mouseY);
		this.recipeBookGui.drawTooltip(this.x, this.y, mouseX, mouseY);
		this.focusOn(this.recipeBookGui);
	}

	@Override
	protected void drawForeground(int mouseX, int mouseY) {
		this.font.draw(this.title.asFormattedString(), 28.0F, 6.0F, 4210752);
		this.font.draw(this.playerInventory.getDisplayName().asFormattedString(), 8.0F, (float)(this.containerHeight - 96 + 2), 4210752);
	}

	@Override
	protected void drawBackground(float delta, int mouseX, int mouseY) {
		RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
		this.minecraft.getTextureManager().bindTexture(BG_TEX);
		int i = this.x;
		int j = (this.height - this.containerHeight) / 2;
		this.blit(i, j, 0, 0, this.containerWidth, this.containerHeight);
	}

	@Override
	protected boolean isPointWithinBounds(int xPosition, int yPosition, int width, int height, double pointX, double pointY) {
		return (!this.isNarrow || !this.recipeBookGui.isOpen()) && super.isPointWithinBounds(xPosition, yPosition, width, height, pointX, pointY);
	}

	@Override
	public boolean mouseClicked(double mouseX, double mouseY, int button) {
		if (this.recipeBookGui.mouseClicked(mouseX, mouseY, button)) {
			return true;
		} else {
			return this.isNarrow && this.recipeBookGui.isOpen() ? true : super.mouseClicked(mouseX, mouseY, button);
		}
	}

	@Override
	protected boolean isClickOutsideBounds(double mouseX, double mouseY, int left, int top, int button) {
		boolean bl = mouseX < (double)left
			|| mouseY < (double)top
			|| mouseX >= (double)(left + this.containerWidth)
			|| mouseY >= (double)(top + this.containerHeight);
		return this.recipeBookGui.isClickOutsideBounds(mouseX, mouseY, this.x, this.y, this.containerWidth, this.containerHeight, button) && bl;
	}

	@Override
	protected void onMouseClick(Slot slot, int invSlot, int button, SlotActionType slotActionType) {
		super.onMouseClick(slot, invSlot, button, slotActionType);
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
	public RecipeBookWidget getRecipeBookWidget() {
		return this.recipeBookGui;
	}
}
