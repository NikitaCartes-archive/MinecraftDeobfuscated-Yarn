package net.minecraft;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

public class class_3517 {
	private final long[] field_15653 = new long[240];
	private int field_15656;
	private int field_15655;
	private int field_15654;

	public void method_15247(long l) {
		this.field_15653[this.field_15654] = l;
		this.field_15654++;
		if (this.field_15654 == 240) {
			this.field_15654 = 0;
		}

		if (this.field_15655 < 240) {
			this.field_15656 = 0;
			this.field_15655++;
		} else {
			this.field_15656 = this.method_15251(this.field_15654 + 1);
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
	public int method_15250() {
		return this.field_15654;
	}

	public int method_15251(int i) {
		return i % 240;
	}

	@Environment(EnvType.CLIENT)
	public long[] method_15246() {
		return this.field_15653;
	}
}
