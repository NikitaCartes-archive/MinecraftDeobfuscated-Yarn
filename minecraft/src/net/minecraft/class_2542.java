package net.minecraft;

import java.util.Random;
import javax.annotation.Nullable;

public class class_2542 extends class_2248 {
	private static final class_265 field_11712 = class_2248.method_9541(3.0, 0.0, 3.0, 12.0, 7.0, 12.0);
	private static final class_265 field_11709 = class_2248.method_9541(1.0, 0.0, 1.0, 15.0, 7.0, 15.0);
	public static final class_2758 field_11711 = class_2741.field_12530;
	public static final class_2758 field_11710 = class_2741.field_12509;

	public class_2542(class_2248.class_2251 arg) {
		super(arg);
		this.method_9590(this.field_10647.method_11664().method_11657(field_11711, Integer.valueOf(0)).method_11657(field_11710, Integer.valueOf(1)));
	}

	@Override
	public void method_9591(class_1937 arg, class_2338 arg2, class_1297 arg3) {
		this.method_10834(arg, arg2, arg3, 100);
		super.method_9591(arg, arg2, arg3);
	}

	@Override
	public void method_9554(class_1937 arg, class_2338 arg2, class_1297 arg3, float f) {
		if (!(arg3 instanceof class_1642)) {
			this.method_10834(arg, arg2, arg3, 3);
		}

		super.method_9554(arg, arg2, arg3, f);
	}

	private void method_10834(class_1937 arg, class_2338 arg2, class_1297 arg3, int i) {
		if (!this.method_10835(arg, arg3)) {
			super.method_9591(arg, arg2, arg3);
		} else {
			if (!arg.field_9236 && arg.field_9229.nextInt(i) == 0) {
				this.method_10833(arg, arg2, arg.method_8320(arg2));
			}
		}
	}

	private void method_10833(class_1937 arg, class_2338 arg2, class_2680 arg3) {
		arg.method_8396(null, arg2, class_3417.field_14687, class_3419.field_15245, 0.7F, 0.9F + arg.field_9229.nextFloat() * 0.2F);
		int i = (Integer)arg3.method_11654(field_11710);
		if (i <= 1) {
			arg.method_8651(arg2, false);
		} else {
			arg.method_8652(arg2, arg3.method_11657(field_11710, Integer.valueOf(i - 1)), 2);
			arg.method_20290(2001, arg2, class_2248.method_9507(arg3));
		}
	}

	@Override
	public void method_9588(class_2680 arg, class_1937 arg2, class_2338 arg3, Random random) {
		if (this.method_10832(arg2) && this.method_10831(arg2, arg3)) {
			int i = (Integer)arg.method_11654(field_11711);
			if (i < 2) {
				arg2.method_8396(null, arg3, class_3417.field_15109, class_3419.field_15245, 0.7F, 0.9F + random.nextFloat() * 0.2F);
				arg2.method_8652(arg3, arg.method_11657(field_11711, Integer.valueOf(i + 1)), 2);
			} else {
				arg2.method_8396(null, arg3, class_3417.field_14902, class_3419.field_15245, 0.7F, 0.9F + random.nextFloat() * 0.2F);
				arg2.method_8650(arg3, false);
				if (!arg2.field_9236) {
					for (int j = 0; j < arg.method_11654(field_11710); j++) {
						arg2.method_20290(2001, arg3, class_2248.method_9507(arg));
						class_1481 lv = class_1299.field_6113.method_5883(arg2);
						lv.method_5614(-24000);
						lv.method_6683(arg3);
						lv.method_5808((double)arg3.method_10263() + 0.3 + (double)j * 0.2, (double)arg3.method_10264(), (double)arg3.method_10260() + 0.3, 0.0F, 0.0F);
						arg2.method_8649(lv);
					}
				}
			}
		}
	}

	private boolean method_10831(class_1922 arg, class_2338 arg2) {
		return arg.method_8320(arg2.method_10074()).method_11614() == class_2246.field_10102;
	}

	@Override
	public void method_9615(class_2680 arg, class_1937 arg2, class_2338 arg3, class_2680 arg4, boolean bl) {
		if (this.method_10831(arg2, arg3) && !arg2.field_9236) {
			arg2.method_20290(2005, arg3, 0);
		}
	}

	private boolean method_10832(class_1937 arg) {
		float f = arg.method_8400(1.0F);
		return (double)f < 0.69 && (double)f > 0.65 ? true : arg.field_9229.nextInt(500) == 0;
	}

	@Override
	public void method_9556(class_1937 arg, class_1657 arg2, class_2338 arg3, class_2680 arg4, @Nullable class_2586 arg5, class_1799 arg6) {
		super.method_9556(arg, arg2, arg3, arg4, arg5, arg6);
		this.method_10833(arg, arg3, arg4);
	}

	@Override
	public boolean method_9616(class_2680 arg, class_1750 arg2) {
		return arg2.method_8041().method_7909() == this.method_8389() && arg.method_11654(field_11710) < 4 ? true : super.method_9616(arg, arg2);
	}

	@Nullable
	@Override
	public class_2680 method_9605(class_1750 arg) {
		class_2680 lv = arg.method_8045().method_8320(arg.method_8037());
		return lv.method_11614() == this
			? lv.method_11657(field_11710, Integer.valueOf(Math.min(4, (Integer)lv.method_11654(field_11710) + 1)))
			: super.method_9605(arg);
	}

	@Override
	public class_1921 method_9551() {
		return class_1921.field_9174;
	}

	@Override
	public class_265 method_9530(class_2680 arg, class_1922 arg2, class_2338 arg3, class_3726 arg4) {
		return arg.method_11654(field_11710) > 1 ? field_11709 : field_11712;
	}

	@Override
	protected void method_9515(class_2689.class_2690<class_2248, class_2680> arg) {
		arg.method_11667(field_11711, field_11710);
	}

	private boolean method_10835(class_1937 arg, class_1297 arg2) {
		if (arg2 instanceof class_1481) {
			return false;
		} else {
			return arg2 instanceof class_1309 && !(arg2 instanceof class_1657) ? arg.method_8450().method_8355(class_1928.field_19388) : true;
		}
	}
}
