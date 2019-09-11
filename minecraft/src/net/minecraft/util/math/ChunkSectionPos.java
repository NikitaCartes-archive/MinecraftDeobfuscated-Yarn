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
		return new ChunkSectionPos(getSectionCoord(blockPos.getX()), getSectionCoord(blockPos.getY()), getSectionCoord(blockPos.getZ()));
	}

	public static ChunkSectionPos from(ChunkPos chunkPos, int i) {
		return new ChunkSectionPos(chunkPos.x, i, chunkPos.z);
	}

	public static ChunkSectionPos from(Entity entity) {
		return new ChunkSectionPos(
			getSectionCoord(MathHelper.floor(entity.x)), getSectionCoord(MathHelper.floor(entity.y)), getSectionCoord(MathHelper.floor(entity.z))
		);
	}

	public static ChunkSectionPos from(long l) {
		return new ChunkSectionPos(getX(l), getY(l), getZ(l));
	}

	public static long offset(long l, Direction direction) {
		return offset(l, direction.getOffsetX(), direction.getOffsetY(), direction.getOffsetZ());
	}

	public static long offset(long l, int i, int j, int k) {
		return asLong(getX(l) + i, getY(l) + j, getZ(l) + k);
	}

	public static int getSectionCoord(int i) {
		return i >> 4;
	}

	public static int getLocalCoord(int i) {
		return i & 15;
	}

	public static short getPackedLocalPos(BlockPos blockPos) {
		int i = getLocalCoord(blockPos.getX());
		int j = getLocalCoord(blockPos.getY());
		int k = getLocalCoord(blockPos.getZ());
		return (short)(i << 8 | k << 4 | j);
	}

	public static int getWorldCoord(int i) {
		return i << 4;
	}

	public static int getX(long l) {
		return (int)(l << 0 >> 42);
	}

	public static int getY(long l) {
		return (int)(l << 44 >> 44);
	}

	public static int getZ(long l) {
		return (int)(l << 22 >> 42);
	}

	public int getSectionX() {
		return this.getX();
	}

	public int getSectionY() {
		return this.getY();
	}

	public int getSectionZ() {
		return this.getZ();
	}

	public int getMinX() {
		return this.getSectionX() << 4;
	}

	public int getMinY() {
		return this.getSectionY() << 4;
	}

	public int getMinZ() {
		return this.getSectionZ() << 4;
	}

	public int getMaxX() {
		return (this.getSectionX() << 4) + 15;
	}

	public int getMaxY() {
		return (this.getSectionY() << 4) + 15;
	}

	public int getMaxZ() {
		return (this.getSectionZ() << 4) + 15;
	}

	public static long fromGlobalPos(long l) {
		return asLong(getSectionCoord(BlockPos.unpackLongX(l)), getSectionCoord(BlockPos.unpackLongY(l)), getSectionCoord(BlockPos.unpackLongZ(l)));
	}

	public static long withZeroZ(long l) {
		return l & -1048576L;
	}

	public BlockPos getMinPos() {
		return new BlockPos(getWorldCoord(this.getSectionX()), getWorldCoord(this.getSectionY()), getWorldCoord(this.getSectionZ()));
	}

	public BlockPos getCenterPos() {
		int i = 8;
		return this.getMinPos().add(8, 8, 8);
	}

	public ChunkPos toChunkPos() {
		return new ChunkPos(this.getSectionX(), this.getSectionZ());
	}

	public static long asLong(int i, int j, int k) {
		long l = 0L;
		l |= ((long)i & 4194303L) << 42;
		l |= ((long)j & 1048575L) << 0;
		return l | ((long)k & 4194303L) << 20;
	}

	public long asLong() {
		return asLong(this.getSectionX(), this.getSectionY(), this.getSectionZ());
	}

	public Stream<BlockPos> streamBlocks() {
		return BlockPos.stream(this.getMinX(), this.getMinY(), this.getMinZ(), this.getMaxX(), this.getMaxY(), this.getMaxZ());
	}

	public static Stream<ChunkSectionPos> stream(ChunkSectionPos chunkSectionPos, int i) {
		int j = chunkSectionPos.getSectionX();
		int k = chunkSectionPos.getSectionY();
		int l = chunkSectionPos.getSectionZ();
		return stream(j - i, k - i, l - i, j + i, k + i, l + i);
	}

	public static Stream<ChunkSectionPos> method_22446(ChunkPos chunkPos, int i) {
		int j = chunkPos.x;
		int k = chunkPos.z;
		return stream(j - i, 0, k - i, j + i, 15, k + i);
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
