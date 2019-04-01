package net.minecraft;

import com.google.common.collect.Lists;
import java.util.List;
import java.util.Random;
import javax.annotation.Nullable;

public class class_3353 {
	private static class_3353.class_3356 method_14712(
		List<class_3443> list, Random random, int i, int j, int k, @Nullable class_2350 arg, int l, class_3098.class_3100 arg2
	) {
		int m = random.nextInt(100);
		if (m >= 80) {
			class_3341 lv = class_3353.class_3355.method_14717(list, random, i, j, k, arg);
			if (lv != null) {
				return new class_3353.class_3355(l, lv, arg, arg2);
			}
		} else if (m >= 70) {
			class_3341 lv = class_3353.class_3358.method_14720(list, random, i, j, k, arg);
			if (lv != null) {
				return new class_3353.class_3358(l, lv, arg, arg2);
			}
		} else {
			class_3341 lv = class_3353.class_3354.method_14714(list, random, i, j, k, arg);
			if (lv != null) {
				return new class_3353.class_3354(l, random, lv, arg, arg2);
			}
		}

		return null;
	}

	private static class_3353.class_3356 method_14711(class_3443 arg, List<class_3443> list, Random random, int i, int j, int k, class_2350 arg2, int l) {
		if (l > 8) {
			return null;
		} else if (Math.abs(i - arg.method_14935().field_14381) <= 80 && Math.abs(k - arg.method_14935().field_14379) <= 80) {
			class_3098.class_3100 lv = ((class_3353.class_3356)arg).field_14421;
			class_3353.class_3356 lv2 = method_14712(list, random, i, j, k, arg2, l + 1, lv);
			if (lv2 != null) {
				list.add(lv2);
				lv2.method_14918(arg, list, random);
			}

			return lv2;
		} else {
			return null;
		}
	}

	public static class class_3354 extends class_3353.class_3356 {
		private final boolean field_14416;
		private final boolean field_14415;
		private boolean field_14414;
		private final int field_14413;

		public class_3354(class_3485 arg, class_2487 arg2) {
			super(class_3773.field_16969, arg2);
			this.field_14416 = arg2.method_10577("hr");
			this.field_14415 = arg2.method_10577("sc");
			this.field_14414 = arg2.method_10577("hps");
			this.field_14413 = arg2.method_10550("Num");
		}

		@Override
		protected void method_14943(class_2487 arg) {
			super.method_14943(arg);
			arg.method_10556("hr", this.field_14416);
			arg.method_10556("sc", this.field_14415);
			arg.method_10556("hps", this.field_14414);
			arg.method_10569("Num", this.field_14413);
		}

		public class_3354(int i, Random random, class_3341 arg, class_2350 arg2, class_3098.class_3100 arg3) {
			super(class_3773.field_16969, i, arg3);
			this.method_14926(arg2);
			this.field_15315 = arg;
			this.field_14416 = random.nextInt(3) == 0;
			this.field_14415 = !this.field_14416 && random.nextInt(23) == 0;
			if (this.method_14934().method_10166() == class_2350.class_2351.field_11051) {
				this.field_14413 = arg.method_14664() / 5;
			} else {
				this.field_14413 = arg.method_14660() / 5;
			}
		}

		public static class_3341 method_14714(List<class_3443> list, Random random, int i, int j, int k, class_2350 arg) {
			class_3341 lv = new class_3341(i, j, k, i, j + 3 - 1, k);

			int l;
			for (l = random.nextInt(3) + 2; l > 0; l--) {
				int m = l * 5;
				switch (arg) {
					case field_11043:
					default:
						lv.field_14378 = i + 3 - 1;
						lv.field_14379 = k - (m - 1);
						break;
					case field_11035:
						lv.field_14378 = i + 3 - 1;
						lv.field_14376 = k + m - 1;
						break;
					case field_11039:
						lv.field_14381 = i - (m - 1);
						lv.field_14376 = k + 3 - 1;
						break;
					case field_11034:
						lv.field_14378 = i + m - 1;
						lv.field_14376 = k + 3 - 1;
				}

				if (class_3443.method_14932(list, lv) == null) {
					break;
				}
			}

			return l > 0 ? lv : null;
		}

