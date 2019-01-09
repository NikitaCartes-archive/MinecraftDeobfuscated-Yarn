package net.minecraft;

import javax.annotation.Nullable;

public class class_3711 extends class_2248 {
	private static final class_2588 field_17355 = new class_2588("container.cartography_table");

	protected class_3711(class_2248.class_2251 arg) {
		super(arg);
	}

	@Override
	public boolean method_9534(class_2680 arg, class_1937 arg2, class_2338 arg3, class_1657 arg4, class_1268 arg5, class_2350 arg6, float f, float g, float h) {
		arg4.method_17355(arg.method_17526(arg2, arg3));
		arg4.method_7281(class_3468.field_15368);
		return true;
	}

	@Nullable
	@Override
	public class_3908 method_17454(class_2680 arg, class_1937 arg2, class_2338 arg3) {
		return new class_747((i, arg3x, arg4) -> new class_3910(i, arg3x, class_3914.method_17392(arg2, arg3)), field_17355);
	}
}
