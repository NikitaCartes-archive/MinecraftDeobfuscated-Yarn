package net.minecraft.client.gui.container;

import com.mojang.blaze3d.platform.GlStateManager;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.ContainerScreen;
import net.minecraft.container.GrindstoneContainer;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.text.TextComponent;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
public class GrindstoneScreen extends ContainerScreen<GrindstoneContainer> {
	private static final Identifier TEXTURE = new Identifier("textures/gui/container/grindstone.png");

	public GrindstoneScreen(GrindstoneContainer grindstoneContainer, PlayerInventory playerInventory, TextComponent textComponent) {
		super(grindstoneContainer, playerInventory, textComponent);
	}

	@Override
	protected void drawForeground(int i, int j) {
		this.fontRenderer.draw(this.name.getFormattedText(), 8.0F, 6.0F, 4210752);
		this.fontRenderer.draw(this.playerInventory.getDisplayName().getFormattedText(), 8.0F, (float)(this.containerHeight - 96 + 2), 4210752);
	}

	@Override
	public void draw(int i, int j, float f) {
		this.drawBackground();
		this.drawBackground(f, i, j);
		super.draw(i, j, f);
		this.drawMouseoverTooltip(i, j);
	}

	@Override
	protected void drawBackground(float f, int i, int j) {
		GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
		this.client.getTextureManager().bindTexture(TEXTURE);
		int k = (this.width - this.containerWidth) / 2;
		int l = (this.height - this.containerHeight) / 2;
		this.drawTexturedRect(k, l, 0, 0, this.containerWidth, this.containerHeight);
		if ((this.container.getSlot(0).hasStack() || this.container.getSlot(1).hasStack()) && !this.container.getSlot(2).hasStack()) {
			this.drawTexturedRect(k + 92, l + 31, this.containerWidth, 0, 28, 21);
		}
	}
}
