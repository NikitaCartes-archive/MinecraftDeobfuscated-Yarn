package net.minecraft;

import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

public class class_2244 extends class_2383 implements class_2343 {
	public static final class_2754<class_2742> field_9967 = class_2741.field_12483;
	public static final class_2746 field_9968 = class_2741.field_12528;
	protected static final class_265 field_16788 = class_2248.method_9541(0.0, 3.0, 0.0, 16.0, 9.0, 16.0);
	protected static final class_265 field_16782 = class_2248.method_9541(0.0, 0.0, 0.0, 3.0, 3.0, 3.0);
	protected static final class_265 field_16784 = class_2248.method_9541(0.0, 0.0, 13.0, 3.0, 3.0, 16.0);
	protected static final class_265 field_16786 = class_2248.method_9541(13.0, 0.0, 0.0, 16.0, 3.0, 3.0);
	protected static final class_265 field_16789 = class_2248.method_9541(13.0, 0.0, 13.0, 16.0, 3.0, 16.0);
	protected static final class_265 field_16787 = class_259.method_1084(field_16788, class_259.method_1084(field_16782, field_16786));
	protected static final class_265 field_16785 = class_259.method_1084(field_16788, class_259.method_1084(field_16784, field_16789));
	protected static final class_265 field_16783 = class_259.method_1084(field_16788, class_259.method_1084(field_16782, field_16784));
	protected static final class_265 field_16790 = class_259.method_1084(field_16788, class_259.method_1084(field_16786, field_16789));
	private final class_1767 field_9966;

	public class_2244(class_1767 arg, class_2248.class_2251 arg2) {
		super(arg2);
		this.field_9966 = arg;
		this.method_9590(this.field_10647.method_11664().method_11657(field_9967, class_2742.field_12557).method_11657(field_9968, Boolean.valueOf(false)));
	}

	@Override
	public class_3620 method_9602(class_2680 arg, class_1922 arg2, class_2338 arg3) {
		return arg.method_11654(field_9967) == class_2742.field_12557 ? this.field_9966.method_7794() : class_3620.field_15979;
	}

	@Override
	public boolean method_9534(class_2680 arg, class_1937 arg2, class_2338 arg3, class_1657 arg4, class_1268 arg5, class_2350 arg6, float f, float g, float h) {
		if (arg2.field_9236) {
			return true;
		} else {
			if (arg.method_11654(field_9967) != class_2742.field_12560) {
				arg3 = arg3.method_10093(arg.method_11654(field_11177));
				arg = arg2.method_8320(arg3);
				if (arg.method_11614() != this) {
					return true;
				}
			}

			if (arg2.field_9247.method_12448() && arg2.method_8310(arg3) != class_1972.field_9461) {
				if ((Boolean)arg.method_11654(field_9968)) {
					class_1657 lv2 = this.method_9485(arg2, arg3);
					if (lv2 != null) {
						arg4.method_7353(new class_2588("block.minecraft.bed.occupied"), true);
						return true;
					}

					arg = arg.method_11657(field_9968, Boolean.valueOf(false));
					arg2.method_8652(arg3, arg, 4);
				}

				class_1657.class_1658 lv3 = arg4.method_7269(arg3);
				if (lv3 == class_1657.class_1658.field_7527) {
					arg = arg.method_11657(field_9968, Boolean.valueOf(true));
					arg2.method_8652(arg3, arg, 4);
					return true;
				} else {
					if (lv3 == class_1657.class_1658.field_7529) {
						arg4.method_7353(new class_2588("block.minecraft.bed.no_sleep"), true);
					} else if (lv3 == class_1657.class_1658.field_7532) {
						arg4.method_7353(new class_2588("block.minecraft.bed.not_safe"), true);
					} else if (lv3 == class_1657.class_1658.field_7530) {
						arg4.method_7353(new class_2588("block.minecraft.bed.too_far_away"), true);
					}

					return true;
				}
			} else {
				arg2.method_8650(arg3);
				class_2338 lv = arg3.method_10093(((class_2350)arg.method_11654(field_11177)).method_10153());
				if (arg2.method_8320(lv).method_11614() == this) {
					arg2.method_8650(lv);
				}

				arg2.method_8454(
					null, class_1282.method_5523(), (double)arg3.method_10263() + 0.5, (double)arg3.method_10264() + 0.5, (double)arg3.method_10260() + 0.5, 5.0F, true, true
				);
				return true;
			}
		}
	}

