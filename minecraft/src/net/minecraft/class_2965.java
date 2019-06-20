package net.minecraft;

public abstract class class_2965 extends class_2347 {
	@Override
	public class_1799 method_10135(class_2342 arg, class_1799 arg2) {
		class_1937 lv = arg.method_10207();
		class_2374 lv2 = class_2315.method_10010(arg);
		class_2350 lv3 = arg.method_10120().method_11654(class_2315.field_10918);
		class_1676 lv4 = this.method_12844(lv, lv2, arg2);
		lv4.method_7485((double)lv3.method_10148(), (double)((float)lv3.method_10164() + 0.1F), (double)lv3.method_10165(), this.method_12846(), this.method_12845());
		lv.method_8649((class_1297)lv4);
		arg2.method_7934(1);
		return arg2;
	}

	@Override
	protected void method_10136(class_2342 arg) {
		arg.method_10207().method_20290(1002, arg.method_10122(), 0);
	}

	protected abstract class_1676 method_12844(class_1937 arg, class_2374 arg2, class_1799 arg3);

	protected float method_12845() {
		return 6.0F;
	}

	protected float method_12846() {
		return 1.1F;
	}
}
