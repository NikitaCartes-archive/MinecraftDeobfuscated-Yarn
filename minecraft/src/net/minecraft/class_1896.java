package net.minecraft;

public class class_1896 extends class_1887 {
	protected class_1896(class_1887.class_1888 arg, class_1886 arg2, class_1304... args) {
		super(arg, arg2, args);
	}

	@Override
	public int method_8182(int i) {
		return 15 + (i - 1) * 9;
	}

	@Override
	public int method_8183() {
		return 3;
	}

	@Override
	public boolean method_8180(class_1887 arg) {
		return super.method_8180(arg) && arg != class_1893.field_9099;
	}
}
