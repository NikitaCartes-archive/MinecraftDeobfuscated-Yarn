package net.minecraft.network.packet.s2c.play;

import java.util.BitSet;
import javax.annotation.Nullable;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.chunk.light.LightingProvider;

public class LightUpdateS2CPacket implements Packet<ClientPlayPacketListener> {
	private final int chunkX;
	private final int chunkZ;
	private final LightData data;

	public LightUpdateS2CPacket(ChunkPos chunkPos, LightingProvider lightProvider, @Nullable BitSet skyBits, @Nullable BitSet blockBits, boolean nonEdge) {
		this.chunkX = chunkPos.x;
		this.chunkZ = chunkPos.z;
		this.data = new LightData(chunkPos, lightProvider, skyBits, blockBits, nonEdge);
	}

	public LightUpdateS2CPacket(PacketByteBuf buf) {
		this.chunkX = buf.readVarInt();
		this.chunkZ = buf.readVarInt();
		this.data = new LightData(buf, this.chunkX, this.chunkZ);
	}

	@Override
	public void write(PacketByteBuf buf) {
		buf.writeVarInt(this.chunkX);
		buf.writeVarInt(this.chunkZ);
		this.data.write(buf);
	}

	public void apply(ClientPlayPacketListener clientPlayPacketListener) {
		clientPlayPacketListener.onLightUpdate(this);
	}

	public int getChunkX() {
		return this.chunkX;
	}

	public int getChunkZ() {
		return this.chunkZ;
	}

	public LightData getData() {
		return this.data;
	}
}