		@Override
		public void method_14918(class_3443 arg, List<class_3443> list, Random random) {
			int i = this.method_14923();
			int j = random.nextInt(4);
			class_2350 lv = this.method_14934();
			if (lv != null) {
				switch (lv) {
					case field_11043:
					default:
						if (j <= 1) {
							class_3353.method_14711(
								arg, list, random, this.field_15315.field_14381, this.field_15315.field_14380 - 1 + random.nextInt(3), this.field_15315.field_14379 - 1, lv, i
							);
						} else if (j == 2) {
							class_3353.method_14711(
								arg,
								list,
								random,
								this.field_15315.field_14381 - 1,
								this.field_15315.field_14380 - 1 + random.nextInt(3),
								this.field_15315.field_14379,
								class_2350.field_11039,
								i
							);
						} else {
							class_3353.method_14711(
								arg,
								list,
								random,
								this.field_15315.field_14378 + 1,
								this.field_15315.field_14380 - 1 + random.nextInt(3),
								this.field_15315.field_14379,
								class_2350.field_11034,
								i
							);
						}
						break;
					case field_11035:
						if (j <= 1) {
							class_3353.method_14711(
								arg, list, random, this.field_15315.field_14381, this.field_15315.field_14380 - 1 + random.nextInt(3), this.field_15315.field_14376 + 1, lv, i
							);
						} else if (j == 2) {
							class_3353.method_14711(
								arg,
								list,
								random,
								this.field_15315.field_14381 - 1,
								this.field_15315.field_14380 - 1 + random.nextInt(3),
								this.field_15315.field_14376 - 3,
								class_2350.field_11039,
								i
							);
						} else {
							class_3353.method_14711(
								arg,
								list,
								random,
								this.field_15315.field_14378 + 1,
								this.field_15315.field_14380 - 1 + random.nextInt(3),
								this.field_15315.field_14376 - 3,
								class_2350.field_11034,
								i
							);
						}
						break;
					case field_11039:
						if (j <= 1) {
							class_3353.method_14711(
								arg, list, random, this.field_15315.field_14381 - 1, this.field_15315.field_14380 - 1 + random.nextInt(3), this.field_15315.field_14379, lv, i
							);
						} else if (j == 2) {
							class_3353.method_14711(
								arg,
								list,
								random,
								this.field_15315.field_14381,
								this.field_15315.field_14380 - 1 + random.nextInt(3),
								this.field_15315.field_14379 - 1,
								class_2350.field_11043,
								i
							);
						} else {
							class_3353.method_14711(
								arg,
								list,
								random,
								this.field_15315.field_14381,
								this.field_15315.field_14380 - 1 + random.nextInt(3),
								this.field_15315.field_14376 + 1,
								class_2350.field_11035,
								i
							);
						}
						break;
					case field_11034:
						if (j <= 1) {
							class_3353.method_14711(
								arg, list, random, this.field_15315.field_14378 + 1, this.field_15315.field_14380 - 1 + random.nextInt(3), this.field_15315.field_14379, lv, i
							);
						} else if (j == 2) {
							class_3353.method_14711(
								arg,
								list,
								random,
								this.field_15315.field_14378 - 3,
								this.field_15315.field_14380 - 1 + random.nextInt(3),
								this.field_15315.field_14379 - 1,
								class_2350.field_11043,
								i
							);
						} else {
							class_3353.method_14711(
								arg,
								list,
								random,
								this.field_15315.field_14378 - 3,
								this.field_15315.field_14380 - 1 + random.nextInt(3),
								this.field_15315.field_14376 + 1,
								class_2350.field_11035,
								i
							);
						}
				}
			}

			if (i < 8) {
				if (lv != class_2350.field_11043 && lv != class_2350.field_11035) {
					for (int k = this.field_15315.field_14381 + 3; k + 3 <= this.field_15315.field_14378; k += 5) {
						int l = random.nextInt(5);
						if (l == 0) {
							class_3353.method_14711(arg, list, random, k, this.field_15315.field_14380, this.field_15315.field_14379 - 1, class_2350.field_11043, i + 1);
						} else if (l == 1) {
							class_3353.method_14711(arg, list, random, k, this.field_15315.field_14380, this.field_15315.field_14376 + 1, class_2350.field_11035, i + 1);
						}
					}
				} else {
					for (int kx = this.field_15315.field_14379 + 3; kx + 3 <= this.field_15315.field_14376; kx += 5) {
						int l = random.nextInt(5);
						if (l == 0) {
							class_3353.method_14711(arg, list, random, this.field_15315.field_14381 - 1, this.field_15315.field_14380, kx, class_2350.field_11039, i + 1);
						} else if (l == 1) {
							class_3353.method_14711(arg, list, random, this.field_15315.field_14378 + 1, this.field_15315.field_14380, kx, class_2350.field_11034, i + 1);
						}
					}
				}
			}
		}

