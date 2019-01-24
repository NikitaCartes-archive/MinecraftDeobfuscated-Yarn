package net.minecraft.client.network.packet;

import java.io.IOException;
import net.minecraft.network.Packet;
import net.minecraft.network.listener.ClientQueryPacketListener;
import net.minecraft.util.PacketByteBuf;

public class QueryPongClientPacket implements Packet<ClientQueryPacketListener> {
	private long startTime;

	public QueryPongClientPacket() {
	}

	public QueryPongClientPacket(long l) {
		this.startTime = l;
	}

	@Override
	public void read(PacketByteBuf packetByteBuf) throws IOException {
		this.startTime = packetByteBuf.readLong();
	}

	@Override
	public void write(PacketByteBuf packetByteBuf) throws IOException {
		packetByteBuf.writeLong(this.startTime);
	}

	public void method_12670(ClientQueryPacketListener clientQueryPacketListener) {
		clientQueryPacketListener.onPong(this);
	}
}
