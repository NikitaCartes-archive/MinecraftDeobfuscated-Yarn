package net.minecraft;

import com.google.common.collect.ImmutableSet;
import java.util.List;
import java.util.Set;
import javax.annotation.Nullable;

public final class class_1675 {
	public static class_239 method_7482(class_1297 arg, boolean bl, boolean bl2, @Nullable class_1297 arg2) {
		double d = arg.field_5987;
		double e = arg.field_6010;
		double f = arg.field_6035;
		double g = arg.field_5967;
		double h = arg.field_5984;
		double i = arg.field_6006;
		class_1937 lv = arg.field_6002;
		class_243 lv2 = new class_243(d, e, f);
		if (!lv.method_8590(arg, arg.method_5829(), (Set<class_1297>)(!bl2 && arg2 != null ? method_7483(arg2) : ImmutableSet.of()))) {
			return new class_239(class_239.class_240.field_1332, lv2, class_2350.method_10142(g, h, i), new class_2338(arg));
		} else {
			class_243 lv3 = new class_243(d + g, e + h, f + i);
			class_239 lv4 = lv.method_8531(lv2, lv3, class_242.field_1348, true, false);
			if (bl) {
				if (lv4 != null) {
					lv3 = new class_243(lv4.field_1329.field_1352, lv4.field_1329.field_1351, lv4.field_1329.field_1350);
				}

				class_1297 lv5 = null;
				List<class_1297> list = lv.method_8335(arg, arg.method_5829().method_1012(g, h, i).method_1014(1.0));
				double j = 0.0;

				for (int k = 0; k < list.size(); k++) {
					class_1297 lv6 = (class_1297)list.get(k);
					if (lv6.method_5863() && (bl2 || !lv6.method_5779(arg2)) && !lv6.field_5960) {
						class_238 lv7 = lv6.method_5829().method_1014(0.3F);
						class_239 lv8 = lv7.method_1004(lv2, lv3);
						if (lv8 != null) {
							double l = lv2.method_1025(lv8.field_1329);
							if (l < j || j == 0.0) {
								lv5 = lv6;
								j = l;
							}
						}
					}
				}

				if (lv5 != null) {
					lv4 = new class_239(lv5);
				}
			}

			return lv4;
		}
	}

	private static Set<class_1297> method_7483(class_1297 arg) {
		class_1297 lv = arg.method_5854();
		return lv != null ? ImmutableSet.of(arg, lv) : ImmutableSet.of(arg);
	}

	public static final void method_7484(class_1297 arg, float f) {
		double d = arg.field_5967;
		double e = arg.field_5984;
		double g = arg.field_6006;
		float h = class_3532.method_15368(d * d + g * g);
		arg.field_6031 = (float)(class_3532.method_15349(g, d) * 180.0F / (float)Math.PI) + 90.0F;
		arg.field_5965 = (float)(class_3532.method_15349((double)h, e) * 180.0F / (float)Math.PI) - 90.0F;

		while (arg.field_5965 - arg.field_6004 < -180.0F) {
			arg.field_6004 -= 360.0F;
		}

		while (arg.field_5965 - arg.field_6004 >= 180.0F) {
			arg.field_6004 += 360.0F;
		}

		while (arg.field_6031 - arg.field_5982 < -180.0F) {
			arg.field_5982 -= 360.0F;
		}

		while (arg.field_6031 - arg.field_5982 >= 180.0F) {
			arg.field_5982 += 360.0F;
		}

		arg.field_5965 = class_3532.method_16439(f, arg.field_6004, arg.field_5965);
		arg.field_6031 = class_3532.method_16439(f, arg.field_5982, arg.field_6031);
	}
}
