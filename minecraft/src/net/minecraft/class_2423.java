package net.minecraft;

import com.google.common.cache.LoadingCache;
import java.util.Random;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

public class class_2423 extends class_2248 {
	public static final class_2754<class_2350.class_2351> field_11310 = class_2741.field_12529;
	protected static final class_265 field_11309 = class_2248.method_9541(0.0, 0.0, 6.0, 16.0, 16.0, 10.0);
	protected static final class_265 field_11308 = class_2248.method_9541(6.0, 0.0, 0.0, 10.0, 16.0, 16.0);

	public class_2423(class_2248.class_2251 arg) {
		super(arg);
		this.method_9590(this.field_10647.method_11664().method_11657(field_11310, class_2350.class_2351.field_11048));
	}

	@Override
	public class_265 method_9530(class_2680 arg, class_1922 arg2, class_2338 arg3, class_3726 arg4) {
		switch ((class_2350.class_2351)arg.method_11654(field_11310)) {
			case field_11051:
				return field_11308;
			case field_11048:
			default:
				return field_11309;
		}
	}

	@Override
	public void method_9588(class_2680 arg, class_1937 arg2, class_2338 arg3, Random random) {
		if (arg2.field_9247.method_12462() && arg2.method_8450().method_8355("doMobSpawning") && random.nextInt(2000) < arg2.method_8407().method_5461()) {
			while (arg2.method_8320(arg3).method_11614() == this) {
				arg3 = arg3.method_10074();
			}

			if (arg2.method_8320(arg3).method_11611(arg2, arg3, class_1299.field_6050)) {
				class_1297 lv = class_1299.field_6050.method_5899(arg2, null, null, null, arg3.method_10084(), class_3730.field_16474, false, false);
				if (lv != null) {
					lv.field_6018 = lv.method_5806();
				}
			}
		}
	}

	public boolean method_10352(class_1936 arg, class_2338 arg2) {
		class_2423.class_2424 lv = this.method_10351(arg, arg2);
		if (lv != null) {
			lv.method_10363();
			return true;
		} else {
			return false;
		}
	}

	@Nullable
	public class_2423.class_2424 method_10351(class_1936 arg, class_2338 arg2) {
		class_2423.class_2424 lv = new class_2423.class_2424(arg, arg2, class_2350.class_2351.field_11048);
		if (lv.method_10360() && lv.field_11313 == 0) {
			return lv;
		} else {
			class_2423.class_2424 lv2 = new class_2423.class_2424(arg, arg2, class_2350.class_2351.field_11051);
			return lv2.method_10360() && lv2.field_11313 == 0 ? lv2 : null;
		}
	}

	@Override
	public class_2680 method_9559(class_2680 arg, class_2350 arg2, class_2680 arg3, class_1936 arg4, class_2338 arg5, class_2338 arg6) {
		class_2350.class_2351 lv = arg2.method_10166();
		class_2350.class_2351 lv2 = arg.method_11654(field_11310);
		boolean bl = lv2 != lv && lv.method_10179();
		return !bl && arg3.method_11614() != this && !new class_2423.class_2424(arg4, arg5, lv2).method_10362()
			? class_2246.field_10124.method_9564()
			: super.method_9559(arg, arg2, arg3, arg4, arg5, arg6);
	}

	@Override
	public class_1921 method_9551() {
		return class_1921.field_9179;
	}

	@Override
	public void method_9548(class_2680 arg, class_1937 arg2, class_2338 arg3, class_1297 arg4) {
		if (!arg4.method_5765() && !arg4.method_5782() && arg4.method_5822()) {
			arg4.method_5717(arg3);
		}
	}

