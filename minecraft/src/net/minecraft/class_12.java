package net.minecraft;

import javax.annotation.Nullable;

public class class_12 extends class_8 {
	private final boolean field_58;

	public class_12(boolean bl) {
		this.field_58 = bl;
	}

	@Override
	public class_9 method_21() {
		return super.method_13(
			class_3532.method_15357(this.field_33.method_5829().field_1323),
			class_3532.method_15357(this.field_33.method_5829().field_1322 + 0.5),
			class_3532.method_15357(this.field_33.method_5829().field_1321)
		);
	}

	@Override
	public class_9 method_16(double d, double e, double f) {
		return super.method_13(
			class_3532.method_15357(d - (double)(this.field_33.method_17681() / 2.0F)),
			class_3532.method_15357(e + 0.5),
			class_3532.method_15357(f - (double)(this.field_33.method_17681() / 2.0F))
		);
	}

	@Override
	public int method_18(class_9[] args, class_9 arg) {
		int i = 0;

		for (class_2350 lv : class_2350.values()) {
			class_9 lv2 = this.method_51(arg.field_40 + lv.method_10148(), arg.field_39 + lv.method_10164(), arg.field_38 + lv.method_10165());
			if (lv2 != null && !lv2.field_42) {
				args[i++] = lv2;
			}
		}

		return i;
	}

	@Override
	public class_7 method_17(class_1922 arg, int i, int j, int k, class_1308 arg2, int l, int m, int n, boolean bl, boolean bl2) {
		return this.method_25(arg, i, j, k);
	}

	@Override
	public class_7 method_25(class_1922 arg, int i, int j, int k) {
		class_2338 lv = new class_2338(i, j, k);
		class_3610 lv2 = arg.method_8316(lv);
		class_2680 lv3 = arg.method_8320(lv);
		if (lv2.method_15769() && lv3.method_11609(arg, lv.method_10074(), class_10.field_48) && lv3.method_11588()) {
			return class_7.field_16;
		} else {
			return lv2.method_15767(class_3486.field_15517) && lv3.method_11609(arg, lv, class_10.field_48) ? class_7.field_18 : class_7.field_22;
		}
	}

	@Nullable
	private class_9 method_51(int i, int j, int k) {
		class_7 lv = this.method_50(i, j, k);
		return (!this.field_58 || lv != class_7.field_16) && lv != class_7.field_18 ? null : this.method_13(i, j, k);
	}

	@Nullable
	@Override
	protected class_9 method_13(int i, int j, int k) {
		class_9 lv = null;
		class_7 lv2 = this.method_25(this.field_33.field_6002, i, j, k);
		float f = this.field_33.method_5944(lv2);
		if (f >= 0.0F) {
			lv = super.method_13(i, j, k);
			lv.field_41 = lv2;
			lv.field_43 = Math.max(lv.field_43, f);
			if (this.field_26.method_8316(new class_2338(i, j, k)).method_15769()) {
				lv.field_43 += 8.0F;
			}
		}

		return lv2 == class_7.field_7 ? lv : lv;
	}

	private class_7 method_50(int i, int j, int k) {
		class_2338.class_2339 lv = new class_2338.class_2339();

		for (int l = i; l < i + this.field_31; l++) {
			for (int m = j; m < j + this.field_30; m++) {
				for (int n = k; n < k + this.field_28; n++) {
					class_3610 lv2 = this.field_26.method_8316(lv.method_10103(l, m, n));
					class_2680 lv3 = this.field_26.method_8320(lv.method_10103(l, m, n));
					if (lv2.method_15769() && lv3.method_11609(this.field_26, lv.method_10074(), class_10.field_48) && lv3.method_11588()) {
						return class_7.field_16;
					}

					if (!lv2.method_15767(class_3486.field_15517)) {
						return class_7.field_22;
					}
				}
			}
		}

		class_2680 lv4 = this.field_26.method_8320(lv);
		return lv4.method_11609(this.field_26, lv, class_10.field_48) ? class_7.field_18 : class_7.field_22;
	}
}
