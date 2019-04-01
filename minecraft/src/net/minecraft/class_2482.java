package net.minecraft;

import javax.annotation.Nullable;

public class class_2482 extends class_2248 implements class_3737 {
	public static final class_2754<class_2771> field_11501 = class_2741.field_12485;
	public static final class_2746 field_11502 = class_2741.field_12508;
	protected static final class_265 field_11500 = class_2248.method_9541(0.0, 0.0, 0.0, 16.0, 8.0, 16.0);
	protected static final class_265 field_11499 = class_2248.method_9541(0.0, 8.0, 0.0, 16.0, 16.0, 16.0);

	public class_2482(class_2248.class_2251 arg) {
		super(arg);
		this.method_9590(this.method_9564().method_11657(field_11501, class_2771.field_12681).method_11657(field_11502, Boolean.valueOf(false)));
	}

	@Override
	public boolean method_9526(class_2680 arg) {
		return arg.method_11654(field_11501) != class_2771.field_12682;
	}

	@Override
	protected void method_9515(class_2689.class_2690<class_2248, class_2680> arg) {
		arg.method_11667(field_11501, field_11502);
	}

	@Override
	public class_265 method_9530(class_2680 arg, class_1922 arg2, class_2338 arg3, class_3726 arg4) {
		class_2771 lv = arg.method_11654(field_11501);
		switch (lv) {
			case field_12682:
				return class_259.method_1077();
			case field_12679:
				return field_11499;
			default:
				return field_11500;
		}
	}

	@Nullable
	@Override
	public class_2680 method_9605(class_1750 arg) {
		class_2338 lv = arg.method_8037();
		class_2680 lv2 = arg.method_8045().method_8320(lv);
		if (lv2.method_11614() == this) {
			return lv2.method_11657(field_11501, class_2771.field_12682).method_11657(field_11502, Boolean.valueOf(false));
		} else {
			class_3610 lv3 = arg.method_8045().method_8316(lv);
			class_2680 lv4 = this.method_9564()
				.method_11657(field_11501, class_2771.field_12681)
				.method_11657(field_11502, Boolean.valueOf(lv3.method_15772() == class_3612.field_15910));
			class_2350 lv5 = arg.method_8038();
			return lv5 != class_2350.field_11033 && (lv5 == class_2350.field_11036 || !(arg.method_17698().field_1351 - (double)lv.method_10264() > 0.5))
				? lv4
				: lv4.method_11657(field_11501, class_2771.field_12679);
		}
	}

	@Override
	public boolean method_9616(class_2680 arg, class_1750 arg2) {
		class_1799 lv = arg2.method_8041();
		class_2771 lv2 = arg.method_11654(field_11501);
		if (lv2 == class_2771.field_12682 || lv.method_7909() != this.method_8389()) {
			return false;
		} else if (arg2.method_7717()) {
			boolean bl = arg2.method_17698().field_1351 - (double)arg2.method_8037().method_10264() > 0.5;
			class_2350 lv3 = arg2.method_8038();
			return lv2 == class_2771.field_12681
				? lv3 == class_2350.field_11036 || bl && lv3.method_10166().method_10179()
				: lv3 == class_2350.field_11033 || !bl && lv3.method_10166().method_10179();
		} else {
			return true;
		}
	}

	@Override
	public class_3610 method_9545(class_2680 arg) {
		return arg.method_11654(field_11502) ? class_3612.field_15910.method_15729(false) : super.method_9545(arg);
	}

	@Override
	public boolean method_10311(class_1936 arg, class_2338 arg2, class_2680 arg3, class_3610 arg4) {
		return arg3.method_11654(field_11501) != class_2771.field_12682 ? class_3737.super.method_10311(arg, arg2, arg3, arg4) : false;
	}

	@Override
	public boolean method_10310(class_1922 arg, class_2338 arg2, class_2680 arg3, class_3611 arg4) {
		return arg3.method_11654(field_11501) != class_2771.field_12682 ? class_3737.super.method_10310(arg, arg2, arg3, arg4) : false;
	}

	@Override
	public class_2680 method_9559(class_2680 arg, class_2350 arg2, class_2680 arg3, class_1936 arg4, class_2338 arg5, class_2338 arg6) {
		if ((Boolean)arg.method_11654(field_11502)) {
			arg4.method_8405().method_8676(arg5, class_3612.field_15910, class_3612.field_15910.method_15789(arg4));
		}

		return super.method_9559(arg, arg2, arg3, arg4, arg5, arg6);
	}

	@Override
	public boolean method_9516(class_2680 arg, class_1922 arg2, class_2338 arg3, class_10 arg4) {
		switch (arg4) {
			case field_50:
				return arg.method_11654(field_11501) == class_2771.field_12681;
			case field_48:
				return arg2.method_8316(arg3).method_15767(class_3486.field_15517);
			case field_51:
				return false;
			default:
				return false;
		}
	}
}
