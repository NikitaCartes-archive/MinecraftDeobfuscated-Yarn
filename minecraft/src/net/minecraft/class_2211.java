package net.minecraft;

import java.util.Random;
import javax.annotation.Nullable;

public class class_2211 extends class_2248 implements class_2256 {
	protected static final class_265 field_9912 = class_2248.method_9541(5.0, 0.0, 5.0, 11.0, 16.0, 11.0);
	protected static final class_265 field_9915 = class_2248.method_9541(3.0, 0.0, 3.0, 13.0, 16.0, 13.0);
	protected static final class_265 field_9913 = class_2248.method_9541(6.5, 0.0, 6.5, 9.5, 16.0, 9.5);
	public static final class_2758 field_9914 = class_2741.field_12521;
	public static final class_2754<class_2737> field_9917 = class_2741.field_12516;
	public static final class_2758 field_9916 = class_2741.field_12549;

	public class_2211(class_2248.class_2251 arg) {
		super(arg);
		this.method_9590(
			this.field_10647
				.method_11664()
				.method_11657(field_9914, Integer.valueOf(0))
				.method_11657(field_9917, class_2737.field_12469)
				.method_11657(field_9916, Integer.valueOf(0))
		);
	}

	@Override
	protected void method_9515(class_2689.class_2690<class_2248, class_2680> arg) {
		arg.method_11667(field_9914, field_9917, field_9916);
	}

	@Override
	public class_2248.class_2250 method_16841() {
		return class_2248.class_2250.field_10657;
	}

	@Override
	public boolean method_9579(class_2680 arg, class_1922 arg2, class_2338 arg3) {
		return true;
	}

	@Override
	public class_265 method_9530(class_2680 arg, class_1922 arg2, class_2338 arg3, class_3726 arg4) {
		class_265 lv = arg.method_11654(field_9917) == class_2737.field_12468 ? field_9915 : field_9912;
		class_243 lv2 = arg.method_11599(arg2, arg3);
		return lv.method_1096(lv2.field_1352, lv2.field_1351, lv2.field_1350);
	}

	@Override
	public class_265 method_9549(class_2680 arg, class_1922 arg2, class_2338 arg3, class_3726 arg4) {
		class_243 lv = arg.method_11599(arg2, arg3);
		return field_9913.method_1096(lv.field_1352, lv.field_1351, lv.field_1350);
	}

	@Nullable
	@Override
	public class_2680 method_9605(class_1750 arg) {
		class_3610 lv = arg.method_8045().method_8316(arg.method_8037());
		if (!lv.method_15769()) {
			return null;
		} else {
			class_2680 lv2 = arg.method_8045().method_8320(arg.method_8037().method_10074());
			if (lv2.method_11602(class_3481.field_15497)) {
				class_2248 lv3 = lv2.method_11614();
				if (lv3 == class_2246.field_10108) {
					return this.method_9564().method_11657(field_9914, Integer.valueOf(0));
				} else if (lv3 == class_2246.field_10211) {
					int i = lv2.method_11654(field_9914) > 0 ? 1 : 0;
					return this.method_9564().method_11657(field_9914, Integer.valueOf(i));
				} else {
					return class_2246.field_10108.method_9564();
				}
			} else {
				return null;
			}
		}
	}

	@Override
	public void method_9588(class_2680 arg, class_1937 arg2, class_2338 arg3, Random random) {
		if (!arg.method_11591(arg2, arg3)) {
			arg2.method_8651(arg3, true);
		} else if ((Integer)arg.method_11654(field_9916) == 0) {
			if (random.nextInt(3) == 0 && arg2.method_8623(arg3.method_10084()) && arg2.method_8624(arg3.method_10084(), 0) >= 9) {
				int i = this.method_9386(arg2, arg3) + 1;
				if (i < 16) {
					this.method_9385(arg, arg2, arg3, random, i);
				}
			}
		}
	}

	@Override
	public boolean method_9558(class_2680 arg, class_1941 arg2, class_2338 arg3) {
		return arg2.method_8320(arg3.method_10074()).method_11602(class_3481.field_15497);
	}

