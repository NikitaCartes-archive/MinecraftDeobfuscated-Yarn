package net.minecraft.network.packet.s2c.play;

import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.PacketType;
import net.minecraft.network.packet.PlayPackets;
import net.minecraft.util.math.ChunkPos;

public record UnloadChunkS2CPacket(ChunkPos pos) implements Packet<ClientPlayPacketListener> {
	public static final PacketCodec<PacketByteBuf, UnloadChunkS2CPacket> CODEC = Packet.createCodec(UnloadChunkS2CPacket::write, UnloadChunkS2CPacket::new);

	private UnloadChunkS2CPacket(PacketByteBuf buf) {
		this(buf.readChunkPos());
	}

	private void write(PacketByteBuf buf) {
		buf.writeChunkPos(this.pos);
	}

	@Override
	public PacketType<UnloadChunkS2CPacket> getPacketId() {
		return PlayPackets.FORGET_LEVEL_CHUNK;
	}

	public void apply(ClientPlayPacketListener clientPlayPacketListener) {
		clientPlayPacketListener.onUnloadChunk(this);
	}
}
