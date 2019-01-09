package net.minecraft;

import java.util.Random;

public class class_1885 extends class_1887 {
	protected class_1885(class_1887.class_1888 arg, class_1304... args) {
		super(arg, class_1886.field_9082, args);
	}

	@Override
	public int method_8182(int i) {
		return 5 + (i - 1) * 8;
	}

	@Override
	public int method_8183() {
		return 3;
	}

	@Override
	public boolean method_8192(class_1799 arg) {
		return arg.method_7963() ? true : super.method_8192(arg);
	}

	public static boolean method_8176(class_1799 arg, int i, Random random) {
		return arg.method_7909() instanceof class_1738 && random.nextFloat() < 0.6F ? false : random.nextInt(i + 1) > 0;
	}
}
