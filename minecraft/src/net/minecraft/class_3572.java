package net.minecraft;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

public final class class_3572 extends class_3558<class_3569.class_3570, class_3569> {
	private static final class_2350[] field_15826 = class_2350.values();
	private static final class_2350[] field_15825 = new class_2350[]{
		class_2350.field_11043, class_2350.field_11035, class_2350.field_11039, class_2350.field_11034
	};

	public class_3572(class_2823 arg) {
		super(arg, class_1944.field_9284, new class_3569(arg));
	}

	@Override
	protected int method_15488(long l, long m, int i) {
		if (m == Long.MAX_VALUE) {
			return 15;
		} else {
			if (l == Long.MAX_VALUE) {
				if (!this.field_15793.method_15565(m)) {
					return 15;
				}

				i = 0;
			}

			if (i >= 15) {
				return i;
			} else {
				int j = this.method_16340(l, m);
				boolean bl = l == Long.MAX_VALUE
					|| class_2338.method_10061(l) == class_2338.method_10061(m)
						&& class_2338.method_10083(l) == class_2338.method_10083(m)
						&& class_2338.method_10071(l) > class_2338.method_10071(m);
				return bl && i == 0 && j == 0 ? 0 : i + Math.max(1, j);
			}
		}
	}

	@Override
	protected void method_15487(long l, int i, boolean bl) {
		long m = class_4076.method_18691(l);
		int j = class_2338.method_10071(l);
		int k = class_4076.method_18684(j);
		int n = class_4076.method_18675(j);
		int o;
		if (k != 0) {
			o = 0;
		} else {
			int p = 0;

			while (!this.field_15793.method_15524(class_4076.method_18678(m, 0, -p - 1, 0)) && this.field_15793.method_15567(n - p - 1)) {
				p++;
			}

			o = p;
		}

		long q = class_2338.method_10096(l, 0, -1 - o * 16, 0);
		long r = class_4076.method_18691(q);
		if (m == r || this.field_15793.method_15524(r)) {
			this.method_15484(l, q, i, bl);
		}

		long s = class_2338.method_10060(l, class_2350.field_11036);
		long t = class_4076.method_18691(s);
		if (m == t || this.field_15793.method_15524(t)) {
			this.method_15484(l, s, i, bl);
		}

		for (class_2350 lv : field_15825) {
			int u = 0;

			do {
				long v = class_2338.method_10096(l, lv.method_10148(), -u, lv.method_10165());
				long w = class_4076.method_18691(v);
				if (m == w) {
					this.method_15484(l, v, i, bl);
					break;
				}

				if (this.field_15793.method_15524(w)) {
					this.method_15484(l, v, i, bl);
				}
			} while (++u >= o * 16);
		}
	}

	@Override
	protected int method_15486(long l, long m, int i) {
		int j = i;
		if (Long.MAX_VALUE != m) {
			int k = this.method_15488(Long.MAX_VALUE, l, 0);
			if (i > k) {
				j = k;
			}

			if (j == 0) {
				return j;
			}
		}

		long n = class_4076.method_18691(l);
		class_2804 lv = this.field_15793.method_15522(n, true);

		for (class_2350 lv2 : field_15826) {
			long o = class_2338.method_10060(l, lv2);
			long p = class_4076.method_18691(o);
			class_2804 lv3;
			if (n == p) {
				lv3 = lv;
			} else {
				lv3 = this.field_15793.method_15522(p, true);
			}

			if (lv3 != null) {
				if (o != m) {
					int q = this.method_15488(o, l, this.method_15517(lv3, o));
					if (j > q) {
						j = q;
					}

					if (j == 0) {
						return j;
					}
				}
			} else if (lv2 != class_2350.field_11033) {
				for (o = class_2338.method_10091(o); !this.field_15793.method_15524(p) && !this.field_15793.method_15568(p); o = class_2338.method_10096(o, 0, 16, 0)) {
					p = class_4076.method_18679(p, class_2350.field_11036);
				}

				class_2804 lv4 = this.field_15793.method_15522(p, true);
				if (o != m) {
					int r;
					if (lv4 != null) {
						r = this.method_15488(o, l, this.method_15517(lv4, o));
					} else {
						r = this.field_15793.method_15566(p) ? 15 : 0;
					}

					if (j > r) {
						j = r;
					}

					if (j == 0) {
						return j;
					}
				}
			}
		}

		return j;
	}

	@Override
	protected void method_15491(long l) {
		this.field_15793.method_15539();
		long m = class_4076.method_18691(l);
		if (this.field_15793.method_15524(m)) {
			super.method_15491(l);
		} else {
			for (l = class_2338.method_10091(l); !this.field_15793.method_15524(m) && !this.field_15793.method_15568(m); l = class_2338.method_10096(l, 0, 16, 0)) {
				m = class_4076.method_18679(m, class_2350.field_11036);
			}

			if (this.field_15793.method_15524(m)) {
				super.method_15491(l);
			}
		}
	}

	@Environment(EnvType.CLIENT)
	@Override
	public String method_15520(long l) {
		return super.method_15520(l) + (this.field_15793.method_15568(l) ? "*" : "");
	}
}
