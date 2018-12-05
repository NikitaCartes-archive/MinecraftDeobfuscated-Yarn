package net.minecraft.client.gui.container;

import com.mojang.blaze3d.platform.GlStateManager;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.ContainerGui;
import net.minecraft.container.GenericContainer;
import net.minecraft.inventory.Inventory;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
public class ContainerGui54 extends ContainerGui {
	private static final Identifier TEXTURE = new Identifier("textures/gui/container/generic_54.png");
	private final Inventory playerInv;
	private final Inventory inventory;
	private final int rows;

	public ContainerGui54(Inventory inventory, Inventory inventory2) {
		super(new GenericContainer(inventory, inventory2, MinecraftClient.getInstance().player));
		this.playerInv = inventory;
		this.inventory = inventory2;
		this.field_2558 = false;
		int i = 222;
		int j = 114;
		this.rows = inventory2.getInvSize() / 9;
		this.containerHeight = 114 + this.rows * 18;
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
		this.drawTexturedRect(k, l, 0, 0, this.containerWidth, this.rows * 18 + 17);
		this.drawTexturedRect(k, l + this.rows * 18 + 17, 0, 126, this.containerWidth, 96);
	}
}
