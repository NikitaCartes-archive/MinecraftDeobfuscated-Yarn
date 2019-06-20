package net.minecraft;

public class class_2347 implements class_2357 {
	@Override
	public final class_1799 dispense(class_2342 arg, class_1799 arg2) {
		class_1799 lv = this.method_10135(arg, arg2);
		this.method_10136(arg);
		this.method_10133(arg, arg.method_10120().method_11654(class_2315.field_10918));
		return lv;
	}

	protected class_1799 method_10135(class_2342 arg, class_1799 arg2) {
		class_2350 lv = arg.method_10120().method_11654(class_2315.field_10918);
		class_2374 lv2 = class_2315.method_10010(arg);
		class_1799 lv3 = arg2.method_7971(1);
		method_10134(arg.method_10207(), lv3, 6, lv, lv2);
		return arg2;
	}

	public static void method_10134(class_1937 arg, class_1799 arg2, int i, class_2350 arg3, class_2374 arg4) {
		double d = arg4.method_10216();
		double e = arg4.method_10214();
		double f = arg4.method_10215();
		if (arg3.method_10166() == class_2350.class_2351.field_11052) {
			e -= 0.125;
		} else {
			e -= 0.15625;
		}

		class_1542 lv = new class_1542(arg, d, e, f, arg2);
		double g = arg.field_9229.nextDouble() * 0.1 + 0.2;
		lv.method_18800(
			arg.field_9229.nextGaussian() * 0.0075F * (double)i + (double)arg3.method_10148() * g,
			arg.field_9229.nextGaussian() * 0.0075F * (double)i + 0.2F,
			arg.field_9229.nextGaussian() * 0.0075F * (double)i + (double)arg3.method_10165() * g
		);
		arg.method_8649(lv);
	}

	protected void method_10136(class_2342 arg) {
		arg.method_10207().method_20290(1000, arg.method_10122(), 0);
	}

	protected void method_10133(class_2342 arg, class_2350 arg2) {
		arg.method_10207().method_20290(2000, arg.method_10122(), arg2.method_10146());
	}
}
