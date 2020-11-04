package net.minecraft.network.packet.s2c.play;

import com.google.common.collect.Lists;
import java.io.IOException;
import java.util.List;
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
	private long skyLightMask;
	private long blockLightMask;
	private long filledSkyLightMask;
	private long filledBlockLightMask;
	private List<byte[]> skyLightUpdates;
	private List<byte[]> blockLightUpdates;
	private boolean field_25659;

	public LightUpdateS2CPacket() {
	}

	public LightUpdateS2CPacket(ChunkPos chunkPos, LightingProvider lightingProvider, boolean bl) {
		this.chunkX = chunkPos.x;
		this.chunkZ = chunkPos.z;
		this.field_25659 = bl;
		this.skyLightUpdates = Lists.<byte[]>newArrayList();
		this.blockLightUpdates = Lists.<byte[]>newArrayList();

		for (int i = 0; i < lightingProvider.method_31928(); i++) {
			ChunkNibbleArray chunkNibbleArray = lightingProvider.get(LightType.SKY).getLightSection(ChunkSectionPos.from(chunkPos, lightingProvider.method_31929() + i));
			ChunkNibbleArray chunkNibbleArray2 = lightingProvider.get(LightType.BLOCK)
				.getLightSection(ChunkSectionPos.from(chunkPos, lightingProvider.method_31929() + i));
			if (chunkNibbleArray != null) {
				if (chunkNibbleArray.isUninitialized()) {
					this.filledSkyLightMask |= 1L << i;
				} else {
					this.skyLightMask |= 1L << i;
					this.skyLightUpdates.add(chunkNibbleArray.asByteArray().clone());
				}
			}

			if (chunkNibbleArray2 != null) {
				if (chunkNibbleArray2.isUninitialized()) {
					this.filledBlockLightMask |= 1L << i;
				} else {
					this.blockLightMask |= 1L << i;
					this.blockLightUpdates.add(chunkNibbleArray2.asByteArray().clone());
				}
			}
		}
	}

	public LightUpdateS2CPacket(ChunkPos pos, LightingProvider lightProvider, int skyLightMask, int blockLightMask, boolean bl) {
		this.chunkX = pos.x;
		this.chunkZ = pos.z;
		this.field_25659 = bl;
		this.skyLightMask = (long)skyLightMask;
		this.blockLightMask = (long)blockLightMask;
		this.skyLightUpdates = Lists.<byte[]>newArrayList();
		this.blockLightUpdates = Lists.<byte[]>newArrayList();

		for (int i = 0; i < lightProvider.method_31928(); i++) {
			if ((this.skyLightMask & 1L << i) != 0L) {
				ChunkNibbleArray chunkNibbleArray = lightProvider.get(LightType.SKY).getLightSection(ChunkSectionPos.from(pos, lightProvider.method_31929() + i));
				if (chunkNibbleArray != null && !chunkNibbleArray.isUninitialized()) {
					this.skyLightUpdates.add(chunkNibbleArray.asByteArray().clone());
				} else {
					this.skyLightMask &= ~(1L << i);
					if (chunkNibbleArray != null) {
						this.filledSkyLightMask |= 1L << i;
					}
				}
			}

			if ((this.blockLightMask & 1L << i) != 0L) {
				ChunkNibbleArray chunkNibbleArray = lightProvider.get(LightType.BLOCK).getLightSection(ChunkSectionPos.from(pos, lightProvider.method_31929() + i));
				if (chunkNibbleArray != null && !chunkNibbleArray.isUninitialized()) {
					this.blockLightUpdates.add(chunkNibbleArray.asByteArray().clone());
				} else {
					this.blockLightMask &= ~(1L << i);
					if (chunkNibbleArray != null) {
						this.filledBlockLightMask |= 1L << i;
					}
				}
			}
		}
	}

	@Override
	public void read(PacketByteBuf buf) throws IOException {
		this.chunkX = buf.readVarInt();
		this.chunkZ = buf.readVarInt();
		this.field_25659 = buf.readBoolean();
		this.skyLightMask = buf.readVarLong();
		this.blockLightMask = buf.readVarLong();
		this.filledSkyLightMask = buf.readVarLong();
		this.filledBlockLightMask = buf.readVarLong();
		this.skyLightUpdates = Lists.<byte[]>newArrayList();

		for (int i = 0; i < 64; i++) {
			if ((this.skyLightMask & 1L << i) != 0L) {
				this.skyLightUpdates.add(buf.readByteArray(2048));
			}
		}

		this.blockLightUpdates = Lists.<byte[]>newArrayList();

		for (int ix = 0; ix < 64; ix++) {
			if ((this.blockLightMask & 1L << ix) != 0L) {
				this.blockLightUpdates.add(buf.readByteArray(2048));
			}
		}
	}

	@Override
	public void write(PacketByteBuf buf) throws IOException {
		buf.writeVarInt(this.chunkX);
		buf.writeVarInt(this.chunkZ);
		buf.writeBoolean(this.field_25659);
		buf.writeVarLong(this.skyLightMask);
		buf.writeVarLong(this.blockLightMask);
		buf.writeVarLong(this.filledSkyLightMask);
		buf.writeVarLong(this.filledBlockLightMask);

		for (byte[] bs : this.skyLightUpdates) {
			buf.writeByteArray(bs);
		}

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
	public long getSkyLightMask() {
		return this.skyLightMask;
	}

	@Environment(EnvType.CLIENT)
	public long getFilledSkyLightMask() {
		return this.filledSkyLightMask;
	}

	@Environment(EnvType.CLIENT)
	public List<byte[]> getSkyLightUpdates() {
		return this.skyLightUpdates;
	}

	@Environment(EnvType.CLIENT)
	public long getBlockLightMask() {
		return this.blockLightMask;
	}

	@Environment(EnvType.CLIENT)
	public long getFilledBlockLightMask() {
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
