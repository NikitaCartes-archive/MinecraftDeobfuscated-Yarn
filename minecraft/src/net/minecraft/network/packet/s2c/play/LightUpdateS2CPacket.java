package net.minecraft.network.packet.s2c.play;

import com.google.common.collect.Lists;
import java.util.BitSet;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.ChunkSectionPos;
import net.minecraft.world.LightType;
import net.minecraft.world.chunk.ChunkNibbleArray;
import net.minecraft.world.chunk.light.LightingProvider;

public class LightUpdateS2CPacket implements Packet<ClientPlayPacketListener> {
	private final int chunkX;
	private final int chunkZ;
	private final BitSet skyLightMask;
	private final BitSet blockLightMask;
	private final BitSet filledSkyLightMask;
	private final BitSet filledBlockLightMask;
	private final List<byte[]> skyLightUpdates;
	private final List<byte[]> blockLightUpdates;
	/**
	 * Whether this updated chunk is not on the edge of the map.
	 */
	private final boolean nonEdge;

	public LightUpdateS2CPacket(ChunkPos chunkPos, LightingProvider lightProvider, @Nullable BitSet bitSet, @Nullable BitSet bitSet2, boolean nonEdge) {
		this.chunkX = chunkPos.x;
		this.chunkZ = chunkPos.z;
		this.nonEdge = nonEdge;
		this.skyLightMask = new BitSet();
		this.blockLightMask = new BitSet();
		this.filledSkyLightMask = new BitSet();
		this.filledBlockLightMask = new BitSet();
		this.skyLightUpdates = Lists.<byte[]>newArrayList();
		this.blockLightUpdates = Lists.<byte[]>newArrayList();

		for (int i = 0; i < lightProvider.method_31928(); i++) {
			if (bitSet == null || bitSet.get(i)) {
				method_33138(chunkPos, lightProvider, LightType.SKY, i, this.skyLightMask, this.filledSkyLightMask, this.skyLightUpdates);
			}

			if (bitSet2 == null || bitSet2.get(i)) {
				method_33138(chunkPos, lightProvider, LightType.BLOCK, i, this.blockLightMask, this.filledBlockLightMask, this.blockLightUpdates);
			}
		}
	}

	private static void method_33138(
		ChunkPos chunkPos, LightingProvider lightProvider, LightType lightType, int i, BitSet bitSet, BitSet bitSet2, List<byte[]> list
	) {
		ChunkNibbleArray chunkNibbleArray = lightProvider.get(lightType).getLightSection(ChunkSectionPos.from(chunkPos, lightProvider.method_31929() + i));
		if (chunkNibbleArray != null) {
			if (chunkNibbleArray.isUninitialized()) {
				bitSet2.set(i);
			} else {
				bitSet.set(i);
				list.add(chunkNibbleArray.asByteArray().clone());
			}
		}
	}

	public LightUpdateS2CPacket(PacketByteBuf buf) {
		this.chunkX = buf.readVarInt();
		this.chunkZ = buf.readVarInt();
		this.nonEdge = buf.readBoolean();
		this.skyLightMask = buf.readBitSet();
		this.blockLightMask = buf.readBitSet();
		this.filledSkyLightMask = buf.readBitSet();
		this.filledBlockLightMask = buf.readBitSet();
		this.skyLightUpdates = buf.readList(packetByteBuf -> packetByteBuf.readByteArray(2048));
		this.blockLightUpdates = buf.readList(packetByteBuf -> packetByteBuf.readByteArray(2048));
	}

	@Override
	public void write(PacketByteBuf buf) {
		buf.writeVarInt(this.chunkX);
		buf.writeVarInt(this.chunkZ);
		buf.writeBoolean(this.nonEdge);
		buf.writeBitSet(this.skyLightMask);
		buf.writeBitSet(this.blockLightMask);
		buf.writeBitSet(this.filledSkyLightMask);
		buf.writeBitSet(this.filledBlockLightMask);
		buf.writeCollection(this.skyLightUpdates, PacketByteBuf::writeByteArray);
		buf.writeCollection(this.blockLightUpdates, PacketByteBuf::writeByteArray);
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

	public BitSet getSkyLightMask() {
		return this.skyLightMask;
	}

	public BitSet getFilledSkyLightMask() {
		return this.filledSkyLightMask;
	}

	public List<byte[]> getSkyLightUpdates() {
		return this.skyLightUpdates;
	}

	public BitSet getBlockLightMask() {
		return this.blockLightMask;
	}

	public BitSet getFilledBlockLightMask() {
		return this.filledBlockLightMask;
	}

	public List<byte[]> getBlockLightUpdates() {
		return this.blockLightUpdates;
	}

	public boolean isNotEdge() {
		return this.nonEdge;
	}
}
