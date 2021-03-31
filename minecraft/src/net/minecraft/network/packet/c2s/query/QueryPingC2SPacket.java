package net.minecraft.network.packet.c2s.query;

import net.minecraft.network.Packet;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.listener.ServerQueryPacketListener;

public class QueryPingC2SPacket implements Packet<ServerQueryPacketListener> {
	private final long startTime;

	public QueryPingC2SPacket(long startTime) {
		this.startTime = startTime;
	}

	public QueryPingC2SPacket(PacketByteBuf buf) {
		this.startTime = buf.readLong();
	}

	@Override
	public void write(PacketByteBuf buf) {
		buf.writeLong(this.startTime);
	}

	public void apply(ServerQueryPacketListener serverQueryPacketListener) {
		serverQueryPacketListener.onPing(this);
	}

	public long getStartTime() {
		return this.startTime;
	}
}
