package net.minecraft;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

public abstract class class_251 {
	private static final class_2350.class_2351[] field_1375 = class_2350.class_2351.values();
	protected final int field_1374;
	protected final int field_1373;
	protected final int field_1372;

	protected class_251(int i, int j, int k) {
		this.field_1374 = i;
		this.field_1373 = j;
		this.field_1372 = k;
	}

	public boolean method_1062(class_2335 arg, int i, int j, int k) {
		return this.method_1044(
			arg.method_10056(i, j, k, class_2350.class_2351.field_11048),
			arg.method_10056(i, j, k, class_2350.class_2351.field_11052),
			arg.method_10056(i, j, k, class_2350.class_2351.field_11051)
		);
	}

	public boolean method_1044(int i, int j, int k) {
		if (i < 0 || j < 0 || k < 0) {
			return false;
		} else {
			return i < this.field_1374 && j < this.field_1373 && k < this.field_1372 ? this.method_1063(i, j, k) : false;
		}
	}

	public boolean method_1057(class_2335 arg, int i, int j, int k) {
		return this.method_1063(
			arg.method_10056(i, j, k, class_2350.class_2351.field_11048),
			arg.method_10056(i, j, k, class_2350.class_2351.field_11052),
			arg.method_10056(i, j, k, class_2350.class_2351.field_11051)
		);
	}

	public abstract boolean method_1063(int i, int j, int k);

	public abstract void method_1049(int i, int j, int k, boolean bl, boolean bl2);

	public boolean method_1056() {
		for (class_2350.class_2351 lv : field_1375) {
			if (this.method_1055(lv) >= this.method_1045(lv)) {
				return true;
			}
		}

		return false;
	}

	public abstract int method_1055(class_2350.class_2351 arg);

	public abstract int method_1045(class_2350.class_2351 arg);

	@Environment(EnvType.CLIENT)
	public int method_1043(class_2350.class_2351 arg, int i, int j) {
		int k = this.method_1051(arg);
		if (i >= 0 && j >= 0) {
			class_2350.class_2351 lv = class_2335.field_10963.method_10058(arg);
			class_2350.class_2351 lv2 = class_2335.field_10965.method_10058(arg);
			if (i < this.method_1051(lv) && j < this.method_1051(lv2)) {
				class_2335 lv3 = class_2335.method_10057(class_2350.class_2351.field_11048, arg);

				for (int l = 0; l < k; l++) {
					if (this.method_1057(lv3, l, i, j)) {
						return l;
					}
				}

				return k;
			} else {
				return k;
			}
		} else {
			return k;
		}
	}

	@Environment(EnvType.CLIENT)
	public int method_1058(class_2350.class_2351 arg, int i, int j) {
		if (i >= 0 && j >= 0) {
			class_2350.class_2351 lv = class_2335.field_10963.method_10058(arg);
			class_2350.class_2351 lv2 = class_2335.field_10965.method_10058(arg);
			if (i < this.method_1051(lv) && j < this.method_1051(lv2)) {
				int k = this.method_1051(arg);
				class_2335 lv3 = class_2335.method_10057(class_2350.class_2351.field_11048, arg);

				for (int l = k - 1; l >= 0; l--) {
					if (this.method_1057(lv3, l, i, j)) {
						return l + 1;
					}
				}

				return 0;
			} else {
				return 0;
			}
		} else {
			return 0;
		}
	}

	public int method_1051(class_2350.class_2351 arg) {
		return arg.method_10173(this.field_1374, this.field_1373, this.field_1372);
	}

	public int method_1050() {
		return this.method_1051(class_2350.class_2351.field_11048);
	}

	public int method_1047() {
		return this.method_1051(class_2350.class_2351.field_11052);
	}

	public int method_1048() {
		return this.method_1051(class_2350.class_2351.field_11051);
	}

	@Environment(EnvType.CLIENT)
	public void method_1064(class_251.class_253 arg, boolean bl) {
		this.method_1052(arg, class_2335.field_10962, bl);
		this.method_1052(arg, class_2335.field_10963, bl);
		this.method_1052(arg, class_2335.field_10965, bl);
	}

