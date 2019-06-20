package net.minecraft;

import java.util.Random;

public class class_2492 extends class_2248 {
	protected static final class_265 field_11521 = class_2248.method_9541(0.0, 0.0, 0.0, 16.0, 14.0, 16.0);

	public class_2492(class_2248.class_2251 arg) {
		super(arg);
	}

	@Override
	public class_265 method_9549(class_2680 arg, class_1922 arg2, class_2338 arg3, class_3726 arg4) {
		return field_11521;
	}

	@Override
	public void method_9548(class_2680 arg, class_1937 arg2, class_2338 arg3, class_1297 arg4) {
		arg4.method_18799(arg4.method_18798().method_18805(0.4, 1.0, 0.4));
	}

	@Override
	public void method_9588(class_2680 arg, class_1937 arg2, class_2338 arg3, Random random) {
		class_2258.method_9657(arg2, arg3.method_10084(), false);
	}

	@Override
	public void method_9612(class_2680 arg, class_1937 arg2, class_2338 arg3, class_2248 arg4, class_2338 arg5, boolean bl) {
		arg2.method_8397().method_8676(arg3, this, this.method_9563(arg2));
	}

	@Override
	public boolean method_9521(class_2680 arg, class_1922 arg2, class_2338 arg3) {
		return true;
	}

	@Override
	public int method_9563(class_1941 arg) {
		return 20;
	}

	@Override
	public void method_9615(class_2680 arg, class_1937 arg2, class_2338 arg3, class_2680 arg4, boolean bl) {
		arg2.method_8397().method_8676(arg3, this, this.method_9563(arg2));
	}

	@Override
	public boolean method_9516(class_2680 arg, class_1922 arg2, class_2338 arg3, class_10 arg4) {
		return false;
	}

	@Override
	public boolean method_9523(class_2680 arg, class_1922 arg2, class_2338 arg3, class_1299<?> arg4) {
		return true;
	}
}
