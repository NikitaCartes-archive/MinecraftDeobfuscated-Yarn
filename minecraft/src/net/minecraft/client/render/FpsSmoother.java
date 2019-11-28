package net.minecraft.client.render;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class FpsSmoother {
	private final long[] times;
	private int size;
	private int index;

	public FpsSmoother(int size) {
		this.times = new long[size];
	}

	public long getTargetUsedTime(long time) {
		if (this.size < this.times.length) {
			this.size++;
		}

		this.times[this.index] = time;
		this.index = (this.index + 1) % this.times.length;
		long l = Long.MAX_VALUE;
		long m = Long.MIN_VALUE;
		long n = 0L;

		for (int i = 0; i < this.size; i++) {
			long o = this.times[i];
			n += o;
			l = Math.min(l, o);
			m = Math.max(m, o);
		}

		if (this.size > 2) {
			n -= l + m;
			return n / (long)(this.size - 2);
		} else {
			return n > 0L ? (long)this.size / n : 0L;
		}
	}
}