	@Environment(EnvType.CLIENT)
	private void method_1052(class_251.class_253 arg, class_2335 arg2, boolean bl) {
		class_2335 lv = arg2.method_10055();
		int i = this.method_1051(lv.method_10058(class_2350.class_2351.field_11048));
		int j = this.method_1051(lv.method_10058(class_2350.class_2351.field_11052));
		int k = this.method_1051(lv.method_10058(class_2350.class_2351.field_11051));

		for (int l = 0; l <= i; l++) {
			for (int m = 0; m <= j; m++) {
				int n = -1;

				for (int o = 0; o <= k; o++) {
					int p = 0;
					int q = 0;

					for (int r = 0; r <= 1; r++) {
						for (int s = 0; s <= 1; s++) {
							if (this.method_1062(lv, l + r - 1, m + s - 1, o)) {
								p++;
								q ^= r ^ s;
							}
						}
					}

					if (p == 1 || p == 3 || p == 2 && (q & 1) == 0) {
						if (bl) {
							if (n == -1) {
								n = o;
							}
						} else {
							arg.consume(
								lv.method_10056(l, m, o, class_2350.class_2351.field_11048),
								lv.method_10056(l, m, o, class_2350.class_2351.field_11052),
								lv.method_10056(l, m, o, class_2350.class_2351.field_11051),
								lv.method_10056(l, m, o + 1, class_2350.class_2351.field_11048),
								lv.method_10056(l, m, o + 1, class_2350.class_2351.field_11052),
								lv.method_10056(l, m, o + 1, class_2350.class_2351.field_11051)
							);
						}
					} else if (n != -1) {
						arg.consume(
							lv.method_10056(l, m, n, class_2350.class_2351.field_11048),
							lv.method_10056(l, m, n, class_2350.class_2351.field_11052),
							lv.method_10056(l, m, n, class_2350.class_2351.field_11051),
							lv.method_10056(l, m, o, class_2350.class_2351.field_11048),
							lv.method_10056(l, m, o, class_2350.class_2351.field_11052),
							lv.method_10056(l, m, o, class_2350.class_2351.field_11051)
						);
						n = -1;
					}
				}
			}
		}
	}

	protected boolean method_1059(int i, int j, int k, int l) {
		for (int m = i; m < j; m++) {
			if (!this.method_1044(k, l, m)) {
				return false;
			}
		}

		return true;
	}

	protected void method_1060(int i, int j, int k, int l, boolean bl) {
		for (int m = i; m < j; m++) {
			this.method_1049(k, l, m, false, bl);
		}
	}

	protected boolean method_1054(int i, int j, int k, int l, int m) {
		for (int n = i; n < j; n++) {
			if (!this.method_1059(k, l, n, m)) {
				return false;
			}
		}

		return true;
	}

	public void method_1053(class_251.class_253 arg, boolean bl) {
		class_251 lv = new class_244(this);

		for (int i = 0; i <= this.field_1374; i++) {
			for (int j = 0; j <= this.field_1373; j++) {
				int k = -1;

				for (int l = 0; l <= this.field_1372; l++) {
					if (lv.method_1044(i, j, l)) {
						if (bl) {
							if (k == -1) {
								k = l;
							}
						} else {
							arg.consume(i, j, l, i + 1, j + 1, l + 1);
						}
					} else if (k != -1) {
						int m = i;
						int n = i;
						int o = j;
						int p = j;
						lv.method_1060(k, l, i, j, false);

						while (lv.method_1059(k, l, m - 1, o)) {
							lv.method_1060(k, l, m - 1, o, false);
							m--;
						}

						while (lv.method_1059(k, l, n + 1, o)) {
							lv.method_1060(k, l, n + 1, o, false);
							n++;
						}

						while (lv.method_1054(m, n + 1, k, l, o - 1)) {
							for (int q = m; q <= n; q++) {
								lv.method_1060(k, l, q, o - 1, false);
							}

							o--;
						}

						while (lv.method_1054(m, n + 1, k, l, p + 1)) {
							for (int q = m; q <= n; q++) {
								lv.method_1060(k, l, q, p + 1, false);
							}

							p++;
						}

						arg.consume(m, o, k, n + 1, p + 1, l);
						k = -1;
					}
				}
			}
		}
	}

	public void method_1046(class_251.class_252 arg) {
		this.method_1061(arg, class_2335.field_10962);
		this.method_1061(arg, class_2335.field_10963);
		this.method_1061(arg, class_2335.field_10965);
	}

	private void method_1061(class_251.class_252 arg, class_2335 arg2) {
		class_2335 lv = arg2.method_10055();
		class_2350.class_2351 lv2 = lv.method_10058(class_2350.class_2351.field_11051);
		int i = this.method_1051(lv.method_10058(class_2350.class_2351.field_11048));
		int j = this.method_1051(lv.method_10058(class_2350.class_2351.field_11052));
		int k = this.method_1051(lv2);
		class_2350 lv3 = class_2350.method_10169(lv2, class_2350.class_2352.field_11060);
		class_2350 lv4 = class_2350.method_10169(lv2, class_2350.class_2352.field_11056);

		for (int l = 0; l < i; l++) {
			for (int m = 0; m < j; m++) {
				boolean bl = false;

				for (int n = 0; n <= k; n++) {
					boolean bl2 = n != k && this.method_1057(lv, l, m, n);
					if (!bl && bl2) {
						arg.consume(
							lv3,
							lv.method_10056(l, m, n, class_2350.class_2351.field_11048),
							lv.method_10056(l, m, n, class_2350.class_2351.field_11052),
							lv.method_10056(l, m, n, class_2350.class_2351.field_11051)
						);
					}

					if (bl && !bl2) {
						arg.consume(
							lv4,
							lv.method_10056(l, m, n - 1, class_2350.class_2351.field_11048),
							lv.method_10056(l, m, n - 1, class_2350.class_2351.field_11052),
							lv.method_10056(l, m, n - 1, class_2350.class_2351.field_11051)
						);
					}

					bl = bl2;
				}
			}
		}
	}

	public interface class_252 {
		void consume(class_2350 arg, int i, int j, int k);
	}

	public interface class_253 {
		void consume(int i, int j, int k, int l, int m, int n);
	}
}
