package net.minecraft;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import java.util.List;
import java.util.Set;

public class class_2665 extends class_2318 {
	public static final class_2746 field_12191 = class_2741.field_12552;
	protected static final class_265 field_12188 = class_2248.method_9541(0.0, 0.0, 0.0, 12.0, 16.0, 16.0);
	protected static final class_265 field_12184 = class_2248.method_9541(4.0, 0.0, 0.0, 16.0, 16.0, 16.0);
	protected static final class_265 field_12186 = class_2248.method_9541(0.0, 0.0, 0.0, 16.0, 16.0, 12.0);
	protected static final class_265 field_12189 = class_2248.method_9541(0.0, 0.0, 4.0, 16.0, 16.0, 16.0);
	protected static final class_265 field_12185 = class_2248.method_9541(0.0, 0.0, 0.0, 16.0, 12.0, 16.0);
	protected static final class_265 field_12190 = class_2248.method_9541(0.0, 4.0, 0.0, 16.0, 16.0, 16.0);
	private final boolean field_12187;

	public class_2665(boolean bl, class_2248.class_2251 arg) {
		super(arg);
		this.method_9590(this.field_10647.method_11664().method_11657(field_10927, class_2350.field_11043).method_11657(field_12191, Boolean.valueOf(false)));
		this.field_12187 = bl;
	}

	@Override
	public boolean method_16362(class_2680 arg, class_1922 arg2, class_2338 arg3) {
		return !(Boolean)arg.method_11654(field_12191);
	}

	@Override
	public class_265 method_9530(class_2680 arg, class_1922 arg2, class_2338 arg3, class_3726 arg4) {
		if ((Boolean)arg.method_11654(field_12191)) {
			switch ((class_2350)arg.method_11654(field_10927)) {
				case field_11033:
					return field_12190;
				case field_11036:
				default:
					return field_12185;
				case field_11043:
					return field_12189;
				case field_11035:
					return field_12186;
				case field_11039:
					return field_12184;
				case field_11034:
					return field_12188;
			}
		} else {
			return class_259.method_1077();
		}
	}

	@Override
	public boolean method_9521(class_2680 arg, class_1922 arg2, class_2338 arg3) {
		return false;
	}

	@Override
	public void method_9567(class_1937 arg, class_2338 arg2, class_2680 arg3, class_1309 arg4, class_1799 arg5) {
		if (!arg.field_9236) {
			this.method_11483(arg, arg2, arg3);
		}
	}

	@Override
	public void method_9612(class_2680 arg, class_1937 arg2, class_2338 arg3, class_2248 arg4, class_2338 arg5, boolean bl) {
		if (!arg2.field_9236) {
			this.method_11483(arg2, arg3, arg);
		}
	}

	@Override
	public void method_9615(class_2680 arg, class_1937 arg2, class_2338 arg3, class_2680 arg4, boolean bl) {
		if (arg4.method_11614() != arg.method_11614()) {
			if (!arg2.field_9236 && arg2.method_8321(arg3) == null) {
				this.method_11483(arg2, arg3, arg);
			}
		}
	}

	@Override
	public class_2680 method_9605(class_1750 arg) {
		return this.method_9564().method_11657(field_10927, arg.method_7715().method_10153()).method_11657(field_12191, Boolean.valueOf(false));
	}

	private void method_11483(class_1937 arg, class_2338 arg2, class_2680 arg3) {
		class_2350 lv = arg3.method_11654(field_10927);
		boolean bl = this.method_11482(arg, arg2, lv);
		if (bl && !(Boolean)arg3.method_11654(field_12191)) {
			if (new class_2674(arg, arg2, lv, true).method_11537()) {
				arg.method_8427(arg2, this, 0, lv.method_10146());
			}
		} else if (!bl && (Boolean)arg3.method_11654(field_12191)) {
			class_2338 lv2 = arg2.method_10079(lv, 2);
			class_2680 lv3 = arg.method_8320(lv2);
			int i = 1;
			if (lv3.method_11614() == class_2246.field_10008 && lv3.method_11654(field_10927) == lv) {
				class_2586 lv4 = arg.method_8321(lv2);
				if (lv4 instanceof class_2669) {
					class_2669 lv5 = (class_2669)lv4;
					if (lv5.method_11501() && (lv5.method_11499(0.0F) < 0.5F || arg.method_8510() == lv5.method_11508() || ((class_3218)arg).method_14177())) {
						i = 2;
					}
				}
			}

			arg.method_8427(arg2, this, i, lv.method_10146());
		}
	}

