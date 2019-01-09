package net.minecraft;

import java.util.Random;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

public class class_3830 extends class_2261 implements class_2256 {
	public static final class_2758 field_17000 = class_2741.field_12497;
	private static final class_265 field_17001 = class_2248.method_9541(3.0, 0.0, 3.0, 13.0, 8.0, 13.0);
	private static final class_265 field_17002 = class_2248.method_9541(1.0, 0.0, 1.0, 15.0, 16.0, 15.0);

	public class_3830(class_2248.class_2251 arg) {
		super(arg);
		this.method_9590(this.field_10647.method_11664().method_11657(field_17000, Integer.valueOf(0)));
	}

	@Environment(EnvType.CLIENT)
	@Override
	public class_1799 method_9574(class_1922 arg, class_2338 arg2, class_2680 arg3) {
		return new class_1799(class_1802.field_16998);
	}

	@Override
	public class_265 method_9529(class_2680 arg, class_1922 arg2, class_2338 arg3) {
		if ((Integer)arg.method_11654(field_17000) == 0) {
			return field_17001;
		} else {
			return arg.method_11654(field_17000) < 3 ? field_17002 : super.method_9529(arg, arg2, arg3);
		}
	}

	@Override
	public void method_9588(class_2680 arg, class_1937 arg2, class_2338 arg3, Random random) {
		super.method_9588(arg, arg2, arg3, random);
		int i = (Integer)arg.method_11654(field_17000);
		if (i < 3 && random.nextInt(5) == 0 && arg2.method_8624(arg3.method_10084(), 0) >= 9) {
			arg2.method_8652(arg3, arg.method_11657(field_17000, Integer.valueOf(i + 1)), 2);
		}
	}

	@Override
	public void method_9548(class_2680 arg, class_1937 arg2, class_2338 arg3, class_1297 arg4) {
		arg4.method_5844(arg, new class_243(0.8F, 0.75, 0.8F));
		if (!arg2.field_9236 && (Integer)arg.method_11654(field_17000) > 0 && (arg4.field_6038 != arg4.field_5987 || arg4.field_5989 != arg4.field_6035)) {
			double d = Math.abs(arg4.field_5987 - arg4.field_6038);
			double e = Math.abs(arg4.field_6035 - arg4.field_5989);
			if (d >= 0.003F || e >= 0.003F) {
				arg4.method_5643(class_1282.field_16992, 1.0F);
			}
		}
	}

	@Override
	public boolean method_9534(class_2680 arg, class_1937 arg2, class_2338 arg3, class_1657 arg4, class_1268 arg5, class_2350 arg6, float f, float g, float h) {
		int i = (Integer)arg.method_11654(field_17000);
		boolean bl = i == 3;
		if (!bl && arg4.method_5998(arg5).method_7909() == class_1802.field_8324) {
			return false;
		} else if (i > 1) {
			int j = 1 + arg2.field_9229.nextInt(2);
			method_9577(arg2, arg3, new class_1799(class_1802.field_16998, j + (bl ? 1 : 0)));
			arg2.method_8652(arg3, arg.method_11657(field_17000, Integer.valueOf(1)), 2);
			return true;
		} else {
			return super.method_9534(arg, arg2, arg3, arg4, arg5, arg6, f, g, h);
		}
	}

	@Override
	protected void method_9515(class_2689.class_2690<class_2248, class_2680> arg) {
		arg.method_11667(field_17000);
	}

	@Override
	public boolean method_9651(class_1922 arg, class_2338 arg2, class_2680 arg3, boolean bl) {
		return (Integer)arg3.method_11654(field_17000) < 3;
	}

	@Override
	public boolean method_9650(class_1937 arg, Random random, class_2338 arg2, class_2680 arg3) {
		return true;
	}

	@Override
	public void method_9652(class_1937 arg, Random random, class_2338 arg2, class_2680 arg3) {
		int i = Math.min(3, (Integer)arg3.method_11654(field_17000) + 1);
		arg.method_8652(arg2, arg3.method_11657(field_17000, Integer.valueOf(i)), 2);
	}
}
