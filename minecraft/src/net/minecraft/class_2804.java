package net.minecraft;

import javax.annotation.Nullable;

public class class_2804 {
	@Nullable
	protected byte[] field_12783;

	public class_2804() {
	}

	public class_2804(byte[] bs) {
		this.field_12783 = bs;
		if (bs.length != 2048) {
			throw new IllegalArgumentException("ChunkNibbleArrays should be 2048 bytes not: " + bs.length);
		}
	}

	protected class_2804(int i) {
		this.field_12783 = new byte[i];
	}

	public int method_12139(int i, int j, int k) {
		return this.method_12141(this.method_12140(i, j, k));
	}

	public void method_12145(int i, int j, int k, int l) {
		this.method_12142(this.method_12140(i, j, k), l);
	}

	protected int method_12140(int i, int j, int k) {
		return j << 8 | k << 4 | i;
	}

	private int method_12141(int i) {
		if (this.field_12783 == null) {
			return 0;
		} else {
			int j = this.method_12138(i);
			return this.method_12143(i) ? this.field_12783[j] & 15 : this.field_12783[j] >> 4 & 15;
		}
	}

	private void method_12142(int i, int j) {
		if (this.field_12783 == null) {
			this.field_12783 = new byte[2048];
		}

		int k = this.method_12138(i);
		if (this.method_12143(i)) {
			this.field_12783[k] = (byte)(this.field_12783[k] & 240 | j & 15);
		} else {
			this.field_12783[k] = (byte)(this.field_12783[k] & 15 | (j & 15) << 4);
		}
	}

	private boolean method_12143(int i) {
		return (i & 1) == 0;
	}

	private int method_12138(int i) {
		return i >> 1;
	}

	public byte[] method_12137() {
		if (this.field_12783 == null) {
			this.field_12783 = new byte[2048];
		}

		return this.field_12783;
	}

	public class_2804 method_12144() {
		return this.field_12783 == null ? new class_2804() : new class_2804((byte[])this.field_12783.clone());
	}

	public String toString() {
		StringBuilder stringBuilder = new StringBuilder();

		for (int i = 0; i < 4096; i++) {
			stringBuilder.append(Integer.toHexString(this.method_12141(i)));
			if ((i & 15) == 15) {
				stringBuilder.append("\n");
			}

			if ((i & 0xFF) == 255) {
				stringBuilder.append("\n");
			}
		}

		return stringBuilder.toString();
	}

	public boolean method_12146() {
		return this.field_12783 == null;
	}
}
