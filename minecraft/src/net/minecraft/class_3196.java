package net.minecraft;

public abstract class class_3196 extends class_3554 {
	protected class_3196(int i, int j, int k) {
		super(i, j, k);
	}

	@Override
	protected boolean method_15494(long l) {
		return l == class_1923.field_17348;
	}

	@Override
	protected void method_15487(long l, int i, boolean bl) {
		class_1923 lv = new class_1923(l);
		int j = lv.field_9181;
		int k = lv.field_9180;

		for (int m = -1; m <= 1; m++) {
			for (int n = -1; n <= 1; n++) {
				long o = class_1923.method_8331(j + m, k + n);
				if (o != l) {
					this.method_15484(l, o, i, bl);
				}
			}
		}
	}

	@Override
	protected int method_15486(long l, long m, int i) {
		int j = i;
		class_1923 lv = new class_1923(l);
		int k = lv.field_9181;
		int n = lv.field_9180;

		for (int o = -1; o <= 1; o++) {
			for (int p = -1; p <= 1; p++) {
				long q = class_1923.method_8331(k + o, n + p);
				if (q == l) {
					q = class_1923.field_17348;
				}

				if (q != m) {
					int r = this.method_15488(q, l, this.method_15480(q));
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
	protected int method_15488(long l, long m, int i) {
		return l == class_1923.field_17348 ? this.method_14028(m) : i + 1;
	}

	protected abstract int method_14028(long l);

	public void method_14027(long l, int i, boolean bl) {
		this.method_15478(class_1923.field_17348, l, i, bl);
	}
}
