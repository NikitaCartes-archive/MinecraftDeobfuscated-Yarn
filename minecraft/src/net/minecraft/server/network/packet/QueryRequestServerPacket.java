package net.minecraft.server.network.packet;

import java.io.IOException;
import net.minecraft.network.Packet;
import net.minecraft.network.listener.ServerQueryPacketListener;
import net.minecraft.util.PacketByteBuf;

public class QueryRequestServerPacket implements Packet<ServerQueryPacketListener> {
	@Override
	public void read(PacketByteBuf packetByteBuf) throws IOException {
	}

	@Override
	public void write(PacketByteBuf packetByteBuf) throws IOException {
	}

	public void apply(ServerQueryPacketListener serverQueryPacketListener) {
		serverQueryPacketListener.onRequest(this);
	}
}
