package net.minecraft.world.chunk;

import javax.annotation.Nullable;

public class ChunkNibbleArray {
	@Nullable
	protected byte[] byteArray;

	public ChunkNibbleArray() {
	}

	public ChunkNibbleArray(byte[] bs) {
		this.byteArray = bs;
		if (bs.length != 2048) {
			throw new IllegalArgumentException("ChunkNibbleArrays should be 2048 bytes not: " + bs.length);
		}
	}

	protected ChunkNibbleArray(int i) {
		this.byteArray = new byte[i];
	}

	public int get(int i, int j, int k) {
		return this.get(this.getIndex(i, j, k));
	}

	public void set(int i, int j, int k, int l) {
		this.set(this.getIndex(i, j, k), l);
	}

	protected int getIndex(int i, int j, int k) {
		return j << 8 | k << 4 | i;
	}

	private int get(int i) {
		if (this.byteArray == null) {
			return 0;
		} else {
			int j = this.divideByTwo(i);
			return this.isEven(i) ? this.byteArray[j] & 15 : this.byteArray[j] >> 4 & 15;
		}
	}

	private void set(int i, int j) {
		if (this.byteArray == null) {
			this.byteArray = new byte[2048];
		}

		int k = this.divideByTwo(i);
		if (this.isEven(i)) {
			this.byteArray[k] = (byte)(this.byteArray[k] & 240 | j & 15);
		} else {
			this.byteArray[k] = (byte)(this.byteArray[k] & 15 | (j & 15) << 4);
		}
	}

	private boolean isEven(int i) {
		return (i & 1) == 0;
	}

	private int divideByTwo(int i) {
		return i >> 1;
	}

	public byte[] asByteArray() {
		if (this.byteArray == null) {
			this.byteArray = new byte[2048];
		}

		return this.byteArray;
	}

	public ChunkNibbleArray copy() {
		return this.byteArray == null ? new ChunkNibbleArray() : new ChunkNibbleArray((byte[])this.byteArray.clone());
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
		return this.byteArray == null;
	}
}
