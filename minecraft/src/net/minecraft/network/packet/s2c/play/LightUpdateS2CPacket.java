package net.minecraft.network.packet.s2c.play;

import com.google.common.collect.Lists;
import java.io.IOException;
import java.util.BitSet;
import java.util.List;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.ChunkSectionPos;
import net.minecraft.world.LightType;
import net.minecraft.world.chunk.ChunkNibbleArray;
import net.minecraft.world.chunk.light.LightingProvider;

public class LightUpdateS2CPacket implements Packet<ClientPlayPacketListener> {
	private int chunkX;
	private int chunkZ;
	private BitSet skyLightMask = new BitSet();
	private BitSet blockLightMask = new BitSet();
	private BitSet filledSkyLightMask = new BitSet();
	private BitSet filledBlockLightMask = new BitSet();
	private final List<byte[]> skyLightUpdates = Lists.<byte[]>newArrayList();
	private final List<byte[]> blockLightUpdates = Lists.<byte[]>newArrayList();
	private boolean field_25659;

	public LightUpdateS2CPacket() {
	}

	public LightUpdateS2CPacket(ChunkPos chunkPos, LightingProvider lightingProvider, @Nullable BitSet bitSet, @Nullable BitSet bitSet2, boolean bl) {
		this.chunkX = chunkPos.x;
		this.chunkZ = chunkPos.z;
		this.field_25659 = bl;

		for (int i = 0; i < lightingProvider.method_31928(); i++) {
			if (bitSet == null || bitSet.get(i)) {
				method_33138(chunkPos, lightingProvider, LightType.SKY, i, this.skyLightMask, this.filledSkyLightMask, this.skyLightUpdates);
			}

			if (bitSet2 == null || bitSet2.get(i)) {
				method_33138(chunkPos, lightingProvider, LightType.BLOCK, i, this.blockLightMask, this.filledBlockLightMask, this.blockLightUpdates);
			}
		}
	}

	private static void method_33138(
		ChunkPos chunkPos, LightingProvider lightingProvider, LightType lightType, int i, BitSet bitSet, BitSet bitSet2, List<byte[]> list
	) {
		ChunkNibbleArray chunkNibbleArray = lightingProvider.get(lightType).getLightSection(ChunkSectionPos.from(chunkPos, lightingProvider.method_31929() + i));
		if (chunkNibbleArray != null) {
			if (chunkNibbleArray.isUninitialized()) {
				bitSet2.set(i);
			} else {
				bitSet.set(i);
				list.add(chunkNibbleArray.asByteArray().clone());
			}
		}
	}

	@Override
	public void read(PacketByteBuf buf) throws IOException {
		this.chunkX = buf.readVarInt();
		this.chunkZ = buf.readVarInt();
		this.field_25659 = buf.readBoolean();
		this.skyLightMask = buf.method_33558();
		this.blockLightMask = buf.method_33558();
		this.filledSkyLightMask = buf.method_33558();
		this.filledBlockLightMask = buf.method_33558();
		int i = buf.readVarInt();

		for (int j = 0; j < i; j++) {
			this.skyLightUpdates.add(buf.readByteArray(2048));
		}

		int j = buf.readVarInt();

		for (int k = 0; k < j; k++) {
			this.blockLightUpdates.add(buf.readByteArray(2048));
		}
	}

	@Override
	public void write(PacketByteBuf buf) throws IOException {
		buf.writeVarInt(this.chunkX);
		buf.writeVarInt(this.chunkZ);
		buf.writeBoolean(this.field_25659);
		buf.method_33557(this.skyLightMask);
		buf.method_33557(this.blockLightMask);
		buf.method_33557(this.filledSkyLightMask);
		buf.method_33557(this.filledBlockLightMask);
		buf.writeVarInt(this.skyLightUpdates.size());

		for (byte[] bs : this.skyLightUpdates) {
			buf.writeByteArray(bs);
		}

		buf.writeVarInt(this.blockLightUpdates.size());

		for (byte[] bs : this.blockLightUpdates) {
			buf.writeByteArray(bs);
		}
	}

	public void apply(ClientPlayPacketListener clientPlayPacketListener) {
		clientPlayPacketListener.onLightUpdate(this);
	}

	@Environment(EnvType.CLIENT)
	public int getChunkX() {
		return this.chunkX;
	}

	@Environment(EnvType.CLIENT)
	public int getChunkZ() {
		return this.chunkZ;
	}

	@Environment(EnvType.CLIENT)
	public BitSet getSkyLightMask() {
		return this.skyLightMask;
	}

	@Environment(EnvType.CLIENT)
	public BitSet getFilledSkyLightMask() {
		return this.filledSkyLightMask;
	}

	@Environment(EnvType.CLIENT)
	public List<byte[]> getSkyLightUpdates() {
		return this.skyLightUpdates;
	}

	@Environment(EnvType.CLIENT)
	public BitSet getBlockLightMask() {
		return this.blockLightMask;
	}

	@Environment(EnvType.CLIENT)
	public BitSet getFilledBlockLightMask() {
		return this.filledBlockLightMask;
	}

	@Environment(EnvType.CLIENT)
	public List<byte[]> getBlockLightUpdates() {
		return this.blockLightUpdates;
	}

	@Environment(EnvType.CLIENT)
	public boolean method_30006() {
		return this.field_25659;
	}
}
