package net.minecraft.world.chunk;

import java.util.Arrays;
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
	public static final int COPY_TIMES = 16;
	public static final int COPY_BLOCK_SIZE = 128;
	public static final int BYTES_LENGTH = 2048;
	private static final int NIBBLE_BITS = 4;
	@Nullable
	protected byte[] bytes;
	private int defaultValue;

	public ChunkNibbleArray() {
		this(0);
	}

	public ChunkNibbleArray(int defaultValue) {
		this.defaultValue = defaultValue;
	}

	public ChunkNibbleArray(byte[] bytes) {
		this.bytes = bytes;
		this.defaultValue = 0;
		if (bytes.length != 2048) {
			throw (IllegalArgumentException)Util.throwOrPause(new IllegalArgumentException("DataLayer should be 2048 bytes not: " + bytes.length));
		}
	}

	/**
	 * {@return the integer value of a nibble, in {@code [0, 15]}}
	 */
	public int get(int x, int y, int z) {
		return this.get(getIndex(x, y, z));
	}

	/**
	 * Sets the value of a nibble.
	 * 
	 * <p>If the {@code value} has bits outside of the lowest 4 set to {@code 1},
	 * (value is outside of {@code [0, 15]}), the extraneous bits are discarded.
	 */
	public void set(int x, int y, int z, int value) {
		this.set(getIndex(x, y, z), value);
	}

	private static int getIndex(int x, int y, int z) {
		return y << 8 | z << 4 | x;
	}

	private int get(int index) {
		if (this.bytes == null) {
			return this.defaultValue;
		} else {
			int i = getArrayIndex(index);
			int j = occupiesSmallerBits(index);
			return this.bytes[i] >> 4 * j & 15;
		}
	}

	private void set(int index, int value) {
		byte[] bs = this.asByteArray();
		int i = getArrayIndex(index);
		int j = occupiesSmallerBits(index);
		int k = ~(15 << 4 * j);
		int l = (value & 15) << 4 * j;
		bs[i] = (byte)(bs[i] & k | l);
	}

	/**
	 * {@return if the nibble at {@code n} is stored in the less
	 * significant (smaller) 4 bits of the byte in the backing array}
	 */
	private static int occupiesSmallerBits(int i) {
		return i & 1;
	}

	private static int getArrayIndex(int i) {
		return i >> 1;
	}

	public void clear(int defaultValue) {
		this.defaultValue = defaultValue;
		this.bytes = null;
	}

	private static byte pack(int value) {
		byte b = (byte)value;

		for (int i = 4; i < 8; i += 4) {
			b = (byte)(b | value << i);
		}

		return b;
	}

	public byte[] asByteArray() {
		if (this.bytes == null) {
			this.bytes = new byte[2048];
			if (this.defaultValue != 0) {
				Arrays.fill(this.bytes, pack(this.defaultValue));
			}
		}

		return this.bytes;
	}

	public ChunkNibbleArray copy() {
		return this.bytes == null ? new ChunkNibbleArray(this.defaultValue) : new ChunkNibbleArray((byte[])this.bytes.clone());
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
	 * {@return a hexadecimal string representation of the {@code y=0} level of
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

	public boolean isArrayUninitialized() {
		return this.bytes == null;
	}

	public boolean isUninitialized(int expectedDefaultValue) {
		return this.bytes == null && this.defaultValue == expectedDefaultValue;
	}

	public boolean isUninitialized() {
		return this.bytes == null && this.defaultValue == 0;
	}
}
