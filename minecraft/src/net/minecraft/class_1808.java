package net.minecraft;

public class class_1808 extends class_1792 {
	private static final class_2357 field_8896 = new class_2347() {
		private final class_2347 field_8898 = new class_2347();

		@Override
		public class_1799 method_10135(class_2342 arg, class_1799 arg2) {
			class_2350 lv = arg.method_10120().method_11654(class_2315.field_10918);
			class_1937 lv2 = arg.method_10207();
			double d = arg.method_10216() + (double)lv.method_10148() * 1.125;
			double e = Math.floor(arg.method_10214()) + (double)lv.method_10164();
			double f = arg.method_10215() + (double)lv.method_10165() * 1.125;
			class_2338 lv3 = arg.method_10122().method_10093(lv);
			class_2680 lv4 = lv2.method_8320(lv3);
			class_2768 lv5 = lv4.method_11614() instanceof class_2241 ? lv4.method_11654(((class_2241)lv4.method_11614()).method_9474()) : class_2768.field_12665;
			double g;
			if (lv4.method_11602(class_3481.field_15463)) {
				if (lv5.method_11897()) {
					g = 0.6;
				} else {
					g = 0.1;
				}
			} else {
				if (!lv4.method_11588() || !lv2.method_8320(lv3.method_10074()).method_11602(class_3481.field_15463)) {
					return this.field_8898.dispense(arg, arg2);
				}

				class_2680 lv6 = lv2.method_8320(lv3.method_10074());
				class_2768 lv7 = lv6.method_11614() instanceof class_2241 ? lv6.method_11654(((class_2241)lv6.method_11614()).method_9474()) : class_2768.field_12665;
				if (lv != class_2350.field_11033 && lv7.method_11897()) {
					g = -0.4;
				} else {
					g = -0.9;
				}
			}

			class_1688 lv8 = class_1688.method_7523(lv2, d, e + g, f, ((class_1808)arg2.method_7909()).field_8897);
			if (arg2.method_7938()) {
				lv8.method_5665(arg2.method_7964());
			}

			lv2.method_8649(lv8);
			arg2.method_7934(1);
			return arg2;
		}

		@Override
		protected void method_10136(class_2342 arg) {
			arg.method_10207().method_20290(1000, arg.method_10122(), 0);
		}
	};
	private final class_1688.class_1689 field_8897;

	public class_1808(class_1688.class_1689 arg, class_1792.class_1793 arg2) {
		super(arg2);
		this.field_8897 = arg;
		class_2315.method_10009(this, field_8896);
	}

	@Override
	public class_1269 method_7884(class_1838 arg) {
		class_1937 lv = arg.method_8045();
		class_2338 lv2 = arg.method_8037();
		class_2680 lv3 = lv.method_8320(lv2);
		if (!lv3.method_11602(class_3481.field_15463)) {
			return class_1269.field_5814;
		} else {
			class_1799 lv4 = arg.method_8041();
			if (!lv.field_9236) {
				class_2768 lv5 = lv3.method_11614() instanceof class_2241 ? lv3.method_11654(((class_2241)lv3.method_11614()).method_9474()) : class_2768.field_12665;
				double d = 0.0;
				if (lv5.method_11897()) {
					d = 0.5;
				}

				class_1688 lv6 = class_1688.method_7523(
					lv, (double)lv2.method_10263() + 0.5, (double)lv2.method_10264() + 0.0625 + d, (double)lv2.method_10260() + 0.5, this.field_8897
				);
				if (lv4.method_7938()) {
					lv6.method_5665(lv4.method_7964());
				}

				lv.method_8649(lv6);
			}

			lv4.method_7934(1);
			return class_1269.field_5812;
		}
	}
}
