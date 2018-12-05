package net.minecraft;

import it.unimi.dsi.fastutil.doubles.DoubleList;

public class class_250 implements class_255 {
	private final DoubleList field_1371;

	public class_250(DoubleList doubleList) {
		this.field_1371 = doubleList;
	}

	@Override
	public boolean method_1065(class_255.class_256 arg) {
		for (int i = 0; i <= this.field_1371.size(); i++) {
			if (!arg.merge(i, i, i)) {
				return false;
			}
		}

		return true;
	}

	@Override
	public DoubleList method_1066() {
		return this.field_1371;
	}
}
