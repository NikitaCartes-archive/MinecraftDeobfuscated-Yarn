package net.minecraft.network.packet.s2c.query;

import net.minecraft.network.Packet;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.listener.ClientQueryPacketListener;

public class QueryPongS2CPacket implements Packet<ClientQueryPacketListener> {
	private final long startTime;

	public QueryPongS2CPacket(long startTime) {
		this.startTime = startTime;
	}

	public QueryPongS2CPacket(PacketByteBuf buf) {
		this.startTime = buf.readLong();
	}

	@Override
	public void write(PacketByteBuf buf) {
		buf.writeLong(this.startTime);
	}

	public void apply(ClientQueryPacketListener clientQueryPacketListener) {
		clientQueryPacketListener.onPong(this);
	}

	public long getStartTime() {
		return this.startTime;
	}
}
