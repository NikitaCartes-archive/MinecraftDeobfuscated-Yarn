package net.minecraft;

public enum class_3636 implements class_3663 {
	field_16052;

	@Override
	public int method_15868(class_3630 arg, int i, int j, int k, int l, int m) {
		if (class_3645.method_15846(m)) {
			int n = 0;
			if (class_3645.method_15846(i)) {
				n++;
			}

			if (class_3645.method_15846(j)) {
				n++;
			}

			if (class_3645.method_15846(l)) {
				n++;
			}

			if (class_3645.method_15846(k)) {
				n++;
			}

			if (n > 3) {
				if (m == class_3645.field_16115) {
					return class_3645.field_16110;
				}

				if (m == class_3645.field_16114) {
					return class_3645.field_16109;
				}

				if (m == class_3645.field_16113) {
					return class_3645.field_16108;
				}

				if (m == class_3645.field_16112) {
					return class_3645.field_16107;
				}

				if (m == class_3645.field_16111) {
					return class_3645.field_16116;
				}

				return class_3645.field_16108;
			}
		}

		return m;
	}
}
