package net.minecraft;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
enum class_453 {
	field_2678(0, 0, 28, 32, 8),
	field_2673(84, 0, 28, 32, 8),
	field_2675(0, 64, 32, 28, 5),
	field_2677(96, 64, 32, 28, 5);

	private final int field_2674;
	private final int field_2672;
	private final int field_2671;
	private final int field_2670;
	private final int field_2669;

	private class_453(int j, int k, int l, int m, int n) {
		this.field_2674 = j;
		this.field_2672 = k;
		this.field_2671 = l;
		this.field_2670 = m;
		this.field_2669 = n;
	}

	public int method_2304() {
		return this.field_2669;
	}

	public void method_2301(class_332 arg, int i, int j, boolean bl, int k) {
		int l = this.field_2674;
		if (k > 0) {
			l += this.field_2671;
		}

		if (k == this.field_2669 - 1) {
			l += this.field_2671;
		}

		int m = bl ? this.field_2672 + this.field_2670 : this.field_2672;
		arg.method_1788(i + this.method_2302(k), j + this.method_2305(k), l, m, this.field_2671, this.field_2670);
	}

	public void method_2306(int i, int j, int k, class_918 arg, class_1799 arg2) {
		int l = i + this.method_2302(k);
		int m = j + this.method_2305(k);
		switch (this) {
			case field_2678:
				l += 6;
				m += 9;
				break;
			case field_2673:
				l += 6;
				m += 6;
				break;
			case field_2675:
				l += 10;
				m += 5;
				break;
			case field_2677:
				l += 6;
				m += 5;
		}

		arg.method_4026(null, arg2, l, m);
	}

	public int method_2302(int i) {
		switch (this) {
			case field_2678:
				return (this.field_2671 + 4) * i;
			case field_2673:
				return (this.field_2671 + 4) * i;
			case field_2675:
				return -this.field_2671 + 4;
			case field_2677:
				return 248;
			default:
				throw new UnsupportedOperationException("Don't know what this tab type is!" + this);
		}
	}

	public int method_2305(int i) {
		switch (this) {
			case field_2678:
				return -this.field_2670 + 4;
			case field_2673:
				return 136;
			case field_2675:
				return this.field_2670 * i;
			case field_2677:
				return this.field_2670 * i;
			default:
				throw new UnsupportedOperationException("Don't know what this tab type is!" + this);
		}
	}

	public boolean method_2303(int i, int j, int k, double d, double e) {
		int l = i + this.method_2302(k);
		int m = j + this.method_2305(k);
		return d > (double)l && d < (double)(l + this.field_2671) && e > (double)m && e < (double)(m + this.field_2670);
	}
}
