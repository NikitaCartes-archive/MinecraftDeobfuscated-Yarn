package net.minecraft;

import com.mojang.datafixers.Dynamic;
import java.util.Random;
import java.util.Set;
import java.util.function.Function;

public class class_3207 extends class_2944<class_3111> {
	private static final class_2680 field_13902 = class_2246.field_10431.method_9564();
	private static final class_2680 field_13899 = class_2246.field_10503.method_9564();
	protected final int field_13900;
	private final boolean field_13903;
	private final class_2680 field_13898;
	private final class_2680 field_13901;

	public class_3207(Function<Dynamic<?>, ? extends class_3111> function, boolean bl) {
		this(function, bl, 4, field_13902, field_13899, false);
	}

	public class_3207(Function<Dynamic<?>, ? extends class_3111> function, boolean bl, int i, class_2680 arg, class_2680 arg2, boolean bl2) {
		super(function, bl);
		this.field_13900 = i;
		this.field_13898 = arg;
		this.field_13901 = arg2;
		this.field_13903 = bl2;
	}

	@Override
	public boolean method_12775(Set<class_2338> set, class_3747 arg, Random random, class_2338 arg2, class_3341 arg3) {
		int i = this.method_14062(random);
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
			} else if (method_16433(arg, arg2.method_10074()) && arg2.method_10264() < 256 - i - 1) {
				this.method_16427(arg, arg2.method_10074());
				int j = 3;
				int kx = 0;

				for (int n = arg2.method_10264() - 3 + i; n <= arg2.method_10264() + i; n++) {
					int l = n - (arg2.method_10264() + i);
					int mx = 1 - l / 2;

					for (int o = arg2.method_10263() - mx; o <= arg2.method_10263() + mx; o++) {
						int p = o - arg2.method_10263();

						for (int q = arg2.method_10260() - mx; q <= arg2.method_10260() + mx; q++) {
							int r = q - arg2.method_10260();
							if (Math.abs(p) != mx || Math.abs(r) != mx || random.nextInt(2) != 0 && l != 0) {
								class_2338 lv2 = new class_2338(o, n, q);
								if (method_16420(arg, lv2) || method_16425(arg, lv2)) {
									this.method_12773(set, arg, lv2, this.field_13901, arg3);
								}
							}
						}
					}
				}

				for (int n = 0; n < i; n++) {
					if (method_16420(arg, arg2.method_10086(n)) || method_16425(arg, arg2.method_10086(n))) {
						this.method_12773(set, arg, arg2.method_10086(n), this.field_13898, arg3);
						if (this.field_13903 && n > 0) {
							if (random.nextInt(3) > 0 && method_16424(arg, arg2.method_10069(-1, n, 0))) {
								this.method_14065(arg, arg2.method_10069(-1, n, 0), class_2541.field_11702);
							}

							if (random.nextInt(3) > 0 && method_16424(arg, arg2.method_10069(1, n, 0))) {
								this.method_14065(arg, arg2.method_10069(1, n, 0), class_2541.field_11696);
							}

							if (random.nextInt(3) > 0 && method_16424(arg, arg2.method_10069(0, n, -1))) {
								this.method_14065(arg, arg2.method_10069(0, n, -1), class_2541.field_11699);
							}

							if (random.nextInt(3) > 0 && method_16424(arg, arg2.method_10069(0, n, 1))) {
								this.method_14065(arg, arg2.method_10069(0, n, 1), class_2541.field_11706);
							}
						}
					}
				}

				if (this.field_13903) {
					for (int nx = arg2.method_10264() - 3 + i; nx <= arg2.method_10264() + i; nx++) {
						int l = nx - (arg2.method_10264() + i);
						int mx = 2 - l / 2;
						class_2338.class_2339 lv3 = new class_2338.class_2339();

						for (int p = arg2.method_10263() - mx; p <= arg2.method_10263() + mx; p++) {
							for (int qx = arg2.method_10260() - mx; qx <= arg2.method_10260() + mx; qx++) {
								lv3.method_10103(p, nx, qx);
								if (method_16416(arg, lv3)) {
									class_2338 lv4 = lv3.method_10067();
									class_2338 lv2 = lv3.method_10078();
									class_2338 lv5 = lv3.method_10095();
									class_2338 lv6 = lv3.method_10072();
									if (random.nextInt(4) == 0 && method_16424(arg, lv4)) {
										this.method_14064(arg, lv4, class_2541.field_11702);
									}

									if (random.nextInt(4) == 0 && method_16424(arg, lv2)) {
										this.method_14064(arg, lv2, class_2541.field_11696);
									}

									if (random.nextInt(4) == 0 && method_16424(arg, lv5)) {
										this.method_14064(arg, lv5, class_2541.field_11699);
									}

									if (random.nextInt(4) == 0 && method_16424(arg, lv6)) {
										this.method_14064(arg, lv6, class_2541.field_11706);
									}
								}
							}
						}
					}

					if (random.nextInt(5) == 0 && i > 5) {
						for (int nx = 0; nx < 2; nx++) {
							for (class_2350 lv7 : class_2350.class_2353.field_11062) {
								if (random.nextInt(4 - nx) == 0) {
									class_2350 lv8 = lv7.method_10153();
									this.method_14063(arg, random.nextInt(3), arg2.method_10069(lv8.method_10148(), i - 5 + nx, lv8.method_10165()), lv7);
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

	protected int method_14062(Random random) {
		return this.field_13900 + random.nextInt(3);
	}

	private void method_14063(class_1945 arg, int i, class_2338 arg2, class_2350 arg3) {
		this.method_13153(
			arg, arg2, class_2246.field_10302.method_9564().method_11657(class_2282.field_10779, Integer.valueOf(i)).method_11657(class_2282.field_11177, arg3)
		);
	}

	private void method_14065(class_1945 arg, class_2338 arg2, class_2746 arg3) {
		this.method_13153(arg, arg2, class_2246.field_10597.method_9564().method_11657(arg3, Boolean.valueOf(true)));
	}

	private void method_14064(class_3747 arg, class_2338 arg2, class_2746 arg3) {
		this.method_14065(arg, arg2, arg3);
		int i = 4;

		for (class_2338 var5 = arg2.method_10074(); method_16424(arg, var5) && i > 0; i--) {
			this.method_14065(arg, var5, arg3);
			var5 = var5.method_10074();
		}
	}
}