	@Environment(EnvType.CLIENT)
	@Override
	public void method_9496(class_2680 arg, class_1937 arg2, class_2338 arg3, Random random) {
		if (random.nextInt(100) == 0) {
			arg2.method_8486(
				(double)arg3.method_10263() + 0.5,
				(double)arg3.method_10264() + 0.5,
				(double)arg3.method_10260() + 0.5,
				class_3417.field_14802,
				class_3419.field_15245,
				0.5F,
				random.nextFloat() * 0.4F + 0.8F,
				false
			);
		}

		for (int i = 0; i < 4; i++) {
			double d = (double)((float)arg3.method_10263() + random.nextFloat());
			double e = (double)((float)arg3.method_10264() + random.nextFloat());
			double f = (double)((float)arg3.method_10260() + random.nextFloat());
			double g = ((double)random.nextFloat() - 0.5) * 0.5;
			double h = ((double)random.nextFloat() - 0.5) * 0.5;
			double j = ((double)random.nextFloat() - 0.5) * 0.5;
			int k = random.nextInt(2) * 2 - 1;
			if (arg2.method_8320(arg3.method_10067()).method_11614() != this && arg2.method_8320(arg3.method_10078()).method_11614() != this) {
				d = (double)arg3.method_10263() + 0.5 + 0.25 * (double)k;
				g = (double)(random.nextFloat() * 2.0F * (float)k);
			} else {
				f = (double)arg3.method_10260() + 0.5 + 0.25 * (double)k;
				j = (double)(random.nextFloat() * 2.0F * (float)k);
			}

			arg2.method_8406(class_2398.field_11214, d, e, f, g, h, j);
		}
	}

	@Environment(EnvType.CLIENT)
	@Override
	public class_1799 method_9574(class_1922 arg, class_2338 arg2, class_2680 arg3) {
		return class_1799.field_8037;
	}

	@Override
	public class_2680 method_9598(class_2680 arg, class_2470 arg2) {
		switch (arg2) {
			case field_11465:
			case field_11463:
				switch ((class_2350.class_2351)arg.method_11654(field_11310)) {
					case field_11051:
						return arg.method_11657(field_11310, class_2350.class_2351.field_11048);
					case field_11048:
						return arg.method_11657(field_11310, class_2350.class_2351.field_11051);
					default:
						return arg;
				}
			default:
				return arg;
		}
	}

	@Override
	protected void method_9515(class_2689.class_2690<class_2248, class_2680> arg) {
		arg.method_11667(field_11310);
	}

	public class_2700.class_2702 method_10350(class_1936 arg, class_2338 arg2) {
		class_2350.class_2351 lv = class_2350.class_2351.field_11051;
		class_2423.class_2424 lv2 = new class_2423.class_2424(arg, arg2, class_2350.class_2351.field_11048);
		LoadingCache<class_2338, class_2694> loadingCache = class_2700.method_11709(arg, true);
		if (!lv2.method_10360()) {
			lv = class_2350.class_2351.field_11048;
			lv2 = new class_2423.class_2424(arg, arg2, class_2350.class_2351.field_11051);
		}

		if (!lv2.method_10360()) {
			return new class_2700.class_2702(arg2, class_2350.field_11043, class_2350.field_11036, loadingCache, 1, 1, 1);
		} else {
			int[] is = new int[class_2350.class_2352.values().length];
			class_2350 lv3 = lv2.field_11314.method_10160();
			class_2338 lv4 = lv2.field_11316.method_10086(lv2.method_10355() - 1);

			for (class_2350.class_2352 lv5 : class_2350.class_2352.values()) {
				class_2700.class_2702 lv6 = new class_2700.class_2702(
					lv3.method_10171() == lv5 ? lv4 : lv4.method_10079(lv2.field_11314, lv2.method_10356() - 1),
					class_2350.method_10156(lv5, lv),
					class_2350.field_11036,
					loadingCache,
					lv2.method_10356(),
					lv2.method_10355(),
					1
				);

				for (int i = 0; i < lv2.method_10356(); i++) {
					for (int j = 0; j < lv2.method_10355(); j++) {
						class_2694 lv7 = lv6.method_11717(i, j, 1);
						if (!lv7.method_11681().method_11588()) {
							is[lv5.ordinal()]++;
						}
					}
				}
			}

			class_2350.class_2352 lv8 = class_2350.class_2352.field_11056;

			for (class_2350.class_2352 lv9 : class_2350.class_2352.values()) {
				if (is[lv9.ordinal()] < is[lv8.ordinal()]) {
					lv8 = lv9;
				}
			}

			return new class_2700.class_2702(
				lv3.method_10171() == lv8 ? lv4 : lv4.method_10079(lv2.field_11314, lv2.method_10356() - 1),
				class_2350.method_10156(lv8, lv),
				class_2350.field_11036,
				loadingCache,
				lv2.method_10356(),
				lv2.method_10355(),
				1
			);
		}
	}

	public static class class_2424 {
		private final class_1936 field_11318;
		private final class_2350.class_2351 field_11317;
		private final class_2350 field_11314;
		private final class_2350 field_11315;
		private int field_11313;
		@Nullable
		private class_2338 field_11316;
		private int field_11312;
		private int field_11311;

