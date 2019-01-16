package net.minecraft.network.listener;

import net.minecraft.text.TextComponent;

public interface PacketListener {
	void onConnectionLost(TextComponent textComponent);
}
