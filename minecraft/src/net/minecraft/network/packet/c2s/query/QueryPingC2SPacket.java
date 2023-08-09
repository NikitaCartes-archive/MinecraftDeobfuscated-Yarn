package net.minecraft.network.packet.c2s.query;

import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.listener.ServerQueryPingPacketListener;
import net.minecraft.network.packet.Packet;

public class QueryPingC2SPacket implements Packet<ServerQueryPingPacketListener> {
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

	public void apply(ServerQueryPingPacketListener serverQueryPingPacketListener) {
		serverQueryPingPacketListener.onQueryPing(this);
	}

	public long getStartTime() {
		return this.startTime;
	}
}
