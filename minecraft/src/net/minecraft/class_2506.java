package net.minecraft;

public class class_2506 extends class_2373 {
	private final class_1767 field_11558;

	public class_2506(class_1767 arg, class_2248.class_2251 arg2) {
		super(arg2);
		this.field_11558 = arg;
	}

	@Override
	public boolean method_9579(class_2680 arg, class_1922 arg2, class_2338 arg3) {
		return true;
	}

	public class_1767 method_10624() {
		return this.field_11558;
	}

	@Override
	public class_1921 method_9551() {
		return class_1921.field_9179;
	}

	@Override
	public boolean method_9521(class_2680 arg, class_1922 arg2, class_2338 arg3) {
		return false;
	}

	@Override
	public void method_9615(class_2680 arg, class_1937 arg2, class_2338 arg3, class_2680 arg4) {
		if (arg4.method_11614() != arg.method_11614()) {
			if (!arg2.field_9236) {
				class_2238.method_9463(arg2, arg3);
			}
		}
	}

	@Override
	public void method_9536(class_2680 arg, class_1937 arg2, class_2338 arg3, class_2680 arg4, boolean bl) {
		if (arg.method_11614() != arg4.method_11614()) {
			if (!arg2.field_9236) {
				class_2238.method_9463(arg2, arg3);
			}
		}
	}
}
