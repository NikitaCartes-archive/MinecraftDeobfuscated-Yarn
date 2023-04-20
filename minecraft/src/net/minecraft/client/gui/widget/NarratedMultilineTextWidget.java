package net.minecraft.client.gui.widget;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder;
import net.minecraft.client.gui.screen.narration.NarrationPart;
import net.minecraft.client.sound.SoundManager;
import net.minecraft.text.Text;

@Environment(EnvType.CLIENT)
public class NarratedMultilineTextWidget extends MultilineTextWidget {
	private static final int FOCUSED_BORDER_COLOR = -1;
	private static final int UNFOCUSED_BORDER_COLOR = -6250336;
	private static final int BACKGROUND_COLOR = 1426063360;
	private static final int EXPANSION = 3;
	private static final int BORDER_WIDTH = 1;

	public NarratedMultilineTextWidget(TextRenderer textRenderer, Text text, int width) {
		super(text, textRenderer);
		this.setMaxWidth(width);
		this.setCentered(true);
		this.active = true;
	}

	@Override
	protected void appendClickableNarrations(NarrationMessageBuilder builder) {
		builder.put(NarrationPart.TITLE, this.getMessage());
	}

	@Override
	public void renderButton(DrawContext context, int mouseX, int mouseY, float delta) {
		int i = this.getX() - 3;
		int j = this.getY() - 3;
		int k = this.getX() + this.getWidth() + 3;
		int l = this.getY() + this.getHeight() + 3;
		int m = this.isFocused() ? -1 : -6250336;
		context.fill(i - 1, j - 1, i, l + 1, m);
		context.fill(k, j - 1, k + 1, l + 1, m);
		context.fill(i, j, k, j - 1, m);
		context.fill(i, l, k, l + 1, m);
		context.fill(i, j, k, l, 1426063360);
		super.renderButton(context, mouseX, mouseY, delta);
	}

	@Override
	public void playDownSound(SoundManager soundManager) {
	}
}
