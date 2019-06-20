package net.minecraft;

import com.google.common.collect.Sets;
import java.util.EnumSet;
import java.util.Set;
import javax.annotation.Nullable;

public class class_6 extends class_14 {
	@Override
	public void method_12(class_1941 arg, class_1308 arg2) {
		super.method_12(arg, arg2);
		this.field_63 = arg2.method_5944(class_7.field_18);
	}

	@Override
	public void method_19() {
		this.field_33.method_5941(class_7.field_18, this.field_63);
		super.method_19();
	}

	@Override
	public class_9 method_21() {
		int i;
		if (this.method_22() && this.field_33.method_5799()) {
			i = class_3532.method_15357(this.field_33.method_5829().field_1322);
			class_2338.class_2339 lv = new class_2338.class_2339(this.field_33.field_5987, (double)i, this.field_33.field_6035);

			for (class_2248 lv2 = this.field_26.method_8320(lv).method_11614(); lv2 == class_2246.field_10382; lv2 = this.field_26.method_8320(lv).method_11614()) {
				lv.method_10102(this.field_33.field_5987, (double)(++i), this.field_33.field_6035);
			}
		} else {
			i = class_3532.method_15357(this.field_33.method_5829().field_1322 + 0.5);
		}

		class_2338 lv3 = new class_2338(this.field_33);
		class_7 lv4 = this.method_9(this.field_33, lv3.method_10263(), i, lv3.method_10260());
		if (this.field_33.method_5944(lv4) < 0.0F) {
			Set<class_2338> set = Sets.<class_2338>newHashSet();
			set.add(new class_2338(this.field_33.method_5829().field_1323, (double)i, this.field_33.method_5829().field_1321));
			set.add(new class_2338(this.field_33.method_5829().field_1323, (double)i, this.field_33.method_5829().field_1324));
			set.add(new class_2338(this.field_33.method_5829().field_1320, (double)i, this.field_33.method_5829().field_1321));
			set.add(new class_2338(this.field_33.method_5829().field_1320, (double)i, this.field_33.method_5829().field_1324));

			for (class_2338 lv5 : set) {
				class_7 lv6 = this.method_10(this.field_33, lv5);
				if (this.field_33.method_5944(lv6) >= 0.0F) {
					return super.method_13(lv5.method_10263(), lv5.method_10264(), lv5.method_10260());
				}
			}
		}

		return super.method_13(lv3.method_10263(), i, lv3.method_10260());
	}

	@Override
	public class_9 method_16(double d, double e, double f) {
		return super.method_13(class_3532.method_15357(d), class_3532.method_15357(e), class_3532.method_15357(f));
	}

	@Override
	public int method_18(class_9[] args, class_9 arg) {
		int i = 0;
		class_9 lv = this.method_13(arg.field_40, arg.field_39, arg.field_38 + 1);
		class_9 lv2 = this.method_13(arg.field_40 - 1, arg.field_39, arg.field_38);
		class_9 lv3 = this.method_13(arg.field_40 + 1, arg.field_39, arg.field_38);
		class_9 lv4 = this.method_13(arg.field_40, arg.field_39, arg.field_38 - 1);
		class_9 lv5 = this.method_13(arg.field_40, arg.field_39 + 1, arg.field_38);
		class_9 lv6 = this.method_13(arg.field_40, arg.field_39 - 1, arg.field_38);
		if (lv != null && !lv.field_42) {
			args[i++] = lv;
		}

		if (lv2 != null && !lv2.field_42) {
			args[i++] = lv2;
		}

		if (lv3 != null && !lv3.field_42) {
			args[i++] = lv3;
		}

		if (lv4 != null && !lv4.field_42) {
			args[i++] = lv4;
		}

		if (lv5 != null && !lv5.field_42) {
			args[i++] = lv5;
		}

		if (lv6 != null && !lv6.field_42) {
			args[i++] = lv6;
		}

		boolean bl = lv4 == null || lv4.field_43 != 0.0F;
		boolean bl2 = lv == null || lv.field_43 != 0.0F;
		boolean bl3 = lv3 == null || lv3.field_43 != 0.0F;
		boolean bl4 = lv2 == null || lv2.field_43 != 0.0F;
		boolean bl5 = lv5 == null || lv5.field_43 != 0.0F;
		boolean bl6 = lv6 == null || lv6.field_43 != 0.0F;
		if (bl && bl4) {
			class_9 lv7 = this.method_13(arg.field_40 - 1, arg.field_39, arg.field_38 - 1);
			if (lv7 != null && !lv7.field_42) {
				args[i++] = lv7;
			}
		}

		if (bl && bl3) {
			class_9 lv7 = this.method_13(arg.field_40 + 1, arg.field_39, arg.field_38 - 1);
			if (lv7 != null && !lv7.field_42) {
				args[i++] = lv7;
			}
		}

		if (bl2 && bl4) {
			class_9 lv7 = this.method_13(arg.field_40 - 1, arg.field_39, arg.field_38 + 1);
			if (lv7 != null && !lv7.field_42) {
				args[i++] = lv7;
			}
		}

		if (bl2 && bl3) {
			class_9 lv7 = this.method_13(arg.field_40 + 1, arg.field_39, arg.field_38 + 1);
			if (lv7 != null && !lv7.field_42) {
				args[i++] = lv7;
			}
		}

		if (bl && bl5) {
			class_9 lv7 = this.method_13(arg.field_40, arg.field_39 + 1, arg.field_38 - 1);
			if (lv7 != null && !lv7.field_42) {
				args[i++] = lv7;
			}
		}

		if (bl2 && bl5) {
			class_9 lv7 = this.method_13(arg.field_40, arg.field_39 + 1, arg.field_38 + 1);
			if (lv7 != null && !lv7.field_42) {
				args[i++] = lv7;
			}
		}

		if (bl3 && bl5) {
			class_9 lv7 = this.method_13(arg.field_40 + 1, arg.field_39 + 1, arg.field_38);
			if (lv7 != null && !lv7.field_42) {
				args[i++] = lv7;
			}
		}

		if (bl4 && bl5) {
			class_9 lv7 = this.method_13(arg.field_40 - 1, arg.field_39 + 1, arg.field_38);
			if (lv7 != null && !lv7.field_42) {
				args[i++] = lv7;
			}
		}

		if (bl && bl6) {
			class_9 lv7 = this.method_13(arg.field_40, arg.field_39 - 1, arg.field_38 - 1);
			if (lv7 != null && !lv7.field_42) {
				args[i++] = lv7;
			}
		}

		if (bl2 && bl6) {
			class_9 lv7 = this.method_13(arg.field_40, arg.field_39 - 1, arg.field_38 + 1);
			if (lv7 != null && !lv7.field_42) {
				args[i++] = lv7;
			}
		}

		if (bl3 && bl6) {
			class_9 lv7 = this.method_13(arg.field_40 + 1, arg.field_39 - 1, arg.field_38);
			if (lv7 != null && !lv7.field_42) {
				args[i++] = lv7;
			}
		}

		if (bl4 && bl6) {
			class_9 lv7 = this.method_13(arg.field_40 - 1, arg.field_39 - 1, arg.field_38);
			if (lv7 != null && !lv7.field_42) {
				args[i++] = lv7;
			}
		}

		return i;
	}

