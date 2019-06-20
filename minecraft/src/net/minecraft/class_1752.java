package net.minecraft;

import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

public class class_1752 extends class_1792 {
	public class_1752(class_1792.class_1793 arg) {
		super(arg);
	}

	@Override
	public class_1269 method_7884(class_1838 arg) {
		class_1937 lv = arg.method_8045();
		class_2338 lv2 = arg.method_8037();
		class_2338 lv3 = lv2.method_10093(arg.method_8038());
		if (method_7720(arg.method_8041(), lv, lv2)) {
			if (!lv.field_9236) {
				lv.method_20290(2005, lv2, 0);
			}

			return class_1269.field_5812;
		} else {
			class_2680 lv4 = lv.method_8320(lv2);
			boolean bl = class_2248.method_20045(lv4, lv, lv2, arg.method_8038());
			if (bl && method_7719(arg.method_8041(), lv, lv3, arg.method_8038())) {
				if (!lv.field_9236) {
					lv.method_20290(2005, lv3, 0);
				}

				return class_1269.field_5812;
			} else {
				return class_1269.field_5811;
			}
		}
	}

	public static boolean method_7720(class_1799 arg, class_1937 arg2, class_2338 arg3) {
		class_2680 lv = arg2.method_8320(arg3);
		if (lv.method_11614() instanceof class_2256) {
			class_2256 lv2 = (class_2256)lv.method_11614();
			if (lv2.method_9651(arg2, arg3, lv, arg2.field_9236)) {
				if (!arg2.field_9236) {
					if (lv2.method_9650(arg2, arg2.field_9229, arg3, lv)) {
						lv2.method_9652(arg2, arg2.field_9229, arg3, lv);
					}

					arg.method_7934(1);
				}

				return true;
			}
		}

		return false;
	}

	public static boolean method_7719(class_1799 arg, class_1937 arg2, class_2338 arg3, @Nullable class_2350 arg4) {
		if (arg2.method_8320(arg3).method_11614() == class_2246.field_10382 && arg2.method_8316(arg3).method_15761() == 8) {
			if (!arg2.field_9236) {
				label79:
				for (int i = 0; i < 128; i++) {
					class_2338 lv = arg3;
					class_1959 lv2 = arg2.method_8310(arg3);
					class_2680 lv3 = class_2246.field_10376.method_9564();

					for (int j = 0; j < i / 16; j++) {
						lv = lv.method_10069(field_8005.nextInt(3) - 1, (field_8005.nextInt(3) - 1) * field_8005.nextInt(3) / 2, field_8005.nextInt(3) - 1);
						lv2 = arg2.method_8310(lv);
						if (class_2248.method_9614(arg2.method_8320(lv).method_11628(arg2, lv))) {
							continue label79;
						}
					}

					if (lv2 == class_1972.field_9408 || lv2 == class_1972.field_9448) {
						if (i == 0 && arg4 != null && arg4.method_10166().method_10179()) {
							lv3 = class_3481.field_15476.method_15142(arg2.field_9229).method_9564().method_11657(class_2222.field_9933, arg4);
						} else if (field_8005.nextInt(4) == 0) {
							lv3 = class_3481.field_15496.method_15142(field_8005).method_9564();
						}
					}

					if (lv3.method_11614().method_9525(class_3481.field_15476)) {
						for (int jx = 0; !lv3.method_11591(arg2, lv) && jx < 4; jx++) {
							lv3 = lv3.method_11657(class_2222.field_9933, class_2350.class_2353.field_11062.method_10183(field_8005));
						}
					}

					if (lv3.method_11591(arg2, lv)) {
						class_2680 lv4 = arg2.method_8320(lv);
						if (lv4.method_11614() == class_2246.field_10382 && arg2.method_8316(lv).method_15761() == 8) {
							arg2.method_8652(lv, lv3, 3);
						} else if (lv4.method_11614() == class_2246.field_10376 && field_8005.nextInt(10) == 0) {
							((class_2256)class_2246.field_10376).method_9652(arg2, field_8005, lv, lv4);
						}
					}
				}

				arg.method_7934(1);
			}

			return true;
		} else {
			return false;
		}
	}

	@Environment(EnvType.CLIENT)
	public static void method_7721(class_1936 arg, class_2338 arg2, int i) {
		if (i == 0) {
			i = 15;
		}

		class_2680 lv = arg.method_8320(arg2);
		if (!lv.method_11588()) {
			for (int j = 0; j < i; j++) {
				double d = field_8005.nextGaussian() * 0.02;
				double e = field_8005.nextGaussian() * 0.02;
				double f = field_8005.nextGaussian() * 0.02;
				arg.method_8406(
					class_2398.field_11211,
					(double)((float)arg2.method_10263() + field_8005.nextFloat()),
					(double)arg2.method_10264() + (double)field_8005.nextFloat() * lv.method_17770(arg, arg2).method_1105(class_2350.class_2351.field_11052),
					(double)((float)arg2.method_10260() + field_8005.nextFloat()),
					d,
					e,
					f
				);
			}
		}
	}
}
