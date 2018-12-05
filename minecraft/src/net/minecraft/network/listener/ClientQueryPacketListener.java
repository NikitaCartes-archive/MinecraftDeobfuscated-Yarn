package net.minecraft.network.listener;

import net.minecraft.client.network.packet.QueryPongClientPacket;
import net.minecraft.client.network.packet.QueryResponseClientPacket;

public interface ClientQueryPacketListener extends PacketListener {
	void onResponse(QueryResponseClientPacket queryResponseClientPacket);

	void onPong(QueryPongClientPacket queryPongClientPacket);
}