		@Override
		protected boolean method_14915(class_1936 arg, class_3341 arg2, Random random, int i, int j, int k, class_2960 arg3) {
			class_2338 lv = new class_2338(this.method_14928(i, k), this.method_14924(j), this.method_14941(i, k));
			if (arg2.method_14662(lv) && arg.method_8320(lv).method_11588() && !arg.method_8320(lv.method_10074()).method_11588()) {
				class_2680 lv2 = class_2246.field_10167
					.method_9564()
					.method_11657(class_2443.field_11369, random.nextBoolean() ? class_2768.field_12665 : class_2768.field_12674);
				this.method_14917(arg, lv2, i, j, k, arg2);
				class_1694 lv3 = new class_1694(
					arg.method_8410(), (double)((float)lv.method_10263() + 0.5F), (double)((float)lv.method_10264() + 0.5F), (double)((float)lv.method_10260() + 0.5F)
				);
				lv3.method_7562(arg3, random.nextLong());
				arg.method_8649(lv3);
				return true;
			} else {
				return false;
			}
		}

		@Override
		public boolean method_14931(class_1936 arg, Random random, class_3341 arg2, class_1923 arg3) {
			if (this.method_14937(arg, arg2)) {
				return false;
			} else {
				int i = 0;
				int j = 2;
				int k = 0;
				int l = 2;
				int m = this.field_14413 * 5 - 1;
				class_2680 lv = this.method_16443();
				this.method_14940(arg, arg2, 0, 0, 0, 2, 1, m, field_15314, field_15314, false);
				this.method_14933(arg, arg2, random, 0.8F, 0, 2, 0, 2, 2, m, field_15314, field_15314, false, false);
				if (this.field_14415) {
					this.method_14933(arg, arg2, random, 0.6F, 0, 0, 0, 2, 1, m, class_2246.field_10343.method_9564(), field_15314, false, true);
				}

				for (int n = 0; n < this.field_14413; n++) {
					int o = 2 + n * 5;
					this.method_14713(arg, arg2, 0, 0, o, 2, 2, random);
					this.method_14715(arg, arg2, random, 0.1F, 0, 2, o - 1);
					this.method_14715(arg, arg2, random, 0.1F, 2, 2, o - 1);
					this.method_14715(arg, arg2, random, 0.1F, 0, 2, o + 1);
					this.method_14715(arg, arg2, random, 0.1F, 2, 2, o + 1);
					this.method_14715(arg, arg2, random, 0.05F, 0, 2, o - 2);
					this.method_14715(arg, arg2, random, 0.05F, 2, 2, o - 2);
					this.method_14715(arg, arg2, random, 0.05F, 0, 2, o + 2);
					this.method_14715(arg, arg2, random, 0.05F, 2, 2, o + 2);
					if (random.nextInt(100) == 0) {
						this.method_14915(arg, arg2, random, 2, 0, o - 1, class_39.field_472);
					}

					if (random.nextInt(100) == 0) {
						this.method_14915(arg, arg2, random, 0, 0, o + 1, class_39.field_472);
					}

					if (this.field_14415 && !this.field_14414) {
						int p = this.method_14924(0);
						int q = o - 1 + random.nextInt(3);
						int r = this.method_14928(1, q);
						int s = this.method_14941(1, q);
						class_2338 lv2 = new class_2338(r, p, s);
						if (arg2.method_14662(lv2) && this.method_14939(arg, 1, 0, q, arg2)) {
							this.field_14414 = true;
							arg.method_8652(lv2, class_2246.field_10260.method_9564(), 2);
							class_2586 lv3 = arg.method_8321(lv2);
							if (lv3 instanceof class_2636) {
								((class_2636)lv3).method_11390().method_8274(class_1299.field_6084);
							}
						}
					}
				}

				for (int n = 0; n <= 2; n++) {
					for (int ox = 0; ox <= m; ox++) {
						int p = -1;
						class_2680 lv4 = this.method_14929(arg, n, -1, ox, arg2);
						if (lv4.method_11588() && this.method_14939(arg, n, -1, ox, arg2)) {
							int r = -1;
							this.method_14917(arg, lv, n, -1, ox, arg2);
						}
					}
				}

				if (this.field_14416) {
					class_2680 lv5 = class_2246.field_10167.method_9564().method_11657(class_2443.field_11369, class_2768.field_12665);

					for (int oxx = 0; oxx <= m; oxx++) {
						class_2680 lv6 = this.method_14929(arg, 1, -1, oxx, arg2);
						if (!lv6.method_11588() && lv6.method_11598(arg, new class_2338(this.method_14928(1, oxx), this.method_14924(-1), this.method_14941(1, oxx)))) {
							float f = this.method_14939(arg, 1, 0, oxx, arg2) ? 0.7F : 0.9F;
							this.method_14945(arg, arg2, random, f, 1, 0, oxx, lv5);
						}
					}
				}

				return true;
			}
		}

