package net.minecraft.util.math;

public record ColumnPos(int x, int z) {
	private static final long field_29757 = 32L;
	private static final long field_29758 = 4294967295L;

	public ChunkPos toChunkPos() {
		return new ChunkPos(ChunkSectionPos.getSectionCoord(this.x), ChunkSectionPos.getSectionCoord(this.z));
	}

	public long pack() {
		return pack(this.x, this.z);
	}

	public static long pack(int x, int z) {
		return (long)x & 4294967295L | ((long)z & 4294967295L) << 32;
	}

	public static int getX(long packed) {
		return (int)(packed & 4294967295L);
	}

	public static int getZ(long packed) {
		return (int)(packed >>> 32 & 4294967295L);
	}

	public String toString() {
		return "[" + this.x + ", " + this.z + "]";
	}

	public int hashCode() {
		return ChunkPos.hashCode(this.x, this.z);
	}
}
