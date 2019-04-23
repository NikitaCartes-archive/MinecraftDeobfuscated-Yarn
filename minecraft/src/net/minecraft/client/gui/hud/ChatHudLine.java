package net.minecraft.client.gui.hud;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.network.chat.Component;

@Environment(EnvType.CLIENT)
public class ChatHudLine {
	private final int tickCreated;
	private final Component contents;
	private final int id;

	public ChatHudLine(int i, Component component, int j) {
		this.contents = component;
		this.tickCreated = i;
		this.id = j;
	}

	public Component getContents() {
		return this.contents;
	}

	public int getTickCreated() {
		return this.tickCreated;
	}

	public int getId() {
		return this.id;
	}
}
