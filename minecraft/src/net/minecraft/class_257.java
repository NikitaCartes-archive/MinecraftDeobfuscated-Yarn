package net.minecraft;

import it.unimi.dsi.fastutil.doubles.AbstractDoubleList;
import it.unimi.dsi.fastutil.doubles.DoubleList;

public class class_257 extends AbstractDoubleList implements class_255 {
	private final DoubleList field_1381;
	private final DoubleList field_1379;
	private final boolean field_1380;

	public class_257(DoubleList doubleList, DoubleList doubleList2, boolean bl) {
		this.field_1381 = doubleList;
		this.field_1379 = doubleList2;
		this.field_1380 = bl;
	}

	public int size() {
		return this.field_1381.size() + this.field_1379.size();
	}

	@Override
	public boolean method_1065(class_255.class_256 arg) {
		return this.field_1380 ? this.method_1067((i, j, k) -> arg.merge(j, i, k)) : this.method_1067(arg);
	}

	private boolean method_1067(class_255.class_256 arg) {
		int i = this.field_1381.size() - 1;

		for (int j = 0; j < i; j++) {
			if (!arg.merge(j, -1, j)) {
				return false;
			}
		}

		if (!arg.merge(i, -1, i)) {
			return false;
		} else {
			for (int jx = 0; jx < this.field_1379.size(); jx++) {
				if (!arg.merge(i, jx, i + 1 + jx)) {
					return false;
				}
			}

			return true;
		}
	}

	@Override
	public double getDouble(int i) {
		return i < this.field_1381.size() ? this.field_1381.getDouble(i) : this.field_1379.getDouble(i - this.field_1381.size());
	}

	@Override
	public DoubleList method_1066() {
		return this;
	}
}