		private void method_14713(class_1936 arg, class_3341 arg2, int i, int j, int k, int l, int m, Random random) {
			if (this.method_14719(arg, arg2, i, m, l, k)) {
				class_2680 lv = this.method_16443();
				class_2680 lv2 = this.method_14718();
				this.method_14940(arg, arg2, i, j, k, i, l - 1, k, lv2.method_11657(class_2354.field_10903, Boolean.valueOf(true)), field_15314, false);
				this.method_14940(arg, arg2, m, j, k, m, l - 1, k, lv2.method_11657(class_2354.field_10907, Boolean.valueOf(true)), field_15314, false);
				if (random.nextInt(4) == 0) {
					this.method_14940(arg, arg2, i, l, k, i, l, k, lv, field_15314, false);
					this.method_14940(arg, arg2, m, l, k, m, l, k, lv, field_15314, false);
				} else {
					this.method_14940(arg, arg2, i, l, k, m, l, k, lv, field_15314, false);
					this.method_14945(
						arg, arg2, random, 0.05F, i + 1, l, k - 1, class_2246.field_10099.method_9564().method_11657(class_2555.field_11731, class_2350.field_11043)
					);
					this.method_14945(
						arg, arg2, random, 0.05F, i + 1, l, k + 1, class_2246.field_10099.method_9564().method_11657(class_2555.field_11731, class_2350.field_11035)
					);
				}
			}
		}

		private void method_14715(class_1936 arg, class_3341 arg2, Random random, float f, int i, int j, int k) {
			if (this.method_14939(arg, i, j, k, arg2)) {
				this.method_14945(arg, arg2, random, f, i, j, k, class_2246.field_10343.method_9564());
			}
		}
	}

	public static class class_3355 extends class_3353.class_3356 {
		private final class_2350 field_14420;
		private final boolean field_14419;

		public class_3355(class_3485 arg, class_2487 arg2) {
			super(class_3773.field_16919, arg2);
			this.field_14419 = arg2.method_10577("tf");
			this.field_14420 = class_2350.method_10139(arg2.method_10550("D"));
		}

		@Override
		protected void method_14943(class_2487 arg) {
			super.method_14943(arg);
			arg.method_10556("tf", this.field_14419);
			arg.method_10569("D", this.field_14420.method_10161());
		}

		public class_3355(int i, class_3341 arg, @Nullable class_2350 arg2, class_3098.class_3100 arg3) {
			super(class_3773.field_16919, i, arg3);
			this.field_14420 = arg2;
			this.field_15315 = arg;
			this.field_14419 = arg.method_14663() > 3;
		}

		public static class_3341 method_14717(List<class_3443> list, Random random, int i, int j, int k, class_2350 arg) {
			class_3341 lv = new class_3341(i, j, k, i, j + 3 - 1, k);
			if (random.nextInt(4) == 0) {
				lv.field_14377 += 4;
			}

			switch (arg) {
				case field_11043:
				default:
					lv.field_14381 = i - 1;
					lv.field_14378 = i + 3;
					lv.field_14379 = k - 4;
					break;
				case field_11035:
					lv.field_14381 = i - 1;
					lv.field_14378 = i + 3;
					lv.field_14376 = k + 3 + 1;
					break;
				case field_11039:
					lv.field_14381 = i - 4;
					lv.field_14379 = k - 1;
					lv.field_14376 = k + 3;
					break;
				case field_11034:
					lv.field_14378 = i + 3 + 1;
					lv.field_14379 = k - 1;
					lv.field_14376 = k + 3;
			}

			return class_3443.method_14932(list, lv) != null ? null : lv;
		}

