package net.minecraft.client.gui.screen;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;

@Environment(EnvType.CLIENT)
public class MessageScreen extends Screen {
	public MessageScreen(Text text) {
		super(text);
	}

	@Override
	public boolean shouldCloseOnEsc() {
		return false;
	}

	@Override
	public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
		this.renderBackgroundTexture(matrices);
		drawCenteredTextWithShadow(matrices, this.textRenderer, this.title, this.width / 2, 70, 16777215);
		super.render(matrices, mouseX, mouseY, delta);
	}
}
