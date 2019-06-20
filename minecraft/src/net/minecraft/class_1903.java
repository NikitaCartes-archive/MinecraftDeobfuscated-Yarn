package net.minecraft;

public class class_1903 extends class_1887 {
	public class_1903(class_1887.class_1888 arg, class_1304... args) {
		super(arg, class_1886.field_9074, args);
	}

	@Override
	public int method_8182(int i) {
		return 5 + (i - 1) * 9;
	}

	@Override
	public int method_20742(int i) {
		return this.method_8182(i) + 15;
	}

	@Override
	public int method_8183() {
		return 3;
	}

	public static float method_8241(int i) {
		return 1.0F - 1.0F / (float)(i + 1);
	}
}
