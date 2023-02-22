package net.minecraft.client.gui.screen.ingame;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.screen.LegacySmithingScreenHandler;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

@Deprecated(
	forRemoval = true
)
@Environment(EnvType.CLIENT)
public class LegacySmithingScreen extends ForgingScreen<LegacySmithingScreenHandler> {
	private static final Identifier TEXTURE = new Identifier("textures/gui/container/legacy_smithing.png");

	public LegacySmithingScreen(LegacySmithingScreenHandler handler, PlayerInventory playerInventory, Text title) {
		super(handler, playerInventory, title, TEXTURE);
		this.titleX = 60;
		this.titleY = 18;
	}

	@Override
	protected void drawInvalidRecipeArrow(MatrixStack matrices, int x, int y) {
		if ((this.handler.getSlot(0).hasStack() || this.handler.getSlot(1).hasStack()) && !this.handler.getSlot(this.handler.getResultSlotIndex()).hasStack()) {
			drawTexture(matrices, x + 99, y + 45, this.backgroundWidth, 0, 28, 21);
		}
	}
}
