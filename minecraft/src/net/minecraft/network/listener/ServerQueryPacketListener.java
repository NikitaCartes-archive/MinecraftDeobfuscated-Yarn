package net.minecraft.network.listener;

import net.minecraft.class_6857;
import net.minecraft.network.packet.c2s.query.QueryPingC2SPacket;
import net.minecraft.network.packet.c2s.query.QueryRequestC2SPacket;

public interface ServerQueryPacketListener extends class_6857 {
	void onPing(QueryPingC2SPacket packet);

	void onRequest(QueryRequestC2SPacket packet);
}
