package net.minecraft.util.math;

import java.util.Spliterators.AbstractSpliterator;
import java.util.function.Consumer;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;
import javax.annotation.Nullable;

public class ChunkPos {
	public static final long MARKER = toLong(1875016, 1875016);
	private static final long field_30953 = 32L;
	private static final long field_30954 = 4294967295L;
	private static final int field_30955 = 5;
	private static final int field_30956 = 31;
	public final int x;
	public final int z;
	private static final int field_30957 = 1664525;
	private static final int field_30958 = 1013904223;
	private static final int field_30959 = -559038737;

	public ChunkPos(int x, int z) {
		this.x = x;
		this.z = z;
	}

	public ChunkPos(BlockPos pos) {
		this.x = ChunkSectionPos.getSectionCoord(pos.getX());
		this.z = ChunkSectionPos.getSectionCoord(pos.getZ());
	}

	public ChunkPos(long pos) {
		this.x = (int)pos;
		this.z = (int)(pos >> 32);
	}

	public long toLong() {
		return toLong(this.x, this.z);
	}

	public static long toLong(int chunkX, int chunkZ) {
		return (long)chunkX & 4294967295L | ((long)chunkZ & 4294967295L) << 32;
	}

	public static long method_37232(BlockPos blockPos) {
		return toLong(ChunkSectionPos.getSectionCoord(blockPos.getX()), ChunkSectionPos.getSectionCoord(blockPos.getZ()));
	}

	public static int getPackedX(long pos) {
		return (int)(pos & 4294967295L);
	}

	public static int getPackedZ(long pos) {
		return (int)(pos >>> 32 & 4294967295L);
	}

	public int hashCode() {
		int i = 1664525 * this.x + 1013904223;
		int j = 1664525 * (this.z ^ -559038737) + 1013904223;
		return i ^ j;
	}

	public boolean equals(Object o) {
		if (this == o) {
			return true;
		} else {
			return !(o instanceof ChunkPos chunkPos) ? false : this.x == chunkPos.x && this.z == chunkPos.z;
		}
	}

	public int getCenterX() {
		return this.getOffsetX(8);
	}

	public int getCenterZ() {
		return this.getOffsetZ(8);
	}

	public int getStartX() {
		return ChunkSectionPos.getBlockCoord(this.x);
	}

	public int getStartZ() {
		return ChunkSectionPos.getBlockCoord(this.z);
	}

	public int getEndX() {
		return this.getOffsetX(15);
	}

	public int getEndZ() {
		return this.getOffsetZ(15);
	}

	public int getRegionX() {
		return this.x >> 5;
	}

	public int getRegionZ() {
		return this.z >> 5;
	}

	public int getRegionRelativeX() {
		return this.x & 31;
	}

	public int getRegionRelativeZ() {
		return this.z & 31;
	}

	public BlockPos getBlockPos(int offsetX, int y, int offsetZ) {
		return new BlockPos(this.getOffsetX(offsetX), y, this.getOffsetZ(offsetZ));
	}

	public int getOffsetX(int offset) {
		return ChunkSectionPos.getOffsetPos(this.x, offset);
	}

	public int getOffsetZ(int offset) {
		return ChunkSectionPos.getOffsetPos(this.z, offset);
	}

	public BlockPos getCenterAtY(int y) {
		return new BlockPos(this.getCenterX(), y, this.getCenterZ());
	}

	public String toString() {
		return "[" + this.x + ", " + this.z + "]";
	}

	public BlockPos getStartPos() {
		return new BlockPos(this.getStartX(), 0, this.getStartZ());
	}

	public int getChebyshevDistance(ChunkPos pos) {
		return Math.max(Math.abs(this.x - pos.x), Math.abs(this.z - pos.z));
	}

	public static Stream<ChunkPos> stream(ChunkPos center, int radius) {
		return stream(new ChunkPos(center.x - radius, center.z - radius), new ChunkPos(center.x + radius, center.z + radius));
	}

	public static Stream<ChunkPos> stream(ChunkPos pos1, ChunkPos pos2) {
		int i = Math.abs(pos1.x - pos2.x) + 1;
		int j = Math.abs(pos1.z - pos2.z) + 1;
		final int k = pos1.x < pos2.x ? 1 : -1;
		final int l = pos1.z < pos2.z ? 1 : -1;
		return StreamSupport.stream(new AbstractSpliterator<ChunkPos>((long)(i * j), 64) {
			@Nullable
			private ChunkPos position;

			public boolean tryAdvance(Consumer<? super ChunkPos> consumer) {
				if (this.position == null) {
					this.position = pos1;
				} else {
					int i = this.position.x;
					int j = this.position.z;
					if (i == pos2.x) {
						if (j == pos2.z) {
							return false;
						}

						this.position = new ChunkPos(pos1.x, j + l);
					} else {
						this.position = new ChunkPos(i + k, j);
					}
				}

				consumer.accept(this.position);
				return true;
			}
		}, false);
	}
}
