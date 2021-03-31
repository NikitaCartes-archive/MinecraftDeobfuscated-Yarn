package net.minecraft.util;

public class MetricsData {
	public static final int field_29839 = 240;
	private final long[] samples = new long[240];
	private int startIndex;
	private int sampleCount;
	private int writeIndex;

	public void pushSample(long time) {
		this.samples[this.writeIndex] = time;
		this.writeIndex++;
		if (this.writeIndex == 240) {
			this.writeIndex = 0;
		}

		if (this.sampleCount < 240) {
			this.startIndex = 0;
			this.sampleCount++;
		} else {
			this.startIndex = this.wrapIndex(this.writeIndex + 1);
		}
	}

	public long method_34912(int i) {
		int j = (this.startIndex + i) % 240;
		int k = this.startIndex;

		long l;
		for (l = 0L; k != j; k++) {
			l += this.samples[k];
		}

		return l / (long)i;
	}

	public int method_34913(int i, int j) {
		return this.method_15248(this.method_34912(i), j, 60);
	}

	public int method_15248(long l, int i, int j) {
		double d = (double)l / (double)(1000000000L / (long)j);
		return (int)(d * (double)i);
	}

	public int getStartIndex() {
		return this.startIndex;
	}

	public int getCurrentIndex() {
		return this.writeIndex;
	}

	public int wrapIndex(int index) {
		return index % 240;
	}

	public long[] getSamples() {
		return this.samples;
	}
}
