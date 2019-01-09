package net.minecraft;

public class class_2406 extends class_2383 {
	private static final class_2588 field_17373 = new class_2588("container.loom");

	protected class_2406(class_2248.class_2251 arg) {
		super(arg);
	}

	@Override
	public boolean method_9534(class_2680 arg, class_1937 arg2, class_2338 arg3, class_1657 arg4, class_1268 arg5, class_2350 arg6, float f, float g, float h) {
		if (arg2.field_9236) {
			return true;
		} else {
			arg4.method_17355(arg.method_17526(arg2, arg3));
			return true;
		}
	}

	@Override
	public class_3908 method_17454(class_2680 arg, class_1937 arg2, class_2338 arg3) {
		return new class_747((i, arg3x, arg4) -> new class_1726(i, arg3x, class_3914.method_17392(arg2, arg3.method_10080(0.5, 0.0, 0.5))), field_17373);
	}

	@Override
	public class_2680 method_9605(class_1750 arg) {
		return this.method_9564().method_11657(field_11177, arg.method_8042());
	}

	@Override
	protected void method_9515(class_2689.class_2690<class_2248, class_2680> arg) {
		arg.method_11667(field_11177);
	}
}
