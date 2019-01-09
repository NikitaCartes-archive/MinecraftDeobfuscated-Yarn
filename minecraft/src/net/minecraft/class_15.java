package net.minecraft;

import javax.annotation.Nullable;

public class class_15 extends class_14 {
	private float field_65;
	private float field_64;

	@Override
	public void method_12(class_1922 arg, class_1308 arg2) {
		super.method_12(arg, arg2);
		arg2.method_5941(class_7.field_18, 0.0F);
		this.field_65 = arg2.method_5944(class_7.field_12);
		arg2.method_5941(class_7.field_12, 6.0F);
		this.field_64 = arg2.method_5944(class_7.field_4);
		arg2.method_5941(class_7.field_4, 4.0F);
	}

	@Override
	public void method_19() {
		this.field_33.method_5941(class_7.field_12, this.field_65);
		this.field_33.method_5941(class_7.field_4, this.field_64);
		super.method_19();
	}

	@Override
	public class_9 method_21() {
		return this.method_13(
			class_3532.method_15357(this.field_33.method_5829().field_1323),
			class_3532.method_15357(this.field_33.method_5829().field_1322 + 0.5),
			class_3532.method_15357(this.field_33.method_5829().field_1321)
		);
	}

	@Override
	public class_9 method_16(double d, double e, double f) {
		return this.method_13(class_3532.method_15357(d), class_3532.method_15357(e + 0.5), class_3532.method_15357(f));
	}

	@Override
	public int method_18(class_9[] args, class_9 arg, class_9 arg2, float f) {
		int i = 0;
		int j = 1;
		class_2338 lv = new class_2338(arg.field_40, arg.field_39, arg.field_38);
		double d = this.method_66(lv);
		class_9 lv2 = this.method_65(arg.field_40, arg.field_39, arg.field_38 + 1, 1, d);
		class_9 lv3 = this.method_65(arg.field_40 - 1, arg.field_39, arg.field_38, 1, d);
		class_9 lv4 = this.method_65(arg.field_40 + 1, arg.field_39, arg.field_38, 1, d);
		class_9 lv5 = this.method_65(arg.field_40, arg.field_39, arg.field_38 - 1, 1, d);
		class_9 lv6 = this.method_65(arg.field_40, arg.field_39 + 1, arg.field_38, 0, d);
		class_9 lv7 = this.method_65(arg.field_40, arg.field_39 - 1, arg.field_38, 1, d);
		if (lv2 != null && !lv2.field_42 && lv2.method_31(arg2) < f) {
			args[i++] = lv2;
		}

		if (lv3 != null && !lv3.field_42 && lv3.method_31(arg2) < f) {
			args[i++] = lv3;
		}

		if (lv4 != null && !lv4.field_42 && lv4.method_31(arg2) < f) {
			args[i++] = lv4;
		}

		if (lv5 != null && !lv5.field_42 && lv5.method_31(arg2) < f) {
			args[i++] = lv5;
		}

		if (lv6 != null && !lv6.field_42 && lv6.method_31(arg2) < f) {
			args[i++] = lv6;
		}

		if (lv7 != null && !lv7.field_42 && lv7.method_31(arg2) < f) {
			args[i++] = lv7;
		}

		boolean bl = lv5 == null || lv5.field_41 == class_7.field_7 || lv5.field_43 != 0.0F;
		boolean bl2 = lv2 == null || lv2.field_41 == class_7.field_7 || lv2.field_43 != 0.0F;
		boolean bl3 = lv4 == null || lv4.field_41 == class_7.field_7 || lv4.field_43 != 0.0F;
		boolean bl4 = lv3 == null || lv3.field_41 == class_7.field_7 || lv3.field_43 != 0.0F;
		if (bl && bl4) {
			class_9 lv8 = this.method_65(arg.field_40 - 1, arg.field_39, arg.field_38 - 1, 1, d);
			if (lv8 != null && !lv8.field_42 && lv8.method_31(arg2) < f) {
				args[i++] = lv8;
			}
		}

		if (bl && bl3) {
			class_9 lv8 = this.method_65(arg.field_40 + 1, arg.field_39, arg.field_38 - 1, 1, d);
			if (lv8 != null && !lv8.field_42 && lv8.method_31(arg2) < f) {
				args[i++] = lv8;
			}
		}

		if (bl2 && bl4) {
			class_9 lv8 = this.method_65(arg.field_40 - 1, arg.field_39, arg.field_38 + 1, 1, d);
			if (lv8 != null && !lv8.field_42 && lv8.method_31(arg2) < f) {
				args[i++] = lv8;
			}
		}

		if (bl2 && bl3) {
			class_9 lv8 = this.method_65(arg.field_40 + 1, arg.field_39, arg.field_38 + 1, 1, d);
			if (lv8 != null && !lv8.field_42 && lv8.method_31(arg2) < f) {
				args[i++] = lv8;
			}
		}

		return i;
	}

