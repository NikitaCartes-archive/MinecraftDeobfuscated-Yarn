package net.minecraft.network.listener;

import net.minecraft.server.network.packet.QueryPingC2SPacket;
import net.minecraft.server.network.packet.QueryRequestC2SPacket;

public interface ServerQueryPacketListener extends PacketListener {
	void method_12697(QueryPingC2SPacket queryPingC2SPacket);

	void method_12698(QueryRequestC2SPacket queryRequestC2SPacket);
}
