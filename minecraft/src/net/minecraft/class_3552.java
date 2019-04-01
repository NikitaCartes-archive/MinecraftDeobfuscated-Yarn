package net.minecraft;

public final class class_3552 extends class_3558<class_3547.class_3548, class_3547> {
	private static final class_2350[] field_15778 = class_2350.values();
	private final class_2338.class_2339 field_16511 = new class_2338.class_2339();

	public class_3552(class_2823 arg) {
		super(arg, class_1944.field_9282, new class_3547(arg));
	}

	private int method_15474(long l) {
		int i = class_2338.method_10061(l);
		int j = class_2338.method_10071(l);
		int k = class_2338.method_10083(l);
		class_1922 lv = this.field_15795.method_12246(i >> 4, k >> 4);
		return lv != null ? lv.method_8317(this.field_16511.method_10103(i, j, k)) : 0;
	}

	@Override
	protected int method_15488(long l, long m, int i) {
		if (m == Long.MAX_VALUE) {
			return 15;
		} else if (l == Long.MAX_VALUE) {
			return i + 15 - this.method_15474(m);
		} else {
			return i >= 15 ? i : i + Math.max(1, this.method_16340(l, m));
		}
	}

	@Override
	protected void method_15487(long l, int i, boolean bl) {
		long m = class_4076.method_18691(l);

		for (class_2350 lv : field_15778) {
			long n = class_2338.method_10060(l, lv);
			long o = class_4076.method_18691(n);
			if (m == o || this.field_15793.method_15524(o)) {
				this.method_15484(l, n, i, bl);
			}
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

		for (class_2350 lv2 : field_15778) {
			long o = class_2338.method_10060(l, lv2);
			if (o != m) {
				long p = class_4076.method_18691(o);
				class_2804 lv3;
				if (n == p) {
					lv3 = lv;
				} else {
					lv3 = this.field_15793.method_15522(p, true);
				}

				if (lv3 != null) {
					int q = this.method_15488(o, l, this.method_15517(lv3, o));
					if (j > q) {
						j = q;
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
	public void method_15514(class_2338 arg, int i) {
		this.field_15793.method_15539();
		this.method_15478(Long.MAX_VALUE, arg.method_10063(), 15 - i, true);
	}
}
