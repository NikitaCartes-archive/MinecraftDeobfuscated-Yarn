package net.minecraft.client.gui.container;

import com.mojang.blaze3d.platform.GlStateManager;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.ContainerScreen;
import net.minecraft.container.ShulkerBoxContainer;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
public class ShulkerBoxScreen extends ContainerScreen<ShulkerBoxContainer> {
	private static final Identifier TEXTURE = new Identifier("textures/gui/container/shulker_box.png");

	public ShulkerBoxScreen(ShulkerBoxContainer shulkerBoxContainer, PlayerInventory playerInventory, Component component) {
		super(shulkerBoxContainer, playerInventory, component);
		this.containerHeight++;
	}

	@Override
	public void render(int i, int j, float f) {
		this.renderBackground();
		super.render(i, j, f);
		this.drawMouseoverTooltip(i, j);
	}

	@Override
	protected void drawForeground(int i, int j) {
		this.font.draw(this.title.getFormattedText(), 8.0F, 6.0F, 4210752);
		this.font.draw(this.playerInventory.getDisplayName().getFormattedText(), 8.0F, (float)(this.containerHeight - 96 + 2), 4210752);
	}

	@Override
	protected void drawBackground(float f, int i, int j) {
		GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
		this.minecraft.getTextureManager().bindTexture(TEXTURE);
		int k = (this.width - this.containerWidth) / 2;
		int l = (this.height - this.containerHeight) / 2;
		this.blit(k, l, 0, 0, this.containerWidth, this.containerHeight);
	}
}