	@Nullable
	@Override
	protected class_9 method_13(int i, int j, int k) {
		class_9 lv = null;
		class_7 lv2 = this.method_9(this.field_33, i, j, k);
		float f = this.field_33.method_5944(lv2);
		if (f >= 0.0F) {
			lv = super.method_13(i, j, k);
			lv.field_41 = lv2;
			lv.field_43 = Math.max(lv.field_43, f);
			if (lv2 == class_7.field_12) {
				lv.field_43++;
			}
		}

		return lv2 != class_7.field_7 && lv2 != class_7.field_12 ? lv : lv;
	}

	@Override
	public class_7 method_17(class_1922 arg, int i, int j, int k, class_1308 arg2, int l, int m, int n, boolean bl, boolean bl2) {
		EnumSet<class_7> enumSet = EnumSet.noneOf(class_7.class);
		class_7 lv = class_7.field_22;
		class_2338 lv2 = new class_2338(arg2);
		lv = this.method_64(arg, i, j, k, l, m, n, bl, bl2, enumSet, lv, lv2);
		if (enumSet.contains(class_7.field_10)) {
			return class_7.field_10;
		} else {
			class_7 lv3 = class_7.field_22;

			for (class_7 lv4 : enumSet) {
				if (arg2.method_5944(lv4) < 0.0F) {
					return lv4;
				}

				if (arg2.method_5944(lv4) >= arg2.method_5944(lv3)) {
					lv3 = lv4;
				}
			}

			return lv == class_7.field_7 && arg2.method_5944(lv3) == 0.0F ? class_7.field_7 : lv3;
		}
	}

	@Override
	public class_7 method_25(class_1922 arg, int i, int j, int k) {
		class_7 lv = this.method_58(arg, i, j, k);
		if (lv == class_7.field_7 && j >= 1) {
			class_2248 lv2 = arg.method_8320(new class_2338(i, j - 1, k)).method_11614();
			class_7 lv3 = this.method_58(arg, i, j - 1, k);
			if (lv3 == class_7.field_3 || lv2 == class_2246.field_10092 || lv3 == class_7.field_14 || lv2 == class_2246.field_17350) {
				lv = class_7.field_3;
			} else if (lv3 == class_7.field_11) {
				lv = class_7.field_11;
			} else if (lv3 == class_7.field_17) {
				lv = class_7.field_17;
			} else {
				lv = lv3 != class_7.field_12 && lv3 != class_7.field_7 && lv3 != class_7.field_18 ? class_7.field_12 : class_7.field_7;
			}
		}

		return this.method_59(arg, i, j, k, lv);
	}

	private class_7 method_10(class_1308 arg, class_2338 arg2) {
		return this.method_9(arg, arg2.method_10263(), arg2.method_10264(), arg2.method_10260());
	}

	private class_7 method_9(class_1308 arg, int i, int j, int k) {
		return this.method_17(this.field_26, i, j, k, arg, this.field_31, this.field_30, this.field_28, this.method_24(), this.method_23());
	}
}
