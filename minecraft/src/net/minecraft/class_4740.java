package net.minecraft;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class class_4740 {
	private final long[] field_21800;
	private int field_21801;
	private int field_21802;

	public class_4740(int i) {
		this.field_21800 = new long[i];
	}

	public long method_24214(long l) {
		if (this.field_21801 < this.field_21800.length) {
			this.field_21801++;
		}

		this.field_21800[this.field_21802] = l;
		this.field_21802 = (this.field_21802 + 1) % this.field_21800.length;
		long m = Long.MAX_VALUE;
		long n = Long.MIN_VALUE;
		long o = 0L;

		for (int i = 0; i < this.field_21801; i++) {
			long p = this.field_21800[i];
			o += p;
			m = Math.min(m, p);
			n = Math.max(n, p);
		}

		if (this.field_21801 > 2) {
			o -= m + n;
			return o / (long)(this.field_21801 - 2);
		} else {
			return o > 0L ? (long)this.field_21801 / o : 0L;
		}
	}
}
