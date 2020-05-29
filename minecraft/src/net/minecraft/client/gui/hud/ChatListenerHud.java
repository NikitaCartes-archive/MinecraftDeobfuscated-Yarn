package net.minecraft.client.gui.hud;

import java.util.UUID;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.ClientChatListener;
import net.minecraft.network.MessageType;
import net.minecraft.text.Text;

@Environment(EnvType.CLIENT)
public class ChatListenerHud implements ClientChatListener {
	private final MinecraftClient client;

	public ChatListenerHud(MinecraftClient client) {
		this.client = client;
	}

	@Override
	public void onChatMessage(MessageType messageType, Text message, UUID uUID) {
		if (!this.client.shouldBlockMessages(uUID)) {
			if (messageType != MessageType.CHAT) {
				this.client.inGameHud.getChatHud().addMessage(message);
			} else {
				this.client.inGameHud.getChatHud().method_27147(message);
			}
		}
	}
}
