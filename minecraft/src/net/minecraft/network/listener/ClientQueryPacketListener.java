package net.minecraft.network.listener;

import net.minecraft.network.packet.s2c.query.QueryPongS2CPacket;
import net.minecraft.network.packet.s2c.query.QueryResponseS2CPacket;

public interface ClientQueryPacketListener extends PacketListener {
	void onResponse(QueryResponseS2CPacket packet);

	void onPong(QueryPongS2CPacket packet);
}
