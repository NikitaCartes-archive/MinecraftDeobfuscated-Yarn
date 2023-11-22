package net.minecraft.client.gui.widget;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder;
import net.minecraft.client.gui.screen.narration.NarrationPart;
import net.minecraft.client.sound.SoundManager;
import net.minecraft.text.Text;
import net.minecraft.util.Colors;

@Environment(EnvType.CLIENT)
public class NarratedMultilineTextWidget extends MultilineTextWidget {
	private static final int BACKGROUND_COLOR = 1426063360;
	private static final int EXPANSION = 4;
	private final boolean alwaysShowBorders;

	public NarratedMultilineTextWidget(int maxWidth, Text message, TextRenderer textRenderer) {
		this(maxWidth, message, textRenderer, true);
	}

	public NarratedMultilineTextWidget(int maxWidth, Text message, TextRenderer textRenderer, boolean alwaysShowBorders) {
		super(message, textRenderer);
		this.setMaxWidth(maxWidth);
		this.setCentered(true);
		this.active = true;
		this.alwaysShowBorders = alwaysShowBorders;
	}

	@Override
	protected void appendClickableNarrations(NarrationMessageBuilder builder) {
		builder.put(NarrationPart.TITLE, this.getMessage());
	}

	@Override
	public void renderWidget(DrawContext context, int mouseX, int mouseY, float delta) {
		if (this.isFocused() || this.alwaysShowBorders) {
			int i = this.getX() - 4;
			int j = this.getY() - 4;
			int k = this.getWidth() + 8;
			int l = this.getHeight() + 8;
			int m = this.alwaysShowBorders ? (this.isFocused() ? Colors.WHITE : Colors.LIGHT_GRAY) : Colors.WHITE;
			context.fill(i + 1, j, i + k, j + l, 1426063360);
			context.drawBorder(i, j, k, l, m);
		}

		super.renderWidget(context, mouseX, mouseY, delta);
	}

	@Override
	public void playDownSound(SoundManager soundManager) {
	}
}
