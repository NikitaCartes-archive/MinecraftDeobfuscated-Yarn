package net.minecraft.client.network.packet;

import com.google.common.collect.Lists;
import java.io.IOException;
import java.util.List;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_2804;
import net.minecraft.network.Packet;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.util.PacketByteBuf;
import net.minecraft.world.LightType;
import net.minecraft.world.chunk.ChunkPos;
import net.minecraft.world.chunk.light.LightingProvider;

public class LightUpdateClientPacket implements Packet<ClientPlayPacketListener> {
	private int chunkX;
	private int chunkZ;
	private int field_12263;
	private int field_12262;
	private int field_16418;
	private int field_16417;
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
			class_2804 lv = lightingProvider.get(LightType.field_9284).method_15544(this.chunkX, -1 + i, this.chunkZ);
			class_2804 lv2 = lightingProvider.get(LightType.field_9282).method_15544(this.chunkX, -1 + i, this.chunkZ);
			if (lv != null) {
				if (lv.method_12146()) {
					this.field_16418 |= 1 << i;
				} else {
					this.field_12263 |= 1 << i;
					this.skyUpdates.add(lv.method_12137().clone());
				}
			}

			if (lv2 != null) {
				if (lv2.method_12146()) {
					this.field_16417 |= 1 << i;
				} else {
					this.field_12262 |= 1 << i;
					this.blockUpdates.add(lv2.method_12137().clone());
				}
			}
		}
	}

	public LightUpdateClientPacket(ChunkPos chunkPos, LightingProvider lightingProvider, int i, int j) {
		this.chunkX = chunkPos.x;
		this.chunkZ = chunkPos.z;
		this.field_12263 = i;
		this.field_12262 = j;
		this.skyUpdates = Lists.<byte[]>newArrayList();
		this.blockUpdates = Lists.<byte[]>newArrayList();

		for (int k = 0; k < 18; k++) {
			if ((this.field_12263 & 1 << k) != 0) {
				class_2804 lv = lightingProvider.get(LightType.field_9284).method_15544(this.chunkX, -1 + k, this.chunkZ);
				if (lv != null && !lv.method_12146()) {
					this.skyUpdates.add(lv.method_12137().clone());
				} else {
					this.field_12263 &= ~(1 << k);
					if (lv != null) {
						this.field_16418 |= 1 << k;
					}
				}
			}

			if ((this.field_12262 & 1 << k) != 0) {
				class_2804 lv = lightingProvider.get(LightType.field_9282).method_15544(this.chunkX, -1 + k, this.chunkZ);
				if (lv != null && !lv.method_12146()) {
					this.blockUpdates.add(lv.method_12137().clone());
				} else {
					this.field_12262 &= ~(1 << k);
					if (lv != null) {
						this.field_16417 |= 1 << k;
					}
				}
			}
		}
	}

	@Override
	public void read(PacketByteBuf packetByteBuf) throws IOException {
		this.chunkX = packetByteBuf.readVarInt();
		this.chunkZ = packetByteBuf.readVarInt();
		this.field_12263 = packetByteBuf.readVarInt();
		this.field_12262 = packetByteBuf.readVarInt();
		this.field_16418 = packetByteBuf.readVarInt();
		this.field_16417 = packetByteBuf.readVarInt();
		this.skyUpdates = Lists.<byte[]>newArrayList();

		for (int i = 0; i < 18; i++) {
			if ((this.field_12263 & 1 << i) != 0) {
				this.skyUpdates.add(packetByteBuf.readByteArray(2048));
			}
		}

		this.blockUpdates = Lists.<byte[]>newArrayList();

		for (int ix = 0; ix < 18; ix++) {
			if ((this.field_12262 & 1 << ix) != 0) {
				this.blockUpdates.add(packetByteBuf.readByteArray(2048));
			}
		}
	}

	@Override
	public void write(PacketByteBuf packetByteBuf) throws IOException {
		packetByteBuf.writeVarInt(this.chunkX);
		packetByteBuf.writeVarInt(this.chunkZ);
		packetByteBuf.writeVarInt(this.field_12263);
		packetByteBuf.writeVarInt(this.field_12262);
		packetByteBuf.writeVarInt(this.field_16418);
		packetByteBuf.writeVarInt(this.field_16417);

		for (byte[] bs : this.skyUpdates) {
			packetByteBuf.writeByteArray(bs);
		}

		for (byte[] bs : this.blockUpdates) {
			packetByteBuf.writeByteArray(bs);
		}
	}

	public void apply(ClientPlayPacketListener clientPlayPacketListener) {
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
		return this.field_12263;
	}

	@Environment(EnvType.CLIENT)
	public int method_16124() {
		return this.field_16418;
	}

	@Environment(EnvType.CLIENT)
	public List<byte[]> method_11555() {
		return this.skyUpdates;
	}

	@Environment(EnvType.CLIENT)
	public int method_11559() {
		return this.field_12262;
	}

	@Environment(EnvType.CLIENT)
	public int method_16125() {
		return this.field_16417;
	}

	@Environment(EnvType.CLIENT)
	public List<byte[]> method_11557() {
		return this.blockUpdates;
	}
}
