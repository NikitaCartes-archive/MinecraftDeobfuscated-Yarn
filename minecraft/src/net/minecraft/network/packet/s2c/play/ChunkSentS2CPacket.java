package net.minecraft.network.packet.s2c.play;

import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.Packet;

public record ChunkSentS2CPacket(int batchSize) implements Packet<ClientPlayPacketListener> {
	public ChunkSentS2CPacket(PacketByteBuf buf) {
		this(buf.readVarInt());
	}

	@Override
	public void write(PacketByteBuf buf) {
		buf.writeVarInt(this.batchSize);
	}

	public void apply(ClientPlayPacketListener clientPlayPacketListener) {
		clientPlayPacketListener.onChunkSent(this);
	}
}
