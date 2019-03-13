package net.minecraft.client.gui.hud;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.text.TextComponent;

@Environment(EnvType.CLIENT)
public class ChatHudLine {
	private final int tickCreated;
	private final TextComponent field_1651;
	private final int id;

	public ChatHudLine(int i, TextComponent textComponent, int j) {
		this.field_1651 = textComponent;
		this.tickCreated = i;
		this.id = j;
	}

	public TextComponent method_1412() {
		return this.field_1651;
	}

	public int getTickCreated() {
		return this.tickCreated;
	}

	public int getId() {
		return this.id;
	}
}
