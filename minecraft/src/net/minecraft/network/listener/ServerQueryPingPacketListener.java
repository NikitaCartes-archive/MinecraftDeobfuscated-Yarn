package net.minecraft.network.listener;

import net.minecraft.network.packet.c2s.query.QueryPingC2SPacket;

public interface ServerQueryPingPacketListener extends PacketListener {
	/**
	 * Handles a packet from client to query the "ping" (connection latency).
	 * This is different from {@link net.minecraft.network.packet.s2c.common.CommonPingS2CPacket},
	 * which can be sent by the server to request acknowledgment.
	 */
	void onQueryPing(QueryPingC2SPacket packet);
}
