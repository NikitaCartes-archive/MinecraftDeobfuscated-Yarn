package net.minecraft;

import org.apache.commons.lang3.Validate;

public class class_3508 {
	private final long[] field_15631;
	private final int field_15633;
	private final long field_15634;
	private final int field_15632;

	public class_3508(int i, int j) {
		this(i, j, new long[class_3532.method_15359(j * i, 64) / 64]);
	}

	public class_3508(int i, int j, long[] ls) {
		Validate.inclusiveBetween(1L, 32L, (long)i);
		this.field_15632 = j;
		this.field_15633 = i;
		this.field_15631 = ls;
		this.field_15634 = (1L << i) - 1L;
		int k = class_3532.method_15359(j * i, 64) / 64;
		if (ls.length != k) {
			throw new RuntimeException("Invalid length given for storage, got: " + ls.length + " but expected: " + k);
		}
	}

	public int method_15214(int i, int j) {
		Validate.inclusiveBetween(0L, (long)(this.field_15632 - 1), (long)i);
		Validate.inclusiveBetween(0L, this.field_15634, (long)j);
		int k = i * this.field_15633;
		int l = k >> 6;
		int m = (i + 1) * this.field_15633 - 1 >> 6;
		int n = k ^ l << 6;
		int o = 0;
		o |= (int)(this.field_15631[l] >>> n & this.field_15634);
		this.field_15631[l] = this.field_15631[l] & ~(this.field_15634 << n) | ((long)j & this.field_15634) << n;
		if (l != m) {
			int p = 64 - n;
			int q = this.field_15633 - p;
			o |= (int)(this.field_15631[m] << p & this.field_15634);
			this.field_15631[m] = this.field_15631[m] >>> q << q | ((long)j & this.field_15634) >> p;
		}

		return o;
	}

	public void method_15210(int i, int j) {
		Validate.inclusiveBetween(0L, (long)(this.field_15632 - 1), (long)i);
		Validate.inclusiveBetween(0L, this.field_15634, (long)j);
		int k = i * this.field_15633;
		int l = k >> 6;
		int m = (i + 1) * this.field_15633 - 1 >> 6;
		int n = k ^ l << 6;
		this.field_15631[l] = this.field_15631[l] & ~(this.field_15634 << n) | ((long)j & this.field_15634) << n;
		if (l != m) {
			int o = 64 - n;
			int p = this.field_15633 - o;
			this.field_15631[m] = this.field_15631[m] >>> p << p | ((long)j & this.field_15634) >> o;
		}
	}

	public int method_15211(int i) {
		Validate.inclusiveBetween(0L, (long)(this.field_15632 - 1), (long)i);
		int j = i * this.field_15633;
		int k = j >> 6;
		int l = (i + 1) * this.field_15633 - 1 >> 6;
		int m = j ^ k << 6;
		if (k == l) {
			return (int)(this.field_15631[k] >>> m & this.field_15634);
		} else {
			int n = 64 - m;
			return (int)((this.field_15631[k] >>> m | this.field_15631[l] << n) & this.field_15634);
		}
	}

	public long[] method_15212() {
		return this.field_15631;
	}

	public int method_15215() {
		return this.field_15632;
	}

	public int method_15213() {
		return this.field_15633;
	}
}
