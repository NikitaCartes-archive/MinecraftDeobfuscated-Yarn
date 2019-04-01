package net.minecraft;

public class class_1409 extends class_1408 {
	private boolean field_6686;

	public class_1409(class_1308 arg, class_1937 arg2) {
		super(arg, arg2);
	}

	@Override
	protected class_13 method_6336(int i) {
		this.field_6678 = new class_14();
		this.field_6678.method_15(true);
		return new class_13(this.field_6678, i);
	}

	@Override
	protected boolean method_6358() {
		return this.field_6684.field_5952 || this.method_6351() || this.field_6684.method_5765();
	}

	@Override
	protected class_243 method_6347() {
		return new class_243(this.field_6684.field_5987, (double)this.method_6362(), this.field_6684.field_6035);
	}

	@Override
	public class_11 method_6348(class_2338 arg) {
		if (this.field_6677.method_8320(arg).method_11588()) {
			class_2338 lv = arg.method_10074();

			while (lv.method_10264() > 0 && this.field_6677.method_8320(lv).method_11588()) {
				lv = lv.method_10074();
			}

			if (lv.method_10264() > 0) {
				return super.method_6348(lv.method_10084());
			}

			while (lv.method_10264() < this.field_6677.method_8322() && this.field_6677.method_8320(lv).method_11588()) {
				lv = lv.method_10084();
			}

			arg = lv;
		}

		if (!this.field_6677.method_8320(arg).method_11620().method_15799()) {
			return super.method_6348(arg);
		} else {
			class_2338 lv = arg.method_10084();

			while (lv.method_10264() < this.field_6677.method_8322() && this.field_6677.method_8320(lv).method_11620().method_15799()) {
				lv = lv.method_10084();
			}

			return super.method_6348(lv);
		}
	}

	@Override
	public class_11 method_6349(class_1297 arg) {
		return this.method_6348(new class_2338(arg));
	}

	private int method_6362() {
		if (this.field_6684.method_5799() && this.method_6350()) {
			int i = (int)this.field_6684.method_5829().field_1322;
			class_2248 lv = this.field_6677
				.method_8320(new class_2338(class_3532.method_15357(this.field_6684.field_5987), i, class_3532.method_15357(this.field_6684.field_6035)))
				.method_11614();
			int j = 0;

			while (lv == class_2246.field_10382) {
				lv = this.field_6677
					.method_8320(new class_2338(class_3532.method_15357(this.field_6684.field_5987), ++i, class_3532.method_15357(this.field_6684.field_6035)))
					.method_11614();
				if (++j > 16) {
					return (int)this.field_6684.method_5829().field_1322;
				}
			}

			return i;
		} else {
			return (int)(this.field_6684.method_5829().field_1322 + 0.5);
		}
	}

	@Override
	protected void method_6359() {
		super.method_6359();
		if (this.field_6686) {
			if (this.field_6677
				.method_8311(
					new class_2338(
						class_3532.method_15357(this.field_6684.field_5987),
						(int)(this.field_6684.method_5829().field_1322 + 0.5),
						class_3532.method_15357(this.field_6684.field_6035)
					)
				)) {
				return;
			}

			for (int i = 0; i < this.field_6681.method_38(); i++) {
				class_9 lv = this.field_6681.method_40(i);
				if (this.field_6677.method_8311(new class_2338(lv.field_40, lv.field_39, lv.field_38))) {
					this.field_6681.method_36(i - 1);
					return;
				}
			}
		}
	}

	@Override
	protected boolean method_6341(class_243 arg, class_243 arg2, int i, int j, int k) {
		int l = class_3532.method_15357(arg.field_1352);
		int m = class_3532.method_15357(arg.field_1350);
		double d = arg2.field_1352 - arg.field_1352;
		double e = arg2.field_1350 - arg.field_1350;
		double f = d * d + e * e;
		if (f < 1.0E-8) {
			return false;
		} else {
			double g = 1.0 / Math.sqrt(f);
			d *= g;
			e *= g;
			i += 2;
			k += 2;
			if (!this.method_6364(l, (int)arg.field_1351, m, i, j, k, arg, d, e)) {
				return false;
			} else {
				i -= 2;
				k -= 2;
				double h = 1.0 / Math.abs(d);
				double n = 1.0 / Math.abs(e);
				double o = (double)l - arg.field_1352;
				double p = (double)m - arg.field_1350;
				if (d >= 0.0) {
					o++;
				}

				if (e >= 0.0) {
					p++;
				}

				o /= d;
				p /= e;
				int q = d < 0.0 ? -1 : 1;
				int r = e < 0.0 ? -1 : 1;
				int s = class_3532.method_15357(arg2.field_1352);
				int t = class_3532.method_15357(arg2.field_1350);
				int u = s - l;
				int v = t - m;

				while (u * q > 0 || v * r > 0) {
					if (o < p) {
						o += h;
						l += q;
						u = s - l;
					} else {
						p += n;
						m += r;
						v = t - m;
					}

					if (!this.method_6364(l, (int)arg.field_1351, m, i, j, k, arg, d, e)) {
						return false;
					}
				}

				return true;
			}
		}
	}

	private boolean method_6364(int i, int j, int k, int l, int m, int n, class_243 arg, double d, double e) {
		int o = i - l / 2;
		int p = k - n / 2;
		if (!this.method_6367(o, j, p, l, m, n, arg, d, e)) {
			return false;
		} else {
			for (int q = o; q < o + l; q++) {
				for (int r = p; r < p + n; r++) {
					double f = (double)q + 0.5 - arg.field_1352;
					double g = (double)r + 0.5 - arg.field_1350;
					if (!(f * d + g * e < 0.0)) {
						class_7 lv = this.field_6678.method_17(this.field_6677, q, j - 1, r, this.field_6684, l, m, n, true, true);
						if (lv == class_7.field_18) {
							return false;
						}

						if (lv == class_7.field_14) {
							return false;
						}

						if (lv == class_7.field_7) {
							return false;
						}

						lv = this.field_6678.method_17(this.field_6677, q, j, r, this.field_6684, l, m, n, true, true);
						float h = this.field_6684.method_5944(lv);
						if (h < 0.0F || h >= 8.0F) {
							return false;
						}

						if (lv == class_7.field_3 || lv == class_7.field_9 || lv == class_7.field_17) {
							return false;
						}
					}
				}
			}

			return true;
		}
	}

	private boolean method_6367(int i, int j, int k, int l, int m, int n, class_243 arg, double d, double e) {
		for (class_2338 lv : class_2338.method_10097(new class_2338(i, j, k), new class_2338(i + l - 1, j + m - 1, k + n - 1))) {
			double f = (double)lv.method_10263() + 0.5 - arg.field_1352;
			double g = (double)lv.method_10260() + 0.5 - arg.field_1350;
			if (!(f * d + g * e < 0.0) && !this.field_6677.method_8320(lv).method_11609(this.field_6677, lv, class_10.field_50)) {
				return false;
			}
		}

		return true;
	}

	public void method_6363(boolean bl) {
		this.field_6678.method_20(bl);
	}

	public boolean method_6366() {
		return this.field_6678.method_23();
	}

	public void method_6361(boolean bl) {
		this.field_6686 = bl;
	}
}
