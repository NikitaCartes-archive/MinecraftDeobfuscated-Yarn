package net.minecraft;

import java.util.Random;

public abstract class class_2312 extends class_2383 {
	protected static final class_265 field_10912 = class_2248.method_9541(0.0, 0.0, 0.0, 16.0, 2.0, 16.0);
	public static final class_2746 field_10911 = class_2741.field_12484;

	protected class_2312(class_2248.class_2251 arg) {
		super(arg);
	}

	@Override
	public class_265 method_9530(class_2680 arg, class_1922 arg2, class_2338 arg3, class_3726 arg4) {
		return field_10912;
	}

	@Override
	public boolean method_9558(class_2680 arg, class_1941 arg2, class_2338 arg3) {
		return method_16361(arg2, arg3.method_10074());
	}

	@Override
	public void method_9588(class_2680 arg, class_1937 arg2, class_2338 arg3, Random random) {
		if (!this.method_9996(arg2, arg3, arg)) {
			boolean bl = (Boolean)arg.method_11654(field_10911);
			boolean bl2 = this.method_9990(arg2, arg3, arg);
			if (bl && !bl2) {
				arg2.method_8652(arg3, arg.method_11657(field_10911, Boolean.valueOf(false)), 2);
			} else if (!bl) {
				arg2.method_8652(arg3, arg.method_11657(field_10911, Boolean.valueOf(true)), 2);
				if (!bl2) {
					arg2.method_8397().method_8675(arg3, this, this.method_9992(arg), class_1953.field_9310);
				}
			}
		}
	}

	@Override
	public int method_9603(class_2680 arg, class_1922 arg2, class_2338 arg3, class_2350 arg4) {
		return arg.method_11597(arg2, arg3, arg4);
	}

	@Override
	public int method_9524(class_2680 arg, class_1922 arg2, class_2338 arg3, class_2350 arg4) {
		if (!(Boolean)arg.method_11654(field_10911)) {
			return 0;
		} else {
			return arg.method_11654(field_11177) == arg4 ? this.method_9993(arg2, arg3, arg) : 0;
		}
	}

	@Override
	public void method_9612(class_2680 arg, class_1937 arg2, class_2338 arg3, class_2248 arg4, class_2338 arg5, boolean bl) {
		if (arg.method_11591(arg2, arg3)) {
			this.method_9998(arg2, arg3, arg);
		} else {
			class_2586 lv = this.method_9570() ? arg2.method_8321(arg3) : null;
			method_9610(arg, arg2, arg3, lv);
			arg2.method_8650(arg3, false);

			for (class_2350 lv2 : class_2350.values()) {
				arg2.method_8452(arg3.method_10093(lv2), this);
			}
		}
	}

	protected void method_9998(class_1937 arg, class_2338 arg2, class_2680 arg3) {
		if (!this.method_9996(arg, arg2, arg3)) {
			boolean bl = (Boolean)arg3.method_11654(field_10911);
			boolean bl2 = this.method_9990(arg, arg2, arg3);
			if (bl != bl2 && !arg.method_8397().method_8677(arg2, this)) {
				class_1953 lv = class_1953.field_9310;
				if (this.method_9988(arg, arg2, arg3)) {
					lv = class_1953.field_9315;
				} else if (bl) {
					lv = class_1953.field_9313;
				}

				arg.method_8397().method_8675(arg2, this, this.method_9992(arg3), lv);
			}
		}
	}

	public boolean method_9996(class_1941 arg, class_2338 arg2, class_2680 arg3) {
		return false;
	}

	protected boolean method_9990(class_1937 arg, class_2338 arg2, class_2680 arg3) {
		return this.method_9991(arg, arg2, arg3) > 0;
	}

	protected int method_9991(class_1937 arg, class_2338 arg2, class_2680 arg3) {
		class_2350 lv = arg3.method_11654(field_11177);
		class_2338 lv2 = arg2.method_10093(lv);
		int i = arg.method_8499(lv2, lv);
		if (i >= 15) {
			return i;
		} else {
			class_2680 lv3 = arg.method_8320(lv2);
			return Math.max(i, lv3.method_11614() == class_2246.field_10091 ? (Integer)lv3.method_11654(class_2457.field_11432) : 0);
		}
	}

	protected int method_10000(class_1941 arg, class_2338 arg2, class_2680 arg3) {
		class_2350 lv = arg3.method_11654(field_11177);
		class_2350 lv2 = lv.method_10170();
		class_2350 lv3 = lv.method_10160();
		return Math.max(this.method_9995(arg, arg2.method_10093(lv2), lv2), this.method_9995(arg, arg2.method_10093(lv3), lv3));
	}

	protected int method_9995(class_1941 arg, class_2338 arg2, class_2350 arg3) {
		class_2680 lv = arg.method_8320(arg2);
		class_2248 lv2 = lv.method_11614();
		if (this.method_9989(lv)) {
			if (lv2 == class_2246.field_10002) {
				return 15;
			} else {
				return lv2 == class_2246.field_10091 ? (Integer)lv.method_11654(class_2457.field_11432) : arg.method_8596(arg2, arg3);
			}
		} else {
			return 0;
		}
	}

	@Override
	public boolean method_9506(class_2680 arg) {
		return true;
	}

	@Override
	public class_2680 method_9605(class_1750 arg) {
		return this.method_9564().method_11657(field_11177, arg.method_8042().method_10153());
	}

	@Override
	public void method_9567(class_1937 arg, class_2338 arg2, class_2680 arg3, class_1309 arg4, class_1799 arg5) {
		if (this.method_9990(arg, arg2, arg3)) {
			arg.method_8397().method_8676(arg2, this, 1);
		}
	}

	@Override
	public void method_9615(class_2680 arg, class_1937 arg2, class_2338 arg3, class_2680 arg4, boolean bl) {
		this.method_9997(arg2, arg3, arg);
	}

	@Override
	public void method_9536(class_2680 arg, class_1937 arg2, class_2338 arg3, class_2680 arg4, boolean bl) {
		if (!bl && arg.method_11614() != arg4.method_11614()) {
			super.method_9536(arg, arg2, arg3, arg4, bl);
			this.method_9997(arg2, arg3, arg);
		}
	}

	protected void method_9997(class_1937 arg, class_2338 arg2, class_2680 arg3) {
		class_2350 lv = arg3.method_11654(field_11177);
		class_2338 lv2 = arg2.method_10093(lv.method_10153());
		arg.method_8492(lv2, this, arg2);
		arg.method_8508(lv2, this, lv);
	}

	protected boolean method_9989(class_2680 arg) {
		return arg.method_11634();
	}

	protected int method_9993(class_1922 arg, class_2338 arg2, class_2680 arg3) {
		return 15;
	}

	public static boolean method_9999(class_2680 arg) {
		return arg.method_11614() instanceof class_2312;
	}

	public boolean method_9988(class_1922 arg, class_2338 arg2, class_2680 arg3) {
		class_2350 lv = ((class_2350)arg3.method_11654(field_11177)).method_10153();
		class_2680 lv2 = arg.method_8320(arg2.method_10093(lv));
		return method_9999(lv2) && lv2.method_11654(field_11177) != lv;
	}

	protected abstract int method_9992(class_2680 arg);

	@Override
	public class_1921 method_9551() {
		return class_1921.field_9174;
	}

	@Override
	public boolean method_9601(class_2680 arg) {
		return true;
	}
}
