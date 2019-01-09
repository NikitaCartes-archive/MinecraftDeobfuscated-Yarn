package net.minecraft;

public class class_2465 extends class_2248 {
	public static final class_2754<class_2350.class_2351> field_11459 = class_2741.field_12496;

	public class_2465(class_2248.class_2251 arg) {
		super(arg);
		this.method_9590(this.method_9564().method_11657(field_11459, class_2350.class_2351.field_11052));
	}

	@Override
	public class_2680 method_9598(class_2680 arg, class_2470 arg2) {
		switch (arg2) {
			case field_11465:
			case field_11463:
				switch ((class_2350.class_2351)arg.method_11654(field_11459)) {
					case field_11048:
						return arg.method_11657(field_11459, class_2350.class_2351.field_11051);
					case field_11051:
						return arg.method_11657(field_11459, class_2350.class_2351.field_11048);
					default:
						return arg;
				}
			default:
				return arg;
		}
	}

	@Override
	protected void method_9515(class_2689.class_2690<class_2248, class_2680> arg) {
		arg.method_11667(field_11459);
	}

	@Override
	public class_2680 method_9605(class_1750 arg) {
		return this.method_9564().method_11657(field_11459, arg.method_8038().method_10166());
	}
}
