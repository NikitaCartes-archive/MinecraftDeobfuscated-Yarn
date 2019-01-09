package net.minecraft;

import com.google.common.collect.Sets;
import java.util.EnumSet;
import java.util.Set;
import javax.annotation.Nullable;

public class class_14 extends class_8 {
	protected float field_63;

	@Override
	public void method_12(class_1922 arg, class_1308 arg2) {
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
			i = (int)this.field_33.method_5829().field_1322;
			class_2338.class_2339 lv = new class_2338.class_2339(class_3532.method_15357(this.field_33.field_5987), i, class_3532.method_15357(this.field_33.field_6035));

			for (class_2248 lv2 = this.field_26.method_8320(lv).method_11614(); lv2 == class_2246.field_10382; lv2 = this.field_26.method_8320(lv).method_11614()) {
				lv.method_10103(class_3532.method_15357(this.field_33.field_5987), ++i, class_3532.method_15357(this.field_33.field_6035));
			}

			i--;
		} else if (this.field_33.field_5952) {
			i = class_3532.method_15357(this.field_33.method_5829().field_1322 + 0.5);
		} else {
			class_2338 lv3 = new class_2338(this.field_33);

			while (
				(this.field_26.method_8320(lv3).method_11588() || this.field_26.method_8320(lv3).method_11609(this.field_26, lv3, class_10.field_50))
					&& lv3.method_10264() > 0
			) {
				lv3 = lv3.method_10074();
			}

			i = lv3.method_10084().method_10264();
		}

		class_2338 lv3 = new class_2338(this.field_33);
		class_7 lv4 = this.method_57(this.field_33, lv3.method_10263(), i, lv3.method_10260());
		if (this.field_33.method_5944(lv4) < 0.0F) {
			Set<class_2338> set = Sets.<class_2338>newHashSet();
			set.add(new class_2338(this.field_33.method_5829().field_1323, (double)i, this.field_33.method_5829().field_1321));
			set.add(new class_2338(this.field_33.method_5829().field_1323, (double)i, this.field_33.method_5829().field_1324));
			set.add(new class_2338(this.field_33.method_5829().field_1320, (double)i, this.field_33.method_5829().field_1321));
			set.add(new class_2338(this.field_33.method_5829().field_1320, (double)i, this.field_33.method_5829().field_1324));

			for (class_2338 lv5 : set) {
				class_7 lv6 = this.method_63(this.field_33, lv5);
				if (this.field_33.method_5944(lv6) >= 0.0F) {
					return this.method_13(lv5.method_10263(), lv5.method_10264(), lv5.method_10260());
				}
			}
		}

