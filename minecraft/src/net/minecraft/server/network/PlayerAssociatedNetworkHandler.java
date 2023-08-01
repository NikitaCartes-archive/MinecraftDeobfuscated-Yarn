package net.minecraft.server.network;

import net.minecraft.network.packet.Packet;

public interface PlayerAssociatedNetworkHandler {
	ServerPlayerEntity getPlayer();

	void sendPacket(Packet<?> packet);

	void method_52398(Packet<?> packet);

	void method_52401();
}
