package net.minecraft;

public enum class_3657 implements class_3660 {
	field_16200;

	@Override
	public int method_15863(class_3628<?> arg, class_3625 arg2, int i, int j) {
		int k = i - 2;
		int l = j - 2;
		int m = k >> 2;
		int n = l >> 2;
		int o = m << 2;
		int p = n << 2;
		arg.method_15830((long)o, (long)p);
		double d = ((double)arg.method_15834(1024) / 1024.0 - 0.5) * 3.6;
		double e = ((double)arg.method_15834(1024) / 1024.0 - 0.5) * 3.6;
		arg.method_15830((long)(o + 4), (long)p);
		double f = ((double)arg.method_15834(1024) / 1024.0 - 0.5) * 3.6 + 4.0;
		double g = ((double)arg.method_15834(1024) / 1024.0 - 0.5) * 3.6;
		arg.method_15830((long)o, (long)(p + 4));
		double h = ((double)arg.method_15834(1024) / 1024.0 - 0.5) * 3.6;
		double q = ((double)arg.method_15834(1024) / 1024.0 - 0.5) * 3.6 + 4.0;
		arg.method_15830((long)(o + 4), (long)(p + 4));
		double r = ((double)arg.method_15834(1024) / 1024.0 - 0.5) * 3.6 + 4.0;
		double s = ((double)arg.method_15834(1024) / 1024.0 - 0.5) * 3.6 + 4.0;
		int t = k & 3;
		int u = l & 3;
		double v = ((double)u - e) * ((double)u - e) + ((double)t - d) * ((double)t - d);
		double w = ((double)u - g) * ((double)u - g) + ((double)t - f) * ((double)t - f);
		double x = ((double)u - q) * ((double)u - q) + ((double)t - h) * ((double)t - h);
		double y = ((double)u - s) * ((double)u - s) + ((double)t - r) * ((double)t - r);
		if (v < w && v < x && v < y) {
			return arg2.method_15825(this.method_16342(o), this.method_16343(p));
		} else if (w < v && w < x && w < y) {
			return arg2.method_15825(this.method_16342(o + 4), this.method_16343(p)) & 0xFF;
		} else {
			return x < v && x < w && x < y
				? arg2.method_15825(this.method_16342(o), this.method_16343(p + 4))
				: arg2.method_15825(this.method_16342(o + 4), this.method_16343(p + 4)) & 0xFF;
		}
	}

	@Override
	public int method_16342(int i) {
		return i >> 2;
	}

	@Override
	public int method_16343(int i) {
		return i >> 2;
	}
}
