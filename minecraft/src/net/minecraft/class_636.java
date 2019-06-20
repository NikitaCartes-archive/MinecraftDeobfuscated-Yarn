package net.minecraft;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class class_636 {
	private final class_310 field_3712;
	private final class_634 field_3720;
	private class_2338 field_3714 = new class_2338(-1, -1, -1);
	private class_1799 field_3718 = class_1799.field_8037;
	private float field_3715;
	private float field_3713;
	private int field_3716;
	private boolean field_3717;
	private class_1934 field_3719 = class_1934.field_9215;
	private int field_3721;

	public class_636(class_310 arg, class_634 arg2) {
		this.field_3712 = arg;
		this.field_3720 = arg2;
	}

	public static void method_2921(class_310 arg, class_636 arg2, class_2338 arg3, class_2350 arg4) {
		if (!arg.field_1687.method_8506(arg.field_1724, arg3, arg4)) {
			arg2.method_2899(arg3);
		}
	}

	public void method_2903(class_1657 arg) {
		this.field_3719.method_8382(arg.field_7503);
	}

	public void method_2907(class_1934 arg) {
		this.field_3719 = arg;
		this.field_3719.method_8382(this.field_3712.field_1724.field_7503);
	}

	public boolean method_2908() {
		return this.field_3719.method_8388();
	}

	public boolean method_2899(class_2338 arg) {
		if (this.field_3719.method_8387()) {
			if (this.field_3719 == class_1934.field_9219) {
				return false;
			}

			if (!this.field_3712.field_1724.method_7294()) {
				class_1799 lv = this.field_3712.field_1724.method_6047();
				if (lv.method_7960()) {
					return false;
				}

				class_2694 lv2 = new class_2694(this.field_3712.field_1687, arg, false);
				if (!lv.method_7940(this.field_3712.field_1687.method_8514(), lv2)) {
					return false;
				}
			}
		}

		class_1937 lv3 = this.field_3712.field_1687;
		class_2680 lv4 = lv3.method_8320(arg);
		if (!this.field_3712.field_1724.method_6047().method_7909().method_7885(lv4, lv3, arg, this.field_3712.field_1724)) {
			return false;
		} else {
			class_2248 lv5 = lv4.method_11614();
			if ((lv5 instanceof class_2288 || lv5 instanceof class_2515 || lv5 instanceof class_3748) && !this.field_3712.field_1724.method_7338()) {
				return false;
			} else if (lv4.method_11588()) {
				return false;
			} else {
				lv5.method_9576(lv3, arg, lv4, this.field_3712.field_1724);
				class_3610 lv6 = lv3.method_8316(arg);
				boolean bl = lv3.method_8652(arg, lv6.method_15759(), 11);
				if (bl) {
					lv5.method_9585(lv3, arg, lv4);
				}

				this.field_3714 = new class_2338(this.field_3714.method_10263(), -1, this.field_3714.method_10260());
				return bl;
			}
		}
	}

	public boolean method_2910(class_2338 arg, class_2350 arg2) {
		if (this.field_3719.method_8387()) {
			if (this.field_3719 == class_1934.field_9219) {
				return false;
			}

			if (!this.field_3712.field_1724.method_7294()) {
				class_1799 lv = this.field_3712.field_1724.method_6047();
				if (lv.method_7960()) {
					return false;
				}

				class_2694 lv2 = new class_2694(this.field_3712.field_1687, arg, false);
				if (!lv.method_7940(this.field_3712.field_1687.method_8514(), lv2)) {
					return false;
				}
			}
		}

		if (!this.field_3712.field_1687.method_8621().method_11952(arg)) {
			return false;
		} else {
			if (this.field_3719.method_8386()) {
				this.field_3712.method_1577().method_4907(this.field_3712.field_1687, arg, this.field_3712.field_1687.method_8320(arg), 1.0F);
				this.field_3720.method_2883(new class_2846(class_2846.class_2847.field_12968, arg, arg2));
				method_2921(this.field_3712, this, arg, arg2);
				this.field_3716 = 5;
			} else if (!this.field_3717 || !this.method_2922(arg)) {
				if (this.field_3717) {
					this.field_3720.method_2883(new class_2846(class_2846.class_2847.field_12971, this.field_3714, arg2));
				}

				class_2680 lv3 = this.field_3712.field_1687.method_8320(arg);
				this.field_3712.method_1577().method_4907(this.field_3712.field_1687, arg, lv3, 0.0F);
				this.field_3720.method_2883(new class_2846(class_2846.class_2847.field_12968, arg, arg2));
				boolean bl = !lv3.method_11588();
				if (bl && this.field_3715 == 0.0F) {
					lv3.method_11636(this.field_3712.field_1687, arg, this.field_3712.field_1724);
				}

				if (bl && lv3.method_11589(this.field_3712.field_1724, this.field_3712.field_1724.field_6002, arg) >= 1.0F) {
					this.method_2899(arg);
				} else {
					this.field_3717 = true;
					this.field_3714 = arg;
					this.field_3718 = this.field_3712.field_1724.method_6047();
					this.field_3715 = 0.0F;
					this.field_3713 = 0.0F;
					this.field_3712.field_1687.method_8517(this.field_3712.field_1724.method_5628(), this.field_3714, (int)(this.field_3715 * 10.0F) - 1);
				}
			}

			return true;
		}
	}

	public void method_2925() {
		if (this.field_3717) {
			this.field_3712.method_1577().method_4907(this.field_3712.field_1687, this.field_3714, this.field_3712.field_1687.method_8320(this.field_3714), -1.0F);
			this.field_3720.method_2883(new class_2846(class_2846.class_2847.field_12971, this.field_3714, class_2350.field_11033));
			this.field_3717 = false;
			this.field_3715 = 0.0F;
			this.field_3712.field_1687.method_8517(this.field_3712.field_1724.method_5628(), this.field_3714, -1);
		}
	}

	public boolean method_2902(class_2338 arg, class_2350 arg2) {
		this.method_2911();
		if (this.field_3716 > 0) {
			this.field_3716--;
			return true;
		} else if (this.field_3719.method_8386() && this.field_3712.field_1687.method_8621().method_11952(arg)) {
			this.field_3716 = 5;
			this.field_3712.method_1577().method_4907(this.field_3712.field_1687, arg, this.field_3712.field_1687.method_8320(arg), 1.0F);
			this.field_3720.method_2883(new class_2846(class_2846.class_2847.field_12968, arg, arg2));
			method_2921(this.field_3712, this, arg, arg2);
			return true;
		} else if (this.method_2922(arg)) {
			class_2680 lv = this.field_3712.field_1687.method_8320(arg);
			if (lv.method_11588()) {
				this.field_3717 = false;
				return false;
			} else {
				this.field_3715 = this.field_3715 + lv.method_11589(this.field_3712.field_1724, this.field_3712.field_1724.field_6002, arg);
				if (this.field_3713 % 4.0F == 0.0F) {
					class_2498 lv2 = lv.method_11638();
					this.field_3712
						.method_1483()
						.method_4873(new class_1109(lv2.method_10596(), class_3419.field_15254, (lv2.method_10597() + 1.0F) / 8.0F, lv2.method_10599() * 0.5F, arg));
				}

				this.field_3713++;
				this.field_3712.method_1577().method_4907(this.field_3712.field_1687, arg, lv, class_3532.method_15363(this.field_3715, 0.0F, 1.0F));
				if (this.field_3715 >= 1.0F) {
					this.field_3717 = false;
					this.field_3720.method_2883(new class_2846(class_2846.class_2847.field_12973, arg, arg2));
					this.method_2899(arg);
					this.field_3715 = 0.0F;
					this.field_3713 = 0.0F;
					this.field_3716 = 5;
				}

				this.field_3712.field_1687.method_8517(this.field_3712.field_1724.method_5628(), this.field_3714, (int)(this.field_3715 * 10.0F) - 1);
				return true;
			}
		} else {
			return this.method_2910(arg, arg2);
		}
	}

	public float method_26749() {
		return this.field_3719.method_26746();
	}

	public float method_26750() {
		return this.field_3719.method_26744();
	}

	public float method_26751() {
		return this.field_3719.method_26745();
	}

	public void method_2927() {
		this.method_2911();
		if (this.field_3720.method_2872().method_10758()) {
			this.field_3720.method_2872().method_10754();
		} else {
			this.field_3720.method_2872().method_10768();
		}
	}

	private boolean method_2922(class_2338 arg) {
		class_1799 lv = this.field_3712.field_1724.method_6047();
		boolean bl = this.field_3718.method_7960() && lv.method_7960();
		if (!this.field_3718.method_7960() && !lv.method_7960()) {
			bl = lv.method_7909() == this.field_3718.method_7909()
				&& class_1799.method_7975(lv, this.field_3718)
				&& (lv.method_7963() || lv.method_7919() == this.field_3718.method_7919());
		}

		return arg.equals(this.field_3714) && bl;
	}

	private void method_2911() {
		int i = this.field_3712.field_1724.field_7514.field_7545;
		if (i != this.field_3721) {
			this.field_3721 = i;
			this.field_3720.method_2883(new class_2868(this.field_3721));
		}
	}

	public class_1269 method_2896(class_746 arg, class_638 arg2, class_1268 arg3, class_3965 arg4) {
		this.method_2911();
		class_2338 lv = arg4.method_17777();
		class_243 lv2 = arg4.method_17784();
		if (!this.field_3712.field_1687.method_8621().method_11952(lv)) {
			return class_1269.field_5814;
		} else {
			class_1799 lv3 = arg.method_5998(arg3);
			if (this.field_3719 == class_1934.field_9219) {
				this.field_3720.method_2883(new class_2885(arg3, arg4));
				return class_1269.field_5812;
			} else {
				boolean bl = !arg.method_6047().method_7960() || !arg.method_6079().method_7960();
				boolean bl2 = arg.method_5715() && bl;
				if (!bl2 && arg2.method_8320(lv).method_11629(arg2, arg, arg3, arg4)) {
					this.field_3720.method_2883(new class_2885(arg3, arg4));
					return class_1269.field_5812;
				} else {
					this.field_3720.method_2883(new class_2885(arg3, arg4));
					if (!lv3.method_7960() && !arg.method_7357().method_7904(lv3.method_7909())) {
						class_1838 lv4 = new class_1838(arg, arg3, arg4);
						class_1269 lv5;
						if (this.field_3719.method_8386()) {
							int i = lv3.method_7947();
							lv5 = lv3.method_7981(lv4);
							lv3.method_7939(i);
						} else {
							lv5 = lv3.method_7981(lv4);
						}

						return lv5;
					} else {
						return class_1269.field_5811;
					}
				}
			}
		}
	}

	public class_1269 method_2919(class_1657 arg, class_1937 arg2, class_1268 arg3) {
		if (this.field_3719 == class_1934.field_9219) {
			return class_1269.field_5811;
		} else {
			this.method_2911();
			this.field_3720.method_2883(new class_2886(arg3));
			class_1799 lv = arg.method_5998(arg3);
			if (arg.method_7357().method_7904(lv.method_7909())) {
				return class_1269.field_5811;
			} else {
				int i = lv.method_7947();
				class_1271<class_1799> lv2 = lv.method_7913(arg2, arg, arg3);
				class_1799 lv3 = lv2.method_5466();
				if (lv3 != lv || lv3.method_7947() != i) {
					arg.method_6122(arg3, lv3);
				}

				return lv2.method_5467();
			}
		}
	}

	public class_746 method_2901(class_638 arg, class_3469 arg2, class_299 arg3) {
		return new class_746(this.field_3712, arg, this.field_3720, arg2, arg3);
	}

	public void method_2918(class_1657 arg, class_1297 arg2) {
		this.method_2911();
		this.field_3720.method_2883(new class_2824(arg2));
		if (this.field_3719 != class_1934.field_9219) {
			arg.method_7324(arg2);
			arg.method_7350();
		}
	}

	public class_1269 method_2905(class_1657 arg, class_1297 arg2, class_1268 arg3) {
		this.method_2911();
		this.field_3720.method_2883(new class_2824(arg2, arg3));
		return this.field_3719 == class_1934.field_9219 ? class_1269.field_5811 : arg.method_7287(arg2, arg3);
	}

	public class_1269 method_2917(class_1657 arg, class_1297 arg2, class_3966 arg3, class_1268 arg4) {
		this.method_2911();
		class_243 lv = arg3.method_17784().method_1023(arg2.field_5987, arg2.field_6010, arg2.field_6035);
		this.field_3720.method_2883(new class_2824(arg2, arg4, lv));
		return this.field_3719 == class_1934.field_9219 ? class_1269.field_5811 : arg2.method_5664(arg, lv, arg4);
	}

	public void method_26748(class_1657 arg) {
		this.method_2911();
		this.field_3720.method_2883(new class_2824());
		if (this.field_3719 != class_1934.field_9219) {
			arg.method_7350();
		}
	}

	public class_1799 method_2906(int i, int j, int k, class_1713 arg, class_1657 arg2) {
		short s = arg2.field_7512.method_7614(arg2.field_7514);
		class_1799 lv = arg2.field_7512.method_7593(j, k, arg, arg2);
		this.field_3720.method_2883(new class_2813(i, j, k, arg, lv, s));
		return lv;
	}

	public void method_2912(int i, class_1860<?> arg, boolean bl) {
		this.field_3720.method_2883(new class_2840(i, arg, bl));
	}

	public void method_2900(int i, int j) {
		this.field_3720.method_2883(new class_2811(i, j));
	}

	public void method_2909(class_1799 arg, int i) {
		if (this.field_3719.method_8386()) {
			this.field_3720.method_2883(new class_2873(i, arg));
		}
	}

	public void method_2915(class_1799 arg) {
		if (this.field_3719.method_8386() && !arg.method_7960()) {
			this.field_3720.method_2883(new class_2873(-1, arg));
		}
	}

	public void method_2897(class_1657 arg) {
		this.method_2911();
		this.field_3720.method_2883(new class_2846(class_2846.class_2847.field_12974, class_2338.field_10980, class_2350.field_11033));
		arg.method_6075();
	}

	public boolean method_2913() {
		return this.field_3719.method_8388();
	}

	public boolean method_2924() {
		return !this.field_3719.method_8386();
	}

	public boolean method_2914() {
		return this.field_3719.method_8386();
	}

	public boolean method_2895() {
		return this.field_3712.field_1724.method_5765() && this.field_3712.field_1724.method_5854() instanceof class_1496;
	}

	public boolean method_2928() {
		return this.field_3719 == class_1934.field_9219;
	}

	public class_1934 method_2920() {
		return this.field_3719;
	}

	public boolean method_2923() {
		return this.field_3717;
	}

	public void method_2916(int i) {
		this.field_3720.method_2883(new class_2838(i));
	}
}
