package net.minecraft.network.packet.s2c.play;

import java.util.BitSet;
import javax.annotation.Nullable;
import net.minecraft.class_6603;
import net.minecraft.class_6606;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.chunk.WorldChunk;
import net.minecraft.world.chunk.light.LightingProvider;

public class ChunkDataS2CPacket implements Packet<ClientPlayPacketListener> {
	private final int chunkX;
	private final int chunkZ;
	private final class_6603 field_34870;
	private final class_6606 field_34871;

	public ChunkDataS2CPacket(WorldChunk worldChunk, LightingProvider lightingProvider, @Nullable BitSet bitSet, @Nullable BitSet bitSet2, boolean bl) {
		ChunkPos chunkPos = worldChunk.getPos();
		this.chunkX = chunkPos.x;
		this.chunkZ = chunkPos.z;
		this.field_34870 = new class_6603(worldChunk);
		this.field_34871 = new class_6606(chunkPos, lightingProvider, bitSet, bitSet2, bl);
	}

	public ChunkDataS2CPacket(PacketByteBuf packetByteBuf) {
		this.chunkX = packetByteBuf.readInt();
		this.chunkZ = packetByteBuf.readInt();
		this.field_34870 = new class_6603(packetByteBuf, this.chunkX, this.chunkZ);
		this.field_34871 = new class_6606(packetByteBuf, this.chunkX, this.chunkZ);
	}

	@Override
	public void write(PacketByteBuf buf) {
		buf.writeInt(this.chunkX);
		buf.writeInt(this.chunkZ);
		this.field_34870.method_38590(buf);
		this.field_34871.method_38603(buf);
	}

	public void apply(ClientPlayPacketListener clientPlayPacketListener) {
		clientPlayPacketListener.onChunkData(this);
	}

	public int getX() {
		return this.chunkX;
	}

	public int getZ() {
		return this.chunkZ;
	}

	public class_6603 method_38598() {
		return this.field_34870;
	}

	public class_6606 method_38599() {
		return this.field_34871;
	}
}
