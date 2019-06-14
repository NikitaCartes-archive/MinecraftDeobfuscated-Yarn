package net.minecraft.client.gui;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.network.MessageType;
import net.minecraft.text.Text;

@Environment(EnvType.CLIENT)
public class GameInfoChatListener implements ClientChatListener {
	private final MinecraftClient client;

	public GameInfoChatListener(MinecraftClient minecraftClient) {
		this.client = minecraftClient;
	}

	@Override
	public void onChatMessage(MessageType messageType, Text text) {
		this.client.field_1705.setOverlayMessage(text, false);
	}
}
