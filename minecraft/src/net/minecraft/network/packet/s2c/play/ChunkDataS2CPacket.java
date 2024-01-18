package net.minecraft.network.packet.s2c.play;

import java.util.BitSet;
import javax.annotation.Nullable;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.PacketType;
import net.minecraft.network.packet.PlayPackets;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.chunk.WorldChunk;
import net.minecraft.world.chunk.light.LightingProvider;

public class ChunkDataS2CPacket implements Packet<ClientPlayPacketListener> {
	public static final PacketCodec<RegistryByteBuf, ChunkDataS2CPacket> CODEC = Packet.createCodec(ChunkDataS2CPacket::method_55883, ChunkDataS2CPacket::new);
	private final int chunkX;
	private final int chunkZ;
	private final ChunkData chunkData;
	private final LightData lightData;

	public ChunkDataS2CPacket(WorldChunk chunk, LightingProvider lightProvider, @Nullable BitSet skyBits, @Nullable BitSet blockBits) {
		ChunkPos chunkPos = chunk.getPos();
		this.chunkX = chunkPos.x;
		this.chunkZ = chunkPos.z;
		this.chunkData = new ChunkData(chunk);
		this.lightData = new LightData(chunkPos, lightProvider, skyBits, blockBits);
	}

	private ChunkDataS2CPacket(RegistryByteBuf buf) {
		this.chunkX = buf.readInt();
		this.chunkZ = buf.readInt();
		this.chunkData = new ChunkData(buf, this.chunkX, this.chunkZ);
		this.lightData = new LightData(buf, this.chunkX, this.chunkZ);
	}

	private void method_55883(RegistryByteBuf buf) {
		buf.writeInt(this.chunkX);
		buf.writeInt(this.chunkZ);
		this.chunkData.write(buf);
		this.lightData.write(buf);
	}

	@Override
	public PacketType<ChunkDataS2CPacket> getPacketId() {
		return PlayPackets.LEVEL_CHUNK_WITH_LIGHT;
	}

	public void apply(ClientPlayPacketListener clientPlayPacketListener) {
		clientPlayPacketListener.onChunkData(this);
	}

	public int getChunkX() {
		return this.chunkX;
	}

	public int getChunkZ() {
		return this.chunkZ;
	}

	public ChunkData getChunkData() {
		return this.chunkData;
	}

	public LightData getLightData() {
		return this.lightData;
	}
}
