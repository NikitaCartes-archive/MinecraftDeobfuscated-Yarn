package net.minecraft;

import javax.annotation.Nullable;

public class class_1950 implements class_1920 {
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

	@Nullable
	private class_2791 method_18474(class_2338 arg) {
		int i = (arg.method_10263() >> 4) - this.field_9304;
		int j = (arg.method_10260() >> 4) - this.field_9303;
		return i >= 0 && i < this.field_9305.length && j >= 0 && j < this.field_9305[i].length ? this.field_9305[i][j] : null;
	}

	@Nullable
	@Override
	public class_2586 method_8321(class_2338 arg) {
		class_2791 lv = this.method_18474(arg);
		return lv == null ? null : lv.method_8321(arg);
	}

	@Override
	public class_2680 method_8320(class_2338 arg) {
		if (class_1937.method_8518(arg)) {
			return class_2246.field_10124.method_9564();
		} else {
			class_2791 lv = this.method_18474(arg);
			return lv != null ? lv.method_8320(arg) : class_2246.field_9987.method_9564();
		}
	}

	@Override
	public class_3610 method_8316(class_2338 arg) {
		if (class_1937.method_8518(arg)) {
			return class_3612.field_15906.method_15785();
		} else {
			class_2791 lv = this.method_18474(arg);
			return lv != null ? lv.method_8316(arg) : class_3612.field_15906.method_15785();
		}
	}

	@Override
	public class_1959 method_8310(class_2338 arg) {
		class_2791 lv = this.method_18474(arg);
		return lv == null ? class_1972.field_9451 : lv.method_16552(arg);
	}

	@Override
	public int method_8314(class_1944 arg, class_2338 arg2) {
		return this.field_9306.method_8314(arg, arg2);
	}
}
