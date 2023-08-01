package net.minecraft.network.listener;

import net.minecraft.network.NetworkSide;

public interface ClientPacketListener extends PacketListener {
	@Override
	default NetworkSide getSide() {
		return NetworkSide.CLIENTBOUND;
	}
}
