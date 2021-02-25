package net.minecraft.network.packet.c2s.query;

import net.minecraft.network.Packet;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.listener.ServerQueryPacketListener;

public class QueryRequestC2SPacket implements Packet<ServerQueryPacketListener> {
	public QueryRequestC2SPacket() {
	}

	public QueryRequestC2SPacket(PacketByteBuf buf) {
	}

	@Override
	public void write(PacketByteBuf buf) {
	}

	public void apply(ServerQueryPacketListener serverQueryPacketListener) {
		serverQueryPacketListener.onRequest(this);
	}
}
