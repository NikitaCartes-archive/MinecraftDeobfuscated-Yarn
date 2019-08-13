package net.minecraft.network.listener;

import net.minecraft.server.network.packet.QueryPingC2SPacket;
import net.minecraft.server.network.packet.QueryRequestC2SPacket;

public interface ServerQueryPacketListener extends PacketListener {
	void onPing(QueryPingC2SPacket queryPingC2SPacket);

	void onRequest(QueryRequestC2SPacket queryRequestC2SPacket);
}
