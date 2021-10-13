package net.minecraft.client.gui;

import java.util.UUID;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.network.MessageType;
import net.minecraft.text.Text;

@Environment(EnvType.CLIENT)
public interface ClientChatListener {
	void onChatMessage(MessageType type, Text message, UUID sender);
}
