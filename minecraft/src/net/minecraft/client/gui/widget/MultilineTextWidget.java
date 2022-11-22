package net.minecraft.client.gui.widget;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.font.MultilineText;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;

@Environment(EnvType.CLIENT)
public class MultilineTextWidget extends ClickableWidget {
	private final MultilineText text;
	private final int fontHeight;
	private final boolean centered;

	private MultilineTextWidget(MultilineText multilineText, TextRenderer textRenderer, Text text, boolean centered) {
		super(0, 0, multilineText.getMaxWidth(), multilineText.count() * 9, text);
		this.text = multilineText;
		this.fontHeight = 9;
		this.centered = centered;
		this.active = false;
	}

	public static MultilineTextWidget createCentered(int width, TextRenderer textRenderer, Text text) {
		MultilineText multilineText = MultilineText.create(textRenderer, text, width);
		return new MultilineTextWidget(multilineText, textRenderer, text, true);
	}

	public static MultilineTextWidget createNonCentered(int width, TextRenderer textRenderer, Text text) {
		MultilineText multilineText = MultilineText.create(textRenderer, text, width);
		return new MultilineTextWidget(multilineText, textRenderer, text, false);
	}

	@Override
	protected void appendClickableNarrations(NarrationMessageBuilder builder) {
	}

	@Override
	public void renderButton(MatrixStack matrices, int mouseX, int mouseY, float delta) {
		if (this.centered) {
			this.text.drawCenterWithShadow(matrices, this.getX() + this.getWidth() / 2, this.getY(), this.fontHeight, 16777215);
		} else {
			this.text.drawWithShadow(matrices, this.getX(), this.getY(), this.fontHeight, 16777215);
		}
	}
}
