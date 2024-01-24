package net.minecraft.util.profiler;

public class PerformanceLog {
	public static final int SIZE = 240;
	private final long[][] data;
	private int currentIndex;
	private int maxIndex;

	public PerformanceLog(int columns) {
		this.data = new long[240][columns];
	}

	public void push(long value) {
		int i = this.wrap(this.currentIndex + this.maxIndex);
		this.data[i][0] = value;
		if (this.maxIndex < 240) {
			this.maxIndex++;
		} else {
			this.currentIndex = this.wrap(this.currentIndex + 1);
		}
	}

	public void push(long value, int column) {
		int i = this.wrap(this.currentIndex + this.maxIndex);
		long[] ls = this.data[i];
		if (column >= 1 && column < ls.length) {
			ls[column] = value;
		} else {
			throw new IndexOutOfBoundsException(column + " out of bounds for dimensions " + ls.length);
		}
	}

	public int size() {
		return this.data.length;
	}

	public int getMaxIndex() {
		return this.maxIndex;
	}

	public long get(int index) {
		return this.get(index, 0);
	}

	public long get(int index, int column) {
		if (index >= 0 && index < this.maxIndex) {
			long[] ls = this.data[this.wrap(this.currentIndex + index)];
			if (column >= 0 && column < ls.length) {
				return ls[column];
			} else {
				throw new IndexOutOfBoundsException(column + " out of bounds for dimensions " + ls.length);
			}
		} else {
			throw new IndexOutOfBoundsException(index + " out of bounds for length " + this.maxIndex);
		}
	}

	private int wrap(int index) {
		return index % 240;
	}

	public void reset() {
		this.currentIndex = 0;
		this.maxIndex = 0;
	}
}
