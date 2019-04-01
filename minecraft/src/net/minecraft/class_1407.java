package net.minecraft;

public class class_1407 extends class_1408 {
	public class_1407(class_1308 arg, class_1937 arg2) {
		super(arg, arg2);
	}

	@Override
	protected class_13 method_6336(int i) {
		this.field_6678 = new class_6();
		this.field_6678.method_15(true);
		return new class_13(this.field_6678, i);
	}

	@Override
	protected boolean method_6358() {
		return this.method_6350() && this.method_6351() || !this.field_6684.method_5765();
	}

	@Override
	protected class_243 method_6347() {
		return new class_243(this.field_6684.field_5987, this.field_6684.field_6010, this.field_6684.field_6035);
	}

	@Override
	public class_11 method_6349(class_1297 arg) {
		return this.method_6348(new class_2338(arg));
	}

	@Override
	public void method_6360() {
		this.field_6675++;
		if (this.field_6679) {
			this.method_6356();
		}

		if (!this.method_6357()) {
			if (this.method_6358()) {
				this.method_6339();
			} else if (this.field_6681 != null && this.field_6681.method_39() < this.field_6681.method_38()) {
				class_243 lv = this.field_6681.method_47(this.field_6684, this.field_6681.method_39());
				if (class_3532.method_15357(this.field_6684.field_5987) == class_3532.method_15357(lv.field_1352)
					&& class_3532.method_15357(this.field_6684.field_6010) == class_3532.method_15357(lv.field_1351)
					&& class_3532.method_15357(this.field_6684.field_6035) == class_3532.method_15357(lv.field_1350)) {
					this.field_6681.method_42(this.field_6681.method_39() + 1);
				}
			}

			class_4209.method_19470(this.field_6677, this.field_6684, this.field_6681, this.field_6683);
			if (!this.method_6357()) {
				class_243 lv = this.field_6681.method_49(this.field_6684);
				this.field_6684.method_5962().method_6239(lv.field_1352, lv.field_1351, lv.field_1350, this.field_6668);
			}
		}
	}

	@Override
	protected boolean method_6341(class_243 arg, class_243 arg2, int i, int j, int k) {
		int l = class_3532.method_15357(arg.field_1352);
		int m = class_3532.method_15357(arg.field_1351);
		int n = class_3532.method_15357(arg.field_1350);
		double d = arg2.field_1352 - arg.field_1352;
		double e = arg2.field_1351 - arg.field_1351;
		double f = arg2.field_1350 - arg.field_1350;
		double g = d * d + e * e + f * f;
		if (g < 1.0E-8) {
			return false;
		} else {
			double h = 1.0 / Math.sqrt(g);
			d *= h;
			e *= h;
			f *= h;
			double o = 1.0 / Math.abs(d);
			double p = 1.0 / Math.abs(e);
			double q = 1.0 / Math.abs(f);
			double r = (double)l - arg.field_1352;
			double s = (double)m - arg.field_1351;
			double t = (double)n - arg.field_1350;
			if (d >= 0.0) {
				r++;
			}

			if (e >= 0.0) {
				s++;
			}

			if (f >= 0.0) {
				t++;
			}

			r /= d;
			s /= e;
			t /= f;
			int u = d < 0.0 ? -1 : 1;
			int v = e < 0.0 ? -1 : 1;
			int w = f < 0.0 ? -1 : 1;
			int x = class_3532.method_15357(arg2.field_1352);
			int y = class_3532.method_15357(arg2.field_1351);
			int z = class_3532.method_15357(arg2.field_1350);
			int aa = x - l;
			int ab = y - m;
			int ac = z - n;

			while (aa * u > 0 || ab * v > 0 || ac * w > 0) {
				if (r < t && r <= s) {
					r += o;
					l += u;
					aa = x - l;
				} else if (s < r && s <= t) {
					s += p;
					m += v;
					ab = y - m;
				} else {
					t += q;
					n += w;
					ac = z - n;
				}
			}

			return true;
		}
	}

	public void method_6332(boolean bl) {
		this.field_6678.method_20(bl);
	}

	public void method_6331(boolean bl) {
		this.field_6678.method_15(bl);
	}

	@Override
	public boolean method_6333(class_2338 arg) {
		return this.field_6677.method_8320(arg).method_11631(this.field_6677, arg, this.field_6684);
	}
}
