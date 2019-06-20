package net.minecraft;

import javax.annotation.Nullable;

public class class_1755 extends class_1792 {
	private final class_3611 field_7905;

	public class_1755(class_3611 arg, class_1792.class_1793 arg2) {
		super(arg2);
		this.field_7905 = arg;
	}

	@Override
	public class_1271<class_1799> method_7836(class_1937 arg, class_1657 arg2, class_1268 arg3) {
		class_1799 lv = arg2.method_5998(arg3);
		class_239 lv2 = method_7872(arg, arg2, this.field_7905 == class_3612.field_15906 ? class_3959.class_242.field_1345 : class_3959.class_242.field_1348);
		if (lv2.method_17783() == class_239.class_240.field_1333) {
			return new class_1271<>(class_1269.field_5811, lv);
		} else if (lv2.method_17783() != class_239.class_240.field_1332) {
			return new class_1271<>(class_1269.field_5811, lv);
		} else {
			class_3965 lv3 = (class_3965)lv2;
			class_2338 lv4 = lv3.method_17777();
			if (!arg.method_8505(arg2, lv4) || !arg2.method_7343(lv4, lv3.method_17780(), lv)) {
				return new class_1271<>(class_1269.field_5814, lv);
			} else if (this.field_7905 == class_3612.field_15906) {
				class_2680 lv5 = arg.method_8320(lv4);
				if (lv5.method_11614() instanceof class_2263) {
					class_3611 lv6 = ((class_2263)lv5.method_11614()).method_9700(arg, lv4, lv5);
					if (lv6 != class_3612.field_15906) {
						arg2.method_7259(class_3468.field_15372.method_14956(this));
						arg2.method_5783(lv6.method_15791(class_3486.field_15518) ? class_3417.field_15202 : class_3417.field_15126, 1.0F, 1.0F);
						class_1799 lv7 = this.method_7730(lv, arg2, lv6.method_15774());
						if (!arg.field_9236) {
							class_174.field_1208.method_8932((class_3222)arg2, new class_1799(lv6.method_15774()));
						}

						return new class_1271<>(class_1269.field_5812, lv7);
					}
				}

				return new class_1271<>(class_1269.field_5814, lv);
			} else {
				class_2680 lv5 = arg.method_8320(lv4);
				class_2338 lv8 = lv5.method_11614() instanceof class_2402 && this.field_7905 == class_3612.field_15910
					? lv4
					: lv3.method_17777().method_10093(lv3.method_17780());
				if (this.method_7731(arg2, arg, lv8, lv3)) {
					this.method_7728(arg, lv, lv8);
					if (arg2 instanceof class_3222) {
						class_174.field_1191.method_9087((class_3222)arg2, lv8, lv);
					}

					arg2.method_7259(class_3468.field_15372.method_14956(this));
					return new class_1271<>(class_1269.field_5812, this.method_7732(lv, arg2));
				} else {
					return new class_1271<>(class_1269.field_5814, lv);
				}
			}
		}
	}

	protected class_1799 method_7732(class_1799 arg, class_1657 arg2) {
		return !arg2.field_7503.field_7477 ? new class_1799(class_1802.field_8550) : arg;
	}

	public void method_7728(class_1937 arg, class_1799 arg2, class_2338 arg3) {
	}

	private class_1799 method_7730(class_1799 arg, class_1657 arg2, class_1792 arg3) {
		if (arg2.field_7503.field_7477) {
			return arg;
		} else {
			arg.method_7934(1);
			if (arg.method_7960()) {
				return new class_1799(arg3);
			} else {
				if (!arg2.field_7514.method_7394(new class_1799(arg3))) {
					arg2.method_7328(new class_1799(arg3), false);
				}

				return arg;
			}
		}
	}

	public boolean method_7731(@Nullable class_1657 arg, class_1937 arg2, class_2338 arg3, @Nullable class_3965 arg4) {
		if (!(this.field_7905 instanceof class_3609)) {
			return false;
		} else {
			class_2680 lv = arg2.method_8320(arg3);
			class_3614 lv2 = lv.method_11620();
			boolean bl = !lv2.method_15799();
			boolean bl2 = lv2.method_15800();
			if (arg2.method_8623(arg3)
				|| bl
				|| bl2
				|| lv.method_11614() instanceof class_2402 && ((class_2402)lv.method_11614()).method_10310(arg2, arg3, lv, this.field_7905)) {
				if (arg2.field_9247.method_12465() && this.field_7905.method_15791(class_3486.field_15517)) {
					int i = arg3.method_10263();
					int j = arg3.method_10264();
					int k = arg3.method_10260();
					arg2.method_8396(
						arg, arg3, class_3417.field_15102, class_3419.field_15245, 0.5F, 2.6F + (arg2.field_9229.nextFloat() - arg2.field_9229.nextFloat()) * 0.8F
					);

					for (int l = 0; l < 8; l++) {
						arg2.method_8406(class_2398.field_11237, (double)i + Math.random(), (double)j + Math.random(), (double)k + Math.random(), 0.0, 0.0, 0.0);
					}
				} else if (lv.method_11614() instanceof class_2402 && this.field_7905 == class_3612.field_15910) {
					if (((class_2402)lv.method_11614()).method_10311(arg2, arg3, lv, ((class_3609)this.field_7905).method_15729(false))) {
						this.method_7727(arg, arg2, arg3);
					}
				} else {
					if (!arg2.field_9236 && (bl || bl2) && !lv2.method_15797()) {
						arg2.method_8651(arg3, true);
					}

					this.method_7727(arg, arg2, arg3);
					arg2.method_8652(arg3, this.field_7905.method_15785().method_15759(), 11);
				}

				return true;
			} else {
				return arg4 == null ? false : this.method_7731(arg, arg2, arg4.method_17777().method_10093(arg4.method_17780()), null);
			}
		}
	}

	protected void method_7727(@Nullable class_1657 arg, class_1936 arg2, class_2338 arg3) {
		class_3414 lv = this.field_7905.method_15791(class_3486.field_15518) ? class_3417.field_15010 : class_3417.field_14834;
		arg2.method_8396(arg, arg3, lv, class_3419.field_15245, 1.0F, 1.0F);
	}
}
