package net.minecraft.client.gui.widget;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;

@Environment(EnvType.CLIENT)
public class TextWidget extends ClickableWidget {
	private int textColor = 16777215;
	private final TextRenderer textRenderer;

	public TextWidget(Text message, TextRenderer textRenderer) {
		this(0, 0, textRenderer.getWidth(message.asOrderedText()), 9, message, textRenderer);
	}

	public TextWidget(int width, int height, Text message, TextRenderer textRenderer) {
		this(0, 0, width, height, message, textRenderer);
	}

	public TextWidget(int x, int y, int width, int height, Text message, TextRenderer textRenderer) {
		super(x, y, width, height, message);
		this.textRenderer = textRenderer;
	}

	public TextWidget setTextColor(int textColor) {
		this.textColor = textColor;
		return this;
	}

	@Override
	public void appendNarrations(NarrationMessageBuilder builder) {
	}

	@Override
	public boolean changeFocus(boolean lookForwards) {
		return false;
	}

	@Override
	public void renderButton(MatrixStack matrices, int mouseX, int mouseY, float delta) {
		drawCenteredText(matrices, this.textRenderer, this.getMessage(), this.getX() + this.getWidth() / 2, this.getY() + (this.getHeight() - 9) / 2, this.textColor);
	}
}