	@Nullable
	private class_1657 method_9485(class_1937 arg, class_2338 arg2) {
		for (class_1657 lv : arg.field_9228) {
			if (lv.method_6113() && lv.field_7519.equals(arg2)) {
				return lv;
			}
		}

		return null;
	}

	@Override
	public void method_9554(class_1937 arg, class_2338 arg2, class_1297 arg3, float f) {
		super.method_9554(arg, arg2, arg3, f * 0.5F);
	}

	@Override
	public void method_9502(class_1922 arg, class_1297 arg2) {
		if (arg2.method_5715()) {
			super.method_9502(arg, arg2);
		} else if (arg2.field_5984 < 0.0) {
			arg2.field_5984 = -arg2.field_5984 * 0.66F;
			if (!(arg2 instanceof class_1309)) {
				arg2.field_5984 *= 0.8;
			}
		}
	}

	@Override
	public class_2680 method_9559(class_2680 arg, class_2350 arg2, class_2680 arg3, class_1936 arg4, class_2338 arg5, class_2338 arg6) {
		if (arg2 == method_9488(arg.method_11654(field_9967), arg.method_11654(field_11177))) {
			return arg3.method_11614() == this && arg3.method_11654(field_9967) != arg.method_11654(field_9967)
				? arg.method_11657(field_9968, arg3.method_11654(field_9968))
				: class_2246.field_10124.method_9564();
		} else {
			return super.method_9559(arg, arg2, arg3, arg4, arg5, arg6);
		}
	}

	private static class_2350 method_9488(class_2742 arg, class_2350 arg2) {
		return arg == class_2742.field_12557 ? arg2 : arg2.method_10153();
	}

	@Override
	public void method_9556(class_1937 arg, class_1657 arg2, class_2338 arg3, class_2680 arg4, @Nullable class_2586 arg5, class_1799 arg6) {
		super.method_9556(arg, arg2, arg3, class_2246.field_10124.method_9564(), arg5, arg6);
	}

	@Override
	public void method_9536(class_2680 arg, class_1937 arg2, class_2338 arg3, class_2680 arg4, boolean bl) {
		if (arg.method_11614() != arg4.method_11614()) {
			super.method_9536(arg, arg2, arg3, arg4, bl);
			arg2.method_8544(arg3);
		}
	}

	@Override
	public void method_9576(class_1937 arg, class_2338 arg2, class_2680 arg3, class_1657 arg4) {
		class_2742 lv = arg3.method_11654(field_9967);
		class_2338 lv2 = arg2.method_10093(method_9488(lv, arg3.method_11654(field_11177)));
		class_2680 lv3 = arg.method_8320(lv2);
		if (lv3.method_11614() == this && lv3.method_11654(field_9967) != lv) {
			arg.method_8652(lv2, class_2246.field_10124.method_9564(), 35);
			arg.method_8444(arg4, 2001, lv2, class_2248.method_9507(lv3));
			if (!arg.field_9236 && !arg4.method_7337()) {
				class_1799 lv4 = arg4.method_6047();
				method_9511(arg3, arg, arg2, null, arg4, lv4);
				method_9511(lv3, arg, lv2, null, arg4, lv4);
			}

			arg4.method_7259(class_3468.field_15427.method_14956(this));
		}

		super.method_9576(arg, arg2, arg3, arg4);
	}

