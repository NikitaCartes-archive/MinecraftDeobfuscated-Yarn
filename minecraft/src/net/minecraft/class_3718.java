package net.minecraft;

import javax.annotation.Nullable;

public class class_3718 extends class_2248 {
	private static final class_2588 field_17650 = new class_2588("container.stonecutter");
	public static final class_2753 field_17649 = class_2383.field_11177;
	protected static final class_265 field_16407 = class_2248.method_9541(0.0, 0.0, 0.0, 16.0, 9.0, 16.0);

	public class_3718(class_2248.class_2251 arg) {
		super(arg);
		this.method_9590(this.field_10647.method_11664().method_11657(field_17649, class_2350.field_11043));
	}

	@Override
	public class_2680 method_9605(class_1750 arg) {
		return this.method_9564().method_11657(field_17649, arg.method_8042().method_10153());
	}

	@Override
	public boolean method_9534(class_2680 arg, class_1937 arg2, class_2338 arg3, class_1657 arg4, class_1268 arg5, class_3965 arg6) {
		arg4.method_17355(arg.method_17526(arg2, arg3));
		return true;
	}

	@Nullable
	@Override
	public class_3908 method_17454(class_2680 arg, class_1937 arg2, class_2338 arg3) {
		return new class_747((i, arg3x, arg4) -> new class_3971(i, arg3x, class_3914.method_17392(arg2, arg3)), field_17650);
	}

	@Override
	public class_265 method_9530(class_2680 arg, class_1922 arg2, class_2338 arg3, class_3726 arg4) {
		return field_16407;
	}

	@Override
	public class_2464 method_9604(class_2680 arg) {
		return class_2464.field_11458;
	}

	@Override
	public class_1921 method_9551() {
		return class_1921.field_9174;
	}

	@Override
	public class_2680 method_9598(class_2680 arg, class_2470 arg2) {
		return arg.method_11657(field_17649, arg2.method_10503(arg.method_11654(field_17649)));
	}

	@Override
	public class_2680 method_9569(class_2680 arg, class_2415 arg2) {
		return arg.method_11626(arg2.method_10345(arg.method_11654(field_17649)));
	}

	@Override
	protected void method_9515(class_2689.class_2690<class_2248, class_2680> arg) {
		arg.method_11667(field_17649);
	}
}
