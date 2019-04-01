package net.minecraft;

public class class_1717 extends class_1735 {
	private final class_1720 field_17083;

	public class_1717(class_1720 arg, class_1263 arg2, int i, int j, int k) {
		super(arg2, i, j, k);
		this.field_17083 = arg;
	}

	@Override
	public boolean method_7680(class_1799 arg) {
		return this.field_17083.method_16945(arg) || method_7636(arg);
	}

	@Override
	public int method_7676(class_1799 arg) {
		return method_7636(arg) ? 1 : super.method_7676(arg);
	}

	public static boolean method_7636(class_1799 arg) {
		return arg.method_7909() == class_1802.field_8550;
	}
}
