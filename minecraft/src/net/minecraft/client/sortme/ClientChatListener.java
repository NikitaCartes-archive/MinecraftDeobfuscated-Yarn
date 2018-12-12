package net.minecraft.client.sortme;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.sortme.ChatMessageType;
import net.minecraft.text.TextComponent;

@Environment(EnvType.CLIENT)
public interface ClientChatListener {
	void method_1794(ChatMessageType chatMessageType, TextComponent textComponent);
}
