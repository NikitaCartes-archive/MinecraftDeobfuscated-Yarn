package net.minecraft.client.gui.hud;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.text.Text;

@Environment(EnvType.CLIENT)
public class ChatHudLine {
	private final int timestamp;
	private final Text field_1651;
	private final int id;

	public ChatHudLine(int i, Text text, int j) {
		this.field_1651 = text;
		this.timestamp = i;
		this.id = j;
	}

	public Text method_1412() {
		return this.field_1651;
	}

	public int getTimestamp() {
		return this.timestamp;
	}

	public int getId() {
		return this.id;
	}
}
