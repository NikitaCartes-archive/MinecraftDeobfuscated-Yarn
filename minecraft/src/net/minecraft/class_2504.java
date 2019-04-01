package net.minecraft;

public class class_2504 extends class_2389 {
	private final class_1767 field_11554;

	public class_2504(class_1767 arg, class_2248.class_2251 arg2) {
		super(arg2);
		this.field_11554 = arg;
		this.method_9590(
			this.field_10647
				.method_11664()
				.method_11657(field_10905, Boolean.valueOf(false))
				.method_11657(field_10907, Boolean.valueOf(false))
				.method_11657(field_10904, Boolean.valueOf(false))
				.method_11657(field_10903, Boolean.valueOf(false))
				.method_11657(field_10900, Boolean.valueOf(false))
		);
	}

	public class_1767 method_10622() {
		return this.field_11554;
	}

	@Override
	public class_1921 method_9551() {
		return class_1921.field_9179;
	}

	@Override
	public void method_9615(class_2680 arg, class_1937 arg2, class_2338 arg3, class_2680 arg4, boolean bl) {
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
