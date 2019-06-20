package net.minecraft;

import com.mojang.datafixers.Dynamic;
import java.util.Arrays;
import java.util.Random;
import java.util.function.Function;

public class class_3506 extends class_3523<class_3527> {
	private static final class_2680 field_15624 = class_2246.field_10611.method_9564();
	private static final class_2680 field_15620 = class_2246.field_10184.method_9564();
	private static final class_2680 field_15625 = class_2246.field_10415.method_9564();
	private static final class_2680 field_15626 = class_2246.field_10143.method_9564();
	private static final class_2680 field_15616 = class_2246.field_10123.method_9564();
	private static final class_2680 field_15621 = class_2246.field_10328.method_9564();
	private static final class_2680 field_15617 = class_2246.field_10590.method_9564();
	protected class_2680[] field_15627;
	protected long field_15622;
	protected class_3543 field_15623;
	protected class_3543 field_15618;
	protected class_3543 field_15619;

	public class_3506(Function<Dynamic<?>, ? extends class_3527> function) {
		super(function);
	}

	public void method_15208(
		Random random, class_2791 arg, class_1959 arg2, int i, int j, int k, double d, class_2680 arg3, class_2680 arg4, int l, long m, class_3527 arg5
	) {
		int n = i & 15;
		int o = j & 15;
		class_2680 lv = field_15624;
		class_2680 lv2 = arg2.method_8722().method_15336();
		int p = (int)(d / 3.0 + 3.0 + random.nextDouble() * 0.25);
		boolean bl = Math.cos(d / 3.0 * Math.PI) > 0.0;
		int q = -1;
		boolean bl2 = false;
		int r = 0;
		class_2338.class_2339 lv3 = new class_2338.class_2339();

		for (int s = k; s >= 0; s--) {
			if (r < 15) {
				lv3.method_10103(n, s, o);
				class_2680 lv4 = arg.method_8320(lv3);
				if (lv4.method_11588()) {
					q = -1;
				} else if (lv4.method_11614() == arg3.method_11614()) {
					if (q == -1) {
						bl2 = false;
						if (p <= 0) {
							lv = class_2246.field_10124.method_9564();
							lv2 = arg3;
						} else if (s >= l - 4 && s <= l + 1) {
							lv = field_15624;
							lv2 = arg2.method_8722().method_15336();
						}

						if (s < l && (lv == null || lv.method_11588())) {
							lv = arg4;
						}

						q = p + Math.max(0, s - l);
						if (s >= l - 1) {
							if (s <= l + 3 + p) {
								arg.method_12010(lv3, arg2.method_8722().method_15337(), false);
								bl2 = true;
							} else {
								class_2680 lv5;
								if (s < 64 || s > 127) {
									lv5 = field_15620;
								} else if (bl) {
									lv5 = field_15625;
								} else {
									lv5 = this.method_15207(i, s, j);
								}

								arg.method_12010(lv3, lv5, false);
							}
						} else {
							arg.method_12010(lv3, lv2, false);
							class_2248 lv6 = lv2.method_11614();
							if (lv6 == class_2246.field_10611
								|| lv6 == class_2246.field_10184
								|| lv6 == class_2246.field_10015
								|| lv6 == class_2246.field_10325
								|| lv6 == class_2246.field_10143
								|| lv6 == class_2246.field_10014
								|| lv6 == class_2246.field_10444
								|| lv6 == class_2246.field_10349
								|| lv6 == class_2246.field_10590
								|| lv6 == class_2246.field_10235
								|| lv6 == class_2246.field_10570
								|| lv6 == class_2246.field_10409
								|| lv6 == class_2246.field_10123
								|| lv6 == class_2246.field_10526
								|| lv6 == class_2246.field_10328
								|| lv6 == class_2246.field_10626) {
								arg.method_12010(lv3, field_15620, false);
							}
						}
					} else if (q > 0) {
						q--;
						if (bl2) {
							arg.method_12010(lv3, field_15620, false);
						} else {
							arg.method_12010(lv3, this.method_15207(i, s, j), false);
						}
					}

					r++;
				}
			}
		}
	}

	@Override
	public void method_15306(long l) {
		if (this.field_15622 != l || this.field_15627 == null) {
			this.method_15209(l);
		}

		if (this.field_15622 != l || this.field_15623 == null || this.field_15618 == null) {
			Random random = new class_2919(l);
			this.field_15623 = new class_3543(random, 4);
			this.field_15618 = new class_3543(random, 1);
		}

		this.field_15622 = l;
	}

	protected void method_15209(long l) {
		this.field_15627 = new class_2680[64];
		Arrays.fill(this.field_15627, field_15625);
		Random random = new class_2919(l);
		this.field_15619 = new class_3543(random, 1);

		for (int i = 0; i < 64; i++) {
			i += random.nextInt(5) + 1;
			if (i < 64) {
				this.field_15627[i] = field_15620;
			}
		}

		int ix = random.nextInt(4) + 2;

		for (int j = 0; j < ix; j++) {
			int k = random.nextInt(3) + 1;
			int m = random.nextInt(64);

			for (int n = 0; m + n < 64 && n < k; n++) {
				this.field_15627[m + n] = field_15626;
			}
		}

		int j = random.nextInt(4) + 2;

		for (int k = 0; k < j; k++) {
			int m = random.nextInt(3) + 2;
			int n = random.nextInt(64);

			for (int o = 0; n + o < 64 && o < m; o++) {
				this.field_15627[n + o] = field_15616;
			}
		}

		int k = random.nextInt(4) + 2;

		for (int m = 0; m < k; m++) {
			int n = random.nextInt(3) + 1;
			int o = random.nextInt(64);

			for (int p = 0; o + p < 64 && p < n; p++) {
				this.field_15627[o + p] = field_15621;
			}
		}

		int m = random.nextInt(3) + 3;
		int n = 0;

		for (int o = 0; o < m; o++) {
			int p = 1;
			n += random.nextInt(16) + 4;

			for (int q = 0; n + q < 64 && q < 1; q++) {
				this.field_15627[n + q] = field_15624;
				if (n + q > 1 && random.nextBoolean()) {
					this.field_15627[n + q - 1] = field_15617;
				}

				if (n + q < 63 && random.nextBoolean()) {
					this.field_15627[n + q + 1] = field_15617;
				}
			}
		}
	}

	protected class_2680 method_15207(int i, int j, int k) {
		int l = (int)Math.round(this.field_15619.method_15437((double)i / 512.0, (double)k / 512.0) * 2.0);
		return this.field_15627[(j + l + 64) % 64];
	}
}
