package net.minecraft;

public class class_4215 {
	public static void method_19548(class_1309 arg, class_1309 arg2) {
		method_19552(arg, arg2);
		method_19555(arg, arg2);
	}

	public static boolean method_19550(class_4095<?> arg, class_1309 arg2) {
		return arg.method_19543(class_4140.field_18442).filter(list -> list.contains(arg2)).isPresent();
	}

	public static boolean method_19551(class_4095<?> arg, class_4140<? extends class_1309> arg2, class_1299<?> arg3) {
		return arg.method_19543(arg2)
			.filter(arg2x -> arg2x.method_5864() == arg3)
			.filter(class_1309::method_5805)
			.filter(arg2x -> method_19550(arg, arg2x))
			.isPresent();
	}

	public static void method_19552(class_1309 arg, class_1309 arg2) {
		method_19554(arg, arg2);
		method_19554(arg2, arg);
	}

	public static void method_19554(class_1309 arg, class_1309 arg2) {
		arg.method_18868().method_18878(class_4140.field_18446, new class_4102(arg2));
	}

	public static void method_19555(class_1309 arg, class_1309 arg2) {
		int i = 2;
		method_19556(arg, arg2, 2);
		method_19556(arg2, arg, 2);
	}

	public static void method_19556(class_1309 arg, class_1309 arg2, int i) {
		float f = (float)arg.method_5996(class_1612.field_7357).method_6194();
		class_4102 lv = new class_4102(arg2);
		class_4142 lv2 = new class_4142(lv, f, i);
		arg.method_18868().method_18878(class_4140.field_18446, lv);
		arg.method_18868().method_18878(class_4140.field_18445, lv2);
	}

	public static void method_19949(class_1309 arg, class_1799 arg2, class_1309 arg3) {
		double d = arg.field_6010 - 0.3F + (double)arg.method_5751();
		class_1542 lv = new class_1542(arg.field_6002, arg.field_5987, d, arg.field_6035, arg2);
		class_2338 lv2 = new class_2338(arg3);
		class_2338 lv3 = new class_2338(arg);
		float f = 0.3F;
		class_243 lv4 = new class_243(lv2.method_10059(lv3));
		lv4 = lv4.method_1029().method_1021(0.3F);
		lv.method_18799(lv4);
		lv.method_6988();
		arg.field_6002.method_8649(lv);
	}
}
