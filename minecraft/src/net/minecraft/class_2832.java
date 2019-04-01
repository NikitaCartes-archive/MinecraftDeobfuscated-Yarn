package net.minecraft;

public class class_2832 {
	public final byte[] field_12894;
	private final int field_12893;
	private final int field_12892;

	public class_2832(byte[] bs, int i) {
		this.field_12894 = bs;
		this.field_12893 = i;
		this.field_12892 = i + 4;
	}

	public int method_12275(int i, int j, int k) {
		int l = i << this.field_12892 | k << this.field_12893 | j;
		int m = l >> 1;
		int n = l & 1;
		return n == 0 ? this.field_12894[m] & 15 : this.field_12894[m] >> 4 & 15;
	}
}
