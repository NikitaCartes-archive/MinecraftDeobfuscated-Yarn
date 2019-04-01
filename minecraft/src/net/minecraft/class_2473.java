package net.minecraft;

import java.util.Random;

public class class_2473 extends class_2261 implements class_2256 {
	public static final class_2758 field_11476 = class_2741.field_12549;
	protected static final class_265 field_11478 = class_2248.method_9541(2.0, 0.0, 2.0, 14.0, 12.0, 14.0);
	private final class_2647 field_11477;

	protected class_2473(class_2647 arg, class_2248.class_2251 arg2) {
		super(arg2);
		this.field_11477 = arg;
		this.method_9590(this.field_10647.method_11664().method_11657(field_11476, Integer.valueOf(0)));
	}

	@Override
	public class_265 method_9530(class_2680 arg, class_1922 arg2, class_2338 arg3, class_3726 arg4) {
		return field_11478;
	}

	@Override
	public void method_9588(class_2680 arg, class_1937 arg2, class_2338 arg3, Random random) {
		super.method_9588(arg, arg2, arg3, random);
		if (arg2.method_8602(arg3.method_10084()) >= 9 && random.nextInt(7) == 0) {
			this.method_10507(arg2, arg3, arg, random);
		}
	}

	public void method_10507(class_1936 arg, class_2338 arg2, class_2680 arg3, Random random) {
		if ((Integer)arg3.method_11654(field_11476) == 0) {
			arg.method_8652(arg2, arg3.method_11572(field_11476), 4);
		} else {
			this.field_11477.method_11431(arg, arg2, arg3, random);
		}
	}

	@Override
	public boolean method_9651(class_1922 arg, class_2338 arg2, class_2680 arg3, boolean bl) {
		return true;
	}

	@Override
	public boolean method_9650(class_1937 arg, Random random, class_2338 arg2, class_2680 arg3) {
		return (double)arg.field_9229.nextFloat() < 0.45;
	}

	@Override
	public void method_9652(class_1937 arg, Random random, class_2338 arg2, class_2680 arg3) {
		this.method_10507(arg, arg2, arg3, random);
	}

	@Override
	protected void method_9515(class_2689.class_2690<class_2248, class_2680> arg) {
		arg.method_11667(field_11476);
	}
}
