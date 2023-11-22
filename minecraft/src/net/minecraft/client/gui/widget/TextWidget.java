package net.minecraft.client.gui.widget;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.text.OrderedText;
import net.minecraft.text.StringVisitable;
import net.minecraft.text.Text;
import net.minecraft.util.Language;

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
	public void renderWidget(DrawContext context, int mouseX, int mouseY, float delta) {
		Text text = this.getMessage();
		TextRenderer textRenderer = this.getTextRenderer();
		int i = this.getWidth();
		int j = textRenderer.getWidth(text);
		int k = this.getX() + Math.round(this.horizontalAlignment * (float)(i - j));
		int l = this.getY() + (this.getHeight() - 9) / 2;
		OrderedText orderedText = j > i ? this.trim(text, i) : text.asOrderedText();
		context.drawTextWithShadow(textRenderer, orderedText, k, l, this.getTextColor());
	}

	private OrderedText trim(Text text, int width) {
		TextRenderer textRenderer = this.getTextRenderer();
		StringVisitable stringVisitable = textRenderer.trimToWidth(text, width - textRenderer.getWidth(ScreenTexts.ELLIPSIS));
		return Language.getInstance().reorder(StringVisitable.concat(stringVisitable, ScreenTexts.ELLIPSIS));
	}
}
