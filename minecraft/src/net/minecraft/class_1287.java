package net.minecraft;

public class class_1287 extends class_1291 {
	protected final double field_5881;

	protected class_1287(boolean bl, int i, double d) {
		super(bl, i);
		this.field_5881 = d;
	}

	@Override
	public double method_5563(int i, class_1322 arg) {
		return this.field_5881 * (double)(i + 1);
	}
}
