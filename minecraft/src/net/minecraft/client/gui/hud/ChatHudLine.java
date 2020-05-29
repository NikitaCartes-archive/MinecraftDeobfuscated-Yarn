package net.minecraft.client.gui.hud;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_5348;

@Environment(EnvType.CLIENT)
public class ChatHudLine {
	private final int creationTick;
	private final class_5348 text;
	private final int id;

	public ChatHudLine(int creationTick, class_5348 arg, int id) {
		this.text = arg;
		this.creationTick = creationTick;
		this.id = id;
	}

	public class_5348 getText() {
		return this.text;
	}

	public int getCreationTick() {
		return this.creationTick;
	}

	public int getId() {
		return this.id;
	}
}
