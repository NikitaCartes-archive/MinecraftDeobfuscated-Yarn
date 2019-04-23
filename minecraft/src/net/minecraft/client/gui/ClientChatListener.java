package net.minecraft.client.gui;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.network.chat.ChatMessageType;
import net.minecraft.network.chat.Component;

@Environment(EnvType.CLIENT)
public interface ClientChatListener {
	void onChatMessage(ChatMessageType chatMessageType, Component component);
}
