package net.minecraft;

public abstract class class_4079 extends class_3554 {
	protected class_4079(int i, int j, int k) {
		super(i, j, k);
	}

	@Override
	protected boolean method_15494(long l) {
		return l == Long.MAX_VALUE;
	}

	@Override
	protected void method_15487(long l, int i, boolean bl) {
		for (int j = -1; j <= 1; j++) {
			for (int k = -1; k <= 1; k++) {
				for (int m = -1; m <= 1; m++) {
					long n = class_4076.method_18678(l, j, k, m);
					if (n != l) {
						this.method_15484(l, n, i, bl);
					}
				}
			}
		}
	}

	@Override
	protected int method_15486(long l, long m, int i) {
		int j = i;

		for (int k = -1; k <= 1; k++) {
			for (int n = -1; n <= 1; n++) {
				for (int o = -1; o <= 1; o++) {
					long p = class_4076.method_18678(l, k, n, o);
					if (p == l) {
						p = Long.MAX_VALUE;
					}

					if (p != m) {
						int q = this.method_15488(p, l, this.method_15480(p));
						if (j > q) {
							j = q;
						}

						if (j == 0) {
							return j;
						}
					}
				}
			}
		}

		return j;
	}

	@Override
	protected int method_15488(long l, long m, int i) {
		return l == Long.MAX_VALUE ? this.method_18749(m) : i + 1;
	}

	protected abstract int method_18749(long l);

	public void method_18750(long l, int i, boolean bl) {
		this.method_15478(Long.MAX_VALUE, l, i, bl);
	}
}
