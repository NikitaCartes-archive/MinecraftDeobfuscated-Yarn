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
import net.minecraft.client.resource.language.I18n;
import net.minecraft.container.CraftingContainer;
import net.minecraft.container.CraftingTableContainer;
import net.minecraft.container.Slot;
import net.minecraft.container.SlotActionType;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

@Environment(EnvType.CLIENT)
public class CraftingTableGui extends ContainerGui implements RecipeBookProvider {
	private static final Identifier BG_TEX = new Identifier("textures/gui/container/crafting_table.png");
	private static final Identifier RECIPE_BUTTON_TEX = new Identifier("textures/gui/recipe_button.png");
	private final RecipeBookGui field_2880 = new RecipeBookGui();
	private boolean isNarrow;
	private final PlayerInventory playerInventory;

	public CraftingTableGui(PlayerInventory playerInventory, World world) {
		this(playerInventory, world, BlockPos.ORIGIN);
	}

	public CraftingTableGui(PlayerInventory playerInventory, World world, BlockPos blockPos) {
		super(new CraftingTableContainer(playerInventory, world, blockPos));
		this.playerInventory = playerInventory;
	}

	@Override
	protected void onInitialized() {
		super.onInitialized();
		this.isNarrow = this.width < 379;
		this.field_2880.initialize(this.width, this.height, this.client, this.isNarrow, (CraftingContainer)this.container);
		this.left = this.field_2880.findLeftEdge(this.isNarrow, this.width, this.containerWidth);
		this.listeners.add(this.field_2880);
		this.addButton(
			new RecipeBookButtonWidget(10, this.left + 5, this.height / 2 - 49, 20, 18, 0, 0, 19, RECIPE_BUTTON_TEX) {
				@Override
				public void onPressed(double d, double e) {
					CraftingTableGui.this.field_2880.reset(CraftingTableGui.this.isNarrow);
					CraftingTableGui.this.field_2880.toggleOpen();
					CraftingTableGui.this.left = CraftingTableGui.this.field_2880
						.findLeftEdge(CraftingTableGui.this.isNarrow, CraftingTableGui.this.width, CraftingTableGui.this.containerWidth);
					this.setPos(CraftingTableGui.this.left + 5, CraftingTableGui.this.height / 2 - 49);
				}
			}
		);
	}

	@Nullable
	@Override
	public GuiEventListener getFocused() {
		return this.field_2880;
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

		this.drawMousoverTooltip(i, j);
		this.field_2880.drawTooltip(this.left, this.top, i, j);
	}

	@Override
	protected void drawForeground(int i, int j) {
		this.fontRenderer.draw(I18n.translate("container.crafting"), 28.0F, 6.0F, 4210752);
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
		boolean bl = d < (double)i || e < (double)j || d >= (double)(i + this.containerWidth) || e >= (double)(j + this.containerHeight);
		return this.field_2880.isClickOutsideBounds(d, e, this.left, this.top, this.containerWidth, this.containerHeight, k) && bl;
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
