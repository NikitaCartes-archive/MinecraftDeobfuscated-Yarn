package net.minecraft;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import java.util.List;
import java.util.Map;
import java.util.Random;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

public class class_2459 extends class_2527 {
	public static final class_2746 field_11446 = class_2741.field_12548;
	private static final Map<class_1922, List<class_2459.class_2460>> field_11445 = Maps.<class_1922, List<class_2459.class_2460>>newHashMap();

	protected class_2459(class_2248.class_2251 arg) {
		super(arg);
		this.method_9590(this.field_10647.method_11664().method_11657(field_11446, Boolean.valueOf(true)));
	}

	@Override
	public int method_9563(class_1941 arg) {
		return 2;
	}

	@Override
	public void method_9615(class_2680 arg, class_1937 arg2, class_2338 arg3, class_2680 arg4) {
		for (class_2350 lv : class_2350.values()) {
			arg2.method_8452(arg3.method_10093(lv), this);
		}
	}

	@Override
	public void method_9536(class_2680 arg, class_1937 arg2, class_2338 arg3, class_2680 arg4, boolean bl) {
		if (!bl) {
			for (class_2350 lv : class_2350.values()) {
				arg2.method_8452(arg3.method_10093(lv), this);
			}
		}
	}

	@Override
	public int method_9524(class_2680 arg, class_1922 arg2, class_2338 arg3, class_2350 arg4) {
		return arg.method_11654(field_11446) && class_2350.field_11036 != arg4 ? 15 : 0;
	}

	protected boolean method_10488(class_1937 arg, class_2338 arg2, class_2680 arg3) {
		return arg.method_8459(arg2.method_10074(), class_2350.field_11033);
	}

	@Override
	public void method_9588(class_2680 arg, class_1937 arg2, class_2338 arg3, Random random) {
		method_10490(arg, arg2, arg3, random, this.method_10488(arg2, arg3, arg));
	}

	public static void method_10490(class_2680 arg, class_1937 arg2, class_2338 arg3, Random random, boolean bl) {
		List<class_2459.class_2460> list = (List<class_2459.class_2460>)field_11445.get(arg2);

		while (list != null && !list.isEmpty() && arg2.method_8510() - ((class_2459.class_2460)list.get(0)).field_11447 > 60L) {
			list.remove(0);
		}

		if ((Boolean)arg.method_11654(field_11446)) {
			if (bl) {
				arg2.method_8652(arg3, arg.method_11657(field_11446, Boolean.valueOf(false)), 3);
				if (method_10489(arg2, arg3, true)) {
					arg2.method_8396(
						null, arg3, class_3417.field_14909, class_3419.field_15245, 0.5F, 2.6F + (arg2.field_9229.nextFloat() - arg2.field_9229.nextFloat()) * 0.8F
					);

					for (int i = 0; i < 5; i++) {
						double d = (double)arg3.method_10263() + random.nextDouble() * 0.6 + 0.2;
						double e = (double)arg3.method_10264() + random.nextDouble() * 0.6 + 0.2;
						double f = (double)arg3.method_10260() + random.nextDouble() * 0.6 + 0.2;
						arg2.method_8406(class_2398.field_11251, d, e, f, 0.0, 0.0, 0.0);
					}

					arg2.method_8397().method_8676(arg3, arg2.method_8320(arg3).method_11614(), 160);
				}
			}
		} else if (!bl && !method_10489(arg2, arg3, false)) {
			arg2.method_8652(arg3, arg.method_11657(field_11446, Boolean.valueOf(true)), 3);
		}
	}

	@Override
	public void method_9612(class_2680 arg, class_1937 arg2, class_2338 arg3, class_2248 arg4, class_2338 arg5) {
		if ((Boolean)arg.method_11654(field_11446) == this.method_10488(arg2, arg3, arg) && !arg2.method_8397().method_8677(arg3, this)) {
			arg2.method_8397().method_8676(arg3, this, this.method_9563(arg2));
		}
	}

	@Override
	public int method_9603(class_2680 arg, class_1922 arg2, class_2338 arg3, class_2350 arg4) {
		return arg4 == class_2350.field_11033 ? arg.method_11597(arg2, arg3, arg4) : 0;
	}

	@Override
	public boolean method_9506(class_2680 arg) {
		return true;
	}

	@Environment(EnvType.CLIENT)
	@Override
	public void method_9496(class_2680 arg, class_1937 arg2, class_2338 arg3, Random random) {
		if ((Boolean)arg.method_11654(field_11446)) {
			double d = (double)arg3.method_10263() + 0.5 + (random.nextDouble() - 0.5) * 0.2;
			double e = (double)arg3.method_10264() + 0.7 + (random.nextDouble() - 0.5) * 0.2;
			double f = (double)arg3.method_10260() + 0.5 + (random.nextDouble() - 0.5) * 0.2;
			arg2.method_8406(class_2390.field_11188, d, e, f, 0.0, 0.0, 0.0);
		}
	}

	@Override
	public int method_9593(class_2680 arg) {
		return arg.method_11654(field_11446) ? super.method_9593(arg) : 0;
	}

	@Override
	protected void method_9515(class_2689.class_2690<class_2248, class_2680> arg) {
		arg.method_11667(field_11446);
	}

	private static boolean method_10489(class_1937 arg, class_2338 arg2, boolean bl) {
		List<class_2459.class_2460> list = (List<class_2459.class_2460>)field_11445.get(arg);
		if (list == null) {
			list = Lists.<class_2459.class_2460>newArrayList();
			field_11445.put(arg, list);
		}

		if (bl) {
			list.add(new class_2459.class_2460(arg2.method_10062(), arg.method_8510()));
		}

		int i = 0;

		for (int j = 0; j < list.size(); j++) {
			class_2459.class_2460 lv = (class_2459.class_2460)list.get(j);
			if (lv.field_11448.equals(arg2)) {
				if (++i >= 8) {
					return true;
				}
			}
		}

		return false;
	}

	public static class class_2460 {
		private final class_2338 field_11448;
		private final long field_11447;

		public class_2460(class_2338 arg, long l) {
			this.field_11448 = arg;
			this.field_11447 = l;
		}
	}
}
