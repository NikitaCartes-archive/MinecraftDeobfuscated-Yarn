package net.minecraft.util.profiler;

public class PerformanceLog {
	public static final int SIZE = 240;
	private final long[] data = new long[240];
	private int currentIndex;
	private int maxIndex;

	public void push(long value) {
		int i = this.wrap(this.currentIndex + this.maxIndex);
		this.data[i] = value;
		if (this.maxIndex < 240) {
			this.maxIndex++;
		} else {
			this.currentIndex = this.wrap(this.currentIndex + 1);
		}
	}

	public int size() {
		return this.data.length;
	}

	public int getMaxIndex() {
		return this.maxIndex;
	}

	public long get(int index) {
		if (index >= 0 && index < this.maxIndex) {
			return this.data[this.wrap(this.currentIndex + index)];
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
