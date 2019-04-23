package net.minecraft.util.math;

import java.util.Spliterators.AbstractSpliterator;
import java.util.function.Consumer;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;
import net.minecraft.entity.Entity;
import net.minecraft.util.CuboidBlockIterator;

public class ChunkSectionPos extends Vec3i {
	private ChunkSectionPos(int i, int j, int k) {
		super(i, j, k);
	}

	public static ChunkSectionPos from(int i, int j, int k) {
		return new ChunkSectionPos(i, j, k);
	}

	public static ChunkSectionPos from(BlockPos blockPos) {
		return new ChunkSectionPos(toChunkCoord(blockPos.getX()), toChunkCoord(blockPos.getY()), toChunkCoord(blockPos.getZ()));
	}

	public static ChunkSectionPos from(ChunkPos chunkPos, int i) {
		return new ChunkSectionPos(chunkPos.x, i, chunkPos.z);
	}

	public static ChunkSectionPos from(Entity entity) {
		return new ChunkSectionPos(toChunkCoord(MathHelper.floor(entity.x)), toChunkCoord(MathHelper.floor(entity.y)), toChunkCoord(MathHelper.floor(entity.z)));
	}

	public static ChunkSectionPos from(long l) {
		return new ChunkSectionPos(unpackLongX(l), unpackLongY(l), unpackLongZ(l));
	}

	public static long offsetPacked(long l, Direction direction) {
		return offsetPacked(l, direction.getOffsetX(), direction.getOffsetY(), direction.getOffsetZ());
	}

	public static long offsetPacked(long l, int i, int j, int k) {
		return asLong(unpackLongX(l) + i, unpackLongY(l) + j, unpackLongZ(l) + k);
	}

	public static int toChunkCoord(int i) {
		return i >> 4;
	}

	public static int toLocalCoord(int i) {
		return i & 15;
	}

	public static short packToShort(BlockPos blockPos) {
		int i = toLocalCoord(blockPos.getX());
		int j = toLocalCoord(blockPos.getY());
		int k = toLocalCoord(blockPos.getZ());
		return (short)(i << 8 | k << 4 | j);
	}

	public static int fromChunkCoord(int i) {
		return i << 4;
	}

	public static int unpackLongX(long l) {
		return (int)(l << 0 >> 42);
	}

	public static int unpackLongY(long l) {
		return (int)(l << 44 >> 44);
	}

	public static int unpackLongZ(long l) {
		return (int)(l << 22 >> 42);
	}

	public int getChunkX() {
		return this.getX();
	}

	public int getChunkY() {
		return this.getY();
	}

	public int getChunkZ() {
		return this.getZ();
	}

	public int getMinX() {
		return this.getChunkX() << 4;
	}

	public int getMinY() {
		return this.getChunkY() << 4;
	}

	public int getMinZ() {
		return this.getChunkZ() << 4;
	}

	public int getMaxX() {
		return (this.getChunkX() << 4) + 15;
	}

	public int getMaxY() {
		return (this.getChunkY() << 4) + 15;
	}

	public int getMaxZ() {
		return (this.getChunkZ() << 4) + 15;
	}

	public static long toChunkLong(long l) {
		return asLong(toChunkCoord(BlockPos.unpackLongX(l)), toChunkCoord(BlockPos.unpackLongY(l)), toChunkCoord(BlockPos.unpackLongZ(l)));
	}

	public static long toLightStorageIndex(long l) {
		return l & -1048576L;
	}

	public BlockPos getMinPos() {
		return new BlockPos(fromChunkCoord(this.getChunkX()), fromChunkCoord(this.getChunkY()), fromChunkCoord(this.getChunkZ()));
	}

	public BlockPos getCenterPos() {
		int i = 8;
		return this.getMinPos().add(8, 8, 8);
	}

	public ChunkPos toChunkPos() {
		return new ChunkPos(this.getChunkX(), this.getChunkZ());
	}

	public static long asLong(int i, int j, int k) {
		long l = 0L;
		l |= ((long)i & 4194303L) << 42;
		l |= ((long)j & 1048575L) << 0;
		return l | ((long)k & 4194303L) << 20;
	}

	public long asLong() {
		return asLong(this.getChunkX(), this.getChunkY(), this.getChunkZ());
	}

	public Stream<BlockPos> streamBlocks() {
		return BlockPos.stream(this.getMinX(), this.getMinY(), this.getMinZ(), this.getMaxX(), this.getMaxY(), this.getMaxZ());
	}

	public static Stream<ChunkSectionPos> stream(ChunkSectionPos chunkSectionPos, int i) {
		int j = chunkSectionPos.getChunkX();
		int k = chunkSectionPos.getChunkY();
		int l = chunkSectionPos.getChunkZ();
		return stream(j - i, k - i, l - i, j + i, k + i, l + i);
	}

	public static Stream<ChunkSectionPos> stream(int i, int j, int k, int l, int m, int n) {
		return StreamSupport.stream(new AbstractSpliterator<ChunkSectionPos>((long)((l - i + 1) * (m - j + 1) * (n - k + 1)), 64) {
			final CuboidBlockIterator iterator = new CuboidBlockIterator(i, j, k, l, m, n);

			public boolean tryAdvance(Consumer<? super ChunkSectionPos> consumer) {
				if (this.iterator.step()) {
					consumer.accept(new ChunkSectionPos(this.iterator.getX(), this.iterator.getY(), this.iterator.getZ()));
					return true;
				} else {
					return false;
				}
			}
		}, false);
	}
}
