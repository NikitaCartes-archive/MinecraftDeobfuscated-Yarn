package net.minecraft;

public enum class_3638 implements class_3662 {
	field_16058;

	@Override
	public int method_15867(class_3630 arg, int i, int j, int k, int l, int m) {
		if (!class_3645.method_15846(m) || class_3645.method_15846(l) && class_3645.method_15846(k) && class_3645.method_15846(i) && class_3645.method_15846(j)) {
			if (!class_3645.method_15846(m)
				&& (class_3645.method_15846(l) || class_3645.method_15846(i) || class_3645.method_15846(k) || class_3645.method_15846(j))
				&& arg.method_15834(5) == 0) {
				if (class_3645.method_15846(l)) {
					return m == 4 ? 4 : l;
				}

				if (class_3645.method_15846(i)) {
					return m == 4 ? 4 : i;
				}

				if (class_3645.method_15846(k)) {
					return m == 4 ? 4 : k;
				}

				if (class_3645.method_15846(j)) {
					return m == 4 ? 4 : j;
				}
			}

			return m;
		} else {
			int n = 1;
			int o = 1;
			if (!class_3645.method_15846(l) && arg.method_15834(n++) == 0) {
				o = l;
			}

			if (!class_3645.method_15846(k) && arg.method_15834(n++) == 0) {
				o = k;
			}

			if (!class_3645.method_15846(i) && arg.method_15834(n++) == 0) {
				o = i;
			}

			if (!class_3645.method_15846(j) && arg.method_15834(n++) == 0) {
				o = j;
			}

			if (arg.method_15834(3) == 0) {
				return o;
			} else {
				return o == 4 ? 4 : m;
			}
		}
	}
}
