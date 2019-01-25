package net.minecraft.client.gui.ingame;

import com.mojang.blaze3d.platform.GlStateManager;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.ContainerScreen;
import net.minecraft.container.BrewingStandContainer;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.text.TextComponent;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;

@Environment(EnvType.CLIENT)
public class BrewingStandScreen extends ContainerScreen<BrewingStandContainer> {
	private static final Identifier TEXTURE = new Identifier("textures/gui/container/brewing_stand.png");
	private static final int[] field_2824 = new int[]{29, 24, 20, 16, 11, 6, 0};

	public BrewingStandScreen(BrewingStandContainer brewingStandContainer, PlayerInventory playerInventory, TextComponent textComponent) {
		super(brewingStandContainer, playerInventory, textComponent);
	}

	@Override
	public void draw(int i, int j, float f) {
		this.drawBackground();
		super.draw(i, j, f);
		this.drawMousoverTooltip(i, j);
	}

	@Override
	protected void drawForeground(int i, int j) {
		this.fontRenderer
			.draw(this.name.getFormattedText(), (float)(this.containerWidth / 2 - this.fontRenderer.getStringWidth(this.name.getFormattedText()) / 2), 6.0F, 4210752);
		this.fontRenderer.draw(this.playerInventory.getDisplayName().getFormattedText(), 8.0F, (float)(this.containerHeight - 96 + 2), 4210752);
	}

	@Override
	protected void drawBackground(float f, int i, int j) {
		GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
		this.client.getTextureManager().bindTexture(TEXTURE);
		int k = (this.width - this.containerWidth) / 2;
		int l = (this.height - this.containerHeight) / 2;
		this.drawTexturedRect(k, l, 0, 0, this.containerWidth, this.containerHeight);
		int m = this.container.method_17377();
		int n = MathHelper.clamp((18 * m + 20 - 1) / 20, 0, 18);
		if (n > 0) {
			this.drawTexturedRect(k + 60, l + 44, 176, 29, n, 4);
		}

		int o = this.container.method_17378();
		if (o > 0) {
			int p = (int)(28.0F * (1.0F - (float)o / 400.0F));
			if (p > 0) {
				this.drawTexturedRect(k + 97, l + 16, 176, 0, 9, p);
			}

			p = field_2824[o / 2 % 7];
			if (p > 0) {
				this.drawTexturedRect(k + 63, l + 14 + 29 - p, 185, 29 - p, 12, p);
			}
		}
	}
}
