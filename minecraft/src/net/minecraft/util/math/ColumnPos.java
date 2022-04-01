package net.minecraft.util.math;

public class ColumnPos {
	private static final long field_29757 = 32L;
	private static final long field_29758 = 4294967295L;
	private static final int field_29759 = 1664525;
	private static final int field_29760 = 1013904223;
	private static final int field_29761 = -559038737;
	public final int x;
	public final int z;

	public ColumnPos(int x, int z) {
		this.x = x;
		this.z = z;
	}

	public ColumnPos(BlockPos pos) {
		this.x = pos.getX();
		this.z = pos.getZ();
	}

	public ChunkPos toChunkPos() {
		return new ChunkPos(ChunkSectionPos.getSectionCoord(this.x), ChunkSectionPos.getSectionCoord(this.z));
	}

	public long pack() {
		return pack(this.x, this.z);
	}

	public static long pack(int x, int z) {
		return (long)x & 4294967295L | ((long)z & 4294967295L) << 32;
	}

	public String toString() {
		return "[" + this.x + ", " + this.z + "]";
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
			return !(o instanceof ColumnPos columnPos) ? false : this.x == columnPos.x && this.z == columnPos.z;
		}
	}
}
