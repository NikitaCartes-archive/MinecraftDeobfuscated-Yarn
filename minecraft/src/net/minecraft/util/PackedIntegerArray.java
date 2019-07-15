package net.minecraft.util;

import java.util.function.IntConsumer;
import net.minecraft.util.math.MathHelper;
import org.apache.commons.lang3.Validate;

public class PackedIntegerArray {
	private final long[] storage;
	private final int elementBits;
	private final long maxValue;
	private final int size;

	public PackedIntegerArray(int i, int j) {
		this(i, j, new long[MathHelper.roundUp(j * i, 64) / 64]);
	}

	public PackedIntegerArray(int i, int j, long[] ls) {
		Validate.inclusiveBetween(1L, 32L, (long)i);
		this.size = j;
		this.elementBits = i;
		this.storage = ls;
		this.maxValue = (1L << i) - 1L;
		int k = MathHelper.roundUp(j * i, 64) / 64;
		if (ls.length != k) {
			throw new RuntimeException("Invalid length given for storage, got: " + ls.length + " but expected: " + k);
		}
	}

	public int setAndGetOldValue(int i, int j) {
		Validate.inclusiveBetween(0L, (long)(this.size - 1), (long)i);
		Validate.inclusiveBetween(0L, this.maxValue, (long)j);
		int k = i * this.elementBits;
		int l = k >> 6;
		int m = (i + 1) * this.elementBits - 1 >> 6;
		int n = k ^ l << 6;
		int o = 0;
		o |= (int)(this.storage[l] >>> n & this.maxValue);
		this.storage[l] = this.storage[l] & ~(this.maxValue << n) | ((long)j & this.maxValue) << n;
		if (l != m) {
			int p = 64 - n;
			int q = this.elementBits - p;
			o |= (int)(this.storage[m] << p & this.maxValue);
			this.storage[m] = this.storage[m] >>> q << q | ((long)j & this.maxValue) >> p;
		}

		return o;
	}

	public void set(int i, int j) {
		Validate.inclusiveBetween(0L, (long)(this.size - 1), (long)i);
		Validate.inclusiveBetween(0L, this.maxValue, (long)j);
		int k = i * this.elementBits;
		int l = k >> 6;
		int m = (i + 1) * this.elementBits - 1 >> 6;
		int n = k ^ l << 6;
		this.storage[l] = this.storage[l] & ~(this.maxValue << n) | ((long)j & this.maxValue) << n;
		if (l != m) {
			int o = 64 - n;
			int p = this.elementBits - o;
			this.storage[m] = this.storage[m] >>> p << p | ((long)j & this.maxValue) >> o;
		}
	}

	public int get(int i) {
		Validate.inclusiveBetween(0L, (long)(this.size - 1), (long)i);
		int j = i * this.elementBits;
		int k = j >> 6;
		int l = (i + 1) * this.elementBits - 1 >> 6;
		int m = j ^ k << 6;
		if (k == l) {
			return (int)(this.storage[k] >>> m & this.maxValue);
		} else {
			int n = 64 - m;
			return (int)((this.storage[k] >>> m | this.storage[l] << n) & this.maxValue);
		}
	}

	public long[] getStorage() {
		return this.storage;
	}

	public int getSize() {
		return this.size;
	}

	public int getElementBits() {
		return this.elementBits;
	}

	public void method_21739(IntConsumer intConsumer) {
		int i = this.storage.length;
		if (i != 0) {
			int j = 0;
			long l = this.storage[0];
			long m = i > 1 ? this.storage[1] : 0L;

			for (int k = 0; k < this.size; k++) {
				int n = k * this.elementBits;
				int o = n >> 6;
				int p = (k + 1) * this.elementBits - 1 >> 6;
				int q = n ^ o << 6;
				if (o != j) {
					l = m;
					m = o + 1 < i ? this.storage[o + 1] : 0L;
					j = o;
				}

				if (o == p) {
					intConsumer.accept((int)(l >>> q & this.maxValue));
				} else {
					int r = 64 - q;
					intConsumer.accept((int)((l >>> q | m << r) & this.maxValue));
				}
			}
		}
	}
}
