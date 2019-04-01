package net.minecraft;

import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import java.util.Map;
import java.util.Random;

public class class_2315 extends class_2237 {
	public static final class_2753 field_10918 = class_2318.field_10927;
	public static final class_2746 field_10920 = class_2741.field_12522;
	private static final Map<class_1792, class_2357> field_10919 = class_156.method_654(
		new Object2ObjectOpenHashMap<>(), object2ObjectOpenHashMap -> object2ObjectOpenHashMap.defaultReturnValue(new class_2347())
	);

	public static void method_10009(class_1935 arg, class_2357 arg2) {
		field_10919.put(arg.method_8389(), arg2);
	}

	protected class_2315(class_2248.class_2251 arg) {
		super(arg);
		this.method_9590(this.field_10647.method_11664().method_11657(field_10918, class_2350.field_11043).method_11657(field_10920, Boolean.valueOf(false)));
	}

	@Override
	public int method_9563(class_1941 arg) {
		return 4;
	}

	@Override
	public boolean method_9534(class_2680 arg, class_1937 arg2, class_2338 arg3, class_1657 arg4, class_1268 arg5, class_3965 arg6) {
		if (arg2.field_9236) {
			return true;
		} else {
			class_2586 lv = arg2.method_8321(arg3);
			if (lv instanceof class_2601) {
				arg4.method_17355((class_2601)lv);
				if (lv instanceof class_2608) {
					arg4.method_7281(class_3468.field_15367);
				} else {
					arg4.method_7281(class_3468.field_15371);
				}
			}

			return true;
		}
	}

	protected void method_10012(class_1937 arg, class_2338 arg2) {
		class_2345 lv = new class_2345(arg, arg2);
		class_2601 lv2 = lv.method_10121();
		int i = lv2.method_11076();
		if (i < 0) {
			arg.method_8535(1001, arg2, 0);
		} else {
			class_1799 lv3 = lv2.method_5438(i);
			class_2357 lv4 = this.method_10011(lv3);
			if (lv4 != class_2357.field_16902) {
				lv2.method_5447(i, lv4.dispense(lv, lv3));
			}
		}
	}

	protected class_2357 method_10011(class_1799 arg) {
		return (class_2357)field_10919.get(arg.method_7909());
	}

	@Override
	public void method_9612(class_2680 arg, class_1937 arg2, class_2338 arg3, class_2248 arg4, class_2338 arg5, boolean bl) {
		boolean bl2 = arg2.method_8479(arg3) || arg2.method_8479(arg3.method_10084());
		boolean bl3 = (Boolean)arg.method_11654(field_10920);
		if (bl2 && !bl3) {
			arg2.method_8397().method_8676(arg3, this, this.method_9563(arg2));
			arg2.method_8652(arg3, arg.method_11657(field_10920, Boolean.valueOf(true)), 4);
		} else if (!bl2 && bl3) {
			arg2.method_8652(arg3, arg.method_11657(field_10920, Boolean.valueOf(false)), 4);
		}
	}

	@Override
	public void method_9588(class_2680 arg, class_1937 arg2, class_2338 arg3, Random random) {
		if (!arg2.field_9236) {
			this.method_10012(arg2, arg3);
		}
	}

	@Override
	public class_2586 method_10123(class_1922 arg) {
		return new class_2601();
	}

	@Override
	public class_2680 method_9605(class_1750 arg) {
		return this.method_9564().method_11657(field_10918, arg.method_7715().method_10153());
	}

	@Override
	public void method_9567(class_1937 arg, class_2338 arg2, class_2680 arg3, class_1309 arg4, class_1799 arg5) {
		if (arg5.method_7938()) {
			class_2586 lv = arg.method_8321(arg2);
			if (lv instanceof class_2601) {
				((class_2601)lv).method_17488(arg5.method_7964());
			}
		}
	}

	@Override
	public void method_9536(class_2680 arg, class_1937 arg2, class_2338 arg3, class_2680 arg4, boolean bl) {
		if (arg.method_11614() != arg4.method_11614()) {
			class_2586 lv = arg2.method_8321(arg3);
			if (lv instanceof class_2601) {
				class_1264.method_5451(arg2, arg3, (class_2601)lv);
				arg2.method_8455(arg3, this);
			}

			super.method_9536(arg, arg2, arg3, arg4, bl);
		}
	}

	public static class_2374 method_10010(class_2342 arg) {
		class_2350 lv = arg.method_10120().method_11654(field_10918);
		double d = arg.method_10216() + 0.7 * (double)lv.method_10148();
		double e = arg.method_10214() + 0.7 * (double)lv.method_10164();
		double f = arg.method_10215() + 0.7 * (double)lv.method_10165();
		return new class_2376(d, e, f);
	}

	@Override
	public boolean method_9498(class_2680 arg) {
		return true;
	}

	@Override
	public int method_9572(class_2680 arg, class_1937 arg2, class_2338 arg3) {
		return class_1703.method_7608(arg2.method_8321(arg3));
	}

	@Override
	public class_2464 method_9604(class_2680 arg) {
		return class_2464.field_11458;
	}

	@Override
	public class_2680 method_9598(class_2680 arg, class_2470 arg2) {
		return arg.method_11657(field_10918, arg2.method_10503(arg.method_11654(field_10918)));
	}

	@Override
	public class_2680 method_9569(class_2680 arg, class_2415 arg2) {
		return arg.method_11626(arg2.method_10345(arg.method_11654(field_10918)));
	}

	@Override
	protected void method_9515(class_2689.class_2690<class_2248, class_2680> arg) {
		arg.method_11667(field_10918, field_10920);
	}
}
