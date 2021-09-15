package net.minecraft;

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
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkSectionPos;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.Heightmap;
import net.minecraft.world.chunk.ChunkSection;
import net.minecraft.world.chunk.WorldChunk;

public class class_6603 {
	private static final int field_34862 = 2097152;
	private final NbtCompound field_34863;
	private final byte[] field_34864;
	private final List<class_6603.class_6604> field_34865;

	public class_6603(WorldChunk worldChunk) {
		this.field_34863 = new NbtCompound();

		for (Entry<Heightmap.Type, Heightmap> entry : worldChunk.getHeightmaps()) {
			if (((Heightmap.Type)entry.getKey()).shouldSendToClient()) {
				this.field_34863.put(((Heightmap.Type)entry.getKey()).getName(), new NbtLongArray(((Heightmap)entry.getValue()).asLongArray()));
			}
		}

		this.field_34864 = new byte[method_38589(worldChunk)];
		method_38591(new PacketByteBuf(this.method_38595()), worldChunk);
		this.field_34865 = Lists.<class_6603.class_6604>newArrayList();

		for (Entry<BlockPos, BlockEntity> entryx : worldChunk.getBlockEntities().entrySet()) {
			this.field_34865.add(class_6603.class_6604.method_38596((BlockEntity)entryx.getValue()));
		}
	}

	public class_6603(PacketByteBuf packetByteBuf, int i, int j) {
		this.field_34863 = packetByteBuf.readNbt();
		if (this.field_34863 == null) {
			throw new RuntimeException("Can't read heightmap in packet for [" + i + ", " + j + "]");
		} else {
			int k = packetByteBuf.readVarInt();
			if (k > 2097152) {
				throw new RuntimeException("Chunk Packet trying to allocate too much memory on read.");
			} else {
				this.field_34864 = new byte[k];
				packetByteBuf.readBytes(this.field_34864);
				this.field_34865 = packetByteBuf.readList(class_6603.class_6604::new);
			}
		}
	}

	public void method_38590(PacketByteBuf packetByteBuf) {
		packetByteBuf.writeNbt(this.field_34863);
		packetByteBuf.writeVarInt(this.field_34864.length);
		packetByteBuf.writeBytes(this.field_34864);
		packetByteBuf.writeCollection(this.field_34865, (packetByteBufx, arg) -> arg.method_38597(packetByteBufx));
	}

	private static int method_38589(WorldChunk worldChunk) {
		int i = 0;

		for (ChunkSection chunkSection : worldChunk.getSectionArray()) {
			i += chunkSection.getPacketSize();
		}

		return i;
	}

	private ByteBuf method_38595() {
		ByteBuf byteBuf = Unpooled.wrappedBuffer(this.field_34864);
		byteBuf.writerIndex(0);
		return byteBuf;
	}

	public static void method_38591(PacketByteBuf packetByteBuf, WorldChunk worldChunk) {
		for (ChunkSection chunkSection : worldChunk.getSectionArray()) {
			chunkSection.toPacket(packetByteBuf);
		}
	}

	public Consumer<class_6603.class_6605> method_38587(int i, int j) {
		return arg -> this.method_38593(arg, i, j);
	}

	private void method_38593(class_6603.class_6605 arg, int i, int j) {
		int k = 16 * i;
		int l = 16 * j;
		BlockPos.Mutable mutable = new BlockPos.Mutable();

		for (class_6603.class_6604 lv : this.field_34865) {
			int m = k + ChunkSectionPos.getLocalCoord(lv.field_34866 >> 4);
			int n = l + ChunkSectionPos.getLocalCoord(lv.field_34866);
			mutable.set(m, lv.field_34867, n);
			arg.accept(mutable, lv.field_34868, lv.field_34869);
		}
	}

	public PacketByteBuf method_38586() {
		return new PacketByteBuf(Unpooled.wrappedBuffer(this.field_34864));
	}

	public NbtCompound method_38594() {
		return this.field_34863;
	}

	static class class_6604 {
		final int field_34866;
		final int field_34867;
		final BlockEntityType<?> field_34868;
		@Nullable
		final NbtCompound field_34869;

		private class_6604(int i, int j, BlockEntityType<?> blockEntityType, @Nullable NbtCompound nbtCompound) {
			this.field_34866 = i;
			this.field_34867 = j;
			this.field_34868 = blockEntityType;
			this.field_34869 = nbtCompound;
		}

		private class_6604(PacketByteBuf packetByteBuf) {
			this.field_34866 = packetByteBuf.readByte();
			this.field_34867 = packetByteBuf.readShort();
			int i = packetByteBuf.readVarInt();
			this.field_34868 = Registry.BLOCK_ENTITY_TYPE.get(i);
			this.field_34869 = packetByteBuf.readNbt();
		}

		void method_38597(PacketByteBuf packetByteBuf) {
			packetByteBuf.writeByte(this.field_34866);
			packetByteBuf.writeShort(this.field_34867);
			packetByteBuf.writeVarInt(Registry.BLOCK_ENTITY_TYPE.getRawId(this.field_34868));
			packetByteBuf.writeNbt(this.field_34869);
		}

		static class_6603.class_6604 method_38596(BlockEntity blockEntity) {
			NbtCompound nbtCompound = blockEntity.toInitialChunkDataNbt();
			BlockPos blockPos = blockEntity.getPos();
			int i = ChunkSectionPos.getLocalCoord(blockPos.getX()) << 4 | ChunkSectionPos.getLocalCoord(blockPos.getZ());
			return new class_6603.class_6604(i, blockPos.getY(), blockEntity.getType(), nbtCompound.isEmpty() ? null : nbtCompound);
		}
	}

	@FunctionalInterface
	public interface class_6605 {
		void accept(BlockPos blockPos, BlockEntityType<?> blockEntityType, @Nullable NbtCompound nbtCompound);
	}
}
