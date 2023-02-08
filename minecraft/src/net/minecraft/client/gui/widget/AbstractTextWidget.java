package net.minecraft.client.gui.widget;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder;
import net.minecraft.text.Text;

@Environment(EnvType.CLIENT)
public abstract class AbstractTextWidget extends ClickableWidget {
	private final TextRenderer textRenderer;
	private int textColor = 16777215;

	public AbstractTextWidget(int x, int y, int width, int height, Text message, TextRenderer textRenderer) {
		super(x, y, width, height, message);
		this.textRenderer = textRenderer;
	}

	@Override
	protected void appendClickableNarrations(NarrationMessageBuilder builder) {
	}

	public AbstractTextWidget setTextColor(int textColor) {
		this.textColor = textColor;
		return this;
	}

	protected final TextRenderer getTextRenderer() {
		return this.textRenderer;
	}

	protected final int getTextColor() {
		return this.textColor;
	}
}
