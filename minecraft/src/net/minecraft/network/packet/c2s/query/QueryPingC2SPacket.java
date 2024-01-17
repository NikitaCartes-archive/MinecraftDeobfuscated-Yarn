package net.minecraft.network.packet.c2s.query;

import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.listener.ServerQueryPingPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.PacketIdentifier;
import net.minecraft.network.packet.PingPackets;

public class QueryPingC2SPacket implements Packet<ServerQueryPingPacketListener> {
	public static final PacketCodec<PacketByteBuf, QueryPingC2SPacket> CODEC = Packet.createCodec(QueryPingC2SPacket::write, QueryPingC2SPacket::new);
	private final long startTime;

	public QueryPingC2SPacket(long startTime) {
		this.startTime = startTime;
	}

	private QueryPingC2SPacket(PacketByteBuf buf) {
		this.startTime = buf.readLong();
	}

	private void write(PacketByteBuf buf) {
		buf.writeLong(this.startTime);
	}

	@Override
	public PacketIdentifier<QueryPingC2SPacket> getPacketId() {
		return PingPackets.PING_REQUEST;
	}

	public void apply(ServerQueryPingPacketListener serverQueryPingPacketListener) {
		serverQueryPingPacketListener.onQueryPing(this);
	}

	public long getStartTime() {
		return this.startTime;
	}
}