		@Override
		public void method_14918(class_3443 arg, List<class_3443> list, Random random) {
			int i = this.method_14923();
			switch (this.field_14420) {
				case field_11043:
				default:
					class_3353.method_14711(
						arg, list, random, this.field_15315.field_14381 + 1, this.field_15315.field_14380, this.field_15315.field_14379 - 1, class_2350.field_11043, i
					);
					class_3353.method_14711(
						arg, list, random, this.field_15315.field_14381 - 1, this.field_15315.field_14380, this.field_15315.field_14379 + 1, class_2350.field_11039, i
					);
					class_3353.method_14711(
						arg, list, random, this.field_15315.field_14378 + 1, this.field_15315.field_14380, this.field_15315.field_14379 + 1, class_2350.field_11034, i
					);
					break;
				case field_11035:
					class_3353.method_14711(
						arg, list, random, this.field_15315.field_14381 + 1, this.field_15315.field_14380, this.field_15315.field_14376 + 1, class_2350.field_11035, i
					);
					class_3353.method_14711(
						arg, list, random, this.field_15315.field_14381 - 1, this.field_15315.field_14380, this.field_15315.field_14379 + 1, class_2350.field_11039, i
					);
					class_3353.method_14711(
						arg, list, random, this.field_15315.field_14378 + 1, this.field_15315.field_14380, this.field_15315.field_14379 + 1, class_2350.field_11034, i
					);
					break;
				case field_11039:
					class_3353.method_14711(
						arg, list, random, this.field_15315.field_14381 + 1, this.field_15315.field_14380, this.field_15315.field_14379 - 1, class_2350.field_11043, i
					);
					class_3353.method_14711(
						arg, list, random, this.field_15315.field_14381 + 1, this.field_15315.field_14380, this.field_15315.field_14376 + 1, class_2350.field_11035, i
					);
					class_3353.method_14711(
						arg, list, random, this.field_15315.field_14381 - 1, this.field_15315.field_14380, this.field_15315.field_14379 + 1, class_2350.field_11039, i
					);
					break;
				case field_11034:
					class_3353.method_14711(
						arg, list, random, this.field_15315.field_14381 + 1, this.field_15315.field_14380, this.field_15315.field_14379 - 1, class_2350.field_11043, i
					);
					class_3353.method_14711(
						arg, list, random, this.field_15315.field_14381 + 1, this.field_15315.field_14380, this.field_15315.field_14376 + 1, class_2350.field_11035, i
					);
					class_3353.method_14711(
						arg, list, random, this.field_15315.field_14378 + 1, this.field_15315.field_14380, this.field_15315.field_14379 + 1, class_2350.field_11034, i
					);
			}

			if (this.field_14419) {
				if (random.nextBoolean()) {
					class_3353.method_14711(
						arg, list, random, this.field_15315.field_14381 + 1, this.field_15315.field_14380 + 3 + 1, this.field_15315.field_14379 - 1, class_2350.field_11043, i
					);
				}

				if (random.nextBoolean()) {
					class_3353.method_14711(
						arg, list, random, this.field_15315.field_14381 - 1, this.field_15315.field_14380 + 3 + 1, this.field_15315.field_14379 + 1, class_2350.field_11039, i
					);
				}

				if (random.nextBoolean()) {
					class_3353.method_14711(
						arg, list, random, this.field_15315.field_14378 + 1, this.field_15315.field_14380 + 3 + 1, this.field_15315.field_14379 + 1, class_2350.field_11034, i
					);
				}

				if (random.nextBoolean()) {
					class_3353.method_14711(
						arg, list, random, this.field_15315.field_14381 + 1, this.field_15315.field_14380 + 3 + 1, this.field_15315.field_14376 + 1, class_2350.field_11035, i
					);
				}
			}
		}

