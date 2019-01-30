package net.minecraft.client.network.packet;

import com.google.common.collect.Lists;
import java.io.IOException;
import java.util.List;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.network.Packet;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.util.PacketByteBuf;
import net.minecraft.world.LightType;
import net.minecraft.world.chunk.ChunkNibbleArray;
import net.minecraft.world.chunk.ChunkPos;
import net.minecraft.world.chunk.light.LightingProvider;

public class LightUpdateClientPacket implements Packet<ClientPlayPacketListener> {
	private int chunkX;
	private int chunkZ;
	private int blockLightUpdateBits;
	private int skyLightUpdateBits;
	private int filledBlockLightBits;
	private int filledSkyLightBits;
	private List<byte[]> skyUpdates;
	private List<byte[]> blockUpdates;

	public LightUpdateClientPacket() {
	}

	public LightUpdateClientPacket(ChunkPos chunkPos, LightingProvider lightingProvider) {
		this.chunkX = chunkPos.x;
		this.chunkZ = chunkPos.z;
		this.skyUpdates = Lists.<byte[]>newArrayList();
		this.blockUpdates = Lists.<byte[]>newArrayList();

		for (int i = 0; i < 18; i++) {
			ChunkNibbleArray chunkNibbleArray = lightingProvider.get(LightType.SKY_LIGHT).getChunkLightArray(this.chunkX, -1 + i, this.chunkZ);
			ChunkNibbleArray chunkNibbleArray2 = lightingProvider.get(LightType.BLOCK_LIGHT).getChunkLightArray(this.chunkX, -1 + i, this.chunkZ);
			if (chunkNibbleArray != null) {
				if (chunkNibbleArray.isUninitialized()) {
					this.filledBlockLightBits |= 1 << i;
				} else {
					this.blockLightUpdateBits |= 1 << i;
					this.skyUpdates.add(chunkNibbleArray.asByteArray().clone());
				}
			}

			if (chunkNibbleArray2 != null) {
				if (chunkNibbleArray2.isUninitialized()) {
					this.filledSkyLightBits |= 1 << i;
				} else {
					this.skyLightUpdateBits |= 1 << i;
					this.blockUpdates.add(chunkNibbleArray2.asByteArray().clone());
				}
			}
		}
	}

	public LightUpdateClientPacket(ChunkPos chunkPos, LightingProvider lightingProvider, int i, int j) {
		this.chunkX = chunkPos.x;
		this.chunkZ = chunkPos.z;
		this.blockLightUpdateBits = i;
		this.skyLightUpdateBits = j;
		this.skyUpdates = Lists.<byte[]>newArrayList();
		this.blockUpdates = Lists.<byte[]>newArrayList();

		for (int k = 0; k < 18; k++) {
			if ((this.blockLightUpdateBits & 1 << k) != 0) {
				ChunkNibbleArray chunkNibbleArray = lightingProvider.get(LightType.SKY_LIGHT).getChunkLightArray(this.chunkX, -1 + k, this.chunkZ);
				if (chunkNibbleArray != null && !chunkNibbleArray.isUninitialized()) {
					this.skyUpdates.add(chunkNibbleArray.asByteArray().clone());
				} else {
					this.blockLightUpdateBits &= ~(1 << k);
					if (chunkNibbleArray != null) {
						this.filledBlockLightBits |= 1 << k;
					}
				}
			}

			if ((this.skyLightUpdateBits & 1 << k) != 0) {
				ChunkNibbleArray chunkNibbleArray = lightingProvider.get(LightType.BLOCK_LIGHT).getChunkLightArray(this.chunkX, -1 + k, this.chunkZ);
				if (chunkNibbleArray != null && !chunkNibbleArray.isUninitialized()) {
					this.blockUpdates.add(chunkNibbleArray.asByteArray().clone());
				} else {
					this.skyLightUpdateBits &= ~(1 << k);
					if (chunkNibbleArray != null) {
						this.filledSkyLightBits |= 1 << k;
					}
				}
			}
		}
	}

	@Override
	public void read(PacketByteBuf packetByteBuf) throws IOException {
		this.chunkX = packetByteBuf.readVarInt();
		this.chunkZ = packetByteBuf.readVarInt();
		this.blockLightUpdateBits = packetByteBuf.readVarInt();
		this.skyLightUpdateBits = packetByteBuf.readVarInt();
		this.filledBlockLightBits = packetByteBuf.readVarInt();
		this.filledSkyLightBits = packetByteBuf.readVarInt();
		this.skyUpdates = Lists.<byte[]>newArrayList();

		for (int i = 0; i < 18; i++) {
			if ((this.blockLightUpdateBits & 1 << i) != 0) {
				this.skyUpdates.add(packetByteBuf.readByteArray(2048));
			}
		}

		this.blockUpdates = Lists.<byte[]>newArrayList();

		for (int ix = 0; ix < 18; ix++) {
			if ((this.skyLightUpdateBits & 1 << ix) != 0) {
				this.blockUpdates.add(packetByteBuf.readByteArray(2048));
			}
		}
	}

	@Override
	public void write(PacketByteBuf packetByteBuf) throws IOException {
		packetByteBuf.writeVarInt(this.chunkX);
		packetByteBuf.writeVarInt(this.chunkZ);
		packetByteBuf.writeVarInt(this.blockLightUpdateBits);
		packetByteBuf.writeVarInt(this.skyLightUpdateBits);
		packetByteBuf.writeVarInt(this.filledBlockLightBits);
		packetByteBuf.writeVarInt(this.filledSkyLightBits);

		for (byte[] bs : this.skyUpdates) {
			packetByteBuf.writeByteArray(bs);
		}

		for (byte[] bs : this.blockUpdates) {
			packetByteBuf.writeByteArray(bs);
		}
	}

	public void method_11560(ClientPlayPacketListener clientPlayPacketListener) {
		clientPlayPacketListener.onLightUpdate(this);
	}

	@Environment(EnvType.CLIENT)
	public int method_11558() {
		return this.chunkX;
	}

	@Environment(EnvType.CLIENT)
	public int method_11554() {
		return this.chunkZ;
	}

	@Environment(EnvType.CLIENT)
	public int method_11556() {
		return this.blockLightUpdateBits;
	}

	@Environment(EnvType.CLIENT)
	public int method_16124() {
		return this.filledBlockLightBits;
	}

	@Environment(EnvType.CLIENT)
	public List<byte[]> method_11555() {
		return this.skyUpdates;
	}

	@Environment(EnvType.CLIENT)
	public int method_11559() {
		return this.skyLightUpdateBits;
	}

	@Environment(EnvType.CLIENT)
	public int method_16125() {
		return this.filledSkyLightBits;
	}

	@Environment(EnvType.CLIENT)
	public List<byte[]> method_11557() {
		return this.blockUpdates;
	}
}
