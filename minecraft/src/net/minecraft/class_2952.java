package net.minecraft;

import java.util.Iterator;

public interface class_2952<T> {
	default void method_12816(int i, int j, int k, class_1860<?> arg, Iterator<T> iterator, int l) {
		int m = i;
		int n = j;
		if (arg instanceof class_1869) {
			class_1869 lv = (class_1869)arg;
			m = lv.method_8150();
			n = lv.method_8158();
		}

		int o = 0;

		for (int p = 0; p < j; p++) {
			if (o == k) {
				o++;
			}

			boolean bl = (float)n < (float)j / 2.0F;
			int q = class_3532.method_15375((float)j / 2.0F - (float)n / 2.0F);
			if (bl && q > p) {
				o += i;
				p++;
			}

			for (int r = 0; r < i; r++) {
				if (!iterator.hasNext()) {
					return;
				}

				bl = (float)m < (float)i / 2.0F;
				q = class_3532.method_15375((float)i / 2.0F - (float)m / 2.0F);
				int s = m;
				boolean bl2 = r < m;
				if (bl) {
					s = q + m;
					bl2 = q <= r && r < q + m;
				}

				if (bl2) {
					this.method_12815(iterator, o, l, p, r);
				} else if (s == r) {
					o += i - r;
					break;
				}

				o++;
			}
		}
	}

	void method_12815(Iterator<T> iterator, int i, int j, int k, int l);
}
