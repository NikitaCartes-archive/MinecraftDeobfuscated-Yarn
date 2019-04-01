package net.minecraft;

import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class class_853 implements class_1920 {
	protected final int field_4488;
	protected final int field_4487;
	protected final class_2338 field_4481;
	protected final int field_4486;
	protected final int field_4484;
	protected final int field_4482;
	protected final class_2818[][] field_4483;
	protected final class_2680[] field_4489;
	protected final class_3610[] field_4485;
	protected final class_1937 field_4490;

	@Nullable
	public static class_853 method_3689(class_1937 arg, class_2338 arg2, class_2338 arg3, int i) {
		int j = arg2.method_10263() - i >> 4;
		int k = arg2.method_10260() - i >> 4;
		int l = arg3.method_10263() + i >> 4;
		int m = arg3.method_10260() + i >> 4;
		class_2818[][] lvs = new class_2818[l - j + 1][m - k + 1];

		for (int n = j; n <= l; n++) {
			for (int o = k; o <= m; o++) {
				lvs[n - j][o - k] = arg.method_8497(n, o);
			}
		}

		boolean bl = true;

		for (int o = arg2.method_10263() >> 4; o <= arg3.method_10263() >> 4; o++) {
			for (int p = arg2.method_10260() >> 4; p <= arg3.method_10260() >> 4; p++) {
				class_2818 lv = lvs[o - j][p - k];
				if (!lv.method_12228(arg2.method_10264(), arg3.method_10264())) {
					bl = false;
				}
			}
		}

		if (bl) {
			return null;
		} else {
			int o = 1;
			class_2338 lv2 = arg2.method_10069(-1, -1, -1);
			class_2338 lv3 = arg3.method_10069(1, 1, 1);
			return new class_853(arg, j, k, lvs, lv2, lv3);
		}
	}

	public class_853(class_1937 arg, int i, int j, class_2818[][] args, class_2338 arg2, class_2338 arg3) {
		this.field_4490 = arg;
		this.field_4488 = i;
		this.field_4487 = j;
		this.field_4483 = args;
		this.field_4481 = arg2;
		this.field_4486 = arg3.method_10263() - arg2.method_10263() + 1;
		this.field_4484 = arg3.method_10264() - arg2.method_10264() + 1;
		this.field_4482 = arg3.method_10260() - arg2.method_10260() + 1;
		this.field_4489 = new class_2680[this.field_4486 * this.field_4484 * this.field_4482];
		this.field_4485 = new class_3610[this.field_4486 * this.field_4484 * this.field_4482];

		for (class_2338 lv : class_2338.method_10097(arg2, arg3)) {
			int k = (lv.method_10263() >> 4) - i;
			int l = (lv.method_10260() >> 4) - j;
			class_2818 lv2 = args[k][l];
			int m = this.method_3691(lv);
			this.field_4489[m] = lv2.method_8320(lv);
			this.field_4485[m] = lv2.method_8316(lv);
		}
	}

	protected final int method_3691(class_2338 arg) {
		return this.method_3690(arg.method_10263(), arg.method_10264(), arg.method_10260());
	}

	protected int method_3690(int i, int j, int k) {
		int l = i - this.field_4481.method_10263();
		int m = j - this.field_4481.method_10264();
		int n = k - this.field_4481.method_10260();
		return n * this.field_4486 * this.field_4484 + m * this.field_4486 + l;
	}

	@Override
	public class_2680 method_8320(class_2338 arg) {
		return this.field_4489[this.method_3691(arg)];
	}

	@Override
	public class_3610 method_8316(class_2338 arg) {
		return this.field_4485[this.method_3691(arg)];
	}

	@Override
	public int method_8314(class_1944 arg, class_2338 arg2) {
		return this.field_4490.method_8314(arg, arg2);
	}

	@Override
	public class_1959 method_8310(class_2338 arg) {
		int i = (arg.method_10263() >> 4) - this.field_4488;
		int j = (arg.method_10260() >> 4) - this.field_4487;
		return this.field_4483[i][j].method_16552(arg);
	}

	@Nullable
	@Override
	public class_2586 method_8321(class_2338 arg) {
		return this.method_3688(arg, class_2818.class_2819.field_12860);
	}

	@Nullable
	public class_2586 method_3688(class_2338 arg, class_2818.class_2819 arg2) {
		int i = (arg.method_10263() >> 4) - this.field_4488;
		int j = (arg.method_10260() >> 4) - this.field_4487;
		return this.field_4483[i][j].method_12201(arg, arg2);
	}
}
