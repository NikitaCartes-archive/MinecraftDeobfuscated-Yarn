package net.minecraft.world.level.storage;

public class AlphaChunkDataArray {
	public final byte[] data;
	private final int zOffset;
	private final int xOffset;

	public AlphaChunkDataArray(byte[] data, int yCoordinateBits) {
		this.data = data;
		this.zOffset = yCoordinateBits;
		this.xOffset = yCoordinateBits + 4;
	}

	public int get(int x, int y, int z) {
		int i = x << this.xOffset | z << this.zOffset | y;
		int j = i >> 1;
		int k = i & 1;
		return k == 0 ? this.data[j] & 15 : this.data[j] >> 4 & 15;
	}
}
