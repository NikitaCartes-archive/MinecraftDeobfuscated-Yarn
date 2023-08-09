package net.minecraft.server.network;

import net.minecraft.network.packet.Packet;

public interface PlayerAssociatedNetworkHandler {
	ServerPlayerEntity getPlayer();

	void sendPacket(Packet<?> packet);
}