		public class_2424(class_1936 arg, class_2338 arg2, class_2350.class_2351 arg3) {
			this.field_11318 = arg;
			this.field_11317 = arg3;
			if (arg3 == class_2350.class_2351.field_11048) {
				this.field_11315 = class_2350.field_11034;
				this.field_11314 = class_2350.field_11039;
			} else {
				this.field_11315 = class_2350.field_11043;
				this.field_11314 = class_2350.field_11035;
			}

			class_2338 lv = arg2;

			while (arg2.method_10264() > lv.method_10264() - 21 && arg2.method_10264() > 0 && this.method_10359(arg.method_8320(arg2.method_10074()))) {
				arg2 = arg2.method_10074();
			}

			int i = this.method_10354(arg2, this.field_11315) - 1;
			if (i >= 0) {
				this.field_11316 = arg2.method_10079(this.field_11315, i);
				this.field_11311 = this.method_10354(this.field_11316, this.field_11314);
				if (this.field_11311 < 2 || this.field_11311 > 21) {
					this.field_11316 = null;
					this.field_11311 = 0;
				}
			}

			if (this.field_11316 != null) {
				this.field_11312 = this.method_10353();
			}
		}

		protected int method_10354(class_2338 arg, class_2350 arg2) {
			int i;
			for (i = 0; i < 22; i++) {
				class_2338 lv = arg.method_10079(arg2, i);
				if (!this.method_10359(this.field_11318.method_8320(lv)) || this.field_11318.method_8320(lv.method_10074()).method_11614() != class_2246.field_10540) {
					break;
				}
			}

			class_2248 lv2 = this.field_11318.method_8320(arg.method_10079(arg2, i)).method_11614();
			return lv2 == class_2246.field_10540 ? i : 0;
		}

		public int method_10355() {
			return this.field_11312;
		}

		public int method_10356() {
			return this.field_11311;
		}

		protected int method_10353() {
			label56:
			for (this.field_11312 = 0; this.field_11312 < 21; this.field_11312++) {
				for (int i = 0; i < this.field_11311; i++) {
					class_2338 lv = this.field_11316.method_10079(this.field_11314, i).method_10086(this.field_11312);
					class_2680 lv2 = this.field_11318.method_8320(lv);
					if (!this.method_10359(lv2)) {
						break label56;
					}

					class_2248 lv3 = lv2.method_11614();
					if (lv3 == class_2246.field_10316) {
						this.field_11313++;
					}

					if (i == 0) {
						lv3 = this.field_11318.method_8320(lv.method_10093(this.field_11315)).method_11614();
						if (lv3 != class_2246.field_10540) {
							break label56;
						}
					} else if (i == this.field_11311 - 1) {
						lv3 = this.field_11318.method_8320(lv.method_10093(this.field_11314)).method_11614();
						if (lv3 != class_2246.field_10540) {
							break label56;
						}
					}
				}
			}

			for (int i = 0; i < this.field_11311; i++) {
				if (this.field_11318.method_8320(this.field_11316.method_10079(this.field_11314, i).method_10086(this.field_11312)).method_11614()
					!= class_2246.field_10540) {
					this.field_11312 = 0;
					break;
				}
			}

			if (this.field_11312 <= 21 && this.field_11312 >= 3) {
				return this.field_11312;
			} else {
				this.field_11316 = null;
				this.field_11311 = 0;
				this.field_11312 = 0;
				return 0;
			}
		}

		protected boolean method_10359(class_2680 arg) {
			class_2248 lv = arg.method_11614();
			return arg.method_11588() || lv == class_2246.field_10036 || lv == class_2246.field_10316;
		}

		public boolean method_10360() {
			return this.field_11316 != null && this.field_11311 >= 2 && this.field_11311 <= 21 && this.field_11312 >= 3 && this.field_11312 <= 21;
		}

		public void method_10363() {
			for (int i = 0; i < this.field_11311; i++) {
				class_2338 lv = this.field_11316.method_10079(this.field_11314, i);

				for (int j = 0; j < this.field_11312; j++) {
					this.field_11318.method_8652(lv.method_10086(j), class_2246.field_10316.method_9564().method_11657(class_2423.field_11310, this.field_11317), 18);
				}
			}
		}

		private boolean method_10361() {
			return this.field_11313 >= this.field_11311 * this.field_11312;
		}

		public boolean method_10362() {
			return this.method_10360() && this.method_10361();
		}
	}
}
