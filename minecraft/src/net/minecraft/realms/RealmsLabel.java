package net.minecraft.realms;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.screen.Screen;

@Environment(EnvType.CLIENT)
public class RealmsLabel implements Element {
	private final String text;
	private final int x;
	private final int y;
	private final int color;

	public RealmsLabel(String text, int x, int y, int color) {
		this.text = text;
		this.x = x;
		this.y = y;
		this.color = color;
	}

	public void render(Screen screen) {
		screen.drawCenteredString(MinecraftClient.getInstance().textRenderer, this.text, this.x, this.y, this.color);
	}

	public String getText() {
		return this.text;
	}
}
