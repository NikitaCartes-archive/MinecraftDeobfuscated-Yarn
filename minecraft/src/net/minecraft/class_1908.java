package net.minecraft;

public class class_1908 extends class_1887 {
	public class_1908(class_1887.class_1888 arg, class_1304... args) {
		super(arg, class_1886.field_9073, args);
	}

	@Override
	public int method_8182(int i) {
		return 1 + (i - 1) * 8;
	}

	@Override
	public int method_20742(int i) {
		return this.method_8182(i) + 20;
	}

	@Override
	public int method_8183() {
		return 5;
	}

	@Override
	public float method_8196(int i, class_1310 arg) {
		return arg == class_1310.field_6292 ? (float)i * 2.5F : 0.0F;
	}
}
