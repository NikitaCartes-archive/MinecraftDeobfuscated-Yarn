package net.minecraft.client.gui.widget;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
public class IconWidget extends ClickableWidget {
	private final Identifier texture;

	public IconWidget(int width, int height, Identifier texture) {
		this(0, 0, width, height, texture);
	}

	public IconWidget(int x, int y, int width, int height, Identifier texture) {
		super(x, y, width, height, Text.empty());
		this.texture = texture;
	}

	@Override
	protected void appendClickableNarrations(NarrationMessageBuilder builder) {
	}

	@Override
	public void renderButton(DrawContext context, int mouseX, int mouseY, float delta) {
		int i = this.getWidth();
		int j = this.getHeight();
		context.drawTexture(this.texture, this.getX(), this.getY(), 0.0F, 0.0F, i, j, i, j);
	}
}
