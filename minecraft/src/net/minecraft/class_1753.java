package net.minecraft;

import java.util.function.Predicate;

public class class_1753 extends class_1811 {
	public class_1753(class_1792.class_1793 arg) {
		super(arg);
		this.method_7863(new class_2960("pull"), (argx, arg2, arg3) -> {
			if (arg3 == null) {
				return 0.0F;
			} else {
				return arg3.method_6030().method_7909() != class_1802.field_8102 ? 0.0F : (float)(argx.method_7935() - arg3.method_6014()) / 20.0F;
			}
		});
		this.method_7863(new class_2960("pulling"), (argx, arg2, arg3) -> arg3 != null && arg3.method_6115() && arg3.method_6030() == argx ? 1.0F : 0.0F);
	}

	@Override
	public void method_7840(class_1799 arg, class_1937 arg2, class_1309 arg3, int i) {
		if (arg3 instanceof class_1657) {
			class_1657 lv = (class_1657)arg3;
			boolean bl = lv.field_7503.field_7477 || class_1890.method_8225(class_1893.field_9125, arg) > 0;
			class_1799 lv2 = lv.method_18808(arg);
			if (!lv2.method_7960() || bl) {
				if (lv2.method_7960()) {
					lv2 = new class_1799(class_1802.field_8107);
				}

				int j = this.method_7881(arg) - i;
				float f = method_7722(j);
				if (!((double)f < 0.1)) {
					boolean bl2 = bl && lv2.method_7909() == class_1802.field_8107;
					if (!arg2.field_9236) {
						class_1744 lv3 = (class_1744)(lv2.method_7909() instanceof class_1744 ? lv2.method_7909() : class_1802.field_8107);
						class_1665 lv4 = lv3.method_7702(arg2, lv2, lv);
						lv4.method_7474(lv, lv.field_5965, lv.field_6031, 0.0F, f * 3.0F, 1.0F);
						if (f == 1.0F) {
							lv4.method_7439(true);
						}

						int k = class_1890.method_8225(class_1893.field_9103, arg);
						if (k > 0) {
							lv4.method_7438(lv4.method_7448() + (double)k * 0.5 + 0.5);
						}

						int l = class_1890.method_8225(class_1893.field_9116, arg);
						if (l > 0) {
							lv4.method_7449(l);
						}

						if (class_1890.method_8225(class_1893.field_9126, arg) > 0) {
							lv4.method_5639(100);
						}

						arg.method_7956(1, lv);
						if (bl2 || lv.field_7503.field_7477 && (lv2.method_7909() == class_1802.field_8236 || lv2.method_7909() == class_1802.field_8087)) {
							lv4.field_7572 = class_1665.class_1666.field_7594;
						}

						arg2.method_8649(lv4);
					}

					arg2.method_8465(
						null,
						lv.field_5987,
						lv.field_6010,
						lv.field_6035,
						class_3417.field_14600,
						class_3419.field_15248,
						1.0F,
						1.0F / (field_8005.nextFloat() * 0.4F + 1.2F) + f * 0.5F
					);
					if (!bl2 && !lv.field_7503.field_7477) {
						lv2.method_7934(1);
						if (lv2.method_7960()) {
							lv.field_7514.method_7378(lv2);
						}
					}

					lv.method_7259(class_3468.field_15372.method_14956(this));
				}
			}
		}
	}

	public static float method_7722(int i) {
		float f = (float)i / 20.0F;
		f = (f * f + f * 2.0F) / 3.0F;
		if (f > 1.0F) {
			f = 1.0F;
		}

		return f;
	}

	@Override
	public int method_7881(class_1799 arg) {
		return 72000;
	}

	@Override
	public class_1839 method_7853(class_1799 arg) {
		return class_1839.field_8953;
	}

	@Override
	public class_1271<class_1799> method_7836(class_1937 arg, class_1657 arg2, class_1268 arg3) {
		class_1799 lv = arg2.method_5998(arg3);
		boolean bl = !arg2.method_18808(lv).method_7960();
		if (arg2.field_7503.field_7477 || bl) {
			arg2.method_6019(arg3);
			return new class_1271<>(class_1269.field_5812, lv);
		} else {
			return bl ? new class_1271<>(class_1269.field_5811, lv) : new class_1271<>(class_1269.field_5814, lv);
		}
	}

	@Override
	public Predicate<class_1799> method_19268() {
		return field_18281;
	}
}
