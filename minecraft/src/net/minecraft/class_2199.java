package net.minecraft;

import javax.annotation.Nullable;

public class class_2199 extends class_2346 {
	public static final class_2753 field_9883 = class_2383.field_11177;
	private static final class_265 field_9882 = class_2248.method_9541(2.0, 0.0, 2.0, 14.0, 4.0, 14.0);
	private static final class_265 field_9885 = class_2248.method_9541(3.0, 4.0, 4.0, 13.0, 5.0, 12.0);
	private static final class_265 field_9888 = class_2248.method_9541(4.0, 5.0, 6.0, 12.0, 10.0, 10.0);
	private static final class_265 field_9884 = class_2248.method_9541(0.0, 10.0, 3.0, 16.0, 16.0, 13.0);
	private static final class_265 field_9891 = class_2248.method_9541(4.0, 4.0, 3.0, 12.0, 5.0, 13.0);
	private static final class_265 field_9889 = class_2248.method_9541(6.0, 5.0, 4.0, 10.0, 10.0, 12.0);
	private static final class_265 field_9886 = class_2248.method_9541(3.0, 10.0, 0.0, 13.0, 16.0, 16.0);
	private static final class_265 field_9887 = class_259.method_17786(field_9882, field_9885, field_9888, field_9884);
	private static final class_265 field_9892 = class_259.method_17786(field_9882, field_9891, field_9889, field_9886);
	private static final class_2588 field_17349 = new class_2588("container.repair");

	public class_2199(class_2248.class_2251 arg) {
		super(arg);
		this.method_9590(this.field_10647.method_11664().method_11657(field_9883, class_2350.field_11043));
	}

	@Override
	public class_2680 method_9605(class_1750 arg) {
		return this.method_9564().method_11657(field_9883, arg.method_8042().method_10170());
	}

	@Override
	public boolean method_9534(class_2680 arg, class_1937 arg2, class_2338 arg3, class_1657 arg4, class_1268 arg5, class_3965 arg6) {
		arg4.method_17355(arg.method_17526(arg2, arg3));
		return true;
	}

	@Nullable
	@Override
	public class_3908 method_17454(class_2680 arg, class_1937 arg2, class_2338 arg3) {
		return new class_747((i, arg3x, arg4) -> new class_1706(i, arg3x, class_3914.method_17392(arg2, arg3)), field_17349);
	}

	@Override
	public class_265 method_9530(class_2680 arg, class_1922 arg2, class_2338 arg3, class_3726 arg4) {
		class_2350 lv = arg.method_11654(field_9883);
		return lv.method_10166() == class_2350.class_2351.field_11048 ? field_9887 : field_9892;
	}

	@Override
	protected void method_10132(class_1540 arg) {
		arg.method_6965(true);
	}

	@Override
	public void method_10127(class_1937 arg, class_2338 arg2, class_2680 arg3, class_2680 arg4) {
		arg.method_20290(1031, arg2, 0);
	}

	@Override
	public void method_10129(class_1937 arg, class_2338 arg2) {
		arg.method_20290(1029, arg2, 0);
	}

	@Nullable
	public static class_2680 method_9346(class_2680 arg) {
		class_2248 lv = arg.method_11614();
		if (lv == class_2246.field_10535) {
			return class_2246.field_10105.method_9564().method_11657(field_9883, arg.method_11654(field_9883));
		} else {
			return lv == class_2246.field_10105 ? class_2246.field_10414.method_9564().method_11657(field_9883, arg.method_11654(field_9883)) : null;
		}
	}

	@Override
	public class_2680 method_9598(class_2680 arg, class_2470 arg2) {
		return arg.method_11657(field_9883, arg2.method_10503(arg.method_11654(field_9883)));
	}

	@Override
	protected void method_9515(class_2689.class_2690<class_2248, class_2680> arg) {
		arg.method_11667(field_9883);
	}

	@Override
	public boolean method_9516(class_2680 arg, class_1922 arg2, class_2338 arg3, class_10 arg4) {
		return false;
	}
}
