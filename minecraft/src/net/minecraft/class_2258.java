package net.minecraft;

import java.util.Random;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

public class class_2258 extends class_2248 implements class_2263 {
	public static final class_2746 field_10680 = class_2741.field_12526;

	public class_2258(class_2248.class_2251 arg) {
		super(arg);
		this.method_9590(this.field_10647.method_11664().method_11657(field_10680, Boolean.valueOf(true)));
	}

	@Override
	public void method_9548(class_2680 arg, class_1937 arg2, class_2338 arg3, class_1297 arg4) {
		class_2680 lv = arg2.method_8320(arg3.method_10084());
		if (lv.method_11588()) {
			arg4.method_5700((Boolean)arg.method_11654(field_10680));
			if (!arg2.field_9236) {
				class_3218 lv2 = (class_3218)arg2;

				for (int i = 0; i < 2; i++) {
					lv2.method_14199(
						class_2398.field_11202,
						(double)((float)arg3.method_10263() + arg2.field_9229.nextFloat()),
						(double)(arg3.method_10264() + 1),
						(double)((float)arg3.method_10260() + arg2.field_9229.nextFloat()),
						1,
						0.0,
						0.0,
						0.0,
						1.0
					);
					lv2.method_14199(
						class_2398.field_11247,
						(double)((float)arg3.method_10263() + arg2.field_9229.nextFloat()),
						(double)(arg3.method_10264() + 1),
						(double)((float)arg3.method_10260() + arg2.field_9229.nextFloat()),
						1,
						0.0,
						0.01,
						0.0,
						0.2
					);
				}
			}
		} else {
			arg4.method_5764((Boolean)arg.method_11654(field_10680));
		}
	}

	@Override
	public void method_9615(class_2680 arg, class_1937 arg2, class_2338 arg3, class_2680 arg4, boolean bl) {
		method_9657(arg2, arg3.method_10084(), method_9656(arg2, arg3.method_10074()));
	}

	@Override
	public void method_9588(class_2680 arg, class_1937 arg2, class_2338 arg3, Random random) {
		method_9657(arg2, arg3.method_10084(), method_9656(arg2, arg3));
	}

	@Override
	public class_3610 method_9545(class_2680 arg) {
		return class_3612.field_15910.method_15729(false);
	}

	public static void method_9657(class_1936 arg, class_2338 arg2, boolean bl) {
		if (method_9658(arg, arg2)) {
			arg.method_8652(arg2, class_2246.field_10422.method_9564().method_11657(field_10680, Boolean.valueOf(bl)), 2);
		}
	}

	public static boolean method_9658(class_1936 arg, class_2338 arg2) {
		class_3610 lv = arg.method_8316(arg2);
		return arg.method_8320(arg2).method_11614() == class_2246.field_10382 && lv.method_15761() >= 8 && lv.method_15771();
	}

	private static boolean method_9656(class_1922 arg, class_2338 arg2) {
		class_2680 lv = arg.method_8320(arg2);
		class_2248 lv2 = lv.method_11614();
		return lv2 == class_2246.field_10422 ? (Boolean)lv.method_11654(field_10680) : lv2 != class_2246.field_10114;
	}

	@Override
	public int method_9563(class_1941 arg) {
		return 5;
	}

	@Environment(EnvType.CLIENT)
	@Override
	public void method_9496(class_2680 arg, class_1937 arg2, class_2338 arg3, Random random) {
		double d = (double)arg3.method_10263();
		double e = (double)arg3.method_10264();
		double f = (double)arg3.method_10260();
		if ((Boolean)arg.method_11654(field_10680)) {
			arg2.method_8494(class_2398.field_11243, d + 0.5, e + 0.8, f, 0.0, 0.0, 0.0);
			if (random.nextInt(200) == 0) {
				arg2.method_8486(d, e, f, class_3417.field_14650, class_3419.field_15245, 0.2F + random.nextFloat() * 0.2F, 0.9F + random.nextFloat() * 0.15F, false);
			}
		} else {
			arg2.method_8494(class_2398.field_11238, d + 0.5, e, f + 0.5, 0.0, 0.04, 0.0);
			arg2.method_8494(class_2398.field_11238, d + (double)random.nextFloat(), e + (double)random.nextFloat(), f + (double)random.nextFloat(), 0.0, 0.04, 0.0);
			if (random.nextInt(200) == 0) {
				arg2.method_8486(d, e, f, class_3417.field_15161, class_3419.field_15245, 0.2F + random.nextFloat() * 0.2F, 0.9F + random.nextFloat() * 0.15F, false);
			}
		}
	}

	@Override
	public class_2680 method_9559(class_2680 arg, class_2350 arg2, class_2680 arg3, class_1936 arg4, class_2338 arg5, class_2338 arg6) {
		if (!arg.method_11591(arg4, arg5)) {
			return class_2246.field_10382.method_9564();
		} else {
			if (arg2 == class_2350.field_11033) {
				arg4.method_8652(arg5, class_2246.field_10422.method_9564().method_11657(field_10680, Boolean.valueOf(method_9656(arg4, arg6))), 2);
			} else if (arg2 == class_2350.field_11036 && arg3.method_11614() != class_2246.field_10422 && method_9658(arg4, arg6)) {
				arg4.method_8397().method_8676(arg5, this, this.method_9563(arg4));
			}

			arg4.method_8405().method_8676(arg5, class_3612.field_15910, class_3612.field_15910.method_15789(arg4));
			return super.method_9559(arg, arg2, arg3, arg4, arg5, arg6);
		}
	}

	@Override
	public boolean method_9558(class_2680 arg, class_1941 arg2, class_2338 arg3) {
		class_2248 lv = arg2.method_8320(arg3.method_10074()).method_11614();
		return lv == class_2246.field_10422 || lv == class_2246.field_10092 || lv == class_2246.field_10114;
	}

	@Override
	public class_265 method_9530(class_2680 arg, class_1922 arg2, class_2338 arg3, class_3726 arg4) {
		return class_259.method_1073();
	}

	@Override
	public class_1921 method_9551() {
		return class_1921.field_9179;
	}

	@Override
	public class_2464 method_9604(class_2680 arg) {
		return class_2464.field_11455;
	}

	@Override
	protected void method_9515(class_2689.class_2690<class_2248, class_2680> arg) {
		arg.method_11667(field_10680);
	}

	@Override
	public class_3611 method_9700(class_1936 arg, class_2338 arg2, class_2680 arg3) {
		arg.method_8652(arg2, class_2246.field_10124.method_9564(), 11);
		return class_3612.field_15910;
	}
}