		@Override
		public boolean method_14931(class_1936 arg, Random random, class_3341 arg2, class_1923 arg3) {
			if (this.method_14937(arg, arg2)) {
				return false;
			} else {
				class_2680 lv = this.method_16443();
				if (this.field_14419) {
					this.method_14940(
						arg,
						arg2,
						this.field_15315.field_14381 + 1,
						this.field_15315.field_14380,
						this.field_15315.field_14379,
						this.field_15315.field_14378 - 1,
						this.field_15315.field_14380 + 3 - 1,
						this.field_15315.field_14376,
						field_15314,
						field_15314,
						false
					);
					this.method_14940(
						arg,
						arg2,
						this.field_15315.field_14381,
						this.field_15315.field_14380,
						this.field_15315.field_14379 + 1,
						this.field_15315.field_14378,
						this.field_15315.field_14380 + 3 - 1,
						this.field_15315.field_14376 - 1,
						field_15314,
						field_15314,
						false
					);
					this.method_14940(
						arg,
						arg2,
						this.field_15315.field_14381 + 1,
						this.field_15315.field_14377 - 2,
						this.field_15315.field_14379,
						this.field_15315.field_14378 - 1,
						this.field_15315.field_14377,
						this.field_15315.field_14376,
						field_15314,
						field_15314,
						false
					);
					this.method_14940(
						arg,
						arg2,
						this.field_15315.field_14381,
						this.field_15315.field_14377 - 2,
						this.field_15315.field_14379 + 1,
						this.field_15315.field_14378,
						this.field_15315.field_14377,
						this.field_15315.field_14376 - 1,
						field_15314,
						field_15314,
						false
					);
					this.method_14940(
						arg,
						arg2,
						this.field_15315.field_14381 + 1,
						this.field_15315.field_14380 + 3,
						this.field_15315.field_14379 + 1,
						this.field_15315.field_14378 - 1,
						this.field_15315.field_14380 + 3,
						this.field_15315.field_14376 - 1,
						field_15314,
						field_15314,
						false
					);
				} else {
					this.method_14940(
						arg,
						arg2,
						this.field_15315.field_14381 + 1,
						this.field_15315.field_14380,
						this.field_15315.field_14379,
						this.field_15315.field_14378 - 1,
						this.field_15315.field_14377,
						this.field_15315.field_14376,
						field_15314,
						field_15314,
						false
					);
					this.method_14940(
						arg,
						arg2,
						this.field_15315.field_14381,
						this.field_15315.field_14380,
						this.field_15315.field_14379 + 1,
						this.field_15315.field_14378,
						this.field_15315.field_14377,
						this.field_15315.field_14376 - 1,
						field_15314,
						field_15314,
						false
					);
				}

				this.method_14716(arg, arg2, this.field_15315.field_14381 + 1, this.field_15315.field_14380, this.field_15315.field_14379 + 1, this.field_15315.field_14377);
				this.method_14716(arg, arg2, this.field_15315.field_14381 + 1, this.field_15315.field_14380, this.field_15315.field_14376 - 1, this.field_15315.field_14377);
				this.method_14716(arg, arg2, this.field_15315.field_14378 - 1, this.field_15315.field_14380, this.field_15315.field_14379 + 1, this.field_15315.field_14377);
				this.method_14716(arg, arg2, this.field_15315.field_14378 - 1, this.field_15315.field_14380, this.field_15315.field_14376 - 1, this.field_15315.field_14377);

				for (int i = this.field_15315.field_14381; i <= this.field_15315.field_14378; i++) {
					for (int j = this.field_15315.field_14379; j <= this.field_15315.field_14376; j++) {
						if (this.method_14929(arg, i, this.field_15315.field_14380 - 1, j, arg2).method_11588()
							&& this.method_14939(arg, i, this.field_15315.field_14380 - 1, j, arg2)) {
							this.method_14917(arg, lv, i, this.field_15315.field_14380 - 1, j, arg2);
						}
					}
				}

				return true;
			}
		}

		private void method_14716(class_1936 arg, class_3341 arg2, int i, int j, int k, int l) {
			if (!this.method_14929(arg, i, l + 1, k, arg2).method_11588()) {
				this.method_14940(arg, arg2, i, j, k, i, l, k, this.method_16443(), field_15314, false);
			}
		}
	}

	abstract static class class_3356 extends class_3443 {
		protected class_3098.class_3100 field_14421;

		public class_3356(class_3773 arg, int i, class_3098.class_3100 arg2) {
			super(arg, i);
			this.field_14421 = arg2;
		}

		public class_3356(class_3773 arg, class_2487 arg2) {
			super(arg, arg2);
			this.field_14421 = class_3098.class_3100.method_13535(arg2.method_10550("MST"));
		}

		@Override
		protected void method_14943(class_2487 arg) {
			arg.method_10569("MST", this.field_14421.ordinal());
		}

		protected class_2680 method_16443() {
			switch (this.field_14421) {
				case field_13692:
				default:
					return class_2246.field_10161.method_9564();
				case field_13691:
					return class_2246.field_10075.method_9564();
			}
		}

		protected class_2680 method_14718() {
			switch (this.field_14421) {
				case field_13692:
				default:
					return class_2246.field_10620.method_9564();
				case field_13691:
					return class_2246.field_10132.method_9564();
			}
		}

		protected boolean method_14719(class_1922 arg, class_3341 arg2, int i, int j, int k, int l) {
			for (int m = i; m <= j; m++) {
				if (this.method_14929(arg, m, k + 1, l, arg2).method_11588()) {
					return false;
				}
			}

			return true;
		}
	}

	public static class class_3357 extends class_3353.class_3356 {
		private final List<class_3341> field_14422 = Lists.<class_3341>newLinkedList();

