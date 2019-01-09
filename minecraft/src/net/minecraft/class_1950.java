package net.minecraft;

import javax.annotation.Nullable;

public class class_1950 implements class_1920 {
	protected final int field_9304;
	protected final int field_9303;
	protected final class_2818[][] field_9305;
	protected boolean field_9302;
	protected final class_1937 field_9306;

	public class_1950(class_1937 arg, class_2338 arg2, class_2338 arg3, int i) {
		this.field_9306 = arg;
		this.field_9304 = arg2.method_10263() - i >> 4;
		this.field_9303 = arg2.method_10260() - i >> 4;
		int j = arg3.method_10263() + i >> 4;
		int k = arg3.method_10260() + i >> 4;
		this.field_9305 = new class_2818[j - this.field_9304 + 1][k - this.field_9303 + 1];
		this.field_9302 = true;

		for (int l = this.field_9304; l <= j; l++) {
			for (int m = this.field_9303; m <= k; m++) {
				this.field_9305[l - this.field_9304][m - this.field_9303] = arg.method_8497(l, m);
			}
		}

		for (int l = arg2.method_10263() >> 4; l <= arg3.method_10263() >> 4; l++) {
			for (int m = arg2.method_10260() >> 4; m <= arg3.method_10260() >> 4; m++) {
				class_2818 lv = this.field_9305[l - this.field_9304][m - this.field_9303];
				if (lv != null && !lv.method_12228(arg2.method_10264(), arg3.method_10264())) {
					this.field_9302 = false;
				}
			}
		}
	}

	@Nullable
	@Override
	public class_2586 method_8321(class_2338 arg) {
		return this.method_8673(arg, class_2818.class_2819.field_12860);
	}

	@Nullable
	public class_2586 method_8673(class_2338 arg, class_2818.class_2819 arg2) {
		int i = (arg.method_10263() >> 4) - this.field_9304;
		int j = (arg.method_10260() >> 4) - this.field_9303;
		return this.field_9305[i][j].method_12201(arg, arg2);
	}

	@Override
	public class_2680 method_8320(class_2338 arg) {
		if (!class_1937.method_8518(arg)) {
			int i = (arg.method_10263() >> 4) - this.field_9304;
			int j = (arg.method_10260() >> 4) - this.field_9303;
			if (i >= 0 && i < this.field_9305.length && j >= 0 && j < this.field_9305[i].length) {
				class_2818 lv = this.field_9305[i][j];
				if (lv != null) {
					return lv.method_8320(arg);
				}
			}
		}

		return class_2246.field_10124.method_9564();
	}

	@Override
	public class_3610 method_8316(class_2338 arg) {
		if (arg.method_10264() >= 0 && arg.method_10264() < 256) {
			int i = (arg.method_10263() >> 4) - this.field_9304;
			int j = (arg.method_10260() >> 4) - this.field_9303;
			if (i >= 0 && i < this.field_9305.length && j >= 0 && j < this.field_9305[i].length) {
				class_2818 lv = this.field_9305[i][j];
				if (lv != null) {
					return lv.method_8316(arg);
				}
			}
		}

		return class_3612.field_15906.method_15785();
	}

	@Override
	public class_1959 method_8310(class_2338 arg) {
		int i = (arg.method_10263() >> 4) - this.field_9304;
		int j = (arg.method_10260() >> 4) - this.field_9303;
		return this.field_9305[i][j].method_16552(arg);
	}

	@Override
	public int method_8314(class_1944 arg, class_2338 arg2) {
		return this.field_9306.method_8314(arg, arg2);
	}
}
