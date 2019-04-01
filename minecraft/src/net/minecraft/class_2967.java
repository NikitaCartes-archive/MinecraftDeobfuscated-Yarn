package net.minecraft;

public class class_2967 extends class_2347 {
	private final class_2347 field_13360 = new class_2347();
	private final class_1690.class_1692 field_13361;

	public class_2967(class_1690.class_1692 arg) {
		this.field_13361 = arg;
	}

	@Override
	public class_1799 method_10135(class_2342 arg, class_1799 arg2) {
		class_2350 lv = arg.method_10120().method_11654(class_2315.field_10918);
		class_1937 lv2 = arg.method_10207();
		double d = arg.method_10216() + (double)((float)lv.method_10148() * 1.125F);
		double e = arg.method_10214() + (double)((float)lv.method_10164() * 1.125F);
		double f = arg.method_10215() + (double)((float)lv.method_10165() * 1.125F);
		class_2338 lv3 = arg.method_10122().method_10093(lv);
		double g;
		if (lv2.method_8316(lv3).method_15767(class_3486.field_15517)) {
			g = 1.0;
		} else {
			if (!lv2.method_8320(lv3).method_11588() || !lv2.method_8316(lv3.method_10074()).method_15767(class_3486.field_15517)) {
				return this.field_13360.dispense(arg, arg2);
			}

			g = 0.0;
		}

		class_1690 lv4 = new class_1690(lv2, d, e + g, f);
		lv4.method_7541(this.field_13361);
		lv4.field_6031 = lv.method_10144();
		lv2.method_8649(lv4);
		arg2.method_7934(1);
		return arg2;
	}

	@Override
	protected void method_10136(class_2342 arg) {
		arg.method_10207().method_8535(1000, arg.method_10122(), 0);
	}
}
