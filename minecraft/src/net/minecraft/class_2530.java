package net.minecraft;

import javax.annotation.Nullable;

public class class_2530 extends class_2248 {
	public static final class_2746 field_11621 = class_2741.field_12539;

	public class_2530(class_2248.class_2251 arg) {
		super(arg);
		this.method_9590(this.method_9564().method_11657(field_11621, Boolean.valueOf(false)));
	}

	@Override
	public void method_9615(class_2680 arg, class_1937 arg2, class_2338 arg3, class_2680 arg4, boolean bl) {
		if (arg4.method_11614() != arg.method_11614()) {
			if (arg2.method_8479(arg3)) {
				method_10738(arg2, arg3);
				arg2.method_8650(arg3, false);
			}
		}
	}

	@Override
	public void method_9612(class_2680 arg, class_1937 arg2, class_2338 arg3, class_2248 arg4, class_2338 arg5, boolean bl) {
		if (arg2.method_8479(arg3)) {
			method_10738(arg2, arg3);
			arg2.method_8650(arg3, false);
		}
	}

	@Override
	public void method_9576(class_1937 arg, class_2338 arg2, class_2680 arg3, class_1657 arg4) {
		if (!arg.method_8608() && !arg4.method_7337() && (Boolean)arg3.method_11654(field_11621)) {
			method_10738(arg, arg2);
		}

		super.method_9576(arg, arg2, arg3, arg4);
	}

	@Override
	public void method_9586(class_1937 arg, class_2338 arg2, class_1927 arg3) {
		if (!arg.field_9236) {
			class_1541 lv = new class_1541(
				arg, (double)((float)arg2.method_10263() + 0.5F), (double)arg2.method_10264(), (double)((float)arg2.method_10260() + 0.5F), arg3.method_8347()
			);
			lv.method_6967((short)(arg.field_9229.nextInt(lv.method_6968() / 4) + lv.method_6968() / 8));
			arg.method_8649(lv);
		}
	}

	public static void method_10738(class_1937 arg, class_2338 arg2) {
		method_10737(arg, arg2, null);
	}

	private static void method_10737(class_1937 arg, class_2338 arg2, @Nullable class_1309 arg3) {
		if (!arg.field_9236) {
			class_1541 lv = new class_1541(
				arg, (double)((float)arg2.method_10263() + 0.5F), (double)arg2.method_10264(), (double)((float)arg2.method_10260() + 0.5F), arg3
			);
			arg.method_8649(lv);
			arg.method_8465(null, lv.field_5987, lv.field_6010, lv.field_6035, class_3417.field_15079, class_3419.field_15245, 1.0F, 1.0F);
		}
	}

	@Override
	public boolean method_9534(class_2680 arg, class_1937 arg2, class_2338 arg3, class_1657 arg4, class_1268 arg5, class_3965 arg6) {
		class_1799 lv = arg4.method_5998(arg5);
		class_1792 lv2 = lv.method_7909();
		if (lv2 != class_1802.field_8884 && lv2 != class_1802.field_8814) {
			return super.method_9534(arg, arg2, arg3, arg4, arg5, arg6);
		} else {
			method_10737(arg2, arg3, arg4);
			arg2.method_8652(arg3, class_2246.field_10124.method_9564(), 11);
			if (lv2 == class_1802.field_8884) {
				lv.method_7956(1, arg4);
			} else {
				lv.method_7934(1);
			}

			return true;
		}
	}

	@Override
	public void method_9548(class_2680 arg, class_1937 arg2, class_2338 arg3, class_1297 arg4) {
		if (!arg2.field_9236 && arg4 instanceof class_1665) {
			class_1665 lv = (class_1665)arg4;
			class_1297 lv2 = lv.method_7452();
			if (lv.method_5809()) {
				method_10737(arg2, arg3, lv2 instanceof class_1309 ? (class_1309)lv2 : null);
				arg2.method_8650(arg3, false);
			}
		}
	}

	@Override
	public boolean method_9533(class_1927 arg) {
		return false;
	}

	@Override
	protected void method_9515(class_2689.class_2690<class_2248, class_2680> arg) {
		arg.method_11667(field_11621);
	}
}
