package net.minecraft.client.gui;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.network.MessageType;
import net.minecraft.text.Text;

@Environment(EnvType.CLIENT)
public interface ClientChatListener {
	void method_1794(MessageType messageType, Text text);
}