	@Nullable
	@Override
	public class_2680 method_9605(class_1750 arg) {
		class_2350 lv = arg.method_8042();
		class_2338 lv2 = arg.method_8037();
		class_2338 lv3 = lv2.method_10093(lv);
		return arg.method_8045().method_8320(lv3).method_11587(arg) ? this.method_9564().method_11657(field_11177, lv) : null;
	}

	@Override
	public class_265 method_9529(class_2680 arg, class_1922 arg2, class_2338 arg3) {
		class_2350 lv = arg.method_11654(field_11177);
		class_2350 lv2 = arg.method_11654(field_9967) == class_2742.field_12560 ? lv : lv.method_10153();
		switch (lv2) {
			case field_11043:
				return field_16787;
			case field_11035:
				return field_16785;
			case field_11039:
				return field_16783;
			default:
				return field_16790;
		}
	}

	@Environment(EnvType.CLIENT)
	@Override
	public boolean method_9589(class_2680 arg) {
		return true;
	}

	@Nullable
	public static class_2338 method_9484(class_1922 arg, class_2338 arg2, int i) {
		class_2350 lv = arg.method_8320(arg2).method_11654(field_11177);
		int j = arg2.method_10263();
		int k = arg2.method_10264();
		int l = arg2.method_10260();

		for (int m = 0; m <= 1; m++) {
			int n = j - lv.method_10148() * m - 1;
			int o = l - lv.method_10165() * m - 1;
			int p = n + 2;
			int q = o + 2;

			for (int r = n; r <= p; r++) {
				for (int s = o; s <= q; s++) {
					class_2338 lv2 = new class_2338(r, k, s);
					if (method_9486(arg, lv2)) {
						if (i <= 0) {
							return lv2;
						}

						i--;
					}
				}
			}
		}

		return null;
	}

	protected static boolean method_9486(class_1922 arg, class_2338 arg2) {
		class_2338 lv = arg2.method_10074();
		return arg.method_8320(lv).method_11631(arg, lv)
			&& !arg.method_8320(arg2).method_11620().method_15799()
			&& !arg.method_8320(arg2.method_10084()).method_11620().method_15799();
	}

	@Override
	public class_3619 method_9527(class_2680 arg) {
		return class_3619.field_15971;
	}

	@Override
	public class_1921 method_9551() {
		return class_1921.field_9174;
	}

	@Override
	public class_2464 method_9604(class_2680 arg) {
		return class_2464.field_11456;
	}

	@Override
	protected void method_9515(class_2689.class_2690<class_2248, class_2680> arg) {
		arg.method_11667(field_11177, field_9967, field_9968);
	}

	@Override
	public class_2586 method_10123(class_1922 arg) {
		return new class_2587(this.field_9966);
	}

	@Override
	public void method_9567(class_1937 arg, class_2338 arg2, class_2680 arg3, @Nullable class_1309 arg4, class_1799 arg5) {
		super.method_9567(arg, arg2, arg3, arg4, arg5);
		if (!arg.field_9236) {
			class_2338 lv = arg2.method_10093(arg3.method_11654(field_11177));
			arg.method_8652(lv, arg3.method_11657(field_9967, class_2742.field_12560), 3);
			arg.method_8408(arg2, class_2246.field_10124);
			arg3.method_11635(arg, arg2, 3);
		}
	}

	@Environment(EnvType.CLIENT)
	public class_1767 method_9487() {
		return this.field_9966;
	}

	@Environment(EnvType.CLIENT)
	@Override
	public long method_9535(class_2680 arg, class_2338 arg2) {
		class_2338 lv = arg2.method_10079(arg.method_11654(field_11177), arg.method_11654(field_9967) == class_2742.field_12560 ? 0 : 1);
		return class_3532.method_15371(lv.method_10263(), arg2.method_10264(), lv.method_10260());
	}

	@Override
	public boolean method_9516(class_2680 arg, class_1922 arg2, class_2338 arg3, class_10 arg4) {
		return false;
	}
}
