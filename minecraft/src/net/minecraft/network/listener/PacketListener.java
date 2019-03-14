package net.minecraft.network.listener;

import net.minecraft.text.TextComponent;

public interface PacketListener {
	void onDisconnected(TextComponent textComponent);
}
