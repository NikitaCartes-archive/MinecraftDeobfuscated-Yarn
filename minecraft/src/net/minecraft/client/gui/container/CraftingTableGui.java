package net.minecraft.client.gui.container;

import com.mojang.blaze3d.platform.GlStateManager;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_344;
import net.minecraft.client.gui.ContainerGui;
import net.minecraft.client.gui.GuiEventListener;
import net.minecraft.client.gui.ingame.RecipeBookProvider;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.container.ActionTypeSlot;
import net.minecraft.container.CraftingContainer;
import net.minecraft.container.CraftingTableContainer;
import net.minecraft.container.Slot;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

@Environment(EnvType.CLIENT)
public class CraftingTableGui extends ContainerGui implements RecipeBookProvider {
	private static final Identifier BG_TEX = new Identifier("textures/gui/container/crafting_table.png");
	private static final Identifier field_2881 = new Identifier("textures/gui/recipe_button.png");
	private final RecipeBookGui field_2880 = new RecipeBookGui();
	private boolean field_2877;
	private final PlayerInventory field_2879;

	public CraftingTableGui(PlayerInventory playerInventory, World world) {
		this(playerInventory, world, BlockPos.ORIGIN);
	}

	public CraftingTableGui(PlayerInventory playerInventory, World world, BlockPos blockPos) {
		super(new CraftingTableContainer(playerInventory, world, blockPos));
		this.field_2879 = playerInventory;
	}

	@Override
	protected void onInitialized() {
		super.onInitialized();
		this.field_2877 = this.width < 379;
		this.field_2880.method_2597(this.width, this.height, this.client, this.field_2877, (CraftingContainer)this.container);
		this.left = this.field_2880.method_2595(this.field_2877, this.width, this.containerWidth);
		this.listeners.add(this.field_2880);
		this.addButton(
			new class_344(10, this.left + 5, this.height / 2 - 49, 20, 18, 0, 0, 19, field_2881) {
				@Override
				public void onPressed(double d, double e) {
					CraftingTableGui.this.field_2880.method_2579(CraftingTableGui.this.field_2877);
					CraftingTableGui.this.field_2880.method_2591();
					CraftingTableGui.this.left = CraftingTableGui.this.field_2880
						.method_2595(CraftingTableGui.this.field_2877, CraftingTableGui.this.width, CraftingTableGui.this.containerWidth);
					this.getPos(CraftingTableGui.this.left + 5, CraftingTableGui.this.height / 2 - 49);
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
		if (this.field_2880.isOpen() && this.field_2877) {
			this.drawBackground(f, i, j);
			this.field_2880.method_2578(i, j, f);
		} else {
			this.field_2880.method_2578(i, j, f);
			super.draw(i, j, f);
			this.field_2880.method_2581(this.left, this.top, true, f);
		}

		this.drawMousoverTooltip(i, j);
		this.field_2880.method_2601(this.left, this.top, i, j);
	}

	@Override
	protected void drawForeground(int i, int j) {
		this.fontRenderer.draw(I18n.translate("container.crafting"), 28.0F, 6.0F, 4210752);
		this.fontRenderer.draw(this.field_2879.getDisplayName().getFormattedText(), 8.0F, (float)(this.containerHeight - 96 + 2), 4210752);
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
		return (!this.field_2877 || !this.field_2880.isOpen()) && super.isPointWithinBounds(i, j, k, l, d, e);
	}

	@Override
	public boolean mouseClicked(double d, double e, int i) {
		if (this.field_2880.mouseClicked(d, e, i)) {
			return true;
		} else {
			return this.field_2877 && this.field_2880.isOpen() ? true : super.mouseClicked(d, e, i);
		}
	}

	@Override
	protected boolean isClickInContainerBounds(double d, double e, int i, int j, int k) {
		boolean bl = d < (double)i || e < (double)j || d >= (double)(i + this.containerWidth) || e >= (double)(j + this.containerHeight);
		return this.field_2880.method_2598(d, e, this.left, this.top, this.containerWidth, this.containerHeight, k) && bl;
	}

	@Override
	protected void onMouseClick(Slot slot, int i, int j, ActionTypeSlot actionTypeSlot) {
		super.onMouseClick(slot, i, j, actionTypeSlot);
		this.field_2880.method_2600(slot);
	}

	@Override
	public void method_16891() {
		this.field_2880.method_2592();
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
