package net.minecraft.network.packet.s2c.query;

import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.listener.ClientQueryPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.PacketType;
import net.minecraft.network.packet.StatusPackets;
import net.minecraft.server.ServerMetadata;

public record QueryResponseS2CPacket(ServerMetadata metadata) implements Packet<ClientQueryPacketListener> {
	public static final PacketCodec<PacketByteBuf, QueryResponseS2CPacket> CODEC = Packet.createCodec(QueryResponseS2CPacket::write, QueryResponseS2CPacket::new);

	private QueryResponseS2CPacket(PacketByteBuf buf) {
		this(buf.decodeAsJson(ServerMetadata.CODEC));
	}

	private void write(PacketByteBuf buf) {
		buf.encodeAsJson(ServerMetadata.CODEC, this.metadata);
	}

	@Override
	public PacketType<QueryResponseS2CPacket> getPacketId() {
		return StatusPackets.STATUS_RESPONSE;
	}

	public void apply(ClientQueryPacketListener clientQueryPacketListener) {
		clientQueryPacketListener.onResponse(this);
	}
}
