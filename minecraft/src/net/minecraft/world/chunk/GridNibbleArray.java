package net.minecraft.world.chunk;

/**
 * A specialized chunk nibble array that ignores the Y parameters and only
 * stores the nibbles for a 16 &times; 16 horizontal chunk slice.
 * 
 * <p>When it is {@linkplain #toByteArray() converted to a byte array}, it
 * fills each Y-level of the returned array with the same nibbles it stores.
 */
public class GridNibbleArray extends ChunkNibbleArray {
	/**
	 * The number of array indices each Y-level uses in a chunk nibble array.
	 */
	public static final int INDICES_PER_Y = 128;

	public GridNibbleArray() {
		super(128);
	}

	public GridNibbleArray(ChunkNibbleArray array, int y) {
		super(128);
		System.arraycopy(array.asByteArray(), y * 128, this.bytes, 0, 128);
	}

	@Override
	protected int getIndex(int x, int y, int z) {
		return z << 4 | x;
	}

	@Override
	public byte[] asByteArray() {
		byte[] bs = new byte[2048];

		for (int i = 0; i < 16; i++) {
			System.arraycopy(this.bytes, 0, bs, i * 128, 128);
		}

		return bs;
	}
}