		public class_3357(int i, Random random, int j, int k, class_3098.class_3100 arg) {
			super(class_3773.field_16915, i, arg);
			this.field_14421 = arg;
			this.field_15315 = new class_3341(j, 50, k, j + 7 + random.nextInt(6), 54 + random.nextInt(6), k + 7 + random.nextInt(6));
		}

		public class_3357(class_3485 arg, class_2487 arg2) {
			super(class_3773.field_16915, arg2);
			class_2499 lv = arg2.method_10554("Entrances", 11);

			for (int i = 0; i < lv.size(); i++) {
				this.field_14422.add(new class_3341(lv.method_10610(i)));
			}
		}

		@Override
		public void method_14918(class_3443 arg, List<class_3443> list, Random random) {
			int i = this.method_14923();
			int j = this.field_15315.method_14663() - 3 - 1;
			if (j <= 0) {
				j = 1;
			}

			int k = 0;

			while (k < this.field_15315.method_14660()) {
				k += random.nextInt(this.field_15315.method_14660());
				if (k + 3 > this.field_15315.method_14660()) {
					break;
				}

				class_3353.class_3356 lv = class_3353.method_14711(
					arg,
					list,
					random,
					this.field_15315.field_14381 + k,
					this.field_15315.field_14380 + random.nextInt(j) + 1,
					this.field_15315.field_14379 - 1,
					class_2350.field_11043,
					i
				);
				if (lv != null) {
					class_3341 lv2 = lv.method_14935();
					this.field_14422
						.add(new class_3341(lv2.field_14381, lv2.field_14380, this.field_15315.field_14379, lv2.field_14378, lv2.field_14377, this.field_15315.field_14379 + 1));
				}

				k += 4;
			}

			k = 0;

			while (k < this.field_15315.method_14660()) {
				k += random.nextInt(this.field_15315.method_14660());
				if (k + 3 > this.field_15315.method_14660()) {
					break;
				}

				class_3353.class_3356 lv = class_3353.method_14711(
					arg,
					list,
					random,
					this.field_15315.field_14381 + k,
					this.field_15315.field_14380 + random.nextInt(j) + 1,
					this.field_15315.field_14376 + 1,
					class_2350.field_11035,
					i
				);
				if (lv != null) {
					class_3341 lv2 = lv.method_14935();
					this.field_14422
						.add(new class_3341(lv2.field_14381, lv2.field_14380, this.field_15315.field_14376 - 1, lv2.field_14378, lv2.field_14377, this.field_15315.field_14376));
				}

				k += 4;
			}

			k = 0;

			while (k < this.field_15315.method_14664()) {
				k += random.nextInt(this.field_15315.method_14664());
				if (k + 3 > this.field_15315.method_14664()) {
					break;
				}

				class_3353.class_3356 lv = class_3353.method_14711(
					arg,
					list,
					random,
					this.field_15315.field_14381 - 1,
					this.field_15315.field_14380 + random.nextInt(j) + 1,
					this.field_15315.field_14379 + k,
					class_2350.field_11039,
					i
				);
				if (lv != null) {
					class_3341 lv2 = lv.method_14935();
					this.field_14422
						.add(new class_3341(this.field_15315.field_14381, lv2.field_14380, lv2.field_14379, this.field_15315.field_14381 + 1, lv2.field_14377, lv2.field_14376));
				}

				k += 4;
			}

			k = 0;

			while (k < this.field_15315.method_14664()) {
				k += random.nextInt(this.field_15315.method_14664());
				if (k + 3 > this.field_15315.method_14664()) {
					break;
				}

				class_3443 lv3 = class_3353.method_14711(
					arg,
					list,
					random,
					this.field_15315.field_14378 + 1,
					this.field_15315.field_14380 + random.nextInt(j) + 1,
					this.field_15315.field_14379 + k,
					class_2350.field_11034,
					i
				);
				if (lv3 != null) {
					class_3341 lv2 = lv3.method_14935();
					this.field_14422
						.add(new class_3341(this.field_15315.field_14378 - 1, lv2.field_14380, lv2.field_14379, this.field_15315.field_14378, lv2.field_14377, lv2.field_14376));
				}

				k += 4;
			}
		}

