package net.minecraft.world.chunk;

import net.minecraft.util.math.BlockPos;

public class ChunkPos {
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

	public static int longX(long l) {
		return (int)(l & 4294967295L);
	}

	public static int longZ(long l) {
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

	public int getXStart() {
		return this.x << 4;
	}

	public int getZStart() {
		return this.z << 4;
	}

	public int getXEnd() {
		return (this.x << 4) + 15;
	}

	public int getZEnd() {
		return (this.z << 4) + 15;
	}

	public BlockPos toBlockPos(int i, int j, int k) {
		return new BlockPos((this.x << 4) + i, j, (this.z << 4) + k);
	}

	public String toString() {
		return "[" + this.x + ", " + this.z + "]";
	}

	public BlockPos method_8323() {
		return new BlockPos(this.x << 4, 0, this.z << 4);
	}
}
