package net.minecraft.network.packet.s2c.play;

import io.netty.buffer.ByteBuf;
import java.util.Optional;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.PacketType;
import net.minecraft.network.packet.PlayPackets;
import net.minecraft.text.Text;
import net.minecraft.text.TextCodecs;

public record ServerMetadataS2CPacket(Text description, Optional<byte[]> favicon) implements Packet<ClientPlayPacketListener> {
	public static final PacketCodec<ByteBuf, ServerMetadataS2CPacket> CODEC = PacketCodec.tuple(
		TextCodecs.PACKET_CODEC,
		ServerMetadataS2CPacket::description,
		PacketCodecs.BYTE_ARRAY.collect(PacketCodecs::optional),
		ServerMetadataS2CPacket::favicon,
		ServerMetadataS2CPacket::new
	);

	@Override
	public PacketType<ServerMetadataS2CPacket> getPacketId() {
		return PlayPackets.SERVER_DATA;
	}

	public void apply(ClientPlayPacketListener clientPlayPacketListener) {
		clientPlayPacketListener.onServerMetadata(this);
	}
}
