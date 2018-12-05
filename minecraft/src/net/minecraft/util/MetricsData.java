package net.minecraft.util;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

public class MetricsData {
	private final long[] samples = new long[240];
	private int field_15656;
	private int sampleCount;
	private int writeIndex;

	public void pushSample(long l) {
		this.samples[this.writeIndex] = l;
		this.writeIndex++;
		if (this.writeIndex == 240) {
			this.writeIndex = 0;
		}

		if (this.sampleCount < 240) {
			this.field_15656 = 0;
			this.sampleCount++;
		} else {
			this.field_15656 = this.method_15251(this.writeIndex + 1);
		}
	}

	@Environment(EnvType.CLIENT)
	public int method_15248(long l, int i, int j) {
		double d = (double)l / (double)(1000000000L / (long)j);
		return (int)(d * (double)i);
	}

	@Environment(EnvType.CLIENT)
	public int method_15249() {
		return this.field_15656;
	}

	@Environment(EnvType.CLIENT)
	public int getCurrentIndex() {
		return this.writeIndex;
	}

	public int method_15251(int i) {
		return i % 240;
	}

	@Environment(EnvType.CLIENT)
	public long[] getSamples() {
		return this.samples;
	}
}
