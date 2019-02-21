package net.minecraft.client.gui.container;

import com.mojang.blaze3d.platform.GlStateManager;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.ContainerScreen;
import net.minecraft.client.gui.ingame.PlayerInventoryScreen;
import net.minecraft.container.HorseContainer;
import net.minecraft.entity.passive.AbstractDonkeyEntity;
import net.minecraft.entity.passive.HorseBaseEntity;
import net.minecraft.entity.passive.LlamaEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
public class HorseScreen extends ContainerScreen<HorseContainer> {
	private static final Identifier TEXTURE = new Identifier("textures/gui/container/horse.png");
	private final HorseBaseEntity entity;
	private float mouseX;
	private float mouseY;

	public HorseScreen(HorseContainer horseContainer, PlayerInventory playerInventory, HorseBaseEntity horseBaseEntity) {
		super(horseContainer, playerInventory, horseBaseEntity.getDisplayName());
		this.entity = horseBaseEntity;
		this.field_2558 = false;
	}

	@Override
	protected void drawForeground(int i, int j) {
		this.fontRenderer.draw(this.name.getFormattedText(), 8.0F, 6.0F, 4210752);
		this.fontRenderer.draw(this.playerInventory.getDisplayName().getFormattedText(), 8.0F, (float)(this.height - 96 + 2), 4210752);
	}

	@Override
	protected void drawBackground(float f, int i, int j) {
		GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
		this.client.getTextureManager().bindTexture(TEXTURE);
		int k = (this.screenWidth - this.width) / 2;
		int l = (this.screenHeight - this.height) / 2;
		this.drawTexturedRect(k, l, 0, 0, this.width, this.height);
		if (this.entity instanceof AbstractDonkeyEntity) {
			AbstractDonkeyEntity abstractDonkeyEntity = (AbstractDonkeyEntity)this.entity;
			if (abstractDonkeyEntity.hasChest()) {
				this.drawTexturedRect(k + 79, l + 17, 0, this.height, abstractDonkeyEntity.method_6702() * 18, 54);
			}
		}

		if (this.entity.method_6765()) {
			this.drawTexturedRect(k + 7, l + 35 - 18, 18, this.height + 54, 18, 18);
		}

		if (this.entity.method_6735()) {
			if (this.entity instanceof LlamaEntity) {
				this.drawTexturedRect(k + 7, l + 35, 36, this.height + 54, 18, 18);
			} else {
				this.drawTexturedRect(k + 7, l + 35, 0, this.height + 54, 18, 18);
			}
		}

		PlayerInventoryScreen.drawEntity(k + 51, l + 60, 17, (float)(k + 51) - this.mouseX, (float)(l + 75 - 50) - this.mouseY, this.entity);
	}

	@Override
	public void draw(int i, int j, float f) {
		this.drawBackground();
		this.mouseX = (float)i;
		this.mouseY = (float)j;
		super.draw(i, j, f);
		this.drawMouseoverTooltip(i, j);
	}
}
