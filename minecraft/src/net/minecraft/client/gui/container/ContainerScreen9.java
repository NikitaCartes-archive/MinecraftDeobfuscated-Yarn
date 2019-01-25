package net.minecraft.client.gui.container;

import com.mojang.blaze3d.platform.GlStateManager;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.ContainerScreen;
import net.minecraft.container.Generic3x3Container;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.text.TextComponent;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
public class ContainerScreen9 extends ContainerScreen<Generic3x3Container> {
	private static final Identifier TEXTURE = new Identifier("textures/gui/container/dispenser.png");

	public ContainerScreen9(Generic3x3Container generic3x3Container, PlayerInventory playerInventory, TextComponent textComponent) {
		super(generic3x3Container, playerInventory, textComponent);
	}

	@Override
	public void draw(int i, int j, float f) {
		this.drawBackground();
		super.draw(i, j, f);
		this.drawMousoverTooltip(i, j);
	}

	@Override
	protected void drawForeground(int i, int j) {
		String string = this.name.getFormattedText();
		this.fontRenderer.draw(string, (float)(this.containerWidth / 2 - this.fontRenderer.getStringWidth(string) / 2), 6.0F, 4210752);
		this.fontRenderer.draw(this.playerInventory.getDisplayName().getFormattedText(), 8.0F, (float)(this.containerHeight - 96 + 2), 4210752);
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
