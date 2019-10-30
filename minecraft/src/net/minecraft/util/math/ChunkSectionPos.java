package net.minecraft.util.math;

import java.util.Spliterators.AbstractSpliterator;
import java.util.function.Consumer;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;
import net.minecraft.entity.Entity;
import net.minecraft.util.CuboidBlockIterator;

public class ChunkSectionPos extends Vec3i {
	private ChunkSectionPos(int x, int y, int z) {
		super(x, y, z);
	}

	public static ChunkSectionPos from(int x, int y, int z) {
		return new ChunkSectionPos(x, y, z);
	}

	public static ChunkSectionPos from(BlockPos pos) {
		return new ChunkSectionPos(getSectionCoord(pos.getX()), getSectionCoord(pos.getY()), getSectionCoord(pos.getZ()));
	}

	public static ChunkSectionPos from(ChunkPos chunkPos, int y) {
		return new ChunkSectionPos(chunkPos.x, y, chunkPos.z);
	}

	public static ChunkSectionPos from(Entity entity) {
		return new ChunkSectionPos(
			getSectionCoord(MathHelper.floor(entity.getX())), getSectionCoord(MathHelper.floor(entity.getY())), getSectionCoord(MathHelper.floor(entity.getZ()))
		);
	}

	public static ChunkSectionPos from(long packed) {
		return new ChunkSectionPos(getX(packed), getY(packed), getZ(packed));
	}

	public static long offset(long packed, Direction direction) {
		return offset(packed, direction.getOffsetX(), direction.getOffsetY(), direction.getOffsetZ());
	}

	public static long offset(long packed, int x, int y, int z) {
		return asLong(getX(packed) + x, getY(packed) + y, getZ(packed) + z);
	}

	public static int getSectionCoord(int coord) {
		return coord >> 4;
	}

	public static int getLocalCoord(int coord) {
		return coord & 15;
	}

	public static short getPackedLocalPos(BlockPos pos) {
		int i = getLocalCoord(pos.getX());
		int j = getLocalCoord(pos.getY());
		int k = getLocalCoord(pos.getZ());
		return (short)(i << 8 | k << 4 | j);
	}

	public static int getWorldCoord(int chunkCoord) {
		return chunkCoord << 4;
	}

	public static int getX(long packed) {
		return (int)(packed << 0 >> 42);
	}

	public static int getY(long packed) {
		return (int)(packed << 44 >> 44);
	}

	public static int getZ(long packed) {
		return (int)(packed << 22 >> 42);
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

	public static long fromGlobalPos(long globalLong) {
		return asLong(
			getSectionCoord(BlockPos.unpackLongX(globalLong)), getSectionCoord(BlockPos.unpackLongY(globalLong)), getSectionCoord(BlockPos.unpackLongZ(globalLong))
		);
	}

	public static long withZeroZ(long pos) {
		return pos & -1048576L;
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

	public static long asLong(int x, int y, int z) {
		long l = 0L;
		l |= ((long)x & 4194303L) << 42;
		l |= ((long)y & 1048575L) << 0;
		return l | ((long)z & 4194303L) << 20;
	}

	public long asLong() {
		return asLong(this.getSectionX(), this.getSectionY(), this.getSectionZ());
	}

	public Stream<BlockPos> streamBlocks() {
		return BlockPos.stream(this.getMinX(), this.getMinY(), this.getMinZ(), this.getMaxX(), this.getMaxY(), this.getMaxZ());
	}

	public static Stream<ChunkSectionPos> stream(ChunkSectionPos center, int radius) {
		int i = center.getSectionX();
		int j = center.getSectionY();
		int k = center.getSectionZ();
		return stream(i - radius, j - radius, k - radius, i + radius, j + radius, k + radius);
	}

	public static Stream<ChunkSectionPos> stream(ChunkPos center, int radius) {
		int i = center.x;
		int j = center.z;
		return stream(i - radius, 0, j - radius, i + radius, 15, j + radius);
	}

	public static Stream<ChunkSectionPos> stream(int minX, int minY, int minZ, int maxX, int maxY, int maxZ) {
		return StreamSupport.stream(new AbstractSpliterator<ChunkSectionPos>((long)((maxX - minX + 1) * (maxY - minY + 1) * (maxZ - minZ + 1)), 64) {
			final CuboidBlockIterator iterator = new CuboidBlockIterator(minX, minY, minZ, maxX, maxY, maxZ);

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
