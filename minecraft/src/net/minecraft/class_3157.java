package net.minecraft;

import com.mojang.datafixers.Dynamic;
import java.util.Random;
import java.util.Set;
import java.util.function.Function;

public class class_3157 extends class_2944<class_3111> {
	private static final class_2680 field_13771 = class_2246.field_10533.method_9564();
	private static final class_2680 field_13772 = class_2246.field_10098.method_9564();

	public class_3157(Function<Dynamic<?>, ? extends class_3111> function, boolean bl) {
		super(function, bl);
	}

	@Override
	public boolean method_12775(Set<class_2338> set, class_3747 arg, Random random, class_2338 arg2, class_3341 arg3) {
		int i = random.nextInt(3) + random.nextInt(3) + 5;
		boolean bl = true;
		if (arg2.method_10264() >= 1 && arg2.method_10264() + i + 1 <= 256) {
			for (int j = arg2.method_10264(); j <= arg2.method_10264() + 1 + i; j++) {
				int k = 1;
				if (j == arg2.method_10264()) {
					k = 0;
				}

				if (j >= arg2.method_10264() + 1 + i - 2) {
					k = 2;
				}

				class_2338.class_2339 lv = new class_2338.class_2339();

				for (int l = arg2.method_10263() - k; l <= arg2.method_10263() + k && bl; l++) {
					for (int m = arg2.method_10260() - k; m <= arg2.method_10260() + k && bl; m++) {
						if (j < 0 || j >= 256) {
							bl = false;
						} else if (!method_16432(arg, lv.method_10103(l, j, m))) {
							bl = false;
						}
					}
				}
			}

			if (!bl) {
				return false;
			} else if (method_16430(arg, arg2.method_10074()) && arg2.method_10264() < 256 - i - 1) {
				this.method_16427(arg, arg2.method_10074());
				class_2350 lv2 = class_2350.class_2353.field_11062.method_10183(random);
				int kx = i - random.nextInt(4) - 1;
				int n = 3 - random.nextInt(3);
				int l = arg2.method_10263();
				int mx = arg2.method_10260();
				int o = 0;

				for (int p = 0; p < i; p++) {
					int q = arg2.method_10264() + p;
					if (p >= kx && n > 0) {
						l += lv2.method_10148();
						mx += lv2.method_10165();
						n--;
					}

					class_2338 lv3 = new class_2338(l, q, mx);
					if (method_16420(arg, lv3)) {
						this.method_13852(set, arg, lv3, arg3);
						o = q;
					}
				}

				class_2338 lv4 = new class_2338(l, o, mx);

				for (int qx = -3; qx <= 3; qx++) {
					for (int r = -3; r <= 3; r++) {
						if (Math.abs(qx) != 3 || Math.abs(r) != 3) {
							this.method_13853(set, arg, lv4.method_10069(qx, 0, r), arg3);
						}
					}
				}

				lv4 = lv4.method_10084();

				for (int qx = -1; qx <= 1; qx++) {
					for (int rx = -1; rx <= 1; rx++) {
						this.method_13853(set, arg, lv4.method_10069(qx, 0, rx), arg3);
					}
				}

				this.method_13853(set, arg, lv4.method_10089(2), arg3);
				this.method_13853(set, arg, lv4.method_10088(2), arg3);
				this.method_13853(set, arg, lv4.method_10077(2), arg3);
				this.method_13853(set, arg, lv4.method_10076(2), arg3);
				l = arg2.method_10263();
				mx = arg2.method_10260();
				class_2350 lv5 = class_2350.class_2353.field_11062.method_10183(random);
				if (lv5 != lv2) {
					int qx = kx - random.nextInt(2) - 1;
					int rx = 1 + random.nextInt(3);
					o = 0;

					for (int s = qx; s < i && rx > 0; rx--) {
						if (s >= 1) {
							int t = arg2.method_10264() + s;
							l += lv5.method_10148();
							mx += lv5.method_10165();
							class_2338 lv6 = new class_2338(l, t, mx);
							if (method_16420(arg, lv6)) {
								this.method_13852(set, arg, lv6, arg3);
								o = t;
							}
						}

						s++;
					}

					if (o > 0) {
						class_2338 lv7 = new class_2338(l, o, mx);

						for (int t = -2; t <= 2; t++) {
							for (int u = -2; u <= 2; u++) {
								if (Math.abs(t) != 2 || Math.abs(u) != 2) {
									this.method_13853(set, arg, lv7.method_10069(t, 0, u), arg3);
								}
							}
						}

						lv7 = lv7.method_10084();

						for (int t = -1; t <= 1; t++) {
							for (int ux = -1; ux <= 1; ux++) {
								this.method_13853(set, arg, lv7.method_10069(t, 0, ux), arg3);
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

	private void method_13852(Set<class_2338> set, class_1945 arg, class_2338 arg2, class_3341 arg3) {
		this.method_12773(set, arg, arg2, field_13771, arg3);
	}

	private void method_13853(Set<class_2338> set, class_3747 arg, class_2338 arg2, class_3341 arg3) {
		if (method_16420(arg, arg2)) {
			this.method_12773(set, arg, arg2, field_13772, arg3);
		}
	}
}
