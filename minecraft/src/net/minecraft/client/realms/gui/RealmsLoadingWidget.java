package net.minecraft.client.realms.gui;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.LoadingDisplay;
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder;
import net.minecraft.client.gui.screen.narration.NarrationPart;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.text.Text;
import net.minecraft.util.Util;

@Environment(EnvType.CLIENT)
public class RealmsLoadingWidget extends ClickableWidget {
	private final TextRenderer textRenderer;

	public RealmsLoadingWidget(TextRenderer textRenderer, Text message) {
		super(0, 0, textRenderer.getWidth(message), 9 * 2, message);
		this.textRenderer = textRenderer;
	}

	@Override
	protected void renderButton(DrawContext context, int mouseX, int mouseY, float delta) {
		int i = this.getX() + this.getWidth() / 2;
		int j = this.getY() + this.getHeight() / 2;
		Text text = this.getMessage();
		context.drawText(this.textRenderer, text, i - this.textRenderer.getWidth(text) / 2, j - 9, -1, false);
		String string = LoadingDisplay.get(Util.getMeasuringTimeMs());
		context.drawText(this.textRenderer, string, i - this.textRenderer.getWidth(string) / 2, j, -8355712, false);
	}

	@Override
	protected void appendClickableNarrations(NarrationMessageBuilder builder) {
		builder.put(NarrationPart.TITLE, this.getMessage());
	}
}
