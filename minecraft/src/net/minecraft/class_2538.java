package net.minecraft;

import java.util.List;
import java.util.Map;
import java.util.Random;

public class class_2538 extends class_2248 {
	public static final class_2746 field_11680 = class_2741.field_12484;
	public static final class_2746 field_11683 = class_2741.field_12493;
	public static final class_2746 field_11679 = class_2741.field_12553;
	public static final class_2746 field_11675 = class_2429.field_11332;
	public static final class_2746 field_11673 = class_2429.field_11335;
	public static final class_2746 field_11678 = class_2429.field_11331;
	public static final class_2746 field_11674 = class_2429.field_11328;
	private static final Map<class_2350, class_2746> field_11676 = class_2310.field_10902;
	protected static final class_265 field_11682 = class_2248.method_9541(0.0, 1.0, 0.0, 16.0, 2.5, 16.0);
	protected static final class_265 field_11681 = class_2248.method_9541(0.0, 0.0, 0.0, 16.0, 8.0, 16.0);
	private final class_2537 field_11677;

	public class_2538(class_2537 arg, class_2248.class_2251 arg2) {
		super(arg2);
		this.method_9590(
			this.field_10647
				.method_11664()
				.method_11657(field_11680, Boolean.valueOf(false))
				.method_11657(field_11683, Boolean.valueOf(false))
				.method_11657(field_11679, Boolean.valueOf(false))
				.method_11657(field_11675, Boolean.valueOf(false))
				.method_11657(field_11673, Boolean.valueOf(false))
				.method_11657(field_11678, Boolean.valueOf(false))
				.method_11657(field_11674, Boolean.valueOf(false))
		);
		this.field_11677 = arg;
	}

	@Override
	public class_265 method_9530(class_2680 arg, class_1922 arg2, class_2338 arg3, class_3726 arg4) {
		return arg.method_11654(field_11683) ? field_11682 : field_11681;
	}

	@Override
	public class_2680 method_9605(class_1750 arg) {
		class_1922 lv = arg.method_8045();
		class_2338 lv2 = arg.method_8037();
		return this.method_9564()
			.method_11657(field_11675, Boolean.valueOf(this.method_10778(lv.method_8320(lv2.method_10095()), class_2350.field_11043)))
			.method_11657(field_11673, Boolean.valueOf(this.method_10778(lv.method_8320(lv2.method_10078()), class_2350.field_11034)))
			.method_11657(field_11678, Boolean.valueOf(this.method_10778(lv.method_8320(lv2.method_10072()), class_2350.field_11035)))
			.method_11657(field_11674, Boolean.valueOf(this.method_10778(lv.method_8320(lv2.method_10067()), class_2350.field_11039)));
	}

	@Override
	public class_2680 method_9559(class_2680 arg, class_2350 arg2, class_2680 arg3, class_1936 arg4, class_2338 arg5, class_2338 arg6) {
		return arg2.method_10166().method_10179()
			? arg.method_11657((class_2769)field_11676.get(arg2), Boolean.valueOf(this.method_10778(arg3, arg2)))
			: super.method_9559(arg, arg2, arg3, arg4, arg5, arg6);
	}

	@Override
	public class_1921 method_9551() {
		return class_1921.field_9179;
	}

	@Override
	public void method_9615(class_2680 arg, class_1937 arg2, class_2338 arg3, class_2680 arg4, boolean bl) {
		if (arg4.method_11614() != arg.method_11614()) {
			this.method_10779(arg2, arg3, arg);
		}
	}

	@Override
	public void method_9536(class_2680 arg, class_1937 arg2, class_2338 arg3, class_2680 arg4, boolean bl) {
		if (!bl && arg.method_11614() != arg4.method_11614()) {
			this.method_10779(arg2, arg3, arg.method_11657(field_11680, Boolean.valueOf(true)));
		}
	}

	@Override
	public void method_9576(class_1937 arg, class_2338 arg2, class_2680 arg3, class_1657 arg4) {
		if (!arg.field_9236 && !arg4.method_6047().method_7960() && arg4.method_6047().method_7909() == class_1802.field_8868) {
			arg.method_8652(arg2, arg3.method_11657(field_11679, Boolean.valueOf(true)), 4);
		}

		super.method_9576(arg, arg2, arg3, arg4);
	}

