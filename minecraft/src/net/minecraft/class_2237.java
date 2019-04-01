package net.minecraft;

import javax.annotation.Nullable;

public abstract class class_2237 extends class_2248 implements class_2343 {
	protected class_2237(class_2248.class_2251 arg) {
		super(arg);
	}

	@Override
	public class_2464 method_9604(class_2680 arg) {
		return class_2464.field_11455;
	}

	@Override
	public boolean method_9592(class_2680 arg, class_1937 arg2, class_2338 arg3, int i, int j) {
		super.method_9592(arg, arg2, arg3, i, j);
		class_2586 lv = arg2.method_8321(arg3);
		return lv == null ? false : lv.method_11004(i, j);
	}

	@Nullable
	@Override
	public class_3908 method_17454(class_2680 arg, class_1937 arg2, class_2338 arg3) {
		class_2586 lv = arg2.method_8321(arg3);
		return lv instanceof class_3908 ? (class_3908)lv : null;
	}
}
