package net.minecraft.world.chunk;

import javax.annotation.Nullable;
import net.minecraft.util.Util;
import net.minecraft.util.annotation.Debug;

/**
 * A chunk nibble array is an array of nibbles for each block position in
 * a chunk. It is most often used to store light data.
 * 
 * <p>A {@index nibble} is 4 bits, storing an integer from {@code 0} to
 * {@code 15}. It takes half the space of a byte.
 * 
 * <p>The nibbles are stored in an X-Z-Y major order; in the backing array,
 * the indices increases by first increasing X, then Z, and finally Y.
 */
public class ChunkNibbleArray {
	public static final int BYTES_LENGTH = 2048;
	public static final int COPY_BLOCK_SIZE = 128;
	private static final int NIBBLE_BITS = 4;
	@Nullable
	protected byte[] bytes;

	public ChunkNibbleArray() {
	}

	public ChunkNibbleArray(byte[] bytes) {
		this.bytes = bytes;
		if (bytes.length != 2048) {
			throw (IllegalArgumentException)Util.throwOrPause(new IllegalArgumentException("ChunkNibbleArrays should be 2048 bytes not: " + bytes.length));
		}
	}

	protected ChunkNibbleArray(int size) {
		this.bytes = new byte[size];
	}

	/**
	 * {@return the integer value of a nibble, in {@code [0, 15]}}
	 */
	public int get(int x, int y, int z) {
		return this.get(this.getIndex(x, y, z));
	}

	/**
	 * Sets the value of a nibble.
	 * 
	 * <p>If the {@code value} has bits outside of the lowest 4 set to {@code 1},
	 * (value is outside of {@code [0, 15]), the extraneous bits are discarded.
	 */
	public void set(int x, int y, int z, int value) {
		this.set(this.getIndex(x, y, z), value);
	}

	protected int getIndex(int x, int y, int z) {
		return y << 8 | z << 4 | x;
	}

	private int get(int index) {
		if (this.bytes == null) {
			return 0;
		} else {
			int i = this.getArrayIndex(index);
			return this.occupiesSmallerBits(index) ? this.bytes[i] & 15 : this.bytes[i] >> 4 & 15;
		}
	}

	private void set(int index, int value) {
		if (this.bytes == null) {
			this.bytes = new byte[2048];
		}

		int i = this.getArrayIndex(index);
		if (this.occupiesSmallerBits(index)) {
			this.bytes[i] = (byte)(this.bytes[i] & 240 | value & 15);
		} else {
			this.bytes[i] = (byte)(this.bytes[i] & 15 | (value & 15) << 4);
		}
	}

	/**
	 * {@return if the nibble at {@code n} is stored in the less
	 * significant (smaller) 4 bits of the byte in the backing array}
	 * 
	 * @param n the index of the nibble, not the array index
	 */
	private boolean occupiesSmallerBits(int n) {
		return (n & 1) == 0;
	}

	private int getArrayIndex(int n) {
		return n >> 1;
	}

	public byte[] asByteArray() {
		if (this.bytes == null) {
			this.bytes = new byte[2048];
		}

		return this.bytes;
	}

	public ChunkNibbleArray copy() {
		return this.bytes == null ? new ChunkNibbleArray() : new ChunkNibbleArray((byte[])this.bytes.clone());
	}

	public String toString() {
		StringBuilder stringBuilder = new StringBuilder();

		for (int i = 0; i < 4096; i++) {
			stringBuilder.append(Integer.toHexString(this.get(i)));
			if ((i & 15) == 15) {
				stringBuilder.append("\n");
			}

			if ((i & 0xFF) == 255) {
				stringBuilder.append("\n");
			}
		}

		return stringBuilder.toString();
	}

	/**
	 * {@return a hexademical string representation of the {@code y=0} level of
	 * this array}
	 * 
	 * <p>It is useful for debugging the grid nibble array.
	 * 
	 * @param unused unused
	 */
	@Debug
	public String bottomToString(int unused) {
		StringBuilder stringBuilder = new StringBuilder();

		for (int i = 0; i < 256; i++) {
			stringBuilder.append(Integer.toHexString(this.get(i)));
			if ((i & 15) == 15) {
				stringBuilder.append("\n");
			}
		}

		return stringBuilder.toString();
	}

	public boolean isUninitialized() {
		return this.bytes == null;
	}
}
