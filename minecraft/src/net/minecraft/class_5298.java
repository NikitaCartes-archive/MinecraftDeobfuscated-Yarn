package net.minecraft;

import net.minecraft.util.math.MathHelper;
import org.apache.commons.lang3.Validate;

public class class_5298 {
	private final long[] field_24641;
	private final int field_24642;
	private final long field_24643;
	private final int field_24644;

	public class_5298(int i, int j) {
		this(i, j, new long[MathHelper.method_28139(j * i, 64) / 64]);
	}

	public class_5298(int i, int j, long[] ls) {
		Validate.inclusiveBetween(1L, 32L, (long)i);
		this.field_24644 = j;
		this.field_24642 = i;
		this.field_24641 = ls;
		this.field_24643 = (1L << i) - 1L;
		int k = MathHelper.method_28139(j * i, 64) / 64;
		if (ls.length != k) {
			throw new IllegalArgumentException("Invalid length given for storage, got: " + ls.length + " but expected: " + k);
		}
	}

	public void method_28153(int i, int j) {
		Validate.inclusiveBetween(0L, (long)(this.field_24644 - 1), (long)i);
		Validate.inclusiveBetween(0L, this.field_24643, (long)j);
		int k = i * this.field_24642;
		int l = k >> 6;
		int m = (i + 1) * this.field_24642 - 1 >> 6;
		int n = k ^ l << 6;
		this.field_24641[l] = this.field_24641[l] & ~(this.field_24643 << n) | ((long)j & this.field_24643) << n;
		if (l != m) {
			int o = 64 - n;
			int p = this.field_24642 - o;
			this.field_24641[m] = this.field_24641[m] >>> p << p | ((long)j & this.field_24643) >> o;
		}
	}

	public int method_28152(int i) {
		Validate.inclusiveBetween(0L, (long)(this.field_24644 - 1), (long)i);
		int j = i * this.field_24642;
		int k = j >> 6;
		int l = (i + 1) * this.field_24642 - 1 >> 6;
		int m = j ^ k << 6;
		if (k == l) {
			return (int)(this.field_24641[k] >>> m & this.field_24643);
		} else {
			int n = 64 - m;
			return (int)((this.field_24641[k] >>> m | this.field_24641[l] << n) & this.field_24643);
		}
	}

	public long[] method_28151() {
		return this.field_24641;
	}

	public int method_28154() {
		return this.field_24642;
	}
}
