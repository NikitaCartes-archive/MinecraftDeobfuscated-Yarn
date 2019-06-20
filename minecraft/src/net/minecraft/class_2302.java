package net.minecraft;

import java.util.Random;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

public class class_2302 extends class_2261 implements class_2256 {
	public static final class_2758 field_10835 = class_2741.field_12550;
	private static final class_265[] field_10836 = new class_265[]{
		class_2248.method_9541(0.0, 0.0, 0.0, 16.0, 2.0, 16.0),
		class_2248.method_9541(0.0, 0.0, 0.0, 16.0, 4.0, 16.0),
		class_2248.method_9541(0.0, 0.0, 0.0, 16.0, 6.0, 16.0),
		class_2248.method_9541(0.0, 0.0, 0.0, 16.0, 8.0, 16.0),
		class_2248.method_9541(0.0, 0.0, 0.0, 16.0, 10.0, 16.0),
		class_2248.method_9541(0.0, 0.0, 0.0, 16.0, 12.0, 16.0),
		class_2248.method_9541(0.0, 0.0, 0.0, 16.0, 14.0, 16.0),
		class_2248.method_9541(0.0, 0.0, 0.0, 16.0, 16.0, 16.0)
	};

	protected class_2302(class_2248.class_2251 arg) {
		super(arg);
		this.method_9590(this.field_10647.method_11664().method_11657(this.method_9824(), Integer.valueOf(0)));
	}

	@Override
	public class_265 method_9530(class_2680 arg, class_1922 arg2, class_2338 arg3, class_3726 arg4) {
		return field_10836[arg.method_11654(this.method_9824())];
	}

	@Override
	protected boolean method_9695(class_2680 arg, class_1922 arg2, class_2338 arg3) {
		return arg.method_11614() == class_2246.field_10362;
	}

	public class_2758 method_9824() {
		return field_10835;
	}

	public int method_9827() {
		return 7;
	}

	protected int method_9829(class_2680 arg) {
		return (Integer)arg.method_11654(this.method_9824());
	}

	public class_2680 method_9828(int i) {
		return this.method_9564().method_11657(this.method_9824(), Integer.valueOf(i));
	}

	public boolean method_9825(class_2680 arg) {
		return (Integer)arg.method_11654(this.method_9824()) >= this.method_9827();
	}

	@Override
	public void method_9588(class_2680 arg, class_1937 arg2, class_2338 arg3, Random random) {
		super.method_9588(arg, arg2, arg3, random);
		if (arg2.method_8624(arg3, 0) >= 9) {
			int i = this.method_9829(arg);
			if (i < this.method_9827()) {
				float f = method_9830(this, arg2, arg3);
				if (random.nextInt((int)(25.0F / f) + 1) == 0) {
					arg2.method_8652(arg3, this.method_9828(i + 1), 2);
				}
			}
		}
	}

	public void method_9826(class_1937 arg, class_2338 arg2, class_2680 arg3) {
		int i = this.method_9829(arg3) + this.method_9831(arg);
		int j = this.method_9827();
		if (i > j) {
			i = j;
		}

		arg.method_8652(arg2, this.method_9828(i), 2);
	}

	protected int method_9831(class_1937 arg) {
		return class_3532.method_15395(arg.field_9229, 2, 5);
	}

	protected static float method_9830(class_2248 arg, class_1922 arg2, class_2338 arg3) {
		float f = 1.0F;
		class_2338 lv = arg3.method_10074();

		for (int i = -1; i <= 1; i++) {
			for (int j = -1; j <= 1; j++) {
				float g = 0.0F;
				class_2680 lv2 = arg2.method_8320(lv.method_10069(i, 0, j));
				if (lv2.method_11614() == class_2246.field_10362) {
					g = 1.0F;
					if ((Integer)lv2.method_11654(class_2344.field_11009) > 0) {
						g = 3.0F;
					}
				}

				if (i != 0 || j != 0) {
					g /= 4.0F;
				}

				f += g;
			}
		}

		class_2338 lv3 = arg3.method_10095();
		class_2338 lv4 = arg3.method_10072();
		class_2338 lv5 = arg3.method_10067();
		class_2338 lv6 = arg3.method_10078();
		boolean bl = arg == arg2.method_8320(lv5).method_11614() || arg == arg2.method_8320(lv6).method_11614();
		boolean bl2 = arg == arg2.method_8320(lv3).method_11614() || arg == arg2.method_8320(lv4).method_11614();
		if (bl && bl2) {
			f /= 2.0F;
		} else {
			boolean bl3 = arg == arg2.method_8320(lv5.method_10095()).method_11614()
				|| arg == arg2.method_8320(lv6.method_10095()).method_11614()
				|| arg == arg2.method_8320(lv6.method_10072()).method_11614()
				|| arg == arg2.method_8320(lv5.method_10072()).method_11614();
			if (bl3) {
				f /= 2.0F;
			}
		}

		return f;
	}

	@Override
	public boolean method_9558(class_2680 arg, class_1941 arg2, class_2338 arg3) {
		return (arg2.method_8624(arg3, 0) >= 8 || arg2.method_8311(arg3)) && super.method_9558(arg, arg2, arg3);
	}

	@Override
	public void method_9548(class_2680 arg, class_1937 arg2, class_2338 arg3, class_1297 arg4) {
		if (arg4 instanceof class_1584 && arg2.method_8450().method_8355(class_1928.field_19388)) {
			arg2.method_8651(arg3, true);
		}

		super.method_9548(arg, arg2, arg3, arg4);
	}

	@Environment(EnvType.CLIENT)
	protected class_1935 method_9832() {
		return class_1802.field_8317;
	}

	@Environment(EnvType.CLIENT)
	@Override
	public class_1799 method_9574(class_1922 arg, class_2338 arg2, class_2680 arg3) {
		return new class_1799(this.method_9832());
	}

	@Override
	public boolean method_9651(class_1922 arg, class_2338 arg2, class_2680 arg3, boolean bl) {
		return !this.method_9825(arg3);
	}

	@Override
	public boolean method_9650(class_1937 arg, Random random, class_2338 arg2, class_2680 arg3) {
		return true;
	}

	@Override
	public void method_9652(class_1937 arg, Random random, class_2338 arg2, class_2680 arg3) {
		this.method_9826(arg, arg2, arg3);
	}

	@Override
	protected void method_9515(class_2689.class_2690<class_2248, class_2680> arg) {
		arg.method_11667(field_10835);
	}
}
