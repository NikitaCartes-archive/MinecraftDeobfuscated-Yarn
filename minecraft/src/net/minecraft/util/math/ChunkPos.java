package net.minecraft.util.math;

import java.util.Spliterators.AbstractSpliterator;
import java.util.function.Consumer;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;
import javax.annotation.Nullable;

public class ChunkPos {
	public static final long INVALID = toLong(1875016, 1875016);
	public final int x;
	public final int z;

	public ChunkPos(int i, int j) {
		this.x = i;
		this.z = j;
	}

	public ChunkPos(BlockPos blockPos) {
		this.x = blockPos.getX() >> 4;
		this.z = blockPos.getZ() >> 4;
	}

	public ChunkPos(long l) {
		this.x = (int)l;
		this.z = (int)(l >> 32);
	}

	public long toLong() {
		return toLong(this.x, this.z);
	}

	public static long toLong(int i, int j) {
		return (long)i & 4294967295L | ((long)j & 4294967295L) << 32;
	}

	public static int getPackedX(long l) {
		return (int)(l & 4294967295L);
	}

	public static int getPackedZ(long l) {
		return (int)(l >>> 32 & 4294967295L);
	}

	public int hashCode() {
		int i = 1664525 * this.x + 1013904223;
		int j = 1664525 * (this.z ^ -559038737) + 1013904223;
		return i ^ j;
	}

	public boolean equals(Object object) {
		if (this == object) {
			return true;
		} else if (!(object instanceof ChunkPos)) {
			return false;
		} else {
			ChunkPos chunkPos = (ChunkPos)object;
			return this.x == chunkPos.x && this.z == chunkPos.z;
		}
	}

	public int getStartX() {
		return this.x << 4;
	}

	public int getStartZ() {
		return this.z << 4;
	}

	public int getEndX() {
		return (this.x << 4) + 15;
	}

	public int getEndZ() {
		return (this.z << 4) + 15;
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

	public BlockPos toBlockPos(int i, int j, int k) {
		return new BlockPos((this.x << 4) + i, j, (this.z << 4) + k);
	}

	public String toString() {
		return "[" + this.x + ", " + this.z + "]";
	}

	public BlockPos getCenterBlockPos() {
		return new BlockPos(this.x << 4, 0, this.z << 4);
	}

	public static Stream<ChunkPos> stream(ChunkPos chunkPos, int i) {
		return stream(new ChunkPos(chunkPos.x - i, chunkPos.z - i), new ChunkPos(chunkPos.x + i, chunkPos.z + i));
	}

	public static Stream<ChunkPos> stream(ChunkPos chunkPos, ChunkPos chunkPos2) {
		int i = Math.abs(chunkPos.x - chunkPos2.x) + 1;
		int j = Math.abs(chunkPos.z - chunkPos2.z) + 1;
		final int k = chunkPos.x < chunkPos2.x ? 1 : -1;
		final int l = chunkPos.z < chunkPos2.z ? 1 : -1;
		return StreamSupport.stream(new AbstractSpliterator<ChunkPos>((long)(i * j), 64) {
			@Nullable
			private ChunkPos position;

			public boolean tryAdvance(Consumer<? super ChunkPos> consumer) {
				if (this.position == null) {
					this.position = chunkPos;
				} else {
					int i = this.position.x;
					int j = this.position.z;
					if (i == chunkPos2.x) {
						if (j == chunkPos2.z) {
							return false;
						}

						this.position = new ChunkPos(chunkPos.x, j + l);
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
