package net.minecraft.network.packet.s2c.play;

import com.google.common.collect.Lists;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import java.io.IOException;
import java.util.List;
import java.util.Map.Entry;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.LongArrayTag;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.Heightmap;
import net.minecraft.world.biome.source.BiomeArray;
import net.minecraft.world.chunk.ChunkSection;
import net.minecraft.world.chunk.WorldChunk;

public class ChunkDataS2CPacket implements Packet<ClientPlayPacketListener> {
	private int chunkX;
	private int chunkZ;
	private int verticalStripBitmask;
	private CompoundTag heightmaps;
	@Nullable
	private int[] biomeArray;
	private byte[] data;
	private List<CompoundTag> blockEntities;
	private boolean isFullChunk;

	public ChunkDataS2CPacket() {
	}

	/**
	 * @param includedSectionsMask a bitmask of the vertical chunk sections that should be included in this packet.
	 * 65535 will send all sections.
	 */
	public ChunkDataS2CPacket(WorldChunk chunk, int includedSectionsMask) {
		ChunkPos chunkPos = chunk.getPos();
		this.chunkX = chunkPos.x;
		this.chunkZ = chunkPos.z;
		this.isFullChunk = includedSectionsMask == 65535;
		this.heightmaps = new CompoundTag();

		for (Entry<Heightmap.Type, Heightmap> entry : chunk.getHeightmaps()) {
			if (((Heightmap.Type)entry.getKey()).shouldSendToClient()) {
				this.heightmaps.put(((Heightmap.Type)entry.getKey()).getName(), new LongArrayTag(((Heightmap)entry.getValue()).asLongArray()));
			}
		}

		if (this.isFullChunk) {
			this.biomeArray = chunk.getBiomeArray().toIntArray();
		}

		this.data = new byte[this.getDataSize(chunk, includedSectionsMask)];
		this.verticalStripBitmask = this.writeData(new PacketByteBuf(this.getWriteBuffer()), chunk, includedSectionsMask);
		this.blockEntities = Lists.<CompoundTag>newArrayList();

		for (Entry<BlockPos, BlockEntity> entryx : chunk.getBlockEntities().entrySet()) {
			BlockPos blockPos = (BlockPos)entryx.getKey();
			BlockEntity blockEntity = (BlockEntity)entryx.getValue();
			int i = blockPos.getY() >> 4;
			if (this.isFullChunk() || (includedSectionsMask & 1 << i) != 0) {
				CompoundTag compoundTag = blockEntity.toInitialChunkDataTag();
				this.blockEntities.add(compoundTag);
			}
		}
	}

	@Override
	public void read(PacketByteBuf buf) throws IOException {
		this.chunkX = buf.readInt();
		this.chunkZ = buf.readInt();
		this.isFullChunk = buf.readBoolean();
		this.verticalStripBitmask = buf.readVarInt();
		this.heightmaps = buf.readCompoundTag();
		if (this.isFullChunk) {
			this.biomeArray = buf.readIntArray(BiomeArray.DEFAULT_LENGTH);
		}

		int i = buf.readVarInt();
		if (i > 2097152) {
			throw new RuntimeException("Chunk Packet trying to allocate too much memory on read.");
		} else {
			this.data = new byte[i];
			buf.readBytes(this.data);
			int j = buf.readVarInt();
			this.blockEntities = Lists.<CompoundTag>newArrayList();

			for (int k = 0; k < j; k++) {
				this.blockEntities.add(buf.readCompoundTag());
			}
		}
	}

	@Override
	public void write(PacketByteBuf buf) throws IOException {
		buf.writeInt(this.chunkX);
		buf.writeInt(this.chunkZ);
		buf.writeBoolean(this.isFullChunk);
		buf.writeVarInt(this.verticalStripBitmask);
		buf.writeCompoundTag(this.heightmaps);
		if (this.biomeArray != null) {
			buf.writeIntArray(this.biomeArray);
		}

		buf.writeVarInt(this.data.length);
		buf.writeBytes(this.data);
		buf.writeVarInt(this.blockEntities.size());

		for (CompoundTag compoundTag : this.blockEntities) {
			buf.writeCompoundTag(compoundTag);
		}
	}

	public void apply(ClientPlayPacketListener clientPlayPacketListener) {
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

	public int writeData(PacketByteBuf packetByteBuf, WorldChunk chunk, int includedSectionsMask) {
		int i = 0;
		ChunkSection[] chunkSections = chunk.getSectionArray();
		int j = 0;

		for (int k = chunkSections.length; j < k; j++) {
			ChunkSection chunkSection = chunkSections[j];
			if (chunkSection != WorldChunk.EMPTY_SECTION && (!this.isFullChunk() || !chunkSection.isEmpty()) && (includedSectionsMask & 1 << j) != 0) {
				i |= 1 << j;
				chunkSection.toPacket(packetByteBuf);
			}
		}

		return i;
	}

	protected int getDataSize(WorldChunk chunk, int includedSectionsMark) {
		int i = 0;
		ChunkSection[] chunkSections = chunk.getSectionArray();
		int j = 0;

		for (int k = chunkSections.length; j < k; j++) {
			ChunkSection chunkSection = chunkSections[j];
			if (chunkSection != WorldChunk.EMPTY_SECTION && (!this.isFullChunk() || !chunkSection.isEmpty()) && (includedSectionsMark & 1 << j) != 0) {
				i += chunkSection.getPacketSize();
			}
		}

		return i;
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
	public int[] getBiomeArray() {
		return this.biomeArray;
	}
}
