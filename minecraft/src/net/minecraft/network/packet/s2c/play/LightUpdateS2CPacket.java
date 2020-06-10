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
	private int skyLightMask;
	private int blockLightMask;
	private int filledSkyLightMask;
	private int filledBlockLightMask;
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

		for (int i = 0; i < 18; i++) {
			ChunkNibbleArray chunkNibbleArray = lightingProvider.get(LightType.SKY).getLightArray(ChunkSectionPos.from(chunkPos, -1 + i));
			ChunkNibbleArray chunkNibbleArray2 = lightingProvider.get(LightType.BLOCK).getLightArray(ChunkSectionPos.from(chunkPos, -1 + i));
			if (chunkNibbleArray != null) {
				if (chunkNibbleArray.isUninitialized()) {
					this.filledSkyLightMask |= 1 << i;
				} else {
					this.skyLightMask |= 1 << i;
					this.skyLightUpdates.add(chunkNibbleArray.asByteArray().clone());
				}
			}

			if (chunkNibbleArray2 != null) {
				if (chunkNibbleArray2.isUninitialized()) {
					this.filledBlockLightMask |= 1 << i;
				} else {
					this.blockLightMask |= 1 << i;
					this.blockLightUpdates.add(chunkNibbleArray2.asByteArray().clone());
				}
			}
		}
	}

	public LightUpdateS2CPacket(ChunkPos pos, LightingProvider lightProvider, int i, int blockLightMask, boolean bl) {
		this.chunkX = pos.x;
		this.chunkZ = pos.z;
		this.field_25659 = bl;
		this.skyLightMask = i;
		this.blockLightMask = blockLightMask;
		this.skyLightUpdates = Lists.<byte[]>newArrayList();
		this.blockLightUpdates = Lists.<byte[]>newArrayList();

		for (int j = 0; j < 18; j++) {
			if ((this.skyLightMask & 1 << j) != 0) {
				ChunkNibbleArray chunkNibbleArray = lightProvider.get(LightType.SKY).getLightArray(ChunkSectionPos.from(pos, -1 + j));
				if (chunkNibbleArray != null && !chunkNibbleArray.isUninitialized()) {
					this.skyLightUpdates.add(chunkNibbleArray.asByteArray().clone());
				} else {
					this.skyLightMask &= ~(1 << j);
					if (chunkNibbleArray != null) {
						this.filledSkyLightMask |= 1 << j;
					}
				}
			}

			if ((this.blockLightMask & 1 << j) != 0) {
				ChunkNibbleArray chunkNibbleArray = lightProvider.get(LightType.BLOCK).getLightArray(ChunkSectionPos.from(pos, -1 + j));
				if (chunkNibbleArray != null && !chunkNibbleArray.isUninitialized()) {
					this.blockLightUpdates.add(chunkNibbleArray.asByteArray().clone());
				} else {
					this.blockLightMask &= ~(1 << j);
					if (chunkNibbleArray != null) {
						this.filledBlockLightMask |= 1 << j;
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
		this.skyLightMask = buf.readVarInt();
		this.blockLightMask = buf.readVarInt();
		this.filledSkyLightMask = buf.readVarInt();
		this.filledBlockLightMask = buf.readVarInt();
		this.skyLightUpdates = Lists.<byte[]>newArrayList();

		for (int i = 0; i < 18; i++) {
			if ((this.skyLightMask & 1 << i) != 0) {
				this.skyLightUpdates.add(buf.readByteArray(2048));
			}
		}

		this.blockLightUpdates = Lists.<byte[]>newArrayList();

		for (int ix = 0; ix < 18; ix++) {
			if ((this.blockLightMask & 1 << ix) != 0) {
				this.blockLightUpdates.add(buf.readByteArray(2048));
			}
		}
	}

	@Override
	public void write(PacketByteBuf buf) throws IOException {
		buf.writeVarInt(this.chunkX);
		buf.writeVarInt(this.chunkZ);
		buf.writeBoolean(this.field_25659);
		buf.writeVarInt(this.skyLightMask);
		buf.writeVarInt(this.blockLightMask);
		buf.writeVarInt(this.filledSkyLightMask);
		buf.writeVarInt(this.filledBlockLightMask);

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
	public int getSkyLightMask() {
		return this.skyLightMask;
	}

	@Environment(EnvType.CLIENT)
	public int getFilledSkyLightMask() {
		return this.filledSkyLightMask;
	}

	@Environment(EnvType.CLIENT)
	public List<byte[]> getSkyLightUpdates() {
		return this.skyLightUpdates;
	}

	@Environment(EnvType.CLIENT)
	public int getBlockLightMask() {
		return this.blockLightMask;
	}

	@Environment(EnvType.CLIENT)
	public int getFilledBlockLightMask() {
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
