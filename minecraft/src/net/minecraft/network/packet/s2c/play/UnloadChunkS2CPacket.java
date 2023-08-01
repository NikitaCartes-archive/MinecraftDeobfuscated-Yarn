package net.minecraft.network.packet.s2c.play;

import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.util.math.ChunkPos;

public record UnloadChunkS2CPacket(ChunkPos pos) implements Packet<ClientPlayPacketListener> {
	public UnloadChunkS2CPacket(PacketByteBuf buf) {
		this(buf.readChunkPos());
	}

	@Override
	public void write(PacketByteBuf buf) {
		buf.writeChunkPos(this.pos);
	}

	public void apply(ClientPlayPacketListener clientPlayPacketListener) {
		clientPlayPacketListener.onUnloadChunk(this);
	}
}
