package net.minecraft;

import com.mojang.datafixers.Dynamic;
import java.util.Random;
import java.util.function.Function;

public abstract class class_3096<T extends class_3037> extends class_2944<T> {
	protected final int field_13683;
	protected final class_2680 field_13685;
	protected final class_2680 field_13684;
	protected final int field_13686;

	public class_3096(Function<Dynamic<?>, ? extends T> function, boolean bl, int i, int j, class_2680 arg, class_2680 arg2) {
		super(function, bl);
		this.field_13683 = i;
		this.field_13686 = j;
		this.field_13685 = arg;
		this.field_13684 = arg2;
	}

	protected int method_13524(Random random) {
		int i = random.nextInt(3) + this.field_13683;
		if (this.field_13686 > 1) {
			i += random.nextInt(this.field_13686);
		}

		return i;
	}

	private boolean method_13527(class_3746 arg, class_2338 arg2, int i) {
		boolean bl = true;
		if (arg2.method_10264() >= 1 && arg2.method_10264() + i + 1 <= 256) {
			for (int j = 0; j <= 1 + i; j++) {
				int k = 2;
				if (j == 0) {
					k = 1;
				} else if (j >= 1 + i - 2) {
					k = 2;
				}

				for (int l = -k; l <= k && bl; l++) {
					for (int m = -k; m <= k && bl; m++) {
						if (arg2.method_10264() + j < 0 || arg2.method_10264() + j >= 256 || !method_16432(arg, arg2.method_10069(l, j, m))) {
							bl = false;
						}
					}
				}
			}

			return bl;
		} else {
			return false;
		}
	}

	private boolean method_13525(class_3747 arg, class_2338 arg2) {
		class_2338 lv = arg2.method_10074();
		if (method_16430(arg, lv) && arg2.method_10264() >= 2) {
			this.method_16427(arg, lv);
			this.method_16427(arg, lv.method_10078());
			this.method_16427(arg, lv.method_10072());
			this.method_16427(arg, lv.method_10072().method_10078());
			return true;
		} else {
			return false;
		}
	}

	protected boolean method_13523(class_3747 arg, class_2338 arg2, int i) {
		return this.method_13527(arg, arg2, i) && this.method_13525(arg, arg2);
	}

	protected void method_13528(class_3747 arg, class_2338 arg2, int i) {
		int j = i * i;

		for (int k = -i; k <= i + 1; k++) {
			for (int l = -i; l <= i + 1; l++) {
				int m = Math.min(Math.abs(k), Math.abs(k - 1));
				int n = Math.min(Math.abs(l), Math.abs(l - 1));
				if (m + n < 7 && m * m + n * n <= j) {
					class_2338 lv = arg2.method_10069(k, 0, l);
					if (method_16420(arg, lv)) {
						this.method_13153(arg, lv, this.field_13684);
					}
				}
			}
		}
	}

	protected void method_13526(class_3747 arg, class_2338 arg2, int i) {
		int j = i * i;

		for (int k = -i; k <= i; k++) {
			for (int l = -i; l <= i; l++) {
				if (k * k + l * l <= j) {
					class_2338 lv = arg2.method_10069(k, 0, l);
					if (method_16420(arg, lv)) {
						this.method_13153(arg, lv, this.field_13684);
					}
				}
			}
		}
	}
}
