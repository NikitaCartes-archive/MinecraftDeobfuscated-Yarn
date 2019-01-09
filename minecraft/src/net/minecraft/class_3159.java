package net.minecraft;

import com.mojang.datafixers.Dynamic;
import java.util.Random;
import java.util.Set;
import java.util.function.Function;

public class class_3159 extends class_2944<class_3111> {
	private static final class_2680 field_13781 = class_2246.field_10010.method_9564();
	private static final class_2680 field_13782 = class_2246.field_10035.method_9564();

	public class_3159(Function<Dynamic<?>, ? extends class_3111> function, boolean bl) {
		super(function, bl);
	}

	@Override
	public boolean method_12775(Set<class_2338> set, class_3747 arg, Random random, class_2338 arg2) {
		int i = random.nextInt(3) + random.nextInt(2) + 6;
		int j = arg2.method_10263();
		int k = arg2.method_10264();
		int l = arg2.method_10260();
		if (k >= 1 && k + i + 1 < 256) {
			class_2338 lv = arg2.method_10074();
			if (!method_16430(arg, lv)) {
				return false;
			} else if (!this.method_13875(arg, arg2, i)) {
				return false;
			} else {
				this.method_16427(arg, lv);
				this.method_16427(arg, lv.method_10078());
				this.method_16427(arg, lv.method_10072());
				this.method_16427(arg, lv.method_10072().method_10078());
				class_2350 lv2 = class_2350.class_2353.field_11062.method_10183(random);
				int m = i - random.nextInt(4);
				int n = 2 - random.nextInt(3);
				int o = j;
				int p = l;
				int q = k + i - 1;

				for (int r = 0; r < i; r++) {
					if (r >= m && n > 0) {
						o += lv2.method_10148();
						p += lv2.method_10165();
						n--;
					}

					int s = k + r;
					class_2338 lv3 = new class_2338(o, s, p);
					if (method_16420(arg, lv3)) {
						this.method_13874(set, arg, lv3);
						this.method_13874(set, arg, lv3.method_10078());
						this.method_13874(set, arg, lv3.method_10072());
						this.method_13874(set, arg, lv3.method_10078().method_10072());
					}
				}

				for (int r = -2; r <= 0; r++) {
					for (int s = -2; s <= 0; s++) {
						int t = -1;
						this.method_13873(arg, o + r, q + t, p + s);
						this.method_13873(arg, 1 + o - r, q + t, p + s);
						this.method_13873(arg, o + r, q + t, 1 + p - s);
						this.method_13873(arg, 1 + o - r, q + t, 1 + p - s);
						if ((r > -2 || s > -1) && (r != -1 || s != -2)) {
							int var28 = 1;
							this.method_13873(arg, o + r, q + var28, p + s);
							this.method_13873(arg, 1 + o - r, q + var28, p + s);
							this.method_13873(arg, o + r, q + var28, 1 + p - s);
							this.method_13873(arg, 1 + o - r, q + var28, 1 + p - s);
						}
					}
				}

				if (random.nextBoolean()) {
					this.method_13873(arg, o, q + 2, p);
					this.method_13873(arg, o + 1, q + 2, p);
					this.method_13873(arg, o + 1, q + 2, p + 1);
					this.method_13873(arg, o, q + 2, p + 1);
				}

				for (int r = -3; r <= 4; r++) {
					for (int sx = -3; sx <= 4; sx++) {
						if ((r != -3 || sx != -3) && (r != -3 || sx != 4) && (r != 4 || sx != -3) && (r != 4 || sx != 4) && (Math.abs(r) < 3 || Math.abs(sx) < 3)) {
							this.method_13873(arg, o + r, q, p + sx);
						}
					}
				}

				for (int r = -1; r <= 2; r++) {
					for (int sxx = -1; sxx <= 2; sxx++) {
						if ((r < 0 || r > 1 || sxx < 0 || sxx > 1) && random.nextInt(3) <= 0) {
							int t = random.nextInt(3) + 2;

							for (int u = 0; u < t; u++) {
								this.method_13874(set, arg, new class_2338(j + r, q - u - 1, l + sxx));
							}

							for (int u = -1; u <= 1; u++) {
								for (int v = -1; v <= 1; v++) {
									this.method_13873(arg, o + r + u, q, p + sxx + v);
								}
							}

							for (int u = -2; u <= 2; u++) {
								for (int v = -2; v <= 2; v++) {
									if (Math.abs(u) != 2 || Math.abs(v) != 2) {
										this.method_13873(arg, o + r + u, q - 1, p + sxx + v);
									}
								}
							}
						}
					}
				}

				return true;
			}
		} else {
			return false;
		}
	}

	private boolean method_13875(class_3746 arg, class_2338 arg2, int i) {
		int j = arg2.method_10263();
		int k = arg2.method_10264();
		int l = arg2.method_10260();
		class_2338.class_2339 lv = new class_2338.class_2339();

		for (int m = 0; m <= i + 1; m++) {
			int n = 1;
			if (m == 0) {
				n = 0;
			}

			if (m >= i - 1) {
				n = 2;
			}

			for (int o = -n; o <= n; o++) {
				for (int p = -n; p <= n; p++) {
					if (!method_16432(arg, lv.method_10103(j + o, k + m, l + p))) {
						return false;
					}
				}
			}
		}

		return true;
	}

	private void method_13874(Set<class_2338> set, class_3747 arg, class_2338 arg2) {
		if (method_16432(arg, arg2)) {
			this.method_12773(set, arg, arg2, field_13781);
		}
	}

	private void method_13873(class_3747 arg, int i, int j, int k) {
		class_2338 lv = new class_2338(i, j, k);
		if (method_16424(arg, lv)) {
			this.method_13153(arg, lv, field_13782);
		}
	}
}
