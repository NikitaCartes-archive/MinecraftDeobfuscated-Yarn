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
	private static final int DEFAULT_MARGIN = 4;
	private final boolean alwaysShowBorders;
	private final int margin;

	public NarratedMultilineTextWidget(int maxWidth, Text message, TextRenderer textRenderer) {
		this(maxWidth, message, textRenderer, 4);
	}

	public NarratedMultilineTextWidget(int maxWidth, Text message, TextRenderer textRenderer, int margin) {
		this(maxWidth, message, textRenderer, true, margin);
	}

	public NarratedMultilineTextWidget(int maxWidth, Text message, TextRenderer textRenderer, boolean alwaysShowBorders, int margin) {
		super(message, textRenderer);
		this.setMaxWidth(maxWidth);
		this.setCentered(true);
		this.active = true;
		this.alwaysShowBorders = alwaysShowBorders;
		this.margin = margin;
	}

	public void initMaxWidth(int baseWidth) {
		this.setMaxWidth(baseWidth - this.margin * 4);
	}

	@Override
	protected void appendClickableNarrations(NarrationMessageBuilder builder) {
		builder.put(NarrationPart.TITLE, this.getMessage());
	}

	@Override
	public void renderWidget(DrawContext context, int mouseX, int mouseY, float delta) {
		if (this.isFocused() || this.alwaysShowBorders) {
			int i = this.getX() - this.margin;
			int j = this.getY() - this.margin;
			int k = this.getWidth() + this.margin * 2;
			int l = this.getHeight() + this.margin * 2;
			int m = this.alwaysShowBorders ? (this.isFocused() ? Colors.WHITE : Colors.LIGHT_GRAY) : Colors.WHITE;
			context.fill(i + 1, j, i + k, j + l, -16777216);
			context.drawBorder(i, j, k, l, m);
		}

		super.renderWidget(context, mouseX, mouseY, delta);
	}

	@Override
	public void playDownSound(SoundManager soundManager) {
	}
}
