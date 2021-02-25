package net.minecraft.network.packet.s2c.play;

import com.google.common.collect.Lists;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import java.util.BitSet;
import java.util.List;
import java.util.Map.Entry;
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
	private final int chunkX;
	private final int chunkZ;
	private final BitSet verticalStripBitmask;
	private final CompoundTag heightmaps;
	private final int[] biomeArray;
	private final byte[] data;
	private final List<CompoundTag> blockEntities;

	public ChunkDataS2CPacket(WorldChunk chunk) {
		ChunkPos chunkPos = chunk.getPos();
		this.chunkX = chunkPos.x;
		this.chunkZ = chunkPos.z;
		this.heightmaps = new CompoundTag();

		for (Entry<Heightmap.Type, Heightmap> entry : chunk.getHeightmaps()) {
			if (((Heightmap.Type)entry.getKey()).shouldSendToClient()) {
				this.heightmaps.put(((Heightmap.Type)entry.getKey()).getName(), new LongArrayTag(((Heightmap)entry.getValue()).asLongArray()));
			}
		}

		this.biomeArray = chunk.getBiomeArray().toIntArray();
		this.data = new byte[this.getDataSize(chunk)];
		this.verticalStripBitmask = this.writeData(new PacketByteBuf(this.getWriteBuffer()), chunk);
		this.blockEntities = Lists.<CompoundTag>newArrayList();

		for (Entry<BlockPos, BlockEntity> entryx : chunk.getBlockEntities().entrySet()) {
			BlockEntity blockEntity = (BlockEntity)entryx.getValue();
			CompoundTag compoundTag = blockEntity.toInitialChunkDataNbt();
			this.blockEntities.add(compoundTag);
		}
	}

	public ChunkDataS2CPacket(PacketByteBuf buf) {
		this.chunkX = buf.readInt();
		this.chunkZ = buf.readInt();
		this.verticalStripBitmask = buf.readBitSet();
		this.heightmaps = buf.readCompoundTag();
		this.biomeArray = buf.readIntArray(BiomeArray.DEFAULT_LENGTH);
		int i = buf.readVarInt();
		if (i > 2097152) {
			throw new RuntimeException("Chunk Packet trying to allocate too much memory on read.");
		} else {
			this.data = new byte[i];
			buf.readBytes(this.data);
			this.blockEntities = buf.readList(PacketByteBuf::readCompoundTag);
		}
	}

	@Override
	public void write(PacketByteBuf buf) {
		buf.writeInt(this.chunkX);
		buf.writeInt(this.chunkZ);
		buf.writeBitSet(this.verticalStripBitmask);
		buf.writeCompoundTag(this.heightmaps);
		buf.writeIntArray(this.biomeArray);
		buf.writeVarInt(this.data.length);
		buf.writeBytes(this.data);
		buf.writeCollection(this.blockEntities, PacketByteBuf::writeCompoundTag);
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

	public BitSet writeData(PacketByteBuf buf, WorldChunk chunk) {
		BitSet bitSet = new BitSet();
		ChunkSection[] chunkSections = chunk.getSectionArray();
		int i = 0;

		for (int j = chunkSections.length; i < j; i++) {
			ChunkSection chunkSection = chunkSections[i];
			if (chunkSection != WorldChunk.EMPTY_SECTION && !chunkSection.isEmpty()) {
				bitSet.set(i);
				chunkSection.toPacket(buf);
			}
		}

		return bitSet;
	}

	protected int getDataSize(WorldChunk chunk) {
		int i = 0;
		ChunkSection[] chunkSections = chunk.getSectionArray();
		int j = 0;

		for (int k = chunkSections.length; j < k; j++) {
			ChunkSection chunkSection = chunkSections[j];
			if (chunkSection != WorldChunk.EMPTY_SECTION && !chunkSection.isEmpty()) {
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
	public BitSet getVerticalStripBitmask() {
		return this.verticalStripBitmask;
	}

	@Environment(EnvType.CLIENT)
	public CompoundTag getHeightmaps() {
		return this.heightmaps;
	}

	@Environment(EnvType.CLIENT)
	public List<CompoundTag> getBlockEntityTagList() {
		return this.blockEntities;
	}

	@Environment(EnvType.CLIENT)
	public int[] getBiomeArray() {
		return this.biomeArray;
	}
}
