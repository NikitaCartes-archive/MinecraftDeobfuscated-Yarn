package net.minecraft.network.listener;

import net.minecraft.network.NetworkSide;

public interface ServerPacketListener extends PacketListener {
	@Override
	default NetworkSide getSide() {
		return NetworkSide.SERVERBOUND;
	}
}
