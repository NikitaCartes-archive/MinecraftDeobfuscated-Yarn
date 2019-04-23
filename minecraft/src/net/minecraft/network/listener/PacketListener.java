package net.minecraft.network.listener;

import net.minecraft.network.chat.Component;

public interface PacketListener {
	void onDisconnected(Component component);
}
