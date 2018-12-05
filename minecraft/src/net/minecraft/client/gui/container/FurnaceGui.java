package net.minecraft.client.gui.container;

import com.mojang.blaze3d.platform.GlStateManager;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_344;
import net.minecraft.block.entity.FurnaceBlockEntity;
import net.minecraft.client.gui.ContainerGui;
import net.minecraft.client.gui.ingame.RecipeBookProvider;
import net.minecraft.container.ActionTypeSlot;
import net.minecraft.container.CraftingContainer;
import net.minecraft.container.FurnaceContainer;
import net.minecraft.container.Slot;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
public class FurnaceGui extends ContainerGui implements RecipeBookProvider {
	private static final Identifier TEXTURE = new Identifier("textures/gui/container/furnace.png");
	private static final Identifier RECIPE_BUTTON_TEXTURE = new Identifier("textures/gui/recipe_button.png");
	private final PlayerInventory playerInv;
	private final Inventory inventory;
	public final RecipeBookFurnaceGui recipeBook = new RecipeBookFurnaceGui();
	private boolean field_2925;

	public FurnaceGui(PlayerInventory playerInventory, Inventory inventory) {
		super(new FurnaceContainer(playerInventory, inventory));
		this.playerInv = playerInventory;
		this.inventory = inventory;
	}

	@Override
	public void onInitialized() {
		super.onInitialized();
		this.field_2925 = this.width < 379;
		this.recipeBook.method_2597(this.width, this.height, this.client, this.field_2925, (CraftingContainer)this.container);
		this.left = this.recipeBook.method_2595(this.field_2925, this.width, this.containerWidth);
		this.addButton(new class_344(10, this.left + 20, this.height / 2 - 49, 20, 18, 0, 0, 19, RECIPE_BUTTON_TEXTURE) {
			@Override
			public void onPressed(double d, double e) {
				FurnaceGui.this.recipeBook.method_2579(FurnaceGui.this.field_2925);
				FurnaceGui.this.recipeBook.method_2591();
				FurnaceGui.this.left = FurnaceGui.this.recipeBook.method_2595(FurnaceGui.this.field_2925, FurnaceGui.this.width, FurnaceGui.this.containerWidth);
				this.getPos(FurnaceGui.this.left + 20, FurnaceGui.this.height / 2 - 49);
			}
		});
	}

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
			this.recipeBook.method_2578(i, j, f);
		} else {
			this.recipeBook.method_2578(i, j, f);
			super.draw(i, j, f);
			this.recipeBook.method_2581(this.left, this.top, true, f);
		}

		this.drawMousoverTooltip(i, j);
		this.recipeBook.method_2601(this.left, this.top, i, j);
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
		this.client.getTextureManager().bindTexture(TEXTURE);
		int k = this.left;
		int l = this.top;
		this.drawTexturedRect(k, l, 0, 0, this.containerWidth, this.containerHeight);
		if (FurnaceBlockEntity.method_11199(this.inventory)) {
			int m = this.method_2482(13);
			this.drawTexturedRect(k + 56, l + 36 + 12 - m, 176, 12 - m, 14, m + 1);
		}

		int m = this.method_2484(24);
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
	protected void onMouseClick(Slot slot, int i, int j, ActionTypeSlot actionTypeSlot) {
		super.onMouseClick(slot, i, j, actionTypeSlot);
		this.recipeBook.method_2600(slot);
	}

	@Override
	public boolean keyPressed(int i, int j, int k) {
		return this.recipeBook.keyPressed(i, j, k) ? false : super.keyPressed(i, j, k);
	}

	@Override
	protected boolean isClickInContainerBounds(double d, double e, int i, int j, int k) {
		boolean bl = d < (double)i || e < (double)j || d >= (double)(i + this.containerWidth) || e >= (double)(j + this.containerHeight);
		return this.recipeBook.method_2598(d, e, this.left, this.top, this.containerWidth, this.containerHeight, k) && bl;
	}

	@Override
	public boolean charTyped(char c, int i) {
		return this.recipeBook.charTyped(c, i) ? true : super.charTyped(c, i);
	}

	@Override
	public void method_16891() {
		this.recipeBook.method_2592();
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

	private int method_2484(int i) {
		int j = this.inventory.getInvProperty(2);
		int k = this.inventory.getInvProperty(3);
		return k != 0 && j != 0 ? j * i / k : 0;
	}

	private int method_2482(int i) {
		int j = this.inventory.getInvProperty(1);
		if (j == 0) {
			j = 200;
		}

		return this.inventory.getInvProperty(0) * i / j;
	}
}