		@Override
		public boolean method_14931(class_1936 arg, Random random, class_3341 arg2, class_1923 arg3) {
			if (this.method_14937(arg, arg2)) {
				return false;
			} else {
				this.method_14940(
					arg,
					arg2,
					this.field_15315.field_14381,
					this.field_15315.field_14380,
					this.field_15315.field_14379,
					this.field_15315.field_14378,
					this.field_15315.field_14380,
					this.field_15315.field_14376,
					class_2246.field_10566.method_9564(),
					field_15314,
					true
				);
				this.method_14940(
					arg,
					arg2,
					this.field_15315.field_14381,
					this.field_15315.field_14380 + 1,
					this.field_15315.field_14379,
					this.field_15315.field_14378,
					Math.min(this.field_15315.field_14380 + 3, this.field_15315.field_14377),
					this.field_15315.field_14376,
					field_15314,
					field_15314,
					false
				);

				for (class_3341 lv : this.field_14422) {
					this.method_14940(
						arg, arg2, lv.field_14381, lv.field_14377 - 2, lv.field_14379, lv.field_14378, lv.field_14377, lv.field_14376, field_15314, field_15314, false
					);
				}

				this.method_14919(
					arg,
					arg2,
					this.field_15315.field_14381,
					this.field_15315.field_14380 + 4,
					this.field_15315.field_14379,
					this.field_15315.field_14378,
					this.field_15315.field_14377,
					this.field_15315.field_14376,
					field_15314,
					false
				);
				return true;
			}
		}

		@Override
		public void method_14922(int i, int j, int k) {
			super.method_14922(i, j, k);

			for (class_3341 lv : this.field_14422) {
				lv.method_14661(i, j, k);
			}
		}

		@Override
		protected void method_14943(class_2487 arg) {
			super.method_14943(arg);
			class_2499 lv = new class_2499();

			for (class_3341 lv2 : this.field_14422) {
				lv.add(lv2.method_14658());
			}

			arg.method_10566("Entrances", lv);
		}
	}

	public static class class_3358 extends class_3353.class_3356 {
		public class_3358(int i, class_3341 arg, class_2350 arg2, class_3098.class_3100 arg3) {
			super(class_3773.field_16968, i, arg3);
			this.method_14926(arg2);
			this.field_15315 = arg;
		}

		public class_3358(class_3485 arg, class_2487 arg2) {
			super(class_3773.field_16968, arg2);
		}

		public static class_3341 method_14720(List<class_3443> list, Random random, int i, int j, int k, class_2350 arg) {
			class_3341 lv = new class_3341(i, j - 5, k, i, j + 3 - 1, k);
			switch (arg) {
				case field_11043:
				default:
					lv.field_14378 = i + 3 - 1;
					lv.field_14379 = k - 8;
					break;
				case field_11035:
					lv.field_14378 = i + 3 - 1;
					lv.field_14376 = k + 8;
					break;
				case field_11039:
					lv.field_14381 = i - 8;
					lv.field_14376 = k + 3 - 1;
					break;
				case field_11034:
					lv.field_14378 = i + 8;
					lv.field_14376 = k + 3 - 1;
			}

			return class_3443.method_14932(list, lv) != null ? null : lv;
		}

		@Override
		public void method_14918(class_3443 arg, List<class_3443> list, Random random) {
			int i = this.method_14923();
			class_2350 lv = this.method_14934();
			if (lv != null) {
				switch (lv) {
					case field_11043:
					default:
						class_3353.method_14711(
							arg, list, random, this.field_15315.field_14381, this.field_15315.field_14380, this.field_15315.field_14379 - 1, class_2350.field_11043, i
						);
						break;
					case field_11035:
						class_3353.method_14711(
							arg, list, random, this.field_15315.field_14381, this.field_15315.field_14380, this.field_15315.field_14376 + 1, class_2350.field_11035, i
						);
						break;
					case field_11039:
						class_3353.method_14711(
							arg, list, random, this.field_15315.field_14381 - 1, this.field_15315.field_14380, this.field_15315.field_14379, class_2350.field_11039, i
						);
						break;
					case field_11034:
						class_3353.method_14711(
							arg, list, random, this.field_15315.field_14378 + 1, this.field_15315.field_14380, this.field_15315.field_14379, class_2350.field_11034, i
						);
				}
			}
		}

		@Override
		public boolean method_14931(class_1936 arg, Random random, class_3341 arg2, class_1923 arg3) {
			if (this.method_14937(arg, arg2)) {
				return false;
			} else {
				this.method_14940(arg, arg2, 0, 5, 0, 2, 7, 1, field_15314, field_15314, false);
				this.method_14940(arg, arg2, 0, 0, 7, 2, 2, 8, field_15314, field_15314, false);

				for (int i = 0; i < 5; i++) {
					this.method_14940(arg, arg2, 0, 5 - i - (i < 4 ? 1 : 0), 2 + i, 2, 7 - i, 2 + i, field_15314, field_15314, false);
				}

				return true;
			}
		}
	}
}
