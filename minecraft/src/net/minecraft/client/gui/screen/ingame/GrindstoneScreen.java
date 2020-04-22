package net.minecraft.client.gui.screen.ingame;

import com.mojang.blaze3d.systems.RenderSystem;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.screen.GrindstoneScreenHandler;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
public class GrindstoneScreen extends HandledScreen<GrindstoneScreenHandler> {
	private static final Identifier TEXTURE = new Identifier("textures/gui/container/grindstone.png");

	public GrindstoneScreen(GrindstoneScreenHandler handler, PlayerInventory inventory, Text title) {
		super(handler, inventory, title);
	}

	@Override
	protected void drawForeground(MatrixStack matrixStack, int i, int j) {
		this.textRenderer.draw(matrixStack, this.title, 8.0F, 6.0F, 4210752);
		this.textRenderer.draw(matrixStack, this.playerInventory.getDisplayName(), 8.0F, (float)(this.backgroundHeight - 96 + 2), 4210752);
	}

	@Override
	public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
		this.renderBackground(matrices);
		this.drawBackground(matrices, delta, mouseX, mouseY);
		super.render(matrices, mouseX, mouseY, delta);
		this.drawMouseoverTooltip(matrices, mouseX, mouseY);
	}

	@Override
	protected void drawBackground(MatrixStack matrixStack, float f, int mouseY, int i) {
		RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
		this.client.getTextureManager().bindTexture(TEXTURE);
		int j = (this.width - this.backgroundWidth) / 2;
		int k = (this.height - this.backgroundHeight) / 2;
		this.drawTexture(matrixStack, j, k, 0, 0, this.backgroundWidth, this.backgroundHeight);
		if ((this.handler.getSlot(0).hasStack() || this.handler.getSlot(1).hasStack()) && !this.handler.getSlot(2).hasStack()) {
			this.drawTexture(matrixStack, j + 92, k + 31, this.backgroundWidth, 0, 28, 21);
		}
	}
}
