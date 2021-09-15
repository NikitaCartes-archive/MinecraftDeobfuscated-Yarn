package net.minecraft.network.packet.s2c.play;

import java.util.BitSet;
import javax.annotation.Nullable;
import net.minecraft.class_6606;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.chunk.light.LightingProvider;

public class LightUpdateS2CPacket implements Packet<ClientPlayPacketListener> {
	private final int chunkX;
	private final int chunkZ;
	private final class_6606 field_34872;

	public LightUpdateS2CPacket(ChunkPos chunkPos, LightingProvider lightProvider, @Nullable BitSet bitSet, @Nullable BitSet bitSet2, boolean nonEdge) {
		this.chunkX = chunkPos.x;
		this.chunkZ = chunkPos.z;
		this.field_34872 = new class_6606(chunkPos, lightProvider, bitSet, bitSet2, nonEdge);
	}

	public LightUpdateS2CPacket(PacketByteBuf packetByteBuf) {
		this.chunkX = packetByteBuf.readVarInt();
		this.chunkZ = packetByteBuf.readVarInt();
		this.field_34872 = new class_6606(packetByteBuf, this.chunkX, this.chunkZ);
	}

	@Override
	public void write(PacketByteBuf buf) {
		buf.writeVarInt(this.chunkX);
		buf.writeVarInt(this.chunkZ);
		this.field_34872.method_38603(buf);
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

	public class_6606 method_38600() {
		return this.field_34872;
	}
}
