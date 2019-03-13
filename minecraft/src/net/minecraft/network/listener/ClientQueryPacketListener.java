package net.minecraft.network.listener;

import net.minecraft.client.network.packet.QueryPongS2CPacket;
import net.minecraft.client.network.packet.QueryResponseS2CPacket;

public interface ClientQueryPacketListener extends PacketListener {
	void method_12667(QueryResponseS2CPacket queryResponseS2CPacket);

	void method_12666(QueryPongS2CPacket queryPongS2CPacket);
}
