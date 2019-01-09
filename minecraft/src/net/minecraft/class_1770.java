package net.minecraft;

public class class_1770 extends class_1792 {
	public class_1770(class_1792.class_1793 arg) {
		super(arg);
		this.method_7863(new class_2960("broken"), (argx, arg2, arg3) -> method_7804(argx) ? 0.0F : 1.0F);
		class_2315.method_10009(this, class_1738.field_7879);
	}

	public static boolean method_7804(class_1799 arg) {
		return arg.method_7919() < arg.method_7936() - 1;
	}

	@Override
	public boolean method_7878(class_1799 arg, class_1799 arg2) {
		return arg2.method_7909() == class_1802.field_8614;
	}

	@Override
	public class_1271<class_1799> method_7836(class_1937 arg, class_1657 arg2, class_1268 arg3) {
		class_1799 lv = arg2.method_5998(arg3);
		class_1304 lv2 = class_1308.method_5953(lv);
		class_1799 lv3 = arg2.method_6118(lv2);
		if (lv3.method_7960()) {
			arg2.method_5673(lv2, lv.method_7972());
			lv.method_7939(0);
			return new class_1271<>(class_1269.field_5812, lv);
		} else {
			return new class_1271<>(class_1269.field_5814, lv);
		}
	}
}
