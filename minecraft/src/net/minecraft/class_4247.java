package net.minecraft;

public class class_4247 extends class_4246 {
	public class_4247(int i, float f) {
		super(i, f, 1);
	}

	@Override
	protected boolean method_18919(class_3218 arg, class_1309 arg2) {
		class_3765 lv = arg.method_19502(new class_2338(arg2));
		return super.method_18919(arg, arg2) && lv != null && lv.method_16504() && !lv.method_20023() && !lv.method_20024();
	}
}
