package net.minecraft.client.gui.hud;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.ClientChatListener;
import net.minecraft.text.ChatMessageType;
import net.minecraft.text.TextComponent;

@Environment(EnvType.CLIENT)
public class ChatListenerHud implements ClientChatListener {
	private final MinecraftClient client;

	public ChatListenerHud(MinecraftClient minecraftClient) {
		this.client = minecraftClient;
	}

	@Override
	public void onChatMessage(ChatMessageType chatMessageType, TextComponent textComponent) {
		this.client.inGameHud.getChatHud().addMessage(textComponent);
	}
}