	@Override
	public class_2680 method_9559(class_2680 arg, class_2350 arg2, class_2680 arg3, class_1936 arg4, class_2338 arg5, class_2338 arg6) {
		if (!arg.method_11591(arg4, arg5)) {
			arg4.method_8397().method_8676(arg5, this, 1);
		}

		if (arg2 == class_2350.field_11036
			&& arg3.method_11614() == class_2246.field_10211
			&& (Integer)arg3.method_11654(field_9914) > (Integer)arg.method_11654(field_9914)) {
			arg4.method_8652(arg5, arg.method_11572(field_9914), 2);
		}

		return super.method_9559(arg, arg2, arg3, arg4, arg5, arg6);
	}

	@Override
	public boolean method_9651(class_1922 arg, class_2338 arg2, class_2680 arg3, boolean bl) {
		int i = this.method_9387(arg, arg2);
		int j = this.method_9386(arg, arg2);
		return i + j + 1 < 16 && (Integer)arg.method_8320(arg2.method_10086(i)).method_11654(field_9916) != 1;
	}

	@Override
	public boolean method_9650(class_1937 arg, Random random, class_2338 arg2, class_2680 arg3) {
		return true;
	}

	@Override
	public void method_9652(class_1937 arg, Random random, class_2338 arg2, class_2680 arg3) {
		int i = this.method_9387(arg, arg2);
		int j = this.method_9386(arg, arg2);
		int k = i + j + 1;
		int l = 1 + random.nextInt(2);

		for (int m = 0; m < l; m++) {
			class_2338 lv = arg2.method_10086(i);
			class_2680 lv2 = arg.method_8320(lv);
			if (k >= 16 || (Integer)lv2.method_11654(field_9916) == 1 || !arg.method_8623(lv.method_10084())) {
				return;
			}

			this.method_9385(lv2, arg, lv, random, k);
			i++;
			k++;
		}
	}

	@Override
	public float method_9594(class_2680 arg, class_1657 arg2, class_1922 arg3, class_2338 arg4) {
		return arg2.method_6047().method_7909() instanceof class_1829 ? 1.0F : super.method_9594(arg, arg2, arg3, arg4);
	}

	@Override
	public class_1921 method_9551() {
		return class_1921.field_9174;
	}

	protected void method_9385(class_2680 arg, class_1937 arg2, class_2338 arg3, Random random, int i) {
		class_2680 lv = arg2.method_8320(arg3.method_10074());
		class_2338 lv2 = arg3.method_10087(2);
		class_2680 lv3 = arg2.method_8320(lv2);
		class_2737 lv4 = class_2737.field_12469;
		if (i >= 1) {
			if (lv.method_11614() != class_2246.field_10211 || lv.method_11654(field_9917) == class_2737.field_12469) {
				lv4 = class_2737.field_12466;
			} else if (lv.method_11614() == class_2246.field_10211 && lv.method_11654(field_9917) != class_2737.field_12469) {
				lv4 = class_2737.field_12468;
				if (lv3.method_11614() == class_2246.field_10211) {
					arg2.method_8652(arg3.method_10074(), lv.method_11657(field_9917, class_2737.field_12466), 3);
					arg2.method_8652(lv2, lv3.method_11657(field_9917, class_2737.field_12469), 3);
				}
			}
		}

		int j = arg.method_11654(field_9914) != 1 && lv3.method_11614() != class_2246.field_10211 ? 0 : 1;
		int k = (i < 11 || !(random.nextFloat() < 0.25F)) && i != 15 ? 0 : 1;
		arg2.method_8652(
			arg3.method_10084(),
			this.method_9564().method_11657(field_9914, Integer.valueOf(j)).method_11657(field_9917, lv4).method_11657(field_9916, Integer.valueOf(k)),
			3
		);
	}

	protected int method_9387(class_1922 arg, class_2338 arg2) {
		int i = 0;

		while (i < 16 && arg.method_8320(arg2.method_10086(i + 1)).method_11614() == class_2246.field_10211) {
			i++;
		}

		return i;
	}

	protected int method_9386(class_1922 arg, class_2338 arg2) {
		int i = 0;

		while (i < 16 && arg.method_8320(arg2.method_10087(i + 1)).method_11614() == class_2246.field_10211) {
			i++;
		}

		return i;
	}
}
