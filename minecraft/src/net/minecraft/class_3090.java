package net.minecraft;

import com.mojang.datafixers.Dynamic;
import java.util.Random;
import java.util.Set;
import java.util.function.Function;

public class class_3090 extends class_3096<class_3111> {
	private static final class_2680 field_13675 = class_2246.field_10037.method_9564();
	private static final class_2680 field_13676 = class_2246.field_9988.method_9564();
	private static final class_2680 field_13674 = class_2246.field_10520.method_9564();
	private final boolean field_13677;

	public class_3090(Function<Dynamic<?>, ? extends class_3111> function, boolean bl, boolean bl2) {
		super(function, bl, 13, 15, field_13675, field_13676);
		this.field_13677 = bl2;
	}

	@Override
	public boolean method_12775(Set<class_2338> set, class_3747 arg, Random random, class_2338 arg2) {
		int i = this.method_13524(random);
		if (!this.method_13523(arg, arg2, i)) {
			return false;
		} else {
			this.method_13495(arg, arg2.method_10263(), arg2.method_10260(), arg2.method_10264() + i, 0, random);

			for (int j = 0; j < i; j++) {
				if (method_16420(arg, arg2.method_10086(j))) {
					this.method_12773(set, arg, arg2.method_10086(j), this.field_13685);
				}

				if (j < i - 1) {
					if (method_16420(arg, arg2.method_10069(1, j, 0))) {
						this.method_12773(set, arg, arg2.method_10069(1, j, 0), this.field_13685);
					}

					if (method_16420(arg, arg2.method_10069(1, j, 1))) {
						this.method_12773(set, arg, arg2.method_10069(1, j, 1), this.field_13685);
					}

					if (method_16420(arg, arg2.method_10069(0, j, 1))) {
						this.method_12773(set, arg, arg2.method_10069(0, j, 1), this.field_13685);
					}
				}
			}

			this.method_13494(arg, random, arg2);
			return true;
		}
	}

	private void method_13495(class_3747 arg, int i, int j, int k, int l, Random random) {
		int m = random.nextInt(5) + (this.field_13677 ? this.field_13683 : 3);
		int n = 0;

		for (int o = k - m; o <= k; o++) {
			int p = k - o;
			int q = l + class_3532.method_15375((float)p / (float)m * 3.5F);
			this.method_13528(arg, new class_2338(i, o, j), q + (p > 0 && q == n && (o & 1) == 0 ? 1 : 0));
			n = q;
		}
	}

	public void method_13494(class_3747 arg, Random random, class_2338 arg2) {
		this.method_13496(arg, arg2.method_10067().method_10095());
		this.method_13496(arg, arg2.method_10089(2).method_10095());
		this.method_13496(arg, arg2.method_10067().method_10077(2));
		this.method_13496(arg, arg2.method_10089(2).method_10077(2));

		for (int i = 0; i < 5; i++) {
			int j = random.nextInt(64);
			int k = j % 8;
			int l = j / 8;
			if (k == 0 || k == 7 || l == 0 || l == 7) {
				this.method_13496(arg, arg2.method_10069(-3 + k, 0, -3 + l));
			}
		}
	}

	private void method_13496(class_3747 arg, class_2338 arg2) {
		for (int i = -2; i <= 2; i++) {
			for (int j = -2; j <= 2; j++) {
				if (Math.abs(i) != 2 || Math.abs(j) != 2) {
					this.method_13493(arg, arg2.method_10069(i, 0, j));
				}
			}
		}
	}

	private void method_13493(class_3747 arg, class_2338 arg2) {
		for (int i = 2; i >= -3; i--) {
			class_2338 lv = arg2.method_10086(i);
			if (method_16430(arg, lv)) {
				this.method_13153(arg, lv, field_13674);
				break;
			}

			if (!method_16424(arg, lv) && i < 0) {
				break;
			}
		}
	}
}
