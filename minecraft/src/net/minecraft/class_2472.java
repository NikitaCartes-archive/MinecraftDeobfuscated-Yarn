package net.minecraft;

import java.util.Random;
import javax.annotation.Nullable;

public class class_2472 extends class_2261 implements class_2256, class_3737 {
	public static final class_2758 field_11472 = class_2741.field_12543;
	public static final class_2746 field_11475 = class_2741.field_12508;
	protected static final class_265 field_11473 = class_2248.method_9541(6.0, 0.0, 6.0, 10.0, 6.0, 10.0);
	protected static final class_265 field_11470 = class_2248.method_9541(3.0, 0.0, 3.0, 13.0, 6.0, 13.0);
	protected static final class_265 field_11471 = class_2248.method_9541(2.0, 0.0, 2.0, 14.0, 6.0, 14.0);
	protected static final class_265 field_11474 = class_2248.method_9541(2.0, 0.0, 2.0, 14.0, 7.0, 14.0);

	protected class_2472(class_2248.class_2251 arg) {
		super(arg);
		this.method_9590(this.field_10647.method_11664().method_11657(field_11472, Integer.valueOf(1)).method_11657(field_11475, Boolean.valueOf(true)));
	}

	@Override
	public int method_9593(class_2680 arg) {
		return this.method_10506(arg) ? 0 : super.method_9593(arg) + 3 * (Integer)arg.method_11654(field_11472);
	}

	@Nullable
	@Override
	public class_2680 method_9605(class_1750 arg) {
		class_2680 lv = arg.method_8045().method_8320(arg.method_8037());
		if (lv.method_11614() == this) {
			return lv.method_11657(field_11472, Integer.valueOf(Math.min(4, (Integer)lv.method_11654(field_11472) + 1)));
		} else {
			class_3610 lv2 = arg.method_8045().method_8316(arg.method_8037());
			boolean bl = lv2.method_15767(class_3486.field_15517) && lv2.method_15761() == 8;
			return super.method_9605(arg).method_11657(field_11475, Boolean.valueOf(bl));
		}
	}

	private boolean method_10506(class_2680 arg) {
		return !(Boolean)arg.method_11654(field_11475);
	}

	@Override
	protected boolean method_9695(class_2680 arg, class_1922 arg2, class_2338 arg3) {
		return !arg.method_11628(arg2, arg3).method_1098(class_2350.field_11036).method_1110();
	}

	@Override
	public boolean method_9558(class_2680 arg, class_1941 arg2, class_2338 arg3) {
		class_2338 lv = arg3.method_10074();
		return this.method_9695(arg2.method_8320(lv), arg2, lv);
	}

	@Override
	public class_2680 method_9559(class_2680 arg, class_2350 arg2, class_2680 arg3, class_1936 arg4, class_2338 arg5, class_2338 arg6) {
		if (!arg.method_11591(arg4, arg5)) {
			return class_2246.field_10124.method_9564();
		} else {
			if ((Boolean)arg.method_11654(field_11475)) {
				arg4.method_8405().method_8676(arg5, class_3612.field_15910, class_3612.field_15910.method_15789(arg4));
			}

			return super.method_9559(arg, arg2, arg3, arg4, arg5, arg6);
		}
	}

	@Override
	public boolean method_9616(class_2680 arg, class_1750 arg2) {
		return arg2.method_8041().method_7909() == this.method_8389() && arg.method_11654(field_11472) < 4 ? true : super.method_9616(arg, arg2);
	}

	@Override
	public class_265 method_9530(class_2680 arg, class_1922 arg2, class_2338 arg3, class_3726 arg4) {
		switch (arg.method_11654(field_11472)) {
			case 1:
			default:
				return field_11473;
			case 2:
				return field_11470;
			case 3:
				return field_11471;
			case 4:
				return field_11474;
		}
	}

	@Override
	public class_3610 method_9545(class_2680 arg) {
		return arg.method_11654(field_11475) ? class_3612.field_15910.method_15729(false) : super.method_9545(arg);
	}

	@Override
	protected void method_9515(class_2689.class_2690<class_2248, class_2680> arg) {
		arg.method_11667(field_11472, field_11475);
	}

	@Override
	public boolean method_9651(class_1922 arg, class_2338 arg2, class_2680 arg3, boolean bl) {
		return true;
	}

	@Override
	public boolean method_9650(class_1937 arg, Random random, class_2338 arg2, class_2680 arg3) {
		return true;
	}

	@Override
	public void method_9652(class_1937 arg, Random random, class_2338 arg2, class_2680 arg3) {
		if (!this.method_10506(arg3) && arg.method_8320(arg2.method_10074()).method_11602(class_3481.field_15461)) {
			int i = 5;
			int j = 1;
			int k = 2;
			int l = 0;
			int m = arg2.method_10263() - 2;
			int n = 0;

			for (int o = 0; o < 5; o++) {
				for (int p = 0; p < j; p++) {
					int q = 2 + arg2.method_10264() - 1;

					for (int r = q - 2; r < q; r++) {
						class_2338 lv = new class_2338(m + o, r, arg2.method_10260() - n + p);
						if (lv != arg2 && random.nextInt(6) == 0 && arg.method_8320(lv).method_11614() == class_2246.field_10382) {
							class_2680 lv2 = arg.method_8320(lv.method_10074());
							if (lv2.method_11602(class_3481.field_15461)) {
								arg.method_8652(lv, class_2246.field_10476.method_9564().method_11657(field_11472, Integer.valueOf(random.nextInt(4) + 1)), 3);
							}
						}
					}
				}

				if (l < 2) {
					j += 2;
					n++;
				} else {
					j -= 2;
					n--;
				}

				l++;
			}

			arg.method_8652(arg2, arg3.method_11657(field_11472, Integer.valueOf(4)), 2);
		}
	}
}
