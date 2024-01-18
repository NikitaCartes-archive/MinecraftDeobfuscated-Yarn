package net.minecraft.network.packet.c2s.play;

import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.listener.ServerPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.PacketType;
import net.minecraft.network.packet.PlayPackets;

public record AcknowledgeChunksC2SPacket(float desiredChunksPerTick) implements Packet<ServerPlayPacketListener> {
	public static final PacketCodec<PacketByteBuf, AcknowledgeChunksC2SPacket> CODEC = Packet.createCodec(
		AcknowledgeChunksC2SPacket::write, AcknowledgeChunksC2SPacket::new
	);

	private AcknowledgeChunksC2SPacket(PacketByteBuf buf) {
		this(buf.readFloat());
	}

	private void write(PacketByteBuf buf) {
		buf.writeFloat(this.desiredChunksPerTick);
	}

	@Override
	public PacketType<AcknowledgeChunksC2SPacket> getPacketId() {
		return PlayPackets.CHUNK_BATCH_RECEIVED;
	}

	public void apply(ServerPlayPacketListener serverPlayPacketListener) {
		serverPlayPacketListener.onAcknowledgeChunks(this);
	}
}
