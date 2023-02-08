package net.minecraft.client.gui.widget;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;

@Environment(EnvType.CLIENT)
public class TextWidget extends AbstractTextWidget {
	private float horizontalAlignment = 0.5F;

	public TextWidget(Text message, TextRenderer textRenderer) {
		this(0, 0, textRenderer.getWidth(message.asOrderedText()), 9, message, textRenderer);
	}

	public TextWidget(int width, int height, Text message, TextRenderer textRenderer) {
		this(0, 0, width, height, message, textRenderer);
	}

	public TextWidget(int x, int y, int width, int height, Text message, TextRenderer textRenderer) {
		super(x, y, width, height, message, textRenderer);
		this.active = false;
	}

	public TextWidget setTextColor(int textColor) {
		super.setTextColor(textColor);
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
	public void renderButton(MatrixStack matrices, int mouseX, int mouseY, float delta) {
		Text text = this.getMessage();
		TextRenderer textRenderer = this.getTextRenderer();
		int i = this.getX() + Math.round(this.horizontalAlignment * (float)(this.getWidth() - textRenderer.getWidth(text)));
		int j = this.getY() + (this.getHeight() - 9) / 2;
		drawTextWithShadow(matrices, textRenderer, text, i, j, this.getTextColor());
	}
}
