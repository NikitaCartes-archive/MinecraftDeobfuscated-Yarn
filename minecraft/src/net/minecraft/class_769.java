package net.minecraft;

import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class class_769 {
	protected final class_761 field_4146;
	protected final class_1937 field_4151;
	protected int field_4149;
	protected int field_4148;
	protected int field_4147;
	public class_851[] field_4150;

	public class_769(class_1937 arg, int i, class_761 arg2, class_850 arg3) {
		this.field_4146 = arg2;
		this.field_4151 = arg;
		this.method_3325(i);
		this.method_3324(arg3);
	}

	protected void method_3324(class_850 arg) {
		int i = this.field_4148 * this.field_4149 * this.field_4147;
		this.field_4150 = new class_851[i];

		for (int j = 0; j < this.field_4148; j++) {
			for (int k = 0; k < this.field_4149; k++) {
				for (int l = 0; l < this.field_4147; l++) {
					int m = this.method_3326(j, k, l);
					this.field_4150[m] = arg.create(this.field_4151, this.field_4146);
					this.field_4150[m].method_3653(j * 16, k * 16, l * 16);
				}
			}
		}
	}

	public void method_3327() {
		for (class_851 lv : this.field_4150) {
			lv.method_3659();
		}
	}

	private int method_3326(int i, int j, int k) {
		return (k * this.field_4149 + j) * this.field_4148 + i;
	}

	protected void method_3325(int i) {
		int j = i * 2 + 1;
		this.field_4148 = j;
		this.field_4149 = 16;
		this.field_4147 = j;
	}

	public void method_3330(double d, double e) {
		int i = class_3532.method_15357(d) - 8;
		int j = class_3532.method_15357(e) - 8;
		int k = this.field_4148 * 16;

		for (int l = 0; l < this.field_4148; l++) {
			int m = this.method_3328(i, k, l);

			for (int n = 0; n < this.field_4147; n++) {
				int o = this.method_3328(j, k, n);

				for (int p = 0; p < this.field_4149; p++) {
					int q = p * 16;
					class_851 lv = this.field_4150[this.method_3326(l, p, n)];
					lv.method_3653(m, q, o);
				}
			}
		}
	}

	private int method_3328(int i, int j, int k) {
		int l = k * 16;
		int m = l - i + j / 2;
		if (m < 0) {
			m -= j - 1;
		}

		return l - m / j * j;
	}

	public void method_16040(int i, int j, int k, boolean bl) {
		int l = Math.floorMod(i, this.field_4148);
		int m = Math.floorMod(j, this.field_4149);
		int n = Math.floorMod(k, this.field_4147);
		class_851 lv = this.field_4150[this.method_3326(l, m, n)];
		lv.method_3654(bl);
	}

	@Nullable
	protected class_851 method_3323(class_2338 arg) {
		int i = class_3532.method_15346(arg.method_10263(), 16);
		int j = class_3532.method_15346(arg.method_10264(), 16);
		int k = class_3532.method_15346(arg.method_10260(), 16);
		if (j >= 0 && j < this.field_4149) {
			i = class_3532.method_15387(i, this.field_4148);
			k = class_3532.method_15387(k, this.field_4147);
			return this.field_4150[this.method_3326(i, j, k)];
		} else {
			return null;
		}
	}
}
