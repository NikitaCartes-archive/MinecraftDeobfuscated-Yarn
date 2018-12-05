package net.minecraft.client.gui.container;

import com.mojang.blaze3d.platform.GlStateManager;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.ContainerGui;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.container.Container;
import net.minecraft.container.ContainerListener;
import net.minecraft.container.GrindstoneContainer;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DefaultedList;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;

@Environment(EnvType.CLIENT)
public class GrindstoneGui extends ContainerGui implements ContainerListener {
	private static final Identifier TEXTURE = new Identifier("textures/gui/container/grindstone.png");
	private final GrindstoneContainer grindstone;
	private final PlayerInventory playerInventory;

	public GrindstoneGui(PlayerInventory playerInventory, World world) {
		super(new GrindstoneContainer(playerInventory, world));
		this.playerInventory = playerInventory;
		this.grindstone = (GrindstoneContainer)this.container;
	}

	@Override
	protected void drawForeground(int i, int j) {
		String string = I18n.translate("container.grindstone_title");
		this.fontRenderer.draw(string, 8.0F, 6.0F, 4210752);
		this.fontRenderer.draw(this.playerInventory.getDisplayName().getFormattedText(), 8.0F, (float)(this.containerHeight - 96 + 2), 4210752);
	}

	@Override
	public void draw(int i, int j, float f) {
		this.drawBackground();
		this.drawBackground(f, i, j);
		super.draw(i, j, f);
		this.drawMousoverTooltip(i, j);
	}

	@Override
	protected void drawBackground(float f, int i, int j) {
		GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
		this.client.getTextureManager().bindTexture(TEXTURE);
		int k = (this.width - this.containerWidth) / 2;
		int l = (this.height - this.containerHeight) / 2;
		this.drawTexturedRect(k, l, 0, 0, this.containerWidth, this.containerHeight);
		if ((this.grindstone.getSlot(0).hasStack() || this.grindstone.getSlot(1).hasStack()) && !this.grindstone.getSlot(2).hasStack()) {
			this.drawTexturedRect(k + 92, l + 31, this.containerWidth, 0, 28, 21);
		}
	}

	@Override
	public void onContainerRegistered(Container container, DefaultedList<ItemStack> defaultedList) {
	}

	@Override
	public void onContainerSlotUpdate(Container container, int i, ItemStack itemStack) {
	}

	@Override
	public void onContainerPropertyUpdate(Container container, int i, int j) {
	}

	@Override
	public void onContainerInvRegistered(Container container, Inventory inventory) {
	}
}
