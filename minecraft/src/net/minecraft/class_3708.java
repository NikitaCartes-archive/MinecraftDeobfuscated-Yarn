package net.minecraft;

import javax.annotation.Nullable;

public class class_3708 extends class_2237 {
	public static final class_2753 field_16320 = class_2741.field_12525;
	public static final class_2746 field_18006 = class_2741.field_12537;

	public class_3708(class_2248.class_2251 arg) {
		super(arg);
		this.method_9590(this.field_10647.method_11664().method_11657(field_16320, class_2350.field_11043).method_11657(field_18006, Boolean.valueOf(false)));
	}

	@Override
	public boolean method_9534(class_2680 arg, class_1937 arg2, class_2338 arg3, class_1657 arg4, class_1268 arg5, class_3965 arg6) {
		if (arg2.field_9236) {
			return true;
		} else {
			class_2586 lv = arg2.method_8321(arg3);
			if (lv instanceof class_3719) {
				arg4.method_17355((class_3719)lv);
				arg4.method_7281(class_3468.field_17271);
			}

			return true;
		}
	}

	@Override
	public void method_9536(class_2680 arg, class_1937 arg2, class_2338 arg3, class_2680 arg4, boolean bl) {
		if (arg.method_11614() != arg4.method_11614()) {
			class_2586 lv = arg2.method_8321(arg3);
			if (lv instanceof class_1263) {
				class_1264.method_5451(arg2, arg3, (class_1263)lv);
				arg2.method_8455(arg3, this);
			}

			super.method_9536(arg, arg2, arg3, arg4, bl);
		}
	}

	@Nullable
	@Override
	public class_2586 method_10123(class_1922 arg) {
		return new class_3719();
	}

	@Override
	public class_2464 method_9604(class_2680 arg) {
		return class_2464.field_11456;
	}

	@Override
	public void method_9567(class_1937 arg, class_2338 arg2, class_2680 arg3, @Nullable class_1309 arg4, class_1799 arg5) {
		if (arg5.method_7938()) {
			class_2586 lv = arg.method_8321(arg2);
			if (lv instanceof class_3719) {
				((class_3719)lv).method_17488(arg5.method_7964());
			}
		}
	}

	@Override
	public void method_9548(class_2680 arg, class_1937 arg2, class_2338 arg3, class_1297 arg4) {
		if (!arg2.field_9236 && arg4 instanceof class_1665) {
			class_1665 lv = (class_1665)arg4;
			class_1297 lv2 = lv.method_7452();
			arg2.method_8437(
				lv2, (double)arg3.method_10263() + 0.5, (double)arg3.method_10264() + 0.5, (double)arg3.method_10260() + 0.5, 5.0F, class_1927.class_4179.field_18687
			);
		}
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
	public class_2680 method_9598(class_2680 arg, class_2470 arg2) {
		return arg.method_11657(field_16320, arg2.method_10503(arg.method_11654(field_16320)));
	}

	@Override
	public class_2680 method_9569(class_2680 arg, class_2415 arg2) {
		return arg.method_11626(arg2.method_10345(arg.method_11654(field_16320)));
	}

	@Override
	protected void method_9515(class_2689.class_2690<class_2248, class_2680> arg) {
		arg.method_11667(field_16320, field_18006);
	}

	@Override
	public class_2680 method_9605(class_1750 arg) {
		return this.method_9564().method_11657(field_16320, arg.method_7715().method_10153());
	}

	@Override
	public boolean method_9601(class_2680 arg) {
		return false;
	}
}
