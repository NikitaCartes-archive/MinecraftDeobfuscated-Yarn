package net.minecraft.client.gui.hud;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.text.TextComponent;

@Environment(EnvType.CLIENT)
public class ChatHudLine {
	private final int tickCreated;
	private final TextComponent contents;
	private final int id;

	public ChatHudLine(int i, TextComponent textComponent, int j) {
		this.contents = textComponent;
		this.tickCreated = i;
		this.id = j;
	}

	public TextComponent getContents() {
		return this.contents;
	}

	public int getTickCreated() {
		return this.tickCreated;
	}

	public int getId() {
		return this.id;
	}
}
