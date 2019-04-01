package net.minecraft;

import java.util.function.Supplier;

public enum class_1834 implements class_1832 {
	field_8922(0, 59, 2.0F, 0.0F, 15, () -> class_1856.method_8106(class_3489.field_15537)),
	field_8927(1, 131, 4.0F, 1.0F, 5, () -> class_1856.method_8091(class_2246.field_10445)),
	field_8923(2, 250, 6.0F, 2.0F, 14, () -> class_1856.method_8091(class_1802.field_8620)),
	field_8930(3, 1561, 8.0F, 3.0F, 10, () -> class_1856.method_8091(class_1802.field_8477)),
	field_8929(0, 32, 12.0F, 0.0F, 22, () -> class_1856.method_8091(class_1802.field_8695));

	private final int field_8925;
	private final int field_8924;
	private final float field_8932;
	private final float field_8931;
	private final int field_8933;
	private final class_3528<class_1856> field_8928;

	private class_1834(int j, int k, float f, float g, int l, Supplier<class_1856> supplier) {
		this.field_8925 = j;
		this.field_8924 = k;
		this.field_8932 = f;
		this.field_8931 = g;
		this.field_8933 = l;
		this.field_8928 = new class_3528<>(supplier);
	}

	@Override
	public int method_8025() {
		return this.field_8924;
	}

	@Override
	public float method_8027() {
		return this.field_8932;
	}

	@Override
	public float method_8028() {
		return this.field_8931;
	}

	@Override
	public int method_8024() {
		return this.field_8925;
	}

	@Override
	public int method_8026() {
		return this.field_8933;
	}

	@Override
	public class_1856 method_8023() {
		return this.field_8928.method_15332();
	}
}
