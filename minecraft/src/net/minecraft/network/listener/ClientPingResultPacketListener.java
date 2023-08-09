package net.minecraft.network.listener;

import net.minecraft.network.packet.s2c.query.PingResultS2CPacket;

public interface ClientPingResultPacketListener extends PacketListener {
	/**
	 * Handles a packet from the server that includes the "ping" (connection latency).
	 * This is different from {@link net.minecraft.network.packet.c2s.common.CommonPongC2SPacket},
	 * which is sent by the client to acknowledgment a ping packet from the server.
	 */
	void onPingResult(PingResultS2CPacket packet);
}
