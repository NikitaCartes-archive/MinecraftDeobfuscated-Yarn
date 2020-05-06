package net.minecraft.client.gui.screen.ingame;

import com.mojang.blaze3d.systems.RenderSystem;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.screen.SmithingScreenHandler;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
public class SmithingScreen extends ForgingScreen<SmithingScreenHandler> {
	private static final Identifier TEXTURE = new Identifier("textures/gui/container/smithing.png");

	public SmithingScreen(SmithingScreenHandler handler, PlayerInventory playerInventory, Text title) {
		super(handler, playerInventory, title, TEXTURE);
	}

	@Override
	protected void drawForeground(MatrixStack matrixStack, int i, int j) {
		RenderSystem.disableBlend();
		this.textRenderer.draw(matrixStack, this.title, 60.0F, 20.0F, 4210752);
		this.textRenderer.draw(matrixStack, this.playerInventory.getDisplayName(), 8.0F, (float)(this.backgroundHeight - 96 + 2), 4210752);
	}
}
