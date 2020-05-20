package net.minecraft.client.gui;

import java.util.UUID;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.network.MessageType;
import net.minecraft.text.Text;

@Environment(EnvType.CLIENT)
public class GameInfoChatListener implements ClientChatListener {
	private final MinecraftClient client;

	public GameInfoChatListener(MinecraftClient client) {
		this.client = client;
	}

	@Override
	public void onChatMessage(MessageType messageType, Text message, UUID uUID) {
		if (!this.client.method_29042(uUID)) {
			this.client.inGameHud.setOverlayMessage(message, false);
		}
	}
}
