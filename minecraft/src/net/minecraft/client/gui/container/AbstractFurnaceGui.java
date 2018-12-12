package net.minecraft.client.gui.container;

import com.mojang.blaze3d.platform.GlStateManager;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.entity.AbstractFurnaceBlockEntity;
import net.minecraft.client.gui.ContainerGui;
import net.minecraft.client.gui.ingame.RecipeBookProvider;
import net.minecraft.client.gui.recipebook.RecipeBookGui;
import net.minecraft.client.gui.widget.RecipeBookButtonWidget;
import net.minecraft.container.AbstractFurnaceContainer;
import net.minecraft.container.CraftingContainer;
import net.minecraft.container.Slot;
import net.minecraft.container.SlotActionType;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
public abstract class AbstractFurnaceGui extends ContainerGui implements RecipeBookProvider {
	private static final Identifier RECIPE_BUTTON_TEXTURE = new Identifier("textures/gui/recipe_button.png");
	private final PlayerInventory playerInv;
	private final Inventory inventory;
	public final AbstractFurnaceRecipeBookGui recipeBook;
	private boolean field_2925;

	public AbstractFurnaceGui(
		AbstractFurnaceContainer abstractFurnaceContainer,
		AbstractFurnaceRecipeBookGui abstractFurnaceRecipeBookGui,
		PlayerInventory playerInventory,
		Inventory inventory
	) {
		super(abstractFurnaceContainer);
		this.recipeBook = abstractFurnaceRecipeBookGui;
		this.playerInv = playerInventory;
		this.inventory = inventory;
	}

	@Override
	public void onInitialized() {
		super.onInitialized();
		this.field_2925 = this.width < 379;
		this.recipeBook.initialize(this.width, this.height, this.client, this.field_2925, (CraftingContainer)this.container);
		this.left = this.recipeBook.findLeftEdge(this.field_2925, this.width, this.containerWidth);
		this.addButton(
			new RecipeBookButtonWidget(10, this.left + 20, this.height / 2 - 49, 20, 18, 0, 0, 19, RECIPE_BUTTON_TEXTURE) {
				@Override
				public void onPressed(double d, double e) {
					AbstractFurnaceGui.this.recipeBook.reset(AbstractFurnaceGui.this.field_2925);
					AbstractFurnaceGui.this.recipeBook.toggleOpen();
					AbstractFurnaceGui.this.left = AbstractFurnaceGui.this.recipeBook
						.findLeftEdge(AbstractFurnaceGui.this.field_2925, AbstractFurnaceGui.this.width, AbstractFurnaceGui.this.containerWidth);
					this.setPos(AbstractFurnaceGui.this.left + 20, AbstractFurnaceGui.this.height / 2 - 49);
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
		if (this.recipeBook.isOpen() && this.field_2925) {
			this.drawBackground(f, i, j);
			this.recipeBook.draw(i, j, f);
		} else {
			this.recipeBook.draw(i, j, f);
			super.draw(i, j, f);
			this.recipeBook.drawGhostSlots(this.left, this.top, true, f);
		}

		this.drawMousoverTooltip(i, j);
		this.recipeBook.drawTooltip(this.left, this.top, i, j);
	}

	@Override
	protected void drawForeground(int i, int j) {
		String string = this.inventory.getDisplayName().getFormattedText();
		this.fontRenderer.draw(string, (float)(this.containerWidth / 2 - this.fontRenderer.getStringWidth(string) / 2), 6.0F, 4210752);
		this.fontRenderer.draw(this.playerInv.getDisplayName().getFormattedText(), 8.0F, (float)(this.containerHeight - 96 + 2), 4210752);
	}

	@Override
	protected void drawBackground(float f, int i, int j) {
		GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
		this.client.getTextureManager().bindTexture(this.getBackgroundTexture());
		int k = this.left;
		int l = this.top;
		this.drawTexturedRect(k, l, 0, 0, this.containerWidth, this.containerHeight);
		if (AbstractFurnaceBlockEntity.isBurningClient(this.inventory)) {
			int m = this.method_2482();
			this.drawTexturedRect(k + 56, l + 36 + 12 - m, 176, 12 - m, 14, m + 1);
		}

		int m = this.method_2484();
		this.drawTexturedRect(k + 79, l + 34, 176, 14, m + 1, 16);
	}

	@Override
	public boolean mouseClicked(double d, double e, int i) {
		if (this.recipeBook.mouseClicked(d, e, i)) {
			return true;
		} else {
			return this.field_2925 && this.recipeBook.isOpen() ? true : super.mouseClicked(d, e, i);
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

	private int method_2484() {
		int i = this.inventory.getInvProperty(2);
		int j = this.inventory.getInvProperty(3);
		return j != 0 && i != 0 ? i * 24 / j : 0;
	}

	private int method_2482() {
		int i = this.inventory.getInvProperty(1);
		if (i == 0) {
			i = 200;
		}

		return this.inventory.getInvProperty(0) * 13 / i;
	}
}
