package net.minecraft;

import com.mojang.datafixers.Dynamic;
import java.util.Random;
import java.util.Set;
import java.util.function.Function;

public class class_3200 extends class_2944<class_3111> {
	private static final class_2680 field_13885 = class_2246.field_10431.method_9564();
	private static final class_2680 field_13886 = class_2246.field_10503.method_9564();

	public class_3200(Function<Dynamic<?>, ? extends class_3111> function) {
		super(function, false);
	}

	@Override
	public boolean method_12775(Set<class_2338> set, class_3747 arg, Random random, class_2338 arg2) {
		int i = random.nextInt(4) + 5;
		arg2 = arg.method_8598(class_2902.class_2903.field_13200, arg2);
		boolean bl = true;
		if (arg2.method_10264() >= 1 && arg2.method_10264() + i + 1 <= 256) {
			for (int j = arg2.method_10264(); j <= arg2.method_10264() + 1 + i; j++) {
				int k = 1;
				if (j == arg2.method_10264()) {
					k = 0;
				}

				if (j >= arg2.method_10264() + 1 + i - 2) {
					k = 3;
				}

				class_2338.class_2339 lv = new class_2338.class_2339();

				for (int l = arg2.method_10263() - k; l <= arg2.method_10263() + k && bl; l++) {
					for (int m = arg2.method_10260() - k; m <= arg2.method_10260() + k && bl; m++) {
						if (j >= 0 && j < 256) {
							lv.method_10103(l, j, m);
							if (!method_16420(arg, lv)) {
								if (method_16422(arg, lv)) {
									if (j > arg2.method_10264()) {
										bl = false;
									}
								} else {
									bl = false;
								}
							}
						} else {
							bl = false;
						}
					}
				}
			}

			if (!bl) {
				return false;
			} else if (method_16430(arg, arg2.method_10074()) && arg2.method_10264() < 256 - i - 1) {
				this.method_16427(arg, arg2.method_10074());

				for (int j = arg2.method_10264() - 3 + i; j <= arg2.method_10264() + i; j++) {
					int kx = j - (arg2.method_10264() + i);
					int n = 2 - kx / 2;

					for (int l = arg2.method_10263() - n; l <= arg2.method_10263() + n; l++) {
						int mx = l - arg2.method_10263();

						for (int o = arg2.method_10260() - n; o <= arg2.method_10260() + n; o++) {
							int p = o - arg2.method_10260();
							if (Math.abs(mx) != n || Math.abs(p) != n || random.nextInt(2) != 0 && kx != 0) {
								class_2338 lv2 = new class_2338(l, j, o);
								if (method_16420(arg, lv2) || method_16425(arg, lv2)) {
									this.method_13153(arg, lv2, field_13886);
								}
							}
						}
					}
				}

				for (int j = 0; j < i; j++) {
					class_2338 lv3 = arg2.method_10086(j);
					if (method_16420(arg, lv3) || method_16422(arg, lv3)) {
						this.method_12773(set, arg, lv3, field_13885);
					}
				}

				for (int jx = arg2.method_10264() - 3 + i; jx <= arg2.method_10264() + i; jx++) {
					int kx = jx - (arg2.method_10264() + i);
					int n = 2 - kx / 2;
					class_2338.class_2339 lv4 = new class_2338.class_2339();

					for (int mx = arg2.method_10263() - n; mx <= arg2.method_10263() + n; mx++) {
						for (int ox = arg2.method_10260() - n; ox <= arg2.method_10260() + n; ox++) {
							lv4.method_10103(mx, jx, ox);
							if (method_16416(arg, lv4)) {
								class_2338 lv5 = lv4.method_10067();
								class_2338 lv2 = lv4.method_10078();
								class_2338 lv6 = lv4.method_10095();
								class_2338 lv7 = lv4.method_10072();
								if (random.nextInt(4) == 0 && method_16424(arg, lv5)) {
									this.method_14030(arg, lv5, class_2541.field_11702);
								}

								if (random.nextInt(4) == 0 && method_16424(arg, lv2)) {
									this.method_14030(arg, lv2, class_2541.field_11696);
								}

								if (random.nextInt(4) == 0 && method_16424(arg, lv6)) {
									this.method_14030(arg, lv6, class_2541.field_11699);
								}

								if (random.nextInt(4) == 0 && method_16424(arg, lv7)) {
									this.method_14030(arg, lv7, class_2541.field_11706);
								}
							}
						}
					}
				}

				return true;
			} else {
				return false;
			}
		} else {
			return false;
		}
	}

	private void method_14030(class_3747 arg, class_2338 arg2, class_2746 arg3) {
		class_2680 lv = class_2246.field_10597.method_9564().method_11657(arg3, Boolean.valueOf(true));
		this.method_13153(arg, arg2, lv);
		int i = 4;

		for (class_2338 var6 = arg2.method_10074(); method_16424(arg, var6) && i > 0; i--) {
			this.method_13153(arg, var6, lv);
			var6 = var6.method_10074();
		}
	}
}