	private boolean method_11482(class_1937 arg, class_2338 arg2, class_2350 arg3) {
		for (class_2350 lv : class_2350.values()) {
			if (lv != arg3 && arg.method_8459(arg2.method_10093(lv), lv)) {
				return true;
			}
		}

		if (arg.method_8459(arg2, class_2350.field_11033)) {
			return true;
		} else {
			class_2338 lv2 = arg2.method_10084();

			for (class_2350 lv3 : class_2350.values()) {
				if (lv3 != class_2350.field_11033 && arg.method_8459(lv2.method_10093(lv3), lv3)) {
					return true;
				}
			}

			return false;
		}
	}

	@Override
	public boolean method_9592(class_2680 arg, class_1937 arg2, class_2338 arg3, int i, int j) {
		class_2350 lv = arg.method_11654(field_10927);
		if (!arg2.field_9236) {
			boolean bl = this.method_11482(arg2, arg3, lv);
			if (bl && (i == 1 || i == 2)) {
				arg2.method_8652(arg3, arg.method_11657(field_12191, Boolean.valueOf(true)), 2);
				return false;
			}

			if (!bl && i == 0) {
				return false;
			}
		}

		if (i == 0) {
			if (!this.method_11481(arg2, arg3, lv, true)) {
				return false;
			}

			arg2.method_8652(arg3, arg.method_11657(field_12191, Boolean.valueOf(true)), 67);
			arg2.method_8396(null, arg3, class_3417.field_15134, class_3419.field_15245, 0.5F, arg2.field_9229.nextFloat() * 0.25F + 0.6F);
		} else if (i == 1 || i == 2) {
			class_2586 lv2 = arg2.method_8321(arg3.method_10093(lv));
			if (lv2 instanceof class_2669) {
				((class_2669)lv2).method_11513();
			}

			arg2.method_8652(
				arg3,
				class_2246.field_10008
					.method_9564()
					.method_11657(class_2667.field_12196, lv)
					.method_11657(class_2667.field_12197, this.field_12187 ? class_2764.field_12634 : class_2764.field_12637),
				3
			);
			arg2.method_8526(arg3, class_2667.method_11489(this.method_9564().method_11657(field_10927, class_2350.method_10143(j & 7)), lv, false, true));
			if (this.field_12187) {
				class_2338 lv3 = arg3.method_10069(lv.method_10148() * 2, lv.method_10164() * 2, lv.method_10165() * 2);
				class_2680 lv4 = arg2.method_8320(lv3);
				class_2248 lv5 = lv4.method_11614();
				boolean bl2 = false;
				if (lv5 == class_2246.field_10008) {
					class_2586 lv6 = arg2.method_8321(lv3);
					if (lv6 instanceof class_2669) {
						class_2669 lv7 = (class_2669)lv6;
						if (lv7.method_11498() == lv && lv7.method_11501()) {
							lv7.method_11513();
							bl2 = true;
						}
					}
				}

				if (!bl2) {
					if (i != 1
						|| lv4.method_11588()
						|| !method_11484(lv4, arg2, lv3, lv.method_10153(), false, lv)
						|| lv4.method_11586() != class_3619.field_15974 && lv5 != class_2246.field_10560 && lv5 != class_2246.field_10615) {
						arg2.method_8650(arg3.method_10093(lv), false);
					} else {
						this.method_11481(arg2, arg3, lv, false);
					}
				}
			} else {
				arg2.method_8650(arg3.method_10093(lv), false);
			}

			arg2.method_8396(null, arg3, class_3417.field_15228, class_3419.field_15245, 0.5F, arg2.field_9229.nextFloat() * 0.15F + 0.6F);
		}

		return true;
	}

	public static boolean method_11484(class_2680 arg, class_1937 arg2, class_2338 arg3, class_2350 arg4, boolean bl, class_2350 arg5) {
		class_2248 lv = arg.method_11614();
		if (lv == class_2246.field_10540) {
			return false;
		} else if (!arg2.method_8621().method_11952(arg3)) {
			return false;
		} else if (arg3.method_10264() >= 0 && (arg4 != class_2350.field_11033 || arg3.method_10264() != 0)) {
			if (arg3.method_10264() <= arg2.method_8322() - 1 && (arg4 != class_2350.field_11036 || arg3.method_10264() != arg2.method_8322() - 1)) {
				if (lv != class_2246.field_10560 && lv != class_2246.field_10615) {
					if (arg.method_11579(arg2, arg3) == -1.0F) {
						return false;
					}

					switch (arg.method_11586()) {
						case field_15972:
							return false;
						case field_15971:
							return bl;
						case field_15970:
							return arg4 == arg5;
					}
				} else if ((Boolean)arg.method_11654(field_12191)) {
					return false;
				}

				return !lv.method_9570();
			} else {
				return false;
			}
		} else {
			return false;
		}
	}

