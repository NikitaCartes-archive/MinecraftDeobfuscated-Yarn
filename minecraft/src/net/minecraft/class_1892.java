package net.minecraft;

public class class_1892 extends class_1887 {
	protected class_1892(class_1887.class_1888 arg, class_1304... args) {
		super(arg, class_1886.field_9074, args);
	}

	@Override
	public int method_8182(int i) {
		return 10 + 20 * (i - 1);
	}

	@Override
	public int method_20742(int i) {
		return super.method_8182(i) + 50;
	}

	@Override
	public int method_8183() {
		return 2;
	}
}
