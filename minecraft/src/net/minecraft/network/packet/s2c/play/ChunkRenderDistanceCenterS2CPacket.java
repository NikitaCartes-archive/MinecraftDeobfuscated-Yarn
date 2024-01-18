package net.minecraft.network.packet.s2c.play;

import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.PacketType;
import net.minecraft.network.packet.PlayPackets;

public class ChunkRenderDistanceCenterS2CPacket implements Packet<ClientPlayPacketListener> {
	public static final PacketCodec<PacketByteBuf, ChunkRenderDistanceCenterS2CPacket> CODEC = Packet.createCodec(
		ChunkRenderDistanceCenterS2CPacket::write, ChunkRenderDistanceCenterS2CPacket::new
	);
	private final int chunkX;
	private final int chunkZ;

	public ChunkRenderDistanceCenterS2CPacket(int x, int z) {
		this.chunkX = x;
		this.chunkZ = z;
	}

	private ChunkRenderDistanceCenterS2CPacket(PacketByteBuf buf) {
		this.chunkX = buf.readVarInt();
		this.chunkZ = buf.readVarInt();
	}

	private void write(PacketByteBuf buf) {
		buf.writeVarInt(this.chunkX);
		buf.writeVarInt(this.chunkZ);
	}

	@Override
	public PacketType<ChunkRenderDistanceCenterS2CPacket> getPacketId() {
		return PlayPackets.SET_CHUNK_CACHE_CENTER;
	}

	public void apply(ClientPlayPacketListener clientPlayPacketListener) {
		clientPlayPacketListener.onChunkRenderDistanceCenter(this);
	}

	public int getChunkX() {
		return this.chunkX;
	}

	public int getChunkZ() {
		return this.chunkZ;
	}
}
