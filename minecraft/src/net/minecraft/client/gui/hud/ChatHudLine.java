package net.minecraft.client.gui.hud;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.text.StringRenderable;

@Environment(EnvType.CLIENT)
public class ChatHudLine {
	private final int creationTick;
	private final StringRenderable text;
	private final int id;

	public ChatHudLine(int creationTick, StringRenderable stringRenderable, int id) {
		this.text = stringRenderable;
		this.creationTick = creationTick;
		this.id = id;
	}

	public StringRenderable getText() {
		return this.text;
	}

	public int getCreationTick() {
		return this.creationTick;
	}

	public int getId() {
		return this.id;
	}
}
