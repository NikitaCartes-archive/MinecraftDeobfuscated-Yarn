package net.minecraft.network.packet.s2c.play;

import com.google.common.collect.Lists;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import java.util.List;
import java.util.Map.Entry;
import java.util.function.Consumer;
import javax.annotation.Nullable;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtLongArray;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.codec.RegistryByteBuf;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkSectionPos;
import net.minecraft.world.Heightmap;
import net.minecraft.world.chunk.ChunkSection;
import net.minecraft.world.chunk.WorldChunk;

public class ChunkData {
	private static final int MAX_SECTIONS_DATA_SIZE = 2097152;
	private final NbtCompound heightmap;
	private final byte[] sectionsData;
	private final List<ChunkData.BlockEntityData> blockEntities;

	public ChunkData(WorldChunk chunk) {
		this.heightmap = new NbtCompound();

		for (Entry<Heightmap.Type, Heightmap> entry : chunk.getHeightmaps()) {
			if (((Heightmap.Type)entry.getKey()).shouldSendToClient()) {
				this.heightmap.put(((Heightmap.Type)entry.getKey()).getName(), new NbtLongArray(((Heightmap)entry.getValue()).asLongArray()));
			}
		}

		this.sectionsData = new byte[getSectionsPacketSize(chunk)];
		writeSections(new PacketByteBuf(this.getWritableSectionsDataBuf()), chunk);
		this.blockEntities = Lists.<ChunkData.BlockEntityData>newArrayList();

		for (Entry<BlockPos, BlockEntity> entryx : chunk.getBlockEntities().entrySet()) {
			this.blockEntities.add(ChunkData.BlockEntityData.of((BlockEntity)entryx.getValue()));
		}
	}

	public ChunkData(RegistryByteBuf buf, int x, int z) {
		this.heightmap = buf.readNbt();
		if (this.heightmap == null) {
			throw new RuntimeException("Can't read heightmap in packet for [" + x + ", " + z + "]");
		} else {
			int i = buf.readVarInt();
			if (i > 2097152) {
				throw new RuntimeException("Chunk Packet trying to allocate too much memory on read.");
			} else {
				this.sectionsData = new byte[i];
				buf.readBytes(this.sectionsData);
				this.blockEntities = ChunkData.BlockEntityData.LIST_PACKET_CODEC.decode(buf);
			}
		}
	}

	public void write(RegistryByteBuf buf) {
		buf.writeNbt(this.heightmap);
		buf.writeVarInt(this.sectionsData.length);
		buf.writeBytes(this.sectionsData);
		ChunkData.BlockEntityData.LIST_PACKET_CODEC.encode(buf, this.blockEntities);
	}

	private static int getSectionsPacketSize(WorldChunk chunk) {
		int i = 0;

		for (ChunkSection chunkSection : chunk.getSectionArray()) {
			i += chunkSection.getPacketSize();
		}

		return i;
	}

	private ByteBuf getWritableSectionsDataBuf() {
		ByteBuf byteBuf = Unpooled.wrappedBuffer(this.sectionsData);
		byteBuf.writerIndex(0);
		return byteBuf;
	}

	public static void writeSections(PacketByteBuf buf, WorldChunk chunk) {
		for (ChunkSection chunkSection : chunk.getSectionArray()) {
			chunkSection.toPacket(buf);
		}
	}

	public Consumer<ChunkData.BlockEntityVisitor> getBlockEntities(int x, int z) {
		return visitor -> this.iterateBlockEntities(visitor, x, z);
	}

	private void iterateBlockEntities(ChunkData.BlockEntityVisitor consumer, int x, int z) {
		int i = 16 * x;
		int j = 16 * z;
		BlockPos.Mutable mutable = new BlockPos.Mutable();

		for (ChunkData.BlockEntityData blockEntityData : this.blockEntities) {
			int k = i + ChunkSectionPos.getLocalCoord(blockEntityData.localXz >> 4);
			int l = j + ChunkSectionPos.getLocalCoord(blockEntityData.localXz);
			mutable.set(k, blockEntityData.y, l);
			consumer.accept(mutable, blockEntityData.type, blockEntityData.nbt);
		}
	}

	public PacketByteBuf getSectionsDataBuf() {
		return new PacketByteBuf(Unpooled.wrappedBuffer(this.sectionsData));
	}

	public NbtCompound getHeightmap() {
		return this.heightmap;
	}

	static class BlockEntityData {
		public static final PacketCodec<RegistryByteBuf, ChunkData.BlockEntityData> PACKET_CODEC = PacketCodec.of(
			ChunkData.BlockEntityData::write, ChunkData.BlockEntityData::new
		);
		public static final PacketCodec<RegistryByteBuf, List<ChunkData.BlockEntityData>> LIST_PACKET_CODEC = PACKET_CODEC.mapResult(PacketCodecs.listMapper());
		final int localXz;
		final int y;
		final BlockEntityType<?> type;
		@Nullable
		final NbtCompound nbt;

		private BlockEntityData(int localXz, int y, BlockEntityType<?> type, @Nullable NbtCompound nbt) {
			this.localXz = localXz;
			this.y = y;
			this.type = type;
			this.nbt = nbt;
		}

		private BlockEntityData(RegistryByteBuf buf) {
			this.localXz = buf.readByte();
			this.y = buf.readShort();
			this.type = PacketCodecs.registry(RegistryKeys.BLOCK_ENTITY_TYPE).decode(buf);
			this.nbt = buf.readNbt();
		}

		private void write(RegistryByteBuf buf) {
			buf.writeByte(this.localXz);
			buf.writeShort(this.y);
			PacketCodecs.registry(RegistryKeys.BLOCK_ENTITY_TYPE).encode(buf, this.type);
			buf.writeNbt(this.nbt);
		}

		static ChunkData.BlockEntityData of(BlockEntity blockEntity) {
			NbtCompound nbtCompound = blockEntity.toInitialChunkDataNbt();
			BlockPos blockPos = blockEntity.getPos();
			int i = ChunkSectionPos.getLocalCoord(blockPos.getX()) << 4 | ChunkSectionPos.getLocalCoord(blockPos.getZ());
			return new ChunkData.BlockEntityData(i, blockPos.getY(), blockEntity.getType(), nbtCompound.isEmpty() ? null : nbtCompound);
		}
	}

	@FunctionalInterface
	public interface BlockEntityVisitor {
		void accept(BlockPos pos, BlockEntityType<?> type, @Nullable NbtCompound nbt);
	}
}
