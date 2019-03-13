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
	private static final Identifier field_16769 = new Identifier("textures/gui/container/grindstone.png");

	public GrindstoneScreen(GrindstoneContainer grindstoneContainer, PlayerInventory playerInventory, TextComponent textComponent) {
		super(grindstoneContainer, playerInventory, textComponent);
	}

	@Override
	protected void drawForeground(int i, int j) {
		this.fontRenderer.draw(this.field_17411.getFormattedText(), 8.0F, 6.0F, 4210752);
		this.fontRenderer.draw(this.playerInventory.method_5476().getFormattedText(), 8.0F, (float)(this.height - 96 + 2), 4210752);
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
		this.client.method_1531().method_4618(field_16769);
		int k = (this.screenWidth - this.width) / 2;
		int l = (this.screenHeight - this.height) / 2;
		this.drawTexturedRect(k, l, 0, 0, this.width, this.height);
		if ((this.container.method_7611(0).hasStack() || this.container.method_7611(1).hasStack()) && !this.container.method_7611(2).hasStack()) {
			this.drawTexturedRect(k + 92, l + 31, this.width, 0, 28, 21);
		}
	}
}
