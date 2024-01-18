package net.minecraft.network.packet.s2c.play;

import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.PacketType;
import net.minecraft.network.packet.PlayPackets;

public record ChunkSentS2CPacket(int batchSize) implements Packet<ClientPlayPacketListener> {
	public static final PacketCodec<PacketByteBuf, ChunkSentS2CPacket> CODEC = Packet.createCodec(ChunkSentS2CPacket::write, ChunkSentS2CPacket::new);

	private ChunkSentS2CPacket(PacketByteBuf buf) {
		this(buf.readVarInt());
	}

	private void write(PacketByteBuf buf) {
		buf.writeVarInt(this.batchSize);
	}

	@Override
	public PacketType<ChunkSentS2CPacket> getPacketId() {
		return PlayPackets.CHUNK_BATCH_FINISHED;
	}

	public void apply(ClientPlayPacketListener clientPlayPacketListener) {
		clientPlayPacketListener.onChunkSent(this);
	}
}