	private double method_66(class_2338 arg) {
		if (!this.field_33.method_5799()) {
			class_2338 lv = arg.method_10074();
			class_265 lv2 = this.field_26.method_8320(lv).method_11628(this.field_26, lv);
			return (double)lv.method_10264() + (lv2.method_1110() ? 0.0 : lv2.method_1105(class_2350.class_2351.field_11052));
		} else {
			return (double)arg.method_10264() + 0.5;
		}
	}

	@Nullable
	private class_9 method_65(int i, int j, int k, int l, double d) {
		class_9 lv = null;
		class_2338 lv2 = new class_2338(i, j, k);
		double e = this.method_66(lv2);
		if (e - d > 1.125) {
			return null;
		} else {
			class_7 lv3 = this.method_17(this.field_26, i, j, k, this.field_33, this.field_31, this.field_30, this.field_28, false, false);
			float f = this.field_33.method_5944(lv3);
			double g = (double)this.field_33.field_5998 / 2.0;
			if (f >= 0.0F) {
				lv = this.method_13(i, j, k);
				lv.field_41 = lv3;
				lv.field_43 = Math.max(lv.field_43, f);
			}

			if (lv3 != class_7.field_18 && lv3 != class_7.field_12) {
				if (lv == null && l > 0 && lv3 != class_7.field_10 && lv3 != class_7.field_19) {
					lv = this.method_65(i, j + 1, k, l - 1, d);
				}

				if (lv3 == class_7.field_7) {
					class_238 lv4 = new class_238(
						(double)i - g + 0.5, (double)j + 0.001, (double)k - g + 0.5, (double)i + g + 0.5, (double)((float)j + this.field_33.field_6019), (double)k + g + 0.5
					);
					if (!this.field_33.field_6002.method_8587(null, lv4)) {
						return null;
					}

					class_7 lv5 = this.method_17(this.field_26, i, j - 1, k, this.field_33, this.field_31, this.field_30, this.field_28, false, false);
					if (lv5 == class_7.field_22) {
						lv = this.method_13(i, j, k);
						lv.field_41 = class_7.field_12;
						lv.field_43 = Math.max(lv.field_43, f);
						return lv;
					}

					if (lv5 == class_7.field_18) {
						lv = this.method_13(i, j, k);
						lv.field_41 = class_7.field_18;
						lv.field_43 = Math.max(lv.field_43, f);
						return lv;
					}

					int m = 0;

					while (j > 0 && lv3 == class_7.field_7) {
						j--;
						if (m++ >= this.field_33.method_5850()) {
							return null;
						}

						lv3 = this.method_17(this.field_26, i, j, k, this.field_33, this.field_31, this.field_30, this.field_28, false, false);
						f = this.field_33.method_5944(lv3);
						if (lv3 != class_7.field_7 && f >= 0.0F) {
							lv = this.method_13(i, j, k);
							lv.field_41 = lv3;
							lv.field_43 = Math.max(lv.field_43, f);
							break;
						}

						if (f < 0.0F) {
							return null;
						}
					}
				}

				return lv;
			} else {
				if (j < this.field_33.field_6002.method_8615() - 10 && lv != null) {
					lv.field_43++;
				}

				return lv;
			}
		}
	}

	@Override
	protected class_7 method_61(class_1922 arg, boolean bl, boolean bl2, class_2338 arg2, class_7 arg3) {
		if (arg3 == class_7.field_21
			&& !(arg.method_8320(arg2).method_11614() instanceof class_2241)
			&& !(arg.method_8320(arg2.method_10074()).method_11614() instanceof class_2241)) {
			arg3 = class_7.field_10;
		}

		if (arg3 == class_7.field_15 || arg3 == class_7.field_23 || arg3 == class_7.field_8) {
			arg3 = class_7.field_22;
		}

		if (arg3 == class_7.field_6) {
			arg3 = class_7.field_22;
		}

		return arg3;
	}

	@Override
	public class_7 method_25(class_1922 arg, int i, int j, int k) {
		class_7 lv = this.method_58(arg, i, j, k);
		if (lv == class_7.field_18) {
			for (class_2350 lv2 : class_2350.values()) {
				class_7 lv3 = this.method_58(arg, i + lv2.method_10148(), j + lv2.method_10164(), k + lv2.method_10165());
				if (lv3 == class_7.field_22) {
					return class_7.field_4;
				}
			}

			return class_7.field_18;
		} else {
			if (lv == class_7.field_7 && j >= 1) {
				class_2248 lv4 = arg.method_8320(new class_2338(i, j - 1, k)).method_11614();
				class_7 lv5 = this.method_58(arg, i, j - 1, k);
				if (lv5 != class_7.field_12 && lv5 != class_7.field_7 && lv5 != class_7.field_14) {
					lv = class_7.field_12;
				} else {
					lv = class_7.field_7;
				}

				if (lv5 == class_7.field_3 || lv4 == class_2246.field_10092 || lv4 == class_2246.field_17350) {
					lv = class_7.field_3;
				}

				if (lv5 == class_7.field_11) {
					lv = class_7.field_11;
				}

				if (lv5 == class_7.field_17) {
					lv = class_7.field_17;
				}
			}

			return this.method_59(arg, i, j, k, lv);
		}
	}
}
