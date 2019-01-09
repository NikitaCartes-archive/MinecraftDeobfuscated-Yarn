package net.minecraft;

public enum class_3647 implements class_3659, class_3740 {
	field_16121;

	@Override
	public int method_15861(class_3630 arg, class_3625 arg2, class_3625 arg3, int i, int j) {
		int k = arg2.method_15825(this.method_16342(i), this.method_16343(j));
		int l = arg3.method_15825(this.method_16342(i), this.method_16343(j));
		if (!class_3645.method_15845(k)) {
			return k;
		} else {
			int m = 8;
			int n = 4;

			for (int o = -8; o <= 8; o += 4) {
				for (int p = -8; p <= 8; p += 4) {
					int q = arg2.method_15825(this.method_16342(i + o), this.method_16343(j + p));
					if (!class_3645.method_15845(q)) {
						if (l == class_3645.field_16115) {
							return class_3645.field_16114;
						}

						if (l == class_3645.field_16111) {
							return class_3645.field_16112;
						}
					}
				}
			}

			if (k == class_3645.field_16108) {
				if (l == class_3645.field_16114) {
					return class_3645.field_16109;
				}

				if (l == class_3645.field_16113) {
					return class_3645.field_16108;
				}

				if (l == class_3645.field_16112) {
					return class_3645.field_16107;
				}

				if (l == class_3645.field_16111) {
					return class_3645.field_16116;
				}
			}

			return l;
		}
	}
}
