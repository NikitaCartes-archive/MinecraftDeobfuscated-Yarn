package net.minecraft;

public enum class_3656 implements class_3660 {
	field_16196,
	field_16198 {
		@Override
		protected int method_15853(class_3628<?> arg, int i, int j, int k, int l) {
			return arg.method_16669(i, j, k, l);
		}
	};

	private class_3656() {
	}

	@Override
	public int method_16342(int i) {
		return i >> 1;
	}

	@Override
	public int method_16343(int i) {
		return i >> 1;
	}

	@Override
	public int method_15863(class_3628<?> arg, class_3625 arg2, int i, int j) {
		int k = arg2.method_15825(this.method_16342(i), this.method_16343(j));
		arg.method_15830((long)(i >> 1 << 1), (long)(j >> 1 << 1));
		int l = i & 1;
		int m = j & 1;
		if (l == 0 && m == 0) {
			return k;
		} else {
			int n = arg2.method_15825(this.method_16342(i), this.method_16343(j + 1));
			int o = arg.method_16670(k, n);
			if (l == 0 && m == 1) {
				return o;
			} else {
				int p = arg2.method_15825(this.method_16342(i + 1), this.method_16343(j));
				int q = arg.method_16670(k, p);
				if (l == 1 && m == 0) {
					return q;
				} else {
					int r = arg2.method_15825(this.method_16342(i + 1), this.method_16343(j + 1));
					return this.method_15853(arg, k, p, n, r);
				}
			}
		}
	}

	protected int method_15853(class_3628<?> arg, int i, int j, int k, int l) {
		if (j == k && k == l) {
			return j;
		} else if (i == j && i == k) {
			return i;
		} else if (i == j && i == l) {
			return i;
		} else if (i == k && i == l) {
			return i;
		} else if (i == j && k != l) {
			return i;
		} else if (i == k && j != l) {
			return i;
		} else if (i == l && j != k) {
			return i;
		} else if (j == k && i != l) {
			return j;
		} else if (j == l && i != k) {
			return j;
		} else {
			return k == l && i != j ? k : arg.method_16669(i, j, k, l);
		}
	}
}