	private boolean method_11481(class_1937 arg, class_2338 arg2, class_2350 arg3, boolean bl) {
		class_2338 lv = arg2.method_10093(arg3);
		if (!bl && arg.method_8320(lv).method_11614() == class_2246.field_10379) {
			arg.method_8652(lv, class_2246.field_10124.method_9564(), 20);
		}

		class_2674 lv2 = new class_2674(arg, arg2, arg3, bl);
		if (!lv2.method_11537()) {
			return false;
		} else {
			List<class_2338> list = lv2.method_11541();
			List<class_2680> list2 = Lists.<class_2680>newArrayList();

			for (int i = 0; i < list.size(); i++) {
				class_2338 lv3 = (class_2338)list.get(i);
				list2.add(arg.method_8320(lv3));
			}

			List<class_2338> list3 = lv2.method_11536();
			int j = list.size() + list3.size();
			class_2680[] lvs = new class_2680[j];
			class_2350 lv4 = bl ? arg3 : arg3.method_10153();
			Set<class_2338> set = Sets.<class_2338>newHashSet(list);

			for (int k = list3.size() - 1; k >= 0; k--) {
				class_2338 lv5 = (class_2338)list3.get(k);
				class_2680 lv6 = arg.method_8320(lv5);
				class_2586 lv7 = lv6.method_11614().method_9570() ? arg.method_8321(lv5) : null;
				method_9610(lv6, arg, lv5, lv7);
				arg.method_8652(lv5, class_2246.field_10124.method_9564(), 18);
				j--;
				lvs[j] = lv6;
			}

			for (int k = list.size() - 1; k >= 0; k--) {
				class_2338 lv5 = (class_2338)list.get(k);
				class_2680 lv6 = arg.method_8320(lv5);
				lv5 = lv5.method_10093(lv4);
				set.remove(lv5);
				arg.method_8652(lv5, class_2246.field_10008.method_9564().method_11657(field_10927, arg3), 68);
				arg.method_8526(lv5, class_2667.method_11489((class_2680)list2.get(k), arg3, bl, false));
				j--;
				lvs[j] = lv6;
			}

			if (bl) {
				class_2764 lv8 = this.field_12187 ? class_2764.field_12634 : class_2764.field_12637;
				class_2680 lv9 = class_2246.field_10379.method_9564().method_11657(class_2671.field_10927, arg3).method_11657(class_2671.field_12224, lv8);
				class_2680 lv6 = class_2246.field_10008
					.method_9564()
					.method_11657(class_2667.field_12196, arg3)
					.method_11657(class_2667.field_12197, this.field_12187 ? class_2764.field_12634 : class_2764.field_12637);
				set.remove(lv);
				arg.method_8652(lv, lv6, 68);
				arg.method_8526(lv, class_2667.method_11489(lv9, arg3, true, true));
			}

			for (class_2338 lv5 : set) {
				arg.method_8652(lv5, class_2246.field_10124.method_9564(), 66);
			}

			for (int k = list3.size() - 1; k >= 0; k--) {
				class_2680 lv9 = lvs[j++];
				class_2338 lv10 = (class_2338)list3.get(k);
				lv9.method_11637(arg, lv10, 2);
				arg.method_8452(lv10, lv9.method_11614());
			}

			for (int k = list.size() - 1; k >= 0; k--) {
				arg.method_8452((class_2338)list.get(k), lvs[j++].method_11614());
			}

			if (bl) {
				arg.method_8452(lv, class_2246.field_10379);
			}

			return true;
		}
	}

	@Override
	public class_2680 method_9598(class_2680 arg, class_2470 arg2) {
		return arg.method_11657(field_10927, arg2.method_10503(arg.method_11654(field_10927)));
	}

	@Override
	public class_2680 method_9569(class_2680 arg, class_2415 arg2) {
		return arg.method_11626(arg2.method_10345(arg.method_11654(field_10927)));
	}

	@Override
	protected void method_9515(class_2689.class_2690<class_2248, class_2680> arg) {
		arg.method_11667(field_10927, field_12191);
	}

	@Override
	public boolean method_9526(class_2680 arg) {
		return (Boolean)arg.method_11654(field_12191);
	}

	@Override
	public boolean method_9516(class_2680 arg, class_1922 arg2, class_2338 arg3, class_10 arg4) {
		return false;
	}
}
