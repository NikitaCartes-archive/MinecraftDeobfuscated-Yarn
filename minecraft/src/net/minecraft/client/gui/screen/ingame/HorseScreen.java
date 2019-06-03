package net.minecraft.client.gui.screen.ingame;

import com.mojang.blaze3d.platform.GlStateManager;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.container.HorseContainer;
import net.minecraft.entity.passive.AbstractDonkeyEntity;
import net.minecraft.entity.passive.HorseBaseEntity;
import net.minecraft.entity.passive.LlamaEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
public class HorseScreen extends AbstractContainerScreen<HorseContainer> {
	private static final Identifier TEXTURE = new Identifier("textures/gui/container/horse.png");
	private final HorseBaseEntity entity;
	private float mouseX;
	private float mouseY;

	public HorseScreen(HorseContainer horseContainer, PlayerInventory playerInventory, HorseBaseEntity horseBaseEntity) {
		super(horseContainer, playerInventory, horseBaseEntity.method_5476());
		this.entity = horseBaseEntity;
		this.passEvents = false;
	}

	@Override
	protected void drawForeground(int i, int j) {
		this.font.draw(this.title.asFormattedString(), 8.0F, 6.0F, 4210752);
		this.font.draw(this.playerInventory.method_5476().asFormattedString(), 8.0F, (float)(this.containerHeight - 96 + 2), 4210752);
	}

	@Override
	protected void drawBackground(float f, int i, int j) {
		GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
		this.minecraft.getTextureManager().bindTexture(TEXTURE);
		int k = (this.width - this.containerWidth) / 2;
		int l = (this.height - this.containerHeight) / 2;
		this.blit(k, l, 0, 0, this.containerWidth, this.containerHeight);
		if (this.entity instanceof AbstractDonkeyEntity) {
			AbstractDonkeyEntity abstractDonkeyEntity = (AbstractDonkeyEntity)this.entity;
			if (abstractDonkeyEntity.hasChest()) {
				this.blit(k + 79, l + 17, 0, this.containerHeight, abstractDonkeyEntity.method_6702() * 18, 54);
			}
		}

		if (this.entity.canBeSaddled()) {
			this.blit(k + 7, l + 35 - 18, 18, this.containerHeight + 54, 18, 18);
		}

		if (this.entity.canEquip()) {
			if (this.entity instanceof LlamaEntity) {
				this.blit(k + 7, l + 35, 36, this.containerHeight + 54, 18, 18);
			} else {
				this.blit(k + 7, l + 35, 0, this.containerHeight + 54, 18, 18);
			}
		}

		InventoryScreen.drawEntity(k + 51, l + 60, 17, (float)(k + 51) - this.mouseX, (float)(l + 75 - 50) - this.mouseY, this.entity);
	}

	@Override
	public void render(int i, int j, float f) {
		this.renderBackground();
		this.mouseX = (float)i;
		this.mouseY = (float)j;
		super.render(i, j, f);
		this.drawMouseoverTooltip(i, j);
	}
}
