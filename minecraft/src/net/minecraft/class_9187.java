package net.minecraft;

import net.minecraft.util.profiler.PerformanceLog;

public abstract class class_9187 implements PerformanceLog {
	protected final long[] defaultArray;
	protected final long[] array;

	protected class_9187(int size, long[] def) {
		if (def.length != size) {
			throw new IllegalArgumentException("defaults have incorrect length of " + def.length);
		} else {
			this.array = new long[size];
			this.defaultArray = def;
		}
	}

	@Override
	public void method_56650(long[] ls) {
		System.arraycopy(ls, 0, this.array, 0, ls.length);
		this.method_56649();
		this.method_56651();
	}

	@Override
	public void push(long value) {
		this.array[0] = value;
		this.method_56649();
		this.method_56651();
	}

	@Override
	public void push(long value, int column) {
		if (column >= 1 && column < this.array.length) {
			this.array[column] = value;
		} else {
			throw new IndexOutOfBoundsException(column + " out of bounds for dimensions " + this.array.length);
		}
	}

	protected abstract void method_56649();

	protected void method_56651() {
		System.arraycopy(this.defaultArray, 0, this.array, 0, this.defaultArray.length);
	}
}
