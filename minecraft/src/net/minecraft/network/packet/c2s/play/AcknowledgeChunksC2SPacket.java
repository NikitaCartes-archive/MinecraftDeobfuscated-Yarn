package net.minecraft.network.packet.c2s.play;

import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.listener.ServerPlayPacketListener;
import net.minecraft.network.packet.Packet;

public record AcknowledgeChunksC2SPacket(float desiredChunksPerTick) implements Packet<ServerPlayPacketListener> {
	public AcknowledgeChunksC2SPacket(PacketByteBuf buf) {
		this(buf.readFloat());
	}

	@Override
	public void write(PacketByteBuf buf) {
		buf.writeFloat(this.desiredChunksPerTick);
	}

	public void apply(ServerPlayPacketListener serverPlayPacketListener) {
		serverPlayPacketListener.onAcknowledgeChunks(this);
	}
}
