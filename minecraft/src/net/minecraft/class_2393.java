package net.minecraft;

import java.util.Random;
import javax.annotation.Nullable;

public class class_2393 extends class_2248 implements class_2402 {
	public static final class_2758 field_11194 = class_2741.field_12517;
	protected static final class_265 field_11195 = class_2248.method_9541(0.0, 0.0, 0.0, 16.0, 9.0, 16.0);

	protected class_2393(class_2248.class_2251 arg) {
		super(arg);
		this.method_9590(this.field_10647.method_11664().method_11657(field_11194, Integer.valueOf(0)));
	}

	@Override
	public class_265 method_9530(class_2680 arg, class_1922 arg2, class_2338 arg3, class_3726 arg4) {
		return field_11195;
	}

	@Nullable
	@Override
	public class_2680 method_9605(class_1750 arg) {
		class_3610 lv = arg.method_8045().method_8316(arg.method_8037());
		return lv.method_15767(class_3486.field_15517) && lv.method_15761() == 8 ? this.method_10292(arg.method_8045()) : null;
	}

	public class_2680 method_10292(class_1936 arg) {
		return this.method_9564().method_11657(field_11194, Integer.valueOf(arg.method_8409().nextInt(25)));
	}

	@Override
	public class_1921 method_9551() {
		return class_1921.field_9174;
	}

	@Override
	public class_3610 method_9545(class_2680 arg) {
		return class_3612.field_15910.method_15729(false);
	}

	@Override
	public void method_9588(class_2680 arg, class_1937 arg2, class_2338 arg3, Random random) {
		if (!arg.method_11591(arg2, arg3)) {
			arg2.method_8651(arg3, true);
		} else {
			class_2338 lv = arg3.method_10084();
			class_2680 lv2 = arg2.method_8320(lv);
			if (lv2.method_11614() == class_2246.field_10382 && (Integer)arg.method_11654(field_11194) < 25 && random.nextDouble() < 0.14) {
				arg2.method_8501(lv, arg.method_11572(field_11194));
			}
		}
	}

	@Override
	public boolean method_9558(class_2680 arg, class_1941 arg2, class_2338 arg3) {
		class_2338 lv = arg3.method_10074();
		class_2680 lv2 = arg2.method_8320(lv);
		class_2248 lv3 = lv2.method_11614();
		return lv3 == class_2246.field_10092 ? false : lv3 == this || lv3 == class_2246.field_10463 || class_2248.method_20045(lv2, arg2, lv, class_2350.field_11036);
	}

	@Override
	public class_2680 method_9559(class_2680 arg, class_2350 arg2, class_2680 arg3, class_1936 arg4, class_2338 arg5, class_2338 arg6) {
		if (!arg.method_11591(arg4, arg5)) {
			if (arg2 == class_2350.field_11033) {
				return class_2246.field_10124.method_9564();
			}

			arg4.method_8397().method_8676(arg5, this, 1);
		}

		if (arg2 == class_2350.field_11036 && arg3.method_11614() == this) {
			return class_2246.field_10463.method_9564();
		} else {
			arg4.method_8405().method_8676(arg5, class_3612.field_15910, class_3612.field_15910.method_15789(arg4));
			return super.method_9559(arg, arg2, arg3, arg4, arg5, arg6);
		}
	}

	@Override
	protected void method_9515(class_2689.class_2690<class_2248, class_2680> arg) {
		arg.method_11667(field_11194);
	}

	@Override
	public boolean method_10310(class_1922 arg, class_2338 arg2, class_2680 arg3, class_3611 arg4) {
		return false;
	}

	@Override
	public boolean method_10311(class_1936 arg, class_2338 arg2, class_2680 arg3, class_3610 arg4) {
		return false;
	}
}
