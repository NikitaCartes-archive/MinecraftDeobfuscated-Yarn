package net.minecraft;

import java.util.Random;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

public class class_2449 extends class_2248 {
	public static final class_2746 field_11392 = class_2459.field_11446;

	public class_2449(class_2248.class_2251 arg) {
		super(arg);
		this.method_9590(this.method_9564().method_11657(field_11392, Boolean.valueOf(false)));
	}

	@Override
	public int method_9593(class_2680 arg) {
		return arg.method_11654(field_11392) ? super.method_9593(arg) : 0;
	}

	@Override
	public void method_9606(class_2680 arg, class_1937 arg2, class_2338 arg3, class_1657 arg4) {
		method_10441(arg, arg2, arg3);
		super.method_9606(arg, arg2, arg3, arg4);
	}

	@Override
	public void method_9591(class_1937 arg, class_2338 arg2, class_1297 arg3) {
		method_10441(arg.method_8320(arg2), arg, arg2);
		super.method_9591(arg, arg2, arg3);
	}

	@Override
	public boolean method_9534(class_2680 arg, class_1937 arg2, class_2338 arg3, class_1657 arg4, class_1268 arg5, class_2350 arg6, float f, float g, float h) {
		method_10441(arg, arg2, arg3);
		return super.method_9534(arg, arg2, arg3, arg4, arg5, arg6, f, g, h);
	}

	private static void method_10441(class_2680 arg, class_1937 arg2, class_2338 arg3) {
		method_10440(arg2, arg3);
		if (!(Boolean)arg.method_11654(field_11392)) {
			arg2.method_8652(arg3, arg.method_11657(field_11392, Boolean.valueOf(true)), 3);
		}
	}

	@Override
	public void method_9588(class_2680 arg, class_1937 arg2, class_2338 arg3, Random random) {
		if ((Boolean)arg.method_11654(field_11392)) {
			arg2.method_8652(arg3, arg.method_11657(field_11392, Boolean.valueOf(false)), 3);
		}
	}

	@Override
	public void method_9565(class_2680 arg, class_1937 arg2, class_2338 arg3, class_1799 arg4) {
		super.method_9565(arg, arg2, arg3, arg4);
		if (class_1890.method_8225(class_1893.field_9099, arg4) == 0) {
			int i = 1 + arg2.field_9229.nextInt(5);
			this.method_9583(arg2, arg3, i);
		}
	}

	@Environment(EnvType.CLIENT)
	@Override
	public void method_9496(class_2680 arg, class_1937 arg2, class_2338 arg3, Random random) {
		if ((Boolean)arg.method_11654(field_11392)) {
			method_10440(arg2, arg3);
		}
	}

	private static void method_10440(class_1937 arg, class_2338 arg2) {
		double d = 0.5625;
		Random random = arg.field_9229;

		for (class_2350 lv : class_2350.values()) {
			class_2338 lv2 = arg2.method_10093(lv);
			if (!arg.method_8320(lv2).method_11598(arg, lv2)) {
				class_2350.class_2351 lv3 = lv.method_10166();
				double e = lv3 == class_2350.class_2351.field_11048 ? 0.5 + 0.5625 * (double)lv.method_10148() : (double)random.nextFloat();
				double f = lv3 == class_2350.class_2351.field_11052 ? 0.5 + 0.5625 * (double)lv.method_10164() : (double)random.nextFloat();
				double g = lv3 == class_2350.class_2351.field_11051 ? 0.5 + 0.5625 * (double)lv.method_10165() : (double)random.nextFloat();
				arg.method_8406(class_2390.field_11188, (double)arg2.method_10263() + e, (double)arg2.method_10264() + f, (double)arg2.method_10260() + g, 0.0, 0.0, 0.0);
			}
		}
	}

	@Override
	protected void method_9515(class_2689.class_2690<class_2248, class_2680> arg) {
		arg.method_11667(field_11392);
	}
}
