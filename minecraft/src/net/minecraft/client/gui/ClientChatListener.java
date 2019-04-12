package net.minecraft.client.gui;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.text.ChatMessageType;
import net.minecraft.text.TextComponent;

@Environment(EnvType.CLIENT)
public interface ClientChatListener {
	void onChatMessage(ChatMessageType chatMessageType, TextComponent textComponent);
}
