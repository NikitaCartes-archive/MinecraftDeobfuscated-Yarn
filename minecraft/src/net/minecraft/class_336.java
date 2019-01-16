package net.minecraft;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.sortme.ClientChatListener;
import net.minecraft.sortme.ChatMessageType;
import net.minecraft.text.TextComponent;

@Environment(EnvType.CLIENT)
public class class_336 implements ClientChatListener {
	private final MinecraftClient client;

	public class_336(MinecraftClient minecraftClient) {
		this.client = minecraftClient;
	}

	@Override
	public void onChatMessage(ChatMessageType chatMessageType, TextComponent textComponent) {
		this.client.inGameHud.setOverlayMessage(textComponent, false);
	}
}
