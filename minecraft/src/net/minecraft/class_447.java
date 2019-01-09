package net.minecraft;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.mojang.blaze3d.platform.GlStateManager;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class class_447 extends class_437 implements class_452 {
	protected final class_437 field_2648;
	protected String field_2649 = "Select world";
	private class_447.class_448 field_2644;
	private class_447.class_449 field_2642;
	private class_447.class_451 field_2646;
	private final class_3469 field_2647;
	private class_358 field_2643;
	private boolean field_2645 = true;

	public class_447(class_437 arg, class_3469 arg2) {
		this.field_2648 = arg;
		this.field_2647 = arg2;
	}

	@Override
	public class_364 getFocused() {
		return this.field_2643;
	}

	@Override
	protected void method_2224() {
		this.field_2649 = class_1074.method_4662("gui.stats");
		this.field_2645 = true;
		this.field_2563.method_1562().method_2883(new class_2799(class_2799.class_2800.field_12775));
	}

	public void method_2270() {
		this.field_2644 = new class_447.class_448(this.field_2563);
		this.field_2642 = new class_447.class_449(this.field_2563);
		this.field_2646 = new class_447.class_451(this.field_2563);
	}

	public void method_2267() {
		this.method_2219(new class_339(0, this.field_2561 / 2 - 100, this.field_2559 - 28, class_1074.method_4662("gui.done")) {
			@Override
			public void method_1826(double d, double e) {
				class_447.this.field_2563.method_1507(class_447.this.field_2648);
			}
		});
		this.method_2219(new class_339(1, this.field_2561 / 2 - 120, this.field_2559 - 52, 80, 20, class_1074.method_4662("stat.generalButton")) {
			@Override
			public void method_1826(double d, double e) {
				class_447.this.field_2643 = class_447.this.field_2644;
			}
		});
		class_339 lv = this.method_2219(new class_339(3, this.field_2561 / 2 - 40, this.field_2559 - 52, 80, 20, class_1074.method_4662("stat.itemsButton")) {
			@Override
			public void method_1826(double d, double e) {
				class_447.this.field_2643 = class_447.this.field_2642;
			}
		});
		class_339 lv2 = this.method_2219(new class_339(4, this.field_2561 / 2 + 40, this.field_2559 - 52, 80, 20, class_1074.method_4662("stat.mobsButton")) {
			@Override
			public void method_1826(double d, double e) {
				class_447.this.field_2643 = class_447.this.field_2646;
			}
		});
		if (this.field_2642.method_1947() == 0) {
			lv.field_2078 = false;
		}

		if (this.field_2646.method_1947() == 0) {
			lv2.field_2078 = false;
		}

		this.field_2557.add((class_363)() -> this.field_2643);
	}

	@Override
	public void method_2214(int i, int j, float f) {
		if (this.field_2645) {
			this.method_2240();
			this.method_1789(this.field_2554, class_1074.method_4662("multiplayer.downloadingStats"), this.field_2561 / 2, this.field_2559 / 2, 16777215);
			this.method_1789(
				this.field_2554, field_2668[(int)(class_156.method_658() / 150L % (long)field_2668.length)], this.field_2561 / 2, this.field_2559 / 2 + 9 * 2, 16777215
			);
		} else {
			this.field_2643.method_1930(i, j, f);
			this.method_1789(this.field_2554, this.field_2649, this.field_2561 / 2, 20, 16777215);
			super.method_2214(i, j, f);
		}
	}

	@Override
	public void method_2300() {
		if (this.field_2645) {
			this.method_2270();
			this.method_2267();
			this.field_2643 = this.field_2644;
			this.field_2645 = false;
		}
	}

	@Override
	public boolean method_2222() {
		return !this.field_2645;
	}

	private int method_2285(int i) {
		return 115 + 40 * i;
	}

	private void method_2289(int i, int j, class_1792 arg) {
		this.method_2272(i + 1, j + 1);
		GlStateManager.enableRescaleNormal();
		class_308.method_1453();
		this.field_2560.method_4010(arg.method_7854(), i + 2, j + 2);
		class_308.method_1450();
		GlStateManager.disableRescaleNormal();
	}

	private void method_2272(int i, int j) {
		this.method_2282(i, j, 0, 0);
	}

	private void method_2282(int i, int j, int k, int l) {
		GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
		this.field_2563.method_1531().method_4618(field_2052);
		float f = 0.0078125F;
		float g = 0.0078125F;
		int m = 18;
		int n = 18;
		class_289 lv = class_289.method_1348();
		class_287 lv2 = lv.method_1349();
		lv2.method_1328(7, class_290.field_1585);
		lv2.method_1315((double)(i + 0), (double)(j + 18), (double)this.field_2050)
			.method_1312((double)((float)(k + 0) * 0.0078125F), (double)((float)(l + 18) * 0.0078125F))
			.method_1344();
		lv2.method_1315((double)(i + 18), (double)(j + 18), (double)this.field_2050)
			.method_1312((double)((float)(k + 18) * 0.0078125F), (double)((float)(l + 18) * 0.0078125F))
			.method_1344();
		lv2.method_1315((double)(i + 18), (double)(j + 0), (double)this.field_2050)
			.method_1312((double)((float)(k + 18) * 0.0078125F), (double)((float)(l + 0) * 0.0078125F))
			.method_1344();
		lv2.method_1315((double)(i + 0), (double)(j + 0), (double)this.field_2050)
			.method_1312((double)((float)(k + 0) * 0.0078125F), (double)((float)(l + 0) * 0.0078125F))
			.method_1344();
		lv.method_1350();
	}

	@Environment(EnvType.CLIENT)
	class class_448 extends class_358 {
		private Iterator<class_3445<class_2960>> field_2650;

		public class_448(class_310 arg2) {
			super(arg2, class_447.this.field_2561, class_447.this.field_2559, 32, class_447.this.field_2559 - 64, 10);
			this.method_1943(false);
		}

		@Override
		protected int method_1947() {
			return class_3468.field_15419.method_14960();
		}

		@Override
		protected boolean method_1955(int i) {
			return false;
		}

		@Override
		protected int method_1928() {
			return this.method_1947() * 10;
		}

		@Override
		protected void method_1936() {
			class_447.this.method_2240();
		}

		@Override
		protected void method_1935(int i, int j, int k, int l, int m, int n, float f) {
			if (i == 0) {
				this.field_2650 = class_3468.field_15419.iterator();
			}

			class_3445<class_2960> lv = (class_3445<class_2960>)this.field_2650.next();
			class_2561 lv2 = new class_2588("stat." + lv.method_14951().toString().replace(':', '.')).method_10854(class_124.field_1080);
			this.method_1780(class_447.this.field_2554, lv2.getString(), j + 2, k + 1, i % 2 == 0 ? 16777215 : 9474192);
			String string = lv.method_14953(class_447.this.field_2647.method_15025(lv));
			this.method_1780(class_447.this.field_2554, string, j + 2 + 213 - class_447.this.field_2554.method_1727(string), k + 1, i % 2 == 0 ? 16777215 : 9474192);
		}
	}

	@Environment(EnvType.CLIENT)
	class class_449 extends class_358 {
		protected final List<class_3448<class_2248>> field_2656;
		protected final List<class_3448<class_1792>> field_2655;
		private final int[] field_2654 = new int[]{3, 4, 1, 2, 5, 6};
		protected int field_2653 = -1;
		protected final List<class_1792> field_2658;
		protected final Comparator<class_1792> field_2661 = new class_447.class_449.class_450();
		@Nullable
		protected class_3448<?> field_2659;
		protected int field_2657;

		public class_449(class_310 arg2) {
			super(arg2, class_447.this.field_2561, class_447.this.field_2559, 32, class_447.this.field_2559 - 64, 20);
			this.field_2656 = Lists.<class_3448<class_2248>>newArrayList();
			this.field_2656.add(class_3468.field_15427);
			this.field_2655 = Lists.<class_3448<class_1792>>newArrayList(
				class_3468.field_15383, class_3468.field_15370, class_3468.field_15372, class_3468.field_15392, class_3468.field_15405
			);
			this.method_1943(false);
			this.method_1927(true, 20);
			Set<class_1792> set = Sets.newIdentityHashSet();

			for (class_1792 lv : class_2378.field_11142) {
				boolean bl = false;

				for (class_3448<class_1792> lv2 : this.field_2655) {
					if (lv2.method_14958(lv) && class_447.this.field_2647.method_15025(lv2.method_14956(lv)) > 0) {
						bl = true;
					}
				}

				if (bl) {
					set.add(lv);
				}
			}

			for (class_2248 lv3 : class_2378.field_11146) {
				boolean bl = false;

				for (class_3448<class_2248> lv2x : this.field_2656) {
					if (lv2x.method_14958(lv3) && class_447.this.field_2647.method_15025(lv2x.method_14956(lv3)) > 0) {
						bl = true;
					}
				}

				if (bl) {
					set.add(lv3.method_8389());
				}
			}

			set.remove(class_1802.field_8162);
			this.field_2658 = Lists.<class_1792>newArrayList(set);
		}

		@Override
		protected void method_1940(int i, int j, class_289 arg) {
			if (!this.field_2164.field_1729.method_1608()) {
				this.field_2653 = -1;
			}

			for (int k = 0; k < this.field_2654.length; k++) {
				class_447.this.method_2282(i + class_447.this.method_2285(k) - 18, j + 1, 0, this.field_2653 == k ? 0 : 18);
			}

			if (this.field_2659 != null) {
				int k = class_447.this.method_2285(this.method_2294(this.field_2659)) - 36;
				int l = this.field_2657 == 1 ? 2 : 1;
				class_447.this.method_2282(i + k, j + 1, 18 * l, 0);
			}

			for (int k = 0; k < this.field_2654.length; k++) {
				int l = this.field_2653 == k ? 1 : 0;
				class_447.this.method_2282(i + class_447.this.method_2285(k) - 18 + l, j + 1 + l, 18 * this.field_2654[k], 18);
			}
		}

		@Override
		protected void method_1935(int i, int j, int k, int l, int m, int n, float f) {
			class_1792 lv = this.method_2296(i);
			class_447.this.method_2289(j + 40, k, lv);

			for (int o = 0; o < this.field_2656.size(); o++) {
				class_3445<class_2248> lv2;
				if (lv instanceof class_1747) {
					lv2 = ((class_3448)this.field_2656.get(o)).method_14956(((class_1747)lv).method_7711());
				} else {
					lv2 = null;
				}

				this.method_2292(lv2, j + class_447.this.method_2285(o), k, i % 2 == 0);
			}

			for (int o = 0; o < this.field_2655.size(); o++) {
				this.method_2292(((class_3448)this.field_2655.get(o)).method_14956(lv), j + class_447.this.method_2285(o + this.field_2656.size()), k, i % 2 == 0);
			}
		}

		@Override
		protected boolean method_1955(int i) {
			return false;
		}

		@Override
		public int method_1932() {
			return 375;
		}

		@Override
		protected int method_1948() {
			return this.field_2168 / 2 + 140;
		}

		@Override
		protected void method_1936() {
			class_447.this.method_2240();
		}

		@Override
		protected void method_1929(int i, int j) {
			this.field_2653 = -1;

			for (int k = 0; k < this.field_2654.length; k++) {
				int l = i - class_447.this.method_2285(k);
				if (l >= -36 && l <= 0) {
					this.field_2653 = k;
					break;
				}
			}

			if (this.field_2653 >= 0) {
				this.method_2295(this.method_2290(this.field_2653));
				this.field_2164.method_1483().method_4873(class_1109.method_4758(class_3417.field_15015, 1.0F));
			}
		}

		private class_3448<?> method_2290(int i) {
			return i < this.field_2656.size() ? (class_3448)this.field_2656.get(i) : (class_3448)this.field_2655.get(i - this.field_2656.size());
		}

		private int method_2294(class_3448<?> arg) {
			int i = this.field_2656.indexOf(arg);
			if (i >= 0) {
				return i;
			} else {
				int j = this.field_2655.indexOf(arg);
				return j >= 0 ? j + this.field_2656.size() : -1;
			}
		}

		@Override
		protected final int method_1947() {
			return this.field_2658.size();
		}

		protected final class_1792 method_2296(int i) {
			return (class_1792)this.field_2658.get(i);
		}

		protected void method_2292(@Nullable class_3445<?> arg, int i, int j, boolean bl) {
			String string = arg == null ? "-" : arg.method_14953(class_447.this.field_2647.method_15025(arg));
			this.method_1780(class_447.this.field_2554, string, i - class_447.this.field_2554.method_1727(string), j + 5, bl ? 16777215 : 9474192);
		}

		@Override
		protected void method_1942(int i, int j) {
			if (j >= this.field_2166 && j <= this.field_2165) {
				int k = this.method_1956((double)i, (double)j);
				int l = (this.field_2168 - this.method_1932()) / 2;
				if (k >= 0) {
					if (i < l + 40 || i > l + 40 + 20) {
						return;
					}

					class_1792 lv = this.method_2296(k);
					this.method_2293(this.method_2291(lv), i, j);
				} else {
					class_2561 lv2 = null;
					int m = i - l;

					for (int n = 0; n < this.field_2654.length; n++) {
						int o = class_447.this.method_2285(n);
						if (m >= o - 18 && m <= o) {
							lv2 = new class_2588(this.method_2290(n).method_14957());
							break;
						}
					}

					this.method_2293(lv2, i, j);
				}
			}
		}

		protected void method_2293(@Nullable class_2561 arg, int i, int j) {
			if (arg != null) {
				String string = arg.method_10863();
				int k = i + 12;
				int l = j - 12;
				int m = class_447.this.field_2554.method_1727(string);
				this.method_1782(k - 3, l - 3, k + m + 3, l + 8 + 3, -1073741824, -1073741824);
				class_447.this.field_2554.method_1720(string, (float)k, (float)l, -1);
			}
		}

		protected class_2561 method_2291(class_1792 arg) {
			return arg.method_7848();
		}

		protected void method_2295(class_3448<?> arg) {
			if (arg != this.field_2659) {
				this.field_2659 = arg;
				this.field_2657 = -1;
			} else if (this.field_2657 == -1) {
				this.field_2657 = 1;
			} else {
				this.field_2659 = null;
				this.field_2657 = 0;
			}

			this.field_2658.sort(this.field_2661);
		}

		@Environment(EnvType.CLIENT)
		class class_450 implements Comparator<class_1792> {
			private class_450() {
			}

			public int method_2297(class_1792 arg, class_1792 arg2) {
				int i;
				int j;
				if (class_449.this.field_2659 == null) {
					i = 0;
					j = 0;
				} else if (class_449.this.field_2656.contains(class_449.this.field_2659)) {
					class_3448<class_2248> lv = (class_3448<class_2248>)class_449.this.field_2659;
					i = arg instanceof class_1747 ? class_447.this.field_2647.method_15024(lv, ((class_1747)arg).method_7711()) : -1;
					j = arg2 instanceof class_1747 ? class_447.this.field_2647.method_15024(lv, ((class_1747)arg2).method_7711()) : -1;
				} else {
					class_3448<class_1792> lv = (class_3448<class_1792>)class_449.this.field_2659;
					i = class_447.this.field_2647.method_15024(lv, arg);
					j = class_447.this.field_2647.method_15024(lv, arg2);
				}

				return i == j
					? class_449.this.field_2657 * Integer.compare(class_1792.method_7880(arg), class_1792.method_7880(arg2))
					: class_449.this.field_2657 * Integer.compare(i, j);
			}
		}
	}

	@Environment(EnvType.CLIENT)
	class class_451 extends class_358 {
		private final List<class_1299<?>> field_2664 = Lists.<class_1299<?>>newArrayList();

		public class_451(class_310 arg2) {
			super(arg2, class_447.this.field_2561, class_447.this.field_2559, 32, class_447.this.field_2559 - 64, 9 * 4);
			this.method_1943(false);

			for (class_1299<?> lv : class_2378.field_11145) {
				if (class_447.this.field_2647.method_15025(class_3468.field_15403.method_14956(lv)) > 0
					|| class_447.this.field_2647.method_15025(class_3468.field_15411.method_14956(lv)) > 0) {
					this.field_2664.add(lv);
				}
			}
		}

		@Override
		protected int method_1947() {
			return this.field_2664.size();
		}

		@Override
		protected boolean method_1955(int i) {
			return false;
		}

		@Override
		protected int method_1928() {
			return this.method_1947() * 9 * 4;
		}

		@Override
		protected void method_1936() {
			class_447.this.method_2240();
		}

		@Override
		protected void method_1935(int i, int j, int k, int l, int m, int n, float f) {
			class_1299<?> lv = (class_1299<?>)this.field_2664.get(i);
			String string = class_1074.method_4662(class_156.method_646("entity", class_1299.method_5890(lv)));
			int o = class_447.this.field_2647.method_15025(class_3468.field_15403.method_14956(lv));
			int p = class_447.this.field_2647.method_15025(class_3468.field_15411.method_14956(lv));
			this.method_1780(class_447.this.field_2554, string, j + 2 - 10, k + 1, 16777215);
			this.method_1780(class_447.this.field_2554, this.method_2299(string, o), j + 2, k + 1 + 9, o == 0 ? 6316128 : 9474192);
			this.method_1780(class_447.this.field_2554, this.method_2298(string, p), j + 2, k + 1 + 9 * 2, p == 0 ? 6316128 : 9474192);
		}

		private String method_2299(String string, int i) {
			String string2 = class_3468.field_15403.method_14957();
			return i == 0 ? class_1074.method_4662(string2 + ".none", string) : class_1074.method_4662(string2, i, string);
		}

		private String method_2298(String string, int i) {
			String string2 = class_3468.field_15411.method_14957();
			return i == 0 ? class_1074.method_4662(string2 + ".none", string) : class_1074.method_4662(string2, string, i);
		}
	}
}
