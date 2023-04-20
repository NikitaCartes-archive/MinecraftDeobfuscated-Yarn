package net.minecraft.client.realms;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.Drawable;
import net.minecraft.text.Text;

@Environment(EnvType.CLIENT)
public class RealmsLabel implements Drawable {
	private final Text text;
	private final int x;
	private final int y;
	private final int color;

	public RealmsLabel(Text text, int x, int y, int color) {
		this.text = text;
		this.x = x;
		this.y = y;
		this.color = color;
	}

	@Override
	public void render(DrawContext context, int mouseX, int mouseY, float delta) {
		context.drawCenteredTextWithShadow(MinecraftClient.getInstance().textRenderer, this.text, this.x, this.y, this.color);
	}

	public Text getText() {
		return this.text;
	}
}
