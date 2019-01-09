package net.minecraft;

public abstract class class_2241 extends class_2248 {
	protected static final class_265 field_9958 = class_2248.method_9541(0.0, 0.0, 0.0, 16.0, 2.0, 16.0);
	protected static final class_265 field_9960 = class_2248.method_9541(0.0, 0.0, 0.0, 16.0, 8.0, 16.0);
	private final boolean field_9959;

	public static boolean method_9479(class_1937 arg, class_2338 arg2) {
		return method_9476(arg.method_8320(arg2));
	}

	public static boolean method_9476(class_2680 arg) {
		return arg.method_11602(class_3481.field_15463);
	}

	protected class_2241(boolean bl, class_2248.class_2251 arg) {
		super(arg);
		this.field_9959 = bl;
	}

	public boolean method_9478() {
		return this.field_9959;
	}

	@Override
	public class_265 method_9529(class_2680 arg, class_1922 arg2, class_2338 arg3) {
		class_2768 lv = arg.method_11614() == this ? arg.method_11654(this.method_9474()) : null;
		return lv != null && lv.method_11897() ? field_9960 : field_9958;
	}

	@Override
	public boolean method_9558(class_2680 arg, class_1941 arg2, class_2338 arg3) {
		class_2338 lv = arg3.method_10074();
		return arg2.method_8320(lv).method_11631(arg2, lv);
	}

	@Override
	public void method_9615(class_2680 arg, class_1937 arg2, class_2338 arg3, class_2680 arg4) {
		if (arg4.method_11614() != arg.method_11614()) {
			if (!arg2.field_9236) {
				arg = this.method_9475(arg2, arg3, arg, true);
				if (this.field_9959) {
					arg.method_11622(arg2, arg3, this, arg3);
				}
			}
		}
	}

	@Override
	public void method_9612(class_2680 arg, class_1937 arg2, class_2338 arg3, class_2248 arg4, class_2338 arg5) {
		if (!arg2.field_9236) {
			class_2768 lv = arg.method_11654(this.method_9474());
			boolean bl = false;
			class_2338 lv2 = arg3.method_10074();
			if (!arg2.method_8320(lv2).method_11631(arg2, lv2)) {
				bl = true;
			}

			class_2338 lv3 = arg3.method_10078();
			if (lv == class_2768.field_12667 && !arg2.method_8320(lv3).method_11631(arg2, lv3)) {
				bl = true;
			} else {
				class_2338 lv4 = arg3.method_10067();
				if (lv == class_2768.field_12666 && !arg2.method_8320(lv4).method_11631(arg2, lv4)) {
					bl = true;
				} else {
					class_2338 lv5 = arg3.method_10095();
					if (lv == class_2768.field_12670 && !arg2.method_8320(lv5).method_11631(arg2, lv5)) {
						bl = true;
					} else {
						class_2338 lv6 = arg3.method_10072();
						if (lv == class_2768.field_12668 && !arg2.method_8320(lv6).method_11631(arg2, lv6)) {
							bl = true;
						}
					}
				}
			}

			if (bl && !arg2.method_8623(arg3)) {
				method_9497(arg, arg2, arg3);
				arg2.method_8650(arg3);
			} else {
				this.method_9477(arg, arg2, arg3, arg4);
			}
		}
	}

	protected void method_9477(class_2680 arg, class_1937 arg2, class_2338 arg3, class_2248 arg4) {
	}

	protected class_2680 method_9475(class_1937 arg, class_2338 arg2, class_2680 arg3, boolean bl) {
		return arg.field_9236 ? arg3 : new class_2452(arg, arg2, arg3).method_10459(arg.method_8479(arg2), bl).method_10462();
	}

	@Override
	public class_3619 method_9527(class_2680 arg) {
		return class_3619.field_15974;
	}

	@Override
	public class_1921 method_9551() {
		return class_1921.field_9174;
	}

	@Override
	public void method_9536(class_2680 arg, class_1937 arg2, class_2338 arg3, class_2680 arg4, boolean bl) {
		if (!bl) {
			super.method_9536(arg, arg2, arg3, arg4, bl);
			if (((class_2768)arg.method_11654(this.method_9474())).method_11897()) {
				arg2.method_8452(arg3.method_10084(), this);
			}

			if (this.field_9959) {
				arg2.method_8452(arg3, this);
				arg2.method_8452(arg3.method_10074(), this);
			}
		}
	}

	public abstract class_2769<class_2768> method_9474();
}