	private void method_10779(class_1937 arg, class_2338 arg2, class_2680 arg3) {
		for (class_2350 lv : new class_2350[]{class_2350.field_11035, class_2350.field_11039}) {
			for (int i = 1; i < 42; i++) {
				class_2338 lv2 = arg2.method_10079(lv, i);
				class_2680 lv3 = arg.method_8320(lv2);
				if (lv3.method_11614() == this.field_11677) {
					if (lv3.method_11654(class_2537.field_11666) == lv.method_10153()) {
						this.field_11677.method_10776(arg, lv2, lv3, false, true, i, arg3);
					}
					break;
				}

				if (lv3.method_11614() != this) {
					break;
				}
			}
		}
	}

	@Override
	public void method_9548(class_2680 arg, class_1937 arg2, class_2338 arg3, class_1297 arg4) {
		if (!arg2.field_9236) {
			if (!(Boolean)arg.method_11654(field_11680)) {
				this.method_10780(arg2, arg3);
			}
		}
	}

	@Override
	public void method_9588(class_2680 arg, class_1937 arg2, class_2338 arg3, Random random) {
		if (!arg2.field_9236) {
			if ((Boolean)arg2.method_8320(arg3).method_11654(field_11680)) {
				this.method_10780(arg2, arg3);
			}
		}
	}

	private void method_10780(class_1937 arg, class_2338 arg2) {
		class_2680 lv = arg.method_8320(arg2);
		boolean bl = (Boolean)lv.method_11654(field_11680);
		boolean bl2 = false;
		List<? extends class_1297> list = arg.method_8335(null, lv.method_17770(arg, arg2).method_1107().method_996(arg2));
		if (!list.isEmpty()) {
			for (class_1297 lv2 : list) {
				if (!lv2.method_5696()) {
					bl2 = true;
					break;
				}
			}
		}

		if (bl2 != bl) {
			lv = lv.method_11657(field_11680, Boolean.valueOf(bl2));
			arg.method_8652(arg2, lv, 3);
			this.method_10779(arg, arg2, lv);
		}

		if (bl2) {
			arg.method_8397().method_8676(new class_2338(arg2), this, this.method_9563(arg));
		}
	}

	public boolean method_10778(class_2680 arg, class_2350 arg2) {
		class_2248 lv = arg.method_11614();
		return lv == this.field_11677 ? arg.method_11654(class_2537.field_11666) == arg2.method_10153() : lv == this;
	}

	@Override
	public class_2680 method_9598(class_2680 arg, class_2470 arg2) {
		switch (arg2) {
			case field_11464:
				return arg.method_11657(field_11675, arg.method_11654(field_11678))
					.method_11657(field_11673, arg.method_11654(field_11674))
					.method_11657(field_11678, arg.method_11654(field_11675))
					.method_11657(field_11674, arg.method_11654(field_11673));
			case field_11465:
				return arg.method_11657(field_11675, arg.method_11654(field_11673))
					.method_11657(field_11673, arg.method_11654(field_11678))
					.method_11657(field_11678, arg.method_11654(field_11674))
					.method_11657(field_11674, arg.method_11654(field_11675));
			case field_11463:
				return arg.method_11657(field_11675, arg.method_11654(field_11674))
					.method_11657(field_11673, arg.method_11654(field_11675))
					.method_11657(field_11678, arg.method_11654(field_11673))
					.method_11657(field_11674, arg.method_11654(field_11678));
			default:
				return arg;
		}
	}

	@Override
	public class_2680 method_9569(class_2680 arg, class_2415 arg2) {
		switch (arg2) {
			case field_11300:
				return arg.method_11657(field_11675, arg.method_11654(field_11678)).method_11657(field_11678, arg.method_11654(field_11675));
			case field_11301:
				return arg.method_11657(field_11673, arg.method_11654(field_11674)).method_11657(field_11674, arg.method_11654(field_11673));
			default:
				return super.method_9569(arg, arg2);
		}
	}

	@Override
	protected void method_9515(class_2689.class_2690<class_2248, class_2680> arg) {
		arg.method_11667(field_11680, field_11683, field_11679, field_11675, field_11673, field_11674, field_11678);
	}
}
