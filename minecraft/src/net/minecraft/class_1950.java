package net.minecraft;

import javax.annotation.Nullable;

public class class_1950 implements class_1941 {
	protected final int field_9304;
	protected final int field_9303;
	protected final class_2791[][] field_9305;
	protected boolean field_9302;
	protected final class_1937 field_9306;

	public class_1950(class_1937 arg, class_2338 arg2, class_2338 arg3) {
		this.field_9306 = arg;
		this.field_9304 = arg2.method_10263() >> 4;
		this.field_9303 = arg2.method_10260() >> 4;
		int i = arg3.method_10263() >> 4;
		int j = arg3.method_10260() >> 4;
		this.field_9305 = new class_2791[i - this.field_9304 + 1][j - this.field_9303 + 1];
		this.field_9302 = true;

		for (int k = this.field_9304; k <= i; k++) {
			for (int l = this.field_9303; l <= j; l++) {
				this.field_9305[k - this.field_9304][l - this.field_9303] = arg.method_8402(k, l, class_2806.field_12803, false);
			}
		}

		for (int k = arg2.method_10263() >> 4; k <= arg3.method_10263() >> 4; k++) {
			for (int l = arg2.method_10260() >> 4; l <= arg3.method_10260() >> 4; l++) {
				class_2791 lv = this.field_9305[k - this.field_9304][l - this.field_9303];
				if (lv != null && !lv.method_12228(arg2.method_10264(), arg3.method_10264())) {
					this.field_9302 = false;
					return;
				}
			}
		}
	}

	@Override
	public int method_8624(class_2338 arg, int i) {
		return this.field_9306.method_8624(arg, i);
	}

	@Nullable
	@Override
	public class_2791 method_8402(int i, int j, class_2806 arg, boolean bl) {
		int k = i - this.field_9304;
		int l = j - this.field_9303;
		if (k >= 0 && k < this.field_9305.length && l >= 0 && l < this.field_9305[k].length) {
			class_2791 lv = this.field_9305[k][l];
			return (class_2791)(lv != null ? lv : new class_2812(this.field_9306, new class_1923(i, j)));
		} else {
			return new class_2812(this.field_9306, new class_1923(i, j));
		}
	}

	@Override
	public boolean method_8393(int i, int j) {
		int k = i - this.field_9304;
		int l = j - this.field_9303;
		return k >= 0 && k < this.field_9305.length && l >= 0 && l < this.field_9305[k].length;
	}

	@Override
	public class_2338 method_8598(class_2902.class_2903 arg, class_2338 arg2) {
		return this.field_9306.method_8598(arg, arg2);
	}

	@Override
	public int method_8589(class_2902.class_2903 arg, int i, int j) {
		return this.field_9306.method_8589(arg, i, j);
	}

	@Override
	public int method_8594() {
		return this.field_9306.method_8594();
	}

	@Override
	public class_2784 method_8621() {
		return this.field_9306.method_8621();
	}

	@Override
	public boolean method_8611(@Nullable class_1297 arg, class_265 arg2) {
		return true;
	}

	@Override
	public boolean method_8608() {
		return false;
	}

	@Override
	public int method_8615() {
		return this.field_9306.method_8615();
	}

	@Override
	public class_2869 method_8597() {
		return this.field_9306.method_8597();
	}

	@Nullable
	@Override
	public class_2586 method_8321(class_2338 arg) {
		class_2791 lv = this.method_16955(arg);
		return lv.method_8321(arg);
	}

	@Override
	public class_2680 method_8320(class_2338 arg) {
		if (class_1937.method_8518(arg)) {
			return class_2246.field_10124.method_9564();
		} else {
			class_2791 lv = this.method_16955(arg);
			return lv.method_8320(arg);
		}
	}

	@Override
	public class_3610 method_8316(class_2338 arg) {
		if (class_1937.method_8518(arg)) {
			return class_3612.field_15906.method_15785();
		} else {
			class_2791 lv = this.method_16955(arg);
			return lv.method_8316(arg);
		}
	}

	@Override
	public class_1959 method_8310(class_2338 arg) {
		class_2791 lv = this.method_16955(arg);
		return lv.method_16552(arg);
	}

	@Override
	public int method_8314(class_1944 arg, class_2338 arg2) {
		return this.field_9306.method_8314(arg, arg2);
	}
}
