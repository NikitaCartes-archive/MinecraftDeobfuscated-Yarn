package net.minecraft.network.handler;

import io.netty.util.Attribute;
import net.minecraft.network.NetworkState;
import net.minecraft.network.packet.Packet;

public interface NetworkStateTransitionHandler {
	static void handle(Attribute<NetworkState.PacketHandler<?>> protocolAttribute, Packet<?> packet) {
		NetworkState networkState = packet.getNewNetworkState();
		if (networkState != null) {
			NetworkState.PacketHandler<?> packetHandler = protocolAttribute.get();
			NetworkState networkState2 = packetHandler.getState();
			if (networkState != networkState2) {
				NetworkState.PacketHandler<?> packetHandler2 = networkState.getHandler(packetHandler.getSide());
				protocolAttribute.set(packetHandler2);
			}
		}
	}
}
