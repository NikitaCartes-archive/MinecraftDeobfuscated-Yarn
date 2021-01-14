package net.minecraft.world.chunk;

import javax.annotation.Nullable;
import net.minecraft.util.Util;

public class ChunkNibbleArray {
	@Nullable
	protected byte[] bytes;

	public ChunkNibbleArray() {
	}

	public ChunkNibbleArray(byte[] bs) {
		this.bytes = bs;
		if (bs.length != 2048) {
			throw (IllegalArgumentException)Util.throwOrPause(new IllegalArgumentException("ChunkNibbleArrays should be 2048 bytes not: " + bs.length));
		}
	}

	protected ChunkNibbleArray(int i) {
		this.bytes = new byte[i];
	}

	public int get(int x, int y, int z) {
		return this.get(this.getIndex(x, y, z));
	}

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
			int i = this.divideByTwo(index);
			return this.isEven(index) ? this.bytes[i] & 15 : this.bytes[i] >> 4 & 15;
		}
	}

	private void set(int index, int value) {
		if (this.bytes == null) {
			this.bytes = new byte[2048];
		}

		int i = this.divideByTwo(index);
		if (this.isEven(index)) {
			this.bytes[i] = (byte)(this.bytes[i] & 240 | value & 15);
		} else {
			this.bytes[i] = (byte)(this.bytes[i] & 15 | (value & 15) << 4);
		}
	}

	private boolean isEven(int n) {
		return (n & 1) == 0;
	}

	private int divideByTwo(int n) {
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

	public boolean isUninitialized() {
		return this.bytes == null;
	}
}
