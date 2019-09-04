package net.minecraft.client.network.packet;

import com.google.common.collect.Lists;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import java.io.IOException;
import java.util.List;
import java.util.Map.Entry;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_4548;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.LongArrayTag;
import net.minecraft.network.Packet;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.util.PacketByteBuf;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.Heightmap;
import net.minecraft.world.chunk.ChunkSection;
import net.minecraft.world.chunk.WorldChunk;

public class ChunkDataS2CPacket implements Packet<ClientPlayPacketListener> {
	private int chunkX;
	private int chunkZ;
	private int verticalStripBitmask;
	private CompoundTag heightmaps;
	@Nullable
	private class_4548 field_20664;
	private byte[] data;
	private List<CompoundTag> blockEntities;
	private boolean isFullChunk;

	public ChunkDataS2CPacket() {
	}

	public ChunkDataS2CPacket(WorldChunk worldChunk, int i) {
		ChunkPos chunkPos = worldChunk.getPos();
		this.chunkX = chunkPos.x;
		this.chunkZ = chunkPos.z;
		this.isFullChunk = i == 65535;
		this.heightmaps = new CompoundTag();

		for (Entry<Heightmap.Type, Heightmap> entry : worldChunk.getHeightmaps()) {
			if (((Heightmap.Type)entry.getKey()).shouldSendToClient()) {
				this.heightmaps.put(((Heightmap.Type)entry.getKey()).getName(), new LongArrayTag(((Heightmap)entry.getValue()).asLongArray()));
			}
		}

		if (this.isFullChunk) {
			this.field_20664 = worldChunk.getBiomeArray().method_22403();
		}

		this.data = new byte[this.getDataSize(worldChunk, i)];
		this.verticalStripBitmask = this.writeData(new PacketByteBuf(this.getWriteBuffer()), worldChunk, i);
		this.blockEntities = Lists.<CompoundTag>newArrayList();

		for (Entry<BlockPos, BlockEntity> entryx : worldChunk.getBlockEntities().entrySet()) {
			BlockPos blockPos = (BlockPos)entryx.getKey();
			BlockEntity blockEntity = (BlockEntity)entryx.getValue();
			int j = blockPos.getY() >> 4;
			if (this.isFullChunk() || (i & 1 << j) != 0) {
				CompoundTag compoundTag = blockEntity.toInitialChunkDataTag();
				this.blockEntities.add(compoundTag);
			}
		}
	}

	@Override
	public void read(PacketByteBuf packetByteBuf) throws IOException {
		this.chunkX = packetByteBuf.readInt();
		this.chunkZ = packetByteBuf.readInt();
		this.isFullChunk = packetByteBuf.readBoolean();
		this.verticalStripBitmask = packetByteBuf.readVarInt();
		this.heightmaps = packetByteBuf.readCompoundTag();
		if (this.isFullChunk) {
			this.field_20664 = new class_4548(packetByteBuf);
		}

		int i = packetByteBuf.readVarInt();
		if (i > 2097152) {
			throw new RuntimeException("Chunk Packet trying to allocate too much memory on read.");
		} else {
			this.data = new byte[i];
			packetByteBuf.readBytes(this.data);
			int j = packetByteBuf.readVarInt();
			this.blockEntities = Lists.<CompoundTag>newArrayList();

			for (int k = 0; k < j; k++) {
				this.blockEntities.add(packetByteBuf.readCompoundTag());
			}
		}
	}

	@Override
	public void write(PacketByteBuf packetByteBuf) throws IOException {
		packetByteBuf.writeInt(this.chunkX);
		packetByteBuf.writeInt(this.chunkZ);
		packetByteBuf.writeBoolean(this.isFullChunk);
		packetByteBuf.writeVarInt(this.verticalStripBitmask);
		packetByteBuf.writeCompoundTag(this.heightmaps);
		if (this.field_20664 != null) {
			this.field_20664.method_22402(packetByteBuf);
		}

		packetByteBuf.writeVarInt(this.data.length);
		packetByteBuf.writeBytes(this.data);
		packetByteBuf.writeVarInt(this.blockEntities.size());

		for (CompoundTag compoundTag : this.blockEntities) {
			packetByteBuf.writeCompoundTag(compoundTag);
		}
	}

	public void method_11528(ClientPlayPacketListener clientPlayPacketListener) {
		clientPlayPacketListener.onChunkData(this);
	}

	@Environment(EnvType.CLIENT)
	public PacketByteBuf getReadBuffer() {
		return new PacketByteBuf(Unpooled.wrappedBuffer(this.data));
	}

	private ByteBuf getWriteBuffer() {
		ByteBuf byteBuf = Unpooled.wrappedBuffer(this.data);
		byteBuf.writerIndex(0);
		return byteBuf;
	}

	public int writeData(PacketByteBuf packetByteBuf, WorldChunk worldChunk, int i) {
		int j = 0;
		ChunkSection[] chunkSections = worldChunk.getSectionArray();
		int k = 0;

		for (int l = chunkSections.length; k < l; k++) {
			ChunkSection chunkSection = chunkSections[k];
			if (chunkSection != WorldChunk.EMPTY_SECTION && (!this.isFullChunk() || !chunkSection.isEmpty()) && (i & 1 << k) != 0) {
				j |= 1 << k;
				chunkSection.toPacket(packetByteBuf);
			}
		}

		return j;
	}

	protected int getDataSize(WorldChunk worldChunk, int i) {
		int j = 0;
		ChunkSection[] chunkSections = worldChunk.getSectionArray();
		int k = 0;

		for (int l = chunkSections.length; k < l; k++) {
			ChunkSection chunkSection = chunkSections[k];
			if (chunkSection != WorldChunk.EMPTY_SECTION && (!this.isFullChunk() || !chunkSection.isEmpty()) && (i & 1 << k) != 0) {
				j += chunkSection.getPacketSize();
			}
		}

		return j;
	}

	@Environment(EnvType.CLIENT)
	public int getX() {
		return this.chunkX;
	}

	@Environment(EnvType.CLIENT)
	public int getZ() {
		return this.chunkZ;
	}

	@Environment(EnvType.CLIENT)
	public int getVerticalStripBitmask() {
		return this.verticalStripBitmask;
	}

	public boolean isFullChunk() {
		return this.isFullChunk;
	}

	@Environment(EnvType.CLIENT)
	public CompoundTag getHeightmaps() {
		return this.heightmaps;
	}

	@Environment(EnvType.CLIENT)
	public List<CompoundTag> getBlockEntityTagList() {
		return this.blockEntities;
	}

	@Nullable
	@Environment(EnvType.CLIENT)
	public class_4548 method_22422() {
		return this.field_20664 == null ? null : this.field_20664.method_22403();
	}
}
