package net.minecraft;

public class class_1884 extends class_1887 {
	protected class_1884(class_1887.class_1888 arg, class_1304... args) {
		super(arg, class_1886.field_9069, args);
	}

	@Override
	public int method_8182(int i) {
		return 1 + 10 * (i - 1);
	}

	@Override
	public int method_20742(int i) {
		return super.method_8182(i) + 50;
	}

	@Override
	public int method_8183() {
		return 5;
	}

	@Override
	public boolean method_8192(class_1799 arg) {
		return arg.method_7909() == class_1802.field_8868 ? true : super.method_8192(arg);
	}
}
