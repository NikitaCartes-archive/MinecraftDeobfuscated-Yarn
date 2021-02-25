package net.minecraft.network.packet.s2c.play;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.listener.ClientPlayPacketListener;

public class ChunkRenderDistanceCenterS2CPacket implements Packet<ClientPlayPacketListener> {
	private final int chunkX;
	private final int chunkZ;

	public ChunkRenderDistanceCenterS2CPacket(int x, int z) {
		this.chunkX = x;
		this.chunkZ = z;
	}

	public ChunkRenderDistanceCenterS2CPacket(PacketByteBuf buf) {
		this.chunkX = buf.readVarInt();
		this.chunkZ = buf.readVarInt();
	}

	@Override
	public void write(PacketByteBuf buf) {
		buf.writeVarInt(this.chunkX);
		buf.writeVarInt(this.chunkZ);
	}

	public void apply(ClientPlayPacketListener clientPlayPacketListener) {
		clientPlayPacketListener.onChunkRenderDistanceCenter(this);
	}

	@Environment(EnvType.CLIENT)
	public int getChunkX() {
		return this.chunkX;
	}

	@Environment(EnvType.CLIENT)
	public int getChunkZ() {
		return this.chunkZ;
	}
}
