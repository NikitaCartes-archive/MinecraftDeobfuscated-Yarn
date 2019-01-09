package net.minecraft;

import com.google.common.math.IntMath;
import it.unimi.dsi.fastutil.doubles.DoubleList;

public final class class_248 implements class_255 {
	private final class_246 field_1367;
	private final int field_1370;
	private final int field_1369;
	private final int field_1368;

	class_248(int i, int j) {
		this.field_1367 = new class_246((int)class_259.method_1079(i, j));
		this.field_1370 = i;
		this.field_1369 = j;
		this.field_1368 = IntMath.gcd(i, j);
	}

	@Override
	public boolean method_1065(class_255.class_256 arg) {
		int i = this.field_1370 / this.field_1368;
		int j = this.field_1369 / this.field_1368;

		for (int k = 0; k <= this.field_1367.size(); k++) {
			if (!arg.merge(k / j, k / i, k)) {
				return false;
			}
		}

		return true;
	}

	@Override
	public DoubleList method_1066() {
		return this.field_1367;
	}
}
