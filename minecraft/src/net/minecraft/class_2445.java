package net.minecraft;

public class class_2445 extends class_2511 {
	protected class_2445(class_2248.class_2251 arg) {
		super(arg);
	}

	@Override
	public boolean method_9534(class_2680 arg, class_1937 arg2, class_2338 arg3, class_1657 arg4, class_1268 arg5, class_3965 arg6) {
		class_1799 lv = arg4.method_5998(arg5);
		if (lv.method_7909() == class_1802.field_8868) {
			if (!arg2.field_9236) {
				class_2350 lv2 = arg6.method_17780();
				class_2350 lv3 = lv2.method_10166() == class_2350.class_2351.field_11052 ? arg4.method_5735().method_10153() : lv2;
				arg2.method_8396(null, arg3, class_3417.field_14619, class_3419.field_15245, 1.0F, 1.0F);
				arg2.method_8652(arg3, class_2246.field_10147.method_9564().method_11657(class_2276.field_10748, lv3), 11);
				class_1542 lv4 = new class_1542(
					arg2,
					(double)arg3.method_10263() + 0.5 + (double)lv3.method_10148() * 0.65,
					(double)arg3.method_10264() + 0.1,
					(double)arg3.method_10260() + 0.5 + (double)lv3.method_10165() * 0.65,
					new class_1799(class_1802.field_8706, 4)
				);
				lv4.method_18800(
					0.05 * (double)lv3.method_10148() + arg2.field_9229.nextDouble() * 0.02, 0.05, 0.05 * (double)lv3.method_10165() + arg2.field_9229.nextDouble() * 0.02
				);
				arg2.method_8649(lv4);
				lv.method_7956(1, arg4, arg2x -> arg2x.method_20236(arg5));
			}

			return true;
		} else {
			return super.method_9534(arg, arg2, arg3, arg4, arg5, arg6);
		}
	}

	@Override
	public class_2513 method_10679() {
		return (class_2513)class_2246.field_9984;
	}

	@Override
	public class_2195 method_10680() {
		return (class_2195)class_2246.field_10331;
	}
}
