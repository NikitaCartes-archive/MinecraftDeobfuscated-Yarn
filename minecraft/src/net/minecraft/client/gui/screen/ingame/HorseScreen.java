package net.minecraft.client.gui.screen.ingame;

import com.mojang.blaze3d.systems.RenderSystem;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.passive.AbstractDonkeyEntity;
import net.minecraft.entity.passive.HorseBaseEntity;
import net.minecraft.entity.passive.LlamaEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.screen.HorseScreenHandler;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
public class HorseScreen extends HandledScreen<HorseScreenHandler> {
	private static final Identifier TEXTURE = new Identifier("textures/gui/container/horse.png");
	private final HorseBaseEntity entity;
	private float mouseX;
	private float mouseY;

	public HorseScreen(HorseScreenHandler handler, PlayerInventory inventory, HorseBaseEntity entity) {
		super(handler, inventory, entity.getDisplayName());
		this.entity = entity;
		this.passEvents = false;
	}

	@Override
	protected void drawForeground(MatrixStack matrixStack, int i, int j) {
		this.textRenderer.draw(matrixStack, this.title, 8.0F, 6.0F, 4210752);
		this.textRenderer.draw(matrixStack, this.playerInventory.getDisplayName(), 8.0F, (float)(this.backgroundHeight - 96 + 2), 4210752);
	}

	@Override
	protected void drawBackground(MatrixStack matrixStack, float f, int mouseY, int i) {
		RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
		this.client.getTextureManager().bindTexture(TEXTURE);
		int j = (this.width - this.backgroundWidth) / 2;
		int k = (this.height - this.backgroundHeight) / 2;
		this.drawTexture(matrixStack, j, k, 0, 0, this.backgroundWidth, this.backgroundHeight);
		if (this.entity instanceof AbstractDonkeyEntity) {
			AbstractDonkeyEntity abstractDonkeyEntity = (AbstractDonkeyEntity)this.entity;
			if (abstractDonkeyEntity.hasChest()) {
				this.drawTexture(matrixStack, j + 79, k + 17, 0, this.backgroundHeight, abstractDonkeyEntity.method_6702() * 18, 54);
			}
		}

		if (this.entity.canBeSaddled()) {
			this.drawTexture(matrixStack, j + 7, k + 35 - 18, 18, this.backgroundHeight + 54, 18, 18);
		}

		if (this.entity.canEquip()) {
			if (this.entity instanceof LlamaEntity) {
				this.drawTexture(matrixStack, j + 7, k + 35, 36, this.backgroundHeight + 54, 18, 18);
			} else {
				this.drawTexture(matrixStack, j + 7, k + 35, 0, this.backgroundHeight + 54, 18, 18);
			}
		}

		InventoryScreen.drawEntity(j + 51, k + 60, 17, (float)(j + 51) - this.mouseX, (float)(k + 75 - 50) - this.mouseY, this.entity);
	}

	@Override
	public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
		this.renderBackground(matrices);
		this.mouseX = (float)mouseX;
		this.mouseY = (float)mouseY;
		super.render(matrices, mouseX, mouseY, delta);
		this.drawMouseoverTooltip(matrices, mouseX, mouseY);
	}
}
