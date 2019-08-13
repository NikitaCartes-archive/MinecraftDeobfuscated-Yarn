package net.minecraft.network.listener;

import net.minecraft.client.network.packet.QueryPongS2CPacket;
import net.minecraft.client.network.packet.QueryResponseS2CPacket;

public interface ClientQueryPacketListener extends PacketListener {
	void onResponse(QueryResponseS2CPacket queryResponseS2CPacket);

	void onPong(QueryPongS2CPacket queryPongS2CPacket);
}