		return this.method_13(lv3.method_10263(), i, lv3.method_10260());
	}

	@Override
	public class_9 method_16(double d, double e, double f) {
		return this.method_13(class_3532.method_15357(d), class_3532.method_15357(e), class_3532.method_15357(f));
	}

	@Override
	public int method_18(class_9[] args, class_9 arg, class_9 arg2, float f) {
		int i = 0;
		int j = 0;
		class_7 lv = this.method_57(this.field_33, arg.field_40, arg.field_39 + 1, arg.field_38);
		if (this.field_33.method_5944(lv) >= 0.0F) {
			j = class_3532.method_15375(Math.max(1.0F, this.field_33.field_6013));
		}

		double d = method_60(this.field_26, new class_2338(arg.field_40, arg.field_39, arg.field_38));
		class_9 lv2 = this.method_62(arg.field_40, arg.field_39, arg.field_38 + 1, j, d, class_2350.field_11035);
		class_9 lv3 = this.method_62(arg.field_40 - 1, arg.field_39, arg.field_38, j, d, class_2350.field_11039);
		class_9 lv4 = this.method_62(arg.field_40 + 1, arg.field_39, arg.field_38, j, d, class_2350.field_11034);
		class_9 lv5 = this.method_62(arg.field_40, arg.field_39, arg.field_38 - 1, j, d, class_2350.field_11043);
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

		boolean bl = lv5 == null || lv5.field_41 == class_7.field_7 || lv5.field_43 != 0.0F;
		boolean bl2 = lv2 == null || lv2.field_41 == class_7.field_7 || lv2.field_43 != 0.0F;
		boolean bl3 = lv4 == null || lv4.field_41 == class_7.field_7 || lv4.field_43 != 0.0F;
		boolean bl4 = lv3 == null || lv3.field_41 == class_7.field_7 || lv3.field_43 != 0.0F;
		if (bl && bl4) {
			class_9 lv6 = this.method_62(arg.field_40 - 1, arg.field_39, arg.field_38 - 1, j, d, class_2350.field_11043);
			if (lv6 != null && !lv6.field_42 && lv6.method_31(arg2) < f) {
				args[i++] = lv6;
			}
		}

		if (bl && bl3) {
			class_9 lv6 = this.method_62(arg.field_40 + 1, arg.field_39, arg.field_38 - 1, j, d, class_2350.field_11043);
			if (lv6 != null && !lv6.field_42 && lv6.method_31(arg2) < f) {
				args[i++] = lv6;
			}
		}

		if (bl2 && bl4) {
			class_9 lv6 = this.method_62(arg.field_40 - 1, arg.field_39, arg.field_38 + 1, j, d, class_2350.field_11035);
			if (lv6 != null && !lv6.field_42 && lv6.method_31(arg2) < f) {
				args[i++] = lv6;
			}
		}

		if (bl2 && bl3) {
			class_9 lv6 = this.method_62(arg.field_40 + 1, arg.field_39, arg.field_38 + 1, j, d, class_2350.field_11035);
			if (lv6 != null && !lv6.field_42 && lv6.method_31(arg2) < f) {
				args[i++] = lv6;
			}
		}

		return i;
	}

	@Nullable
	private class_9 method_62(int i, int j, int k, int l, double d, class_2350 arg) {
		class_9 lv = null;
		class_2338 lv2 = new class_2338(i, j, k);
		double e = method_60(this.field_26, lv2);
		if (e - d > 1.125) {
			return null;
		} else {
			class_7 lv3 = this.method_57(this.field_33, i, j, k);
			float f = this.field_33.method_5944(lv3);
			double g = (double)this.field_33.field_5998 / 2.0;
			if (f >= 0.0F) {
				lv = this.method_13(i, j, k);
				lv.field_41 = lv3;
				lv.field_43 = Math.max(lv.field_43, f);
			}

			if (lv3 == class_7.field_12) {
				return lv;
			} else {
				if (lv == null && l > 0 && lv3 != class_7.field_10 && lv3 != class_7.field_19) {
					lv = this.method_62(i, j + 1, k, l - 1, d, arg);
					if (lv != null && (lv.field_41 == class_7.field_7 || lv.field_41 == class_7.field_12) && this.field_33.field_5998 < 1.0F) {
						double h = (double)(i - arg.method_10148()) + 0.5;
						double m = (double)(k - arg.method_10165()) + 0.5;
						class_238 lv4 = new class_238(
							h - g, (double)j + 0.001, m - g, h + g, (double)this.field_33.field_6019 + method_60(this.field_26, lv2.method_10084()) - 0.002, m + g
						);
						if (!this.field_33.field_6002.method_8587(null, lv4)) {
							lv = null;
						}
					}
				}

				if (lv3 == class_7.field_18 && !this.method_22()) {
					if (this.method_57(this.field_33, i, j - 1, k) != class_7.field_18) {
						return lv;
					}

					while (j > 0) {
						lv3 = this.method_57(this.field_33, i, --j, k);
						if (lv3 != class_7.field_18) {
							return lv;
						}

						lv = this.method_13(i, j, k);
						lv.field_41 = lv3;
						lv.field_43 = Math.max(lv.field_43, this.field_33.method_5944(lv3));
					}
				}

				if (lv3 == class_7.field_7) {
					class_238 lv5 = new class_238(
						(double)i - g + 0.5, (double)j + 0.001, (double)k - g + 0.5, (double)i + g + 0.5, (double)((float)j + this.field_33.field_6019), (double)k + g + 0.5
					);
					if (!this.field_33.field_6002.method_8587(null, lv5)) {
						return null;
					}

					if (this.field_33.field_5998 >= 1.0F) {
						class_7 lv6 = this.method_57(this.field_33, i, j - 1, k);
						if (lv6 == class_7.field_22) {
							lv = this.method_13(i, j, k);
							lv.field_41 = class_7.field_12;
							lv.field_43 = Math.max(lv.field_43, f);
							return lv;
						}
					}

					int n = 0;

					while (j > 0 && lv3 == class_7.field_7) {
						j--;
						if (n++ >= this.field_33.method_5850()) {
							return null;
						}

						lv3 = this.method_57(this.field_33, i, j, k);
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
			}
		}
	}

	public static double method_60(class_1922 arg, class_2338 arg2) {
		class_2338 lv = arg2.method_10074();
		class_265 lv2 = arg.method_8320(lv).method_11628(arg, lv);
		return (double)lv.method_10264() + (lv2.method_1110() ? 0.0 : lv2.method_1105(class_2350.class_2351.field_11052));
	}

	@Override
	public class_7 method_17(class_1922 arg, int i, int j, int k, class_1308 arg2, int l, int m, int n, boolean bl, boolean bl2) {
		EnumSet<class_7> enumSet = EnumSet.noneOf(class_7.class);
		class_7 lv = class_7.field_22;
		double d = (double)arg2.field_5998 / 2.0;
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

	public class_7 method_64(
		class_1922 arg, int i, int j, int k, int l, int m, int n, boolean bl, boolean bl2, EnumSet<class_7> enumSet, class_7 arg2, class_2338 arg3
	) {
		for (int o = 0; o < l; o++) {
			for (int p = 0; p < m; p++) {
				for (int q = 0; q < n; q++) {
					int r = o + i;
					int s = p + j;
					int t = q + k;
					class_7 lv = this.method_25(arg, r, s, t);
					lv = this.method_61(arg, bl, bl2, arg3, lv);
					if (o == 0 && p == 0 && q == 0) {
						arg2 = lv;
					}

					enumSet.add(lv);
				}
			}
		}

		return arg2;
	}

	protected class_7 method_61(class_1922 arg, boolean bl, boolean bl2, class_2338 arg2, class_7 arg3) {
		if (arg3 == class_7.field_23 && bl && bl2) {
			arg3 = class_7.field_12;
		}

		if (arg3 == class_7.field_15 && !bl2) {
			arg3 = class_7.field_22;
		}

		if (arg3 == class_7.field_21
			&& !(arg.method_8320(arg2).method_11614() instanceof class_2241)
			&& !(arg.method_8320(arg2.method_10074()).method_11614() instanceof class_2241)) {
			arg3 = class_7.field_10;
		}

		if (arg3 == class_7.field_6) {
			arg3 = class_7.field_22;
		}

		return arg3;
	}

	private class_7 method_63(class_1308 arg, class_2338 arg2) {
		return this.method_57(arg, arg2.method_10263(), arg2.method_10264(), arg2.method_10260());
	}

	private class_7 method_57(class_1308 arg, int i, int j, int k) {
		return this.method_17(this.field_26, i, j, k, arg, this.field_31, this.field_30, this.field_28, this.method_24(), this.method_23());
	}

	@Override
	public class_7 method_25(class_1922 arg, int i, int j, int k) {
		class_7 lv = this.method_58(arg, i, j, k);
		if (lv == class_7.field_7 && j >= 1) {
			class_2248 lv2 = arg.method_8320(new class_2338(i, j - 1, k)).method_11614();
			class_7 lv3 = this.method_58(arg, i, j - 1, k);
			lv = lv3 != class_7.field_12 && lv3 != class_7.field_7 && lv3 != class_7.field_18 && lv3 != class_7.field_14 ? class_7.field_12 : class_7.field_7;
			if (lv3 == class_7.field_3 || lv2 == class_2246.field_10092 || lv2 == class_2246.field_17350) {
				lv = class_7.field_3;
			}

			if (lv3 == class_7.field_11) {
				lv = class_7.field_11;
			}

			if (lv3 == class_7.field_17) {
				lv = class_7.field_17;
			}
		}

		return this.method_59(arg, i, j, k, lv);
	}

	public class_7 method_59(class_1922 arg, int i, int j, int k, class_7 arg2) {
		if (arg2 == class_7.field_12) {
			try (class_2338.class_2340 lv = class_2338.class_2340.method_10109()) {
				for (int l = -1; l <= 1; l++) {
					for (int m = -1; m <= 1; m++) {
						if (l != 0 || m != 0) {
							class_2248 lv2 = arg.method_8320(lv.method_10113(l + i, j, m + k)).method_11614();
							if (lv2 == class_2246.field_10029) {
								arg2 = class_7.field_20;
							} else if (lv2 == class_2246.field_10036) {
								arg2 = class_7.field_9;
							} else if (lv2 == class_2246.field_16999) {
								arg2 = class_7.field_5;
							}
						}
					}
				}
			}
		}

		return arg2;
	}

	protected class_7 method_58(class_1922 arg, int i, int j, int k) {
		class_2338 lv = new class_2338(i, j, k);
		class_2680 lv2 = arg.method_8320(lv);
		class_2248 lv3 = lv2.method_11614();
		class_3614 lv4 = lv2.method_11620();
		if (lv2.method_11588()) {
			return class_7.field_7;
		} else if (lv3.method_9525(class_3481.field_15487) || lv3 == class_2246.field_10588) {
			return class_7.field_19;
		} else if (lv3 == class_2246.field_10036) {
			return class_7.field_3;
		} else if (lv3 == class_2246.field_10029) {
			return class_7.field_11;
		} else if (lv3 == class_2246.field_16999) {
			return class_7.field_17;
		} else if (lv3 instanceof class_2323 && lv4 == class_3614.field_15932 && !(Boolean)lv2.method_11654(class_2323.field_10945)) {
			return class_7.field_23;
		} else if (lv3 instanceof class_2323 && lv4 == class_3614.field_15953 && !(Boolean)lv2.method_11654(class_2323.field_10945)) {
			return class_7.field_8;
		} else if (lv3 instanceof class_2323 && (Boolean)lv2.method_11654(class_2323.field_10945)) {
			return class_7.field_15;
		} else if (lv3 instanceof class_2241) {
			return class_7.field_21;
		} else if (lv3 instanceof class_2397) {
			return class_7.field_6;
		} else if (!lv3.method_9525(class_3481.field_16584)
			&& !lv3.method_9525(class_3481.field_15504)
			&& (!(lv3 instanceof class_2349) || (Boolean)lv2.method_11654(class_2349.field_11026))) {
			class_3610 lv5 = arg.method_8316(lv);
			if (lv5.method_15767(class_3486.field_15517)) {
				return class_7.field_18;
			} else if (lv5.method_15767(class_3486.field_15518)) {
				return class_7.field_14;
			} else {
				return lv2.method_11609(arg, lv, class_10.field_50) ? class_7.field_7 : class_7.field_22;
			}
		} else {
			return class_7.field_10;
		}
	}
}
