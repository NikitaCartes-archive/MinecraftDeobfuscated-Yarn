package net.minecraft.client.network.packet;

import java.io.IOException;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.network.Packet;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.util.PacketByteBuf;

public class ChunkRenderDistanceCenterS2CPacket implements Packet<ClientPlayPacketListener> {
	private int chunkX;
	private int chunkZ;

	public ChunkRenderDistanceCenterS2CPacket() {
	}

	public ChunkRenderDistanceCenterS2CPacket(int i, int j) {
		this.chunkX = i;
		this.chunkZ = j;
	}

	@Override
	public void read(PacketByteBuf packetByteBuf) throws IOException {
		this.chunkX = packetByteBuf.readVarInt();
		this.chunkZ = packetByteBuf.readVarInt();
	}

	@Override
	public void write(PacketByteBuf packetByteBuf) throws IOException {
		packetByteBuf.writeVarInt(this.chunkX);
		packetByteBuf.writeVarInt(this.chunkZ);
	}

	public void method_20321(ClientPlayPacketListener clientPlayPacketListener) {
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
