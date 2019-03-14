package net.minecraft.client.sortme;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.sortme.ChatMessageType;
import net.minecraft.text.TextComponent;

@Environment(EnvType.CLIENT)
public interface ClientChatListener {
	void onChatMessage(ChatMessageType chatMessageType, TextComponent textComponent);
}
