package net.minecraft.network.listener;

import net.minecraft.server.network.packet.QueryPingServerPacket;
import net.minecraft.server.network.packet.QueryRequestServerPacket;

public interface ServerQueryPacketListener extends PacketListener {
	void onPing(QueryPingServerPacket queryPingServerPacket);

	void onRequest(QueryRequestServerPacket queryRequestServerPacket);
}
