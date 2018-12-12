package net.minecraft.client.gui.hud;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.sortme.ClientChatListener;
import net.minecraft.sortme.ChatMessageType;
import net.minecraft.text.TextComponent;

@Environment(EnvType.CLIENT)
public class ChatListenerHud implements ClientChatListener {
	private final MinecraftClient client;

	public ChatListenerHud(MinecraftClient minecraftClient) {
		this.client = minecraftClient;
	}

	@Override
	public void method_1794(ChatMessageType chatMessageType, TextComponent textComponent) {
		this.client.hudInGame.getHudChat().addMessage(textComponent);
	}
}
