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
	private float horizontalAlignment = 0.5F;

	public TextWidget(Text message, TextRenderer textRenderer) {
		this(0, 0, textRenderer.getWidth(message.asOrderedText()), 9, message, textRenderer);
	}

	public TextWidget(int width, int height, Text message, TextRenderer textRenderer) {
		this(0, 0, width, height, message, textRenderer);
	}

	public TextWidget(int x, int y, int width, int height, Text message, TextRenderer textRenderer) {
		super(x, y, width, height, message);
		this.textRenderer = textRenderer;
		this.active = false;
	}

	public TextWidget setTextColor(int textColor) {
		this.textColor = textColor;
		return this;
	}

	private TextWidget align(float horizontalAlignment) {
		this.horizontalAlignment = horizontalAlignment;
		return this;
	}

	public TextWidget alignLeft() {
		return this.align(0.0F);
	}

	public TextWidget alignCenter() {
		return this.align(0.5F);
	}

	public TextWidget alignRight() {
		return this.align(1.0F);
	}

	@Override
	public void appendClickableNarrations(NarrationMessageBuilder builder) {
	}

	@Override
	public void renderButton(MatrixStack matrices, int mouseX, int mouseY, float delta) {
		Text text = this.getMessage();
		int i = this.getX() + Math.round(this.horizontalAlignment * (float)(this.getWidth() - this.textRenderer.getWidth(text)));
		int j = this.getY() + (this.getHeight() - 9) / 2;
		drawTextWithShadow(matrices, this.textRenderer, text, i, j, this.textColor);
	}
}
