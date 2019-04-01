package net.minecraft;

import com.google.common.collect.ImmutableSet;
import java.util.Optional;
import java.util.Set;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

public final class class_1675 {
	public static class_239 method_18076(class_1297 arg, boolean bl, boolean bl2, @Nullable class_1297 arg2, class_3959.class_3960 arg3) {
		return method_7482(
			arg,
			bl,
			bl2,
			arg2,
			arg3,
			true,
			arg2x -> !arg2x.method_7325() && arg2x.method_5863() && (bl2 || !arg2x.method_5779(arg2)) && !arg2x.field_5960,
			arg.method_5829().method_18804(arg.method_18798()).method_1014(1.0)
		);
	}

	public static class_239 method_18074(class_1297 arg, class_238 arg2, Predicate<class_1297> predicate, class_3959.class_3960 arg3, boolean bl) {
		return method_7482(arg, bl, false, null, arg3, false, predicate, arg2);
	}

	@Nullable
	public static class_3966 method_18077(class_1937 arg, class_1297 arg2, class_243 arg3, class_243 arg4, class_238 arg5, Predicate<class_1297> predicate) {
		return method_18078(arg, arg2, arg3, arg4, arg5, predicate, Double.MAX_VALUE);
	}

	private static class_239 method_7482(
		class_1297 arg, boolean bl, boolean bl2, @Nullable class_1297 arg2, class_3959.class_3960 arg3, boolean bl3, Predicate<class_1297> predicate, class_238 arg4
	) {
		double d = arg.field_5987;
		double e = arg.field_6010;
		double f = arg.field_6035;
		class_243 lv = arg.method_18798();
		class_1937 lv2 = arg.field_6002;
		class_243 lv3 = new class_243(d, e, f);
		if (bl3 && !lv2.method_8590(arg, arg.method_5829(), (Set<class_1297>)(!bl2 && arg2 != null ? method_7483(arg2) : ImmutableSet.of()))) {
			return new class_3965(lv3, class_2350.method_10142(lv.field_1352, lv.field_1351, lv.field_1350), new class_2338(arg), false);
		} else {
			class_243 lv4 = lv3.method_1019(lv);
			class_239 lv5 = lv2.method_17742(new class_3959(lv3, lv4, arg3, class_3959.class_242.field_1348, arg));
			if (bl) {
				if (lv5.method_17783() != class_239.class_240.field_1333) {
					lv4 = lv5.method_17784();
				}

				class_239 lv6 = method_18077(lv2, arg, lv3, lv4, arg4, predicate);
				if (lv6 != null) {
					lv5 = lv6;
				}
			}

			return lv5;
		}
	}

	@Nullable
	@Environment(EnvType.CLIENT)
	public static class_3966 method_18075(class_1297 arg, class_243 arg2, class_243 arg3, class_238 arg4, Predicate<class_1297> predicate, double d) {
		class_1937 lv = arg.field_6002;
		double e = d;
		class_1297 lv2 = null;
		class_243 lv3 = null;

		for (class_1297 lv4 : lv.method_8333(arg, arg4, predicate)) {
			class_238 lv5 = lv4.method_5829().method_1014((double)lv4.method_5871());
			Optional<class_243> optional = lv5.method_992(arg2, arg3);
			if (lv5.method_1006(arg2)) {
				if (e >= 0.0) {
					lv2 = lv4;
					lv3 = (class_243)optional.orElse(arg2);
					e = 0.0;
				}
			} else if (optional.isPresent()) {
				class_243 lv6 = (class_243)optional.get();
				double f = arg2.method_1025(lv6);
				if (f < e || e == 0.0) {
					if (lv4.method_5668() == arg.method_5668()) {
						if (e == 0.0) {
							lv2 = lv4;
							lv3 = lv6;
						}
					} else {
						lv2 = lv4;
						lv3 = lv6;
						e = f;
					}
				}
			}
		}

		return lv2 == null ? null : new class_3966(lv2, lv3);
	}

	@Nullable
	public static class_3966 method_18078(
		class_1937 arg, class_1297 arg2, class_243 arg3, class_243 arg4, class_238 arg5, Predicate<class_1297> predicate, double d
	) {
		double e = d;
		class_1297 lv = null;

		for (class_1297 lv2 : arg.method_8333(arg2, arg5, predicate)) {
			class_238 lv3 = lv2.method_5829().method_1014(0.3F);
			Optional<class_243> optional = lv3.method_992(arg3, arg4);
			if (optional.isPresent()) {
				double f = arg3.method_1025((class_243)optional.get());
				if (f < e) {
					lv = lv2;
					e = f;
				}
			}
		}

		return lv == null ? null : new class_3966(lv);
	}

	private static Set<class_1297> method_7483(class_1297 arg) {
		class_1297 lv = arg.method_5854();
		return lv != null ? ImmutableSet.of(arg, lv) : ImmutableSet.of(arg);
	}

	public static final void method_7484(class_1297 arg, float f) {
		class_243 lv = arg.method_18798();
		float g = class_3532.method_15368(class_1297.method_17996(lv));
		arg.field_6031 = (float)(class_3532.method_15349(lv.field_1350, lv.field_1352) * 180.0F / (float)Math.PI) + 90.0F;
		arg.field_5965 = (float)(class_3532.method_15349((double)g, lv.field_1351) * 180.0F / (float)Math.PI) - 90.0F;

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

	public static class_1268 method_18812(class_1309 arg, class_1792 arg2) {
		return arg.method_6047().method_7909() == arg2 ? class_1268.field_5808 : class_1268.field_5810;
	}

	public static class_1665 method_18813(class_1309 arg, class_1799 arg2, float f) {
		class_1744 lv = (class_1744)(arg2.method_7909() instanceof class_1744 ? arg2.method_7909() : class_1802.field_8107);
		class_1665 lv2 = lv.method_7702(arg.field_6002, arg2, arg);
		lv2.method_7435(arg, f);
		if (arg2.method_7909() == class_1802.field_8087 && lv2 instanceof class_1667) {
			((class_1667)lv2).method_7459(arg2);
		}

		return lv2;
	}
}
