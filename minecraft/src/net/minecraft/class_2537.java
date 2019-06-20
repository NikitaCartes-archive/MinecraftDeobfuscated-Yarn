package net.minecraft;

import com.google.common.base.MoreObjects;
import java.util.Random;
import javax.annotation.Nullable;

public class class_2537 extends class_2248 {
	public static final class_2753 field_11666 = class_2383.field_11177;
	public static final class_2746 field_11671 = class_2741.field_12484;
	public static final class_2746 field_11669 = class_2741.field_12493;
	protected static final class_265 field_11665 = class_2248.method_9541(5.0, 0.0, 10.0, 11.0, 10.0, 16.0);
	protected static final class_265 field_11668 = class_2248.method_9541(5.0, 0.0, 0.0, 11.0, 10.0, 6.0);
	protected static final class_265 field_11670 = class_2248.method_9541(10.0, 0.0, 5.0, 16.0, 10.0, 11.0);
	protected static final class_265 field_11667 = class_2248.method_9541(0.0, 0.0, 5.0, 6.0, 10.0, 11.0);

	public class_2537(class_2248.class_2251 arg) {
		super(arg);
		this.method_9590(
			this.field_10647
				.method_11664()
				.method_11657(field_11666, class_2350.field_11043)
				.method_11657(field_11671, Boolean.valueOf(false))
				.method_11657(field_11669, Boolean.valueOf(false))
		);
	}

	@Override
	public class_265 method_9530(class_2680 arg, class_1922 arg2, class_2338 arg3, class_3726 arg4) {
		switch ((class_2350)arg.method_11654(field_11666)) {
			case field_11034:
			default:
				return field_11667;
			case field_11039:
				return field_11670;
			case field_11035:
				return field_11668;
			case field_11043:
				return field_11665;
		}
	}

	@Override
	public boolean method_9558(class_2680 arg, class_1941 arg2, class_2338 arg3) {
		class_2350 lv = arg.method_11654(field_11666);
		class_2338 lv2 = arg3.method_10093(lv.method_10153());
		class_2680 lv3 = arg2.method_8320(lv2);
		return lv.method_10166().method_10179() && class_2248.method_20045(lv3, arg2, lv2, lv) && !lv3.method_11634();
	}

	@Override
	public class_2680 method_9559(class_2680 arg, class_2350 arg2, class_2680 arg3, class_1936 arg4, class_2338 arg5, class_2338 arg6) {
		return arg2.method_10153() == arg.method_11654(field_11666) && !arg.method_11591(arg4, arg5)
			? class_2246.field_10124.method_9564()
			: super.method_9559(arg, arg2, arg3, arg4, arg5, arg6);
	}

	@Nullable
	@Override
	public class_2680 method_9605(class_1750 arg) {
		class_2680 lv = this.method_9564().method_11657(field_11671, Boolean.valueOf(false)).method_11657(field_11669, Boolean.valueOf(false));
		class_1941 lv2 = arg.method_8045();
		class_2338 lv3 = arg.method_8037();
		class_2350[] lvs = arg.method_7718();

		for (class_2350 lv4 : lvs) {
			if (lv4.method_10166().method_10179()) {
				class_2350 lv5 = lv4.method_10153();
				lv = lv.method_11657(field_11666, lv5);
				if (lv.method_11591(lv2, lv3)) {
					return lv;
				}
			}
		}

		return null;
	}

	@Override
	public void method_9567(class_1937 arg, class_2338 arg2, class_2680 arg3, class_1309 arg4, class_1799 arg5) {
		this.method_10776(arg, arg2, arg3, false, false, -1, null);
	}

