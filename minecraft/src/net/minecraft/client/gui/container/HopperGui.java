package net.minecraft.client.gui.container;

import com.mojang.blaze3d.platform.GlStateManager;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.ContainerGui;
import net.minecraft.container.HopperContainer;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
public class HopperGui extends ContainerGui {
	private static final Identifier TEXTURE = new Identifier("textures/gui/container/hopper.png");
	private final Inventory playerInv;
	private final Inventory inventory;

	public HopperGui(PlayerInventory playerInventory, Inventory inventory) {
		super(new HopperContainer(playerInventory, inventory, MinecraftClient.getInstance().player));
		this.playerInv = playerInventory;
		this.inventory = inventory;
		this.field_2558 = false;
		this.containerHeight = 133;
	}

	@Override
	public void draw(int i, int j, float f) {
		this.drawBackground();
		super.draw(i, j, f);
		this.drawMousoverTooltip(i, j);
	}

	@Override
	protected void drawForeground(int i, int j) {
		this.fontRenderer.draw(this.inventory.getDisplayName().getFormattedText(), 8.0F, 6.0F, 4210752);
		this.fontRenderer.draw(this.playerInv.getDisplayName().getFormattedText(), 8.0F, (float)(this.containerHeight - 96 + 2), 4210752);
	}

	@Override
	protected void drawBackground(float f, int i, int j) {
		GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
		this.client.getTextureManager().bindTexture(TEXTURE);
		int k = (this.width - this.containerWidth) / 2;
		int l = (this.height - this.containerHeight) / 2;
		this.drawTexturedRect(k, l, 0, 0, this.containerWidth, this.containerHeight);
	}
}
