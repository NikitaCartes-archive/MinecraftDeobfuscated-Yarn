package net.minecraft;

import java.util.Random;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

public class class_2397 extends class_2248 {
	public static final class_2758 field_11199 = class_2741.field_12541;
	public static final class_2746 field_11200 = class_2741.field_12514;
	protected static boolean field_11198;

	public class_2397(class_2248.class_2251 arg) {
		super(arg);
		this.method_9590(this.field_10647.method_11664().method_11657(field_11199, Integer.valueOf(7)).method_11657(field_11200, Boolean.valueOf(false)));
	}

	@Override
	public boolean method_9542(class_2680 arg) {
		return (Integer)arg.method_11654(field_11199) == 7 && !(Boolean)arg.method_11654(field_11200);
	}

	@Override
	public void method_9514(class_2680 arg, class_1937 arg2, class_2338 arg3, Random random) {
		if (!(Boolean)arg.method_11654(field_11200) && (Integer)arg.method_11654(field_11199) == 7) {
			method_9497(arg, arg2, arg3);
			arg2.method_8650(arg3, false);
		}
	}

	@Override
	public void method_9588(class_2680 arg, class_1937 arg2, class_2338 arg3, Random random) {
		arg2.method_8652(arg3, method_10300(arg, arg2, arg3), 3);
	}

	@Override
	public int method_9505(class_2680 arg, class_1922 arg2, class_2338 arg3) {
		return 1;
	}

	@Override
	public class_2680 method_9559(class_2680 arg, class_2350 arg2, class_2680 arg3, class_1936 arg4, class_2338 arg5, class_2338 arg6) {
		int i = method_10302(arg3) + 1;
		if (i != 1 || (Integer)arg.method_11654(field_11199) != i) {
			arg4.method_8397().method_8676(arg5, this, 1);
		}

		return arg;
	}

	private static class_2680 method_10300(class_2680 arg, class_1936 arg2, class_2338 arg3) {
		int i = 7;

		try (class_2338.class_2340 lv = class_2338.class_2340.method_10109()) {
			for (class_2350 lv2 : class_2350.values()) {
				lv.method_10114(arg3).method_10118(lv2);
				i = Math.min(i, method_10302(arg2.method_8320(lv)) + 1);
				if (i == 1) {
					break;
				}
			}
		}

		return arg.method_11657(field_11199, Integer.valueOf(i));
	}

	private static int method_10302(class_2680 arg) {
		if (class_3481.field_15475.method_15141(arg.method_11614())) {
			return 0;
		} else {
			return arg.method_11614() instanceof class_2397 ? (Integer)arg.method_11654(field_11199) : 7;
		}
	}

	@Environment(EnvType.CLIENT)
	@Override
	public void method_9496(class_2680 arg, class_1937 arg2, class_2338 arg3, Random random) {
		if (arg2.method_8520(arg3.method_10084())) {
			if (random.nextInt(15) == 1) {
				class_2338 lv = arg3.method_10074();
				class_2680 lv2 = arg2.method_8320(lv);
				if (!lv2.method_11619() || !class_2248.method_20045(lv2, arg2, lv, class_2350.field_11036)) {
					double d = (double)((float)arg3.method_10263() + random.nextFloat());
					double e = (double)arg3.method_10264() - 0.05;
					double f = (double)((float)arg3.method_10260() + random.nextFloat());
					arg2.method_8406(class_2398.field_11232, d, e, f, 0.0, 0.0, 0.0);
				}
			}
		}
	}

	@Environment(EnvType.CLIENT)
	public static void method_10301(boolean bl) {
		field_11198 = bl;
	}

	@Override
	public boolean method_9601(class_2680 arg) {
		return false;
	}

	@Override
	public class_1921 method_9551() {
		return field_11198 ? class_1921.field_9175 : class_1921.field_9178;
	}

	@Override
	public boolean method_16362(class_2680 arg, class_1922 arg2, class_2338 arg3) {
		return false;
	}

	@Override
	public boolean method_9523(class_2680 arg, class_1922 arg2, class_2338 arg3, class_1299<?> arg4) {
		return arg4 == class_1299.field_6081 || arg4 == class_1299.field_6104;
	}

	@Override
	protected void method_9515(class_2689.class_2690<class_2248, class_2680> arg) {
		arg.method_11667(field_11199, field_11200);
	}

	@Override
	public class_2680 method_9605(class_1750 arg) {
		return method_10300(this.method_9564().method_11657(field_11200, Boolean.valueOf(true)), arg.method_8045(), arg.method_8037());
	}
}
