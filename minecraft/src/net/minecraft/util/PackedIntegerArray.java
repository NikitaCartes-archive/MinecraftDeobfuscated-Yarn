package net.minecraft.util;

import java.util.function.IntConsumer;
import net.minecraft.util.math.MathHelper;
import org.apache.commons.lang3.Validate;

public class PackedIntegerArray {
	private final long[] storage;
	private final int elementBits;
	private final long maxValue;
	private final int size;

	public PackedIntegerArray(int elementBits, int size) {
		this(elementBits, size, new long[MathHelper.roundUp(size * elementBits, 64) / 64]);
	}

	public PackedIntegerArray(int elementBits, int size, long[] storage) {
		Validate.inclusiveBetween(1L, 32L, (long)elementBits);
		this.size = size;
		this.elementBits = elementBits;
		this.storage = storage;
		this.maxValue = (1L << elementBits) - 1L;
		int i = MathHelper.roundUp(size * elementBits, 64) / 64;
		if (storage.length != i) {
			throw (RuntimeException)Util.throwOrPause(new RuntimeException("Invalid length given for storage, got: " + storage.length + " but expected: " + i));
		}
	}

	public int setAndGetOldValue(int index, int value) {
		Validate.inclusiveBetween(0L, (long)(this.size - 1), (long)index);
		Validate.inclusiveBetween(0L, this.maxValue, (long)value);
		int i = index * this.elementBits;
		int j = i >> 6;
		int k = (index + 1) * this.elementBits - 1 >> 6;
		int l = i ^ j << 6;
		int m = 0;
		m |= (int)(this.storage[j] >>> l & this.maxValue);
		this.storage[j] = this.storage[j] & ~(this.maxValue << l) | ((long)value & this.maxValue) << l;
		if (j != k) {
			int n = 64 - l;
			int o = this.elementBits - n;
			m |= (int)(this.storage[k] << n & this.maxValue);
			this.storage[k] = this.storage[k] >>> o << o | ((long)value & this.maxValue) >> n;
		}

		return m;
	}

	public void set(int index, int value) {
		Validate.inclusiveBetween(0L, (long)(this.size - 1), (long)index);
		Validate.inclusiveBetween(0L, this.maxValue, (long)value);
		int i = index * this.elementBits;
		int j = i >> 6;
		int k = (index + 1) * this.elementBits - 1 >> 6;
		int l = i ^ j << 6;
		this.storage[j] = this.storage[j] & ~(this.maxValue << l) | ((long)value & this.maxValue) << l;
		if (j != k) {
			int m = 64 - l;
			int n = this.elementBits - m;
			this.storage[k] = this.storage[k] >>> n << n | ((long)value & this.maxValue) >> m;
		}
	}

	public int get(int index) {
		Validate.inclusiveBetween(0L, (long)(this.size - 1), (long)index);
		int i = index * this.elementBits;
		int j = i >> 6;
		int k = (index + 1) * this.elementBits - 1 >> 6;
		int l = i ^ j << 6;
		if (j == k) {
			return (int)(this.storage[j] >>> l & this.maxValue);
		} else {
			int m = 64 - l;
			return (int)((this.storage[j] >>> l | this.storage[k] << m) & this.maxValue);
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

	public void forEach(IntConsumer consumer) {
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
					consumer.accept((int)(l >>> q & this.maxValue));
				} else {
					int r = 64 - q;
					consumer.accept((int)((l >>> q | m << r) & this.maxValue));
				}
			}
		}
	}
}