	public void method_10776(class_1937 arg, class_2338 arg2, class_2680 arg3, boolean bl, boolean bl2, int i, @Nullable class_2680 arg4) {
		class_2350 lv = arg3.method_11654(field_11666);
		boolean bl3 = (Boolean)arg3.method_11654(field_11669);
		boolean bl4 = (Boolean)arg3.method_11654(field_11671);
		boolean bl5 = !bl;
		boolean bl6 = false;
		int j = 0;
		class_2680[] lvs = new class_2680[42];

		for (int k = 1; k < 42; k++) {
			class_2338 lv2 = arg2.method_10079(lv, k);
			class_2680 lv3 = arg.method_8320(lv2);
			if (lv3.method_11614() == class_2246.field_10348) {
				if (lv3.method_11654(field_11666) == lv.method_10153()) {
					j = k;
				}
				break;
			}

			if (lv3.method_11614() != class_2246.field_10589 && k != i) {
				lvs[k] = null;
				bl5 = false;
			} else {
				if (k == i) {
					lv3 = MoreObjects.firstNonNull(arg4, lv3);
				}

				boolean bl7 = !(Boolean)lv3.method_11654(class_2538.field_11679);
				boolean bl8 = (Boolean)lv3.method_11654(class_2538.field_11680);
				bl6 |= bl7 && bl8;
				lvs[k] = lv3;
				if (k == i) {
					arg.method_8397().method_8676(arg2, this, this.method_9563(arg));
					bl5 &= bl7;
				}
			}
		}

		bl5 &= j > 1;
		bl6 &= bl5;
		class_2680 lv4 = this.method_9564().method_11657(field_11669, Boolean.valueOf(bl5)).method_11657(field_11671, Boolean.valueOf(bl6));
		if (j > 0) {
			class_2338 lv2x = arg2.method_10079(lv, j);
			class_2350 lv5 = lv.method_10153();
			arg.method_8652(lv2x, lv4.method_11657(field_11666, lv5), 3);
			this.method_10775(arg, lv2x, lv5);
			this.method_10777(arg, lv2x, bl5, bl6, bl3, bl4);
		}

		this.method_10777(arg, arg2, bl5, bl6, bl3, bl4);
		if (!bl) {
			arg.method_8652(arg2, lv4.method_11657(field_11666, lv), 3);
			if (bl2) {
				this.method_10775(arg, arg2, lv);
			}
		}

		if (bl3 != bl5) {
			for (int l = 1; l < j; l++) {
				class_2338 lv6 = arg2.method_10079(lv, l);
				class_2680 lv7 = lvs[l];
				if (lv7 != null) {
					arg.method_8652(lv6, lv7.method_11657(field_11669, Boolean.valueOf(bl5)), 3);
					if (!arg.method_8320(lv6).method_11588()) {
					}
				}
			}
		}
	}

	@Override
	public void method_9588(class_2680 arg, class_1937 arg2, class_2338 arg3, Random random) {
		this.method_10776(arg2, arg3, arg, false, true, -1, null);
	}

	private void method_10777(class_1937 arg, class_2338 arg2, boolean bl, boolean bl2, boolean bl3, boolean bl4) {
		if (bl2 && !bl4) {
			arg.method_8396(null, arg2, class_3417.field_14674, class_3419.field_15245, 0.4F, 0.6F);
		} else if (!bl2 && bl4) {
			arg.method_8396(null, arg2, class_3417.field_14787, class_3419.field_15245, 0.4F, 0.5F);
		} else if (bl && !bl3) {
			arg.method_8396(null, arg2, class_3417.field_14859, class_3419.field_15245, 0.4F, 0.7F);
		} else if (!bl && bl3) {
			arg.method_8396(null, arg2, class_3417.field_14595, class_3419.field_15245, 0.4F, 1.2F / (arg.field_9229.nextFloat() * 0.2F + 0.9F));
		}
	}

	private void method_10775(class_1937 arg, class_2338 arg2, class_2350 arg3) {
		arg.method_8452(arg2, this);
		arg.method_8452(arg2.method_10093(arg3.method_10153()), this);
	}

	@Override
	public void method_9536(class_2680 arg, class_1937 arg2, class_2338 arg3, class_2680 arg4, boolean bl) {
		if (!bl && arg.method_11614() != arg4.method_11614()) {
			boolean bl2 = (Boolean)arg.method_11654(field_11669);
			boolean bl3 = (Boolean)arg.method_11654(field_11671);
			if (bl2 || bl3) {
				this.method_10776(arg2, arg3, arg, true, false, -1, null);
			}

			if (bl3) {
				arg2.method_8452(arg3, this);
				arg2.method_8452(arg3.method_10093(((class_2350)arg.method_11654(field_11666)).method_10153()), this);
			}

			super.method_9536(arg, arg2, arg3, arg4, bl);
		}
	}

	@Override
	public int method_9524(class_2680 arg, class_1922 arg2, class_2338 arg3, class_2350 arg4) {
		return arg.method_11654(field_11671) ? 15 : 0;
	}

	@Override
	public int method_9603(class_2680 arg, class_1922 arg2, class_2338 arg3, class_2350 arg4) {
		if (!(Boolean)arg.method_11654(field_11671)) {
			return 0;
		} else {
			return arg.method_11654(field_11666) == arg4 ? 15 : 0;
		}
	}

	@Override
	public boolean method_9506(class_2680 arg) {
		return true;
	}

	@Override
	public class_1921 method_9551() {
		return class_1921.field_9175;
	}

	@Override
	public class_2680 method_9598(class_2680 arg, class_2470 arg2) {
		return arg.method_11657(field_11666, arg2.method_10503(arg.method_11654(field_11666)));
	}

	@Override
	public class_2680 method_9569(class_2680 arg, class_2415 arg2) {
		return arg.method_11626(arg2.method_10345(arg.method_11654(field_11666)));
	}

	@Override
	protected void method_9515(class_2689.class_2690<class_2248, class_2680> arg) {
		arg.method_11667(field_11666, field_11671, field_11669);
	}
}
