package net.minecraft.client.gui.screen.ingame;

import com.mojang.blaze3d.systems.RenderSystem;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.container.BrewingStandContainer;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;

@Environment(EnvType.CLIENT)
public class BrewingStandScreen extends AbstractContainerScreen<BrewingStandContainer> {
	private static final Identifier TEXTURE = new Identifier("textures/gui/container/brewing_stand.png");
	private static final int[] BUBBLE_PROGRESS = new int[]{29, 24, 20, 16, 11, 6, 0};

	public BrewingStandScreen(BrewingStandContainer brewingStandContainer, PlayerInventory playerInventory, Text text) {
		super(brewingStandContainer, playerInventory, text);
	}

	@Override
	public void render(int mouseX, int mouseY, float delta) {
		this.renderBackground();
		super.render(mouseX, mouseY, delta);
		this.drawMouseoverTooltip(mouseX, mouseY);
	}

	@Override
	protected void drawForeground(int mouseX, int mouseY) {
		this.font
			.draw(this.title.asFormattedString(), (float)(this.containerWidth / 2 - this.font.getStringWidth(this.title.asFormattedString()) / 2), 6.0F, 4210752);
		this.font.draw(this.playerInventory.getDisplayName().asFormattedString(), 8.0F, (float)(this.containerHeight - 96 + 2), 4210752);
	}

	@Override
	protected void drawBackground(float delta, int mouseX, int mouseY) {
		RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
		this.minecraft.getTextureManager().bindTexture(TEXTURE);
		int i = (this.width - this.containerWidth) / 2;
		int j = (this.height - this.containerHeight) / 2;
		this.blit(i, j, 0, 0, this.containerWidth, this.containerHeight);
		int k = this.container.getFuel();
		int l = MathHelper.clamp((18 * k + 20 - 1) / 20, 0, 18);
		if (l > 0) {
			this.blit(i + 60, j + 44, 176, 29, l, 4);
		}

		int m = this.container.getBrewTime();
		if (m > 0) {
			int n = (int)(28.0F * (1.0F - (float)m / 400.0F));
			if (n > 0) {
				this.blit(i + 97, j + 16, 176, 0, 9, n);
			}

			n = BUBBLE_PROGRESS[m / 2 % 7];
			if (n > 0) {
				this.blit(i + 63, j + 14 + 29 - n, 185, 29 - n, 12, n);
			}
		}
	}
}
