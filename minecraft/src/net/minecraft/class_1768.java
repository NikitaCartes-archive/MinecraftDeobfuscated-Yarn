package net.minecraft;

public class class_1768 extends class_1738 {
	public class_1768(class_1741 arg, class_1304 arg2, class_1792.class_1793 arg3) {
		super(arg, arg2, arg3);
	}

	public boolean method_7801(class_1799 arg) {
		class_2487 lv = arg.method_7941("display");
		return lv != null && lv.method_10573("color", 99);
	}

	public int method_7800(class_1799 arg) {
		class_2487 lv = arg.method_7941("display");
		return lv != null && lv.method_10573("color", 99) ? lv.method_10550("color") : 10511680;
	}

	public void method_7798(class_1799 arg) {
		class_2487 lv = arg.method_7941("display");
		if (lv != null && lv.method_10545("color")) {
			lv.method_10551("color");
		}
	}

	public void method_7799(class_1799 arg, int i) {
		arg.method_7911("display").method_10569("color", i);
	}
}
