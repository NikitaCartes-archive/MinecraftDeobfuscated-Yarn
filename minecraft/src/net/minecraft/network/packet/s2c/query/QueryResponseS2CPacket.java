package net.minecraft.network.packet.s2c.query;

import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.listener.ClientQueryPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.server.ServerMetadata;

public record QueryResponseS2CPacket(ServerMetadata metadata) implements Packet<ClientQueryPacketListener> {
	public QueryResponseS2CPacket(PacketByteBuf buf) {
		this(buf.decodeAsJson(ServerMetadata.CODEC));
	}

	@Override
	public void write(PacketByteBuf buf) {
		buf.encodeAsJson(ServerMetadata.CODEC, this.metadata);
	}

	public void apply(ClientQueryPacketListener clientQueryPacketListener) {
		clientQueryPacketListener.onResponse(this);
	}
}
