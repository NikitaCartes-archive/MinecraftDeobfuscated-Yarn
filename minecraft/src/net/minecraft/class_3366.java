package net.minecraft;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.Set;

public class class_3366 {
	static class class_3367 implements class_3366.class_3375 {
		private class_3367() {
		}

		@Override
		public boolean method_14769(class_3366.class_3388 arg) {
			return arg.field_14482[class_2350.field_11034.method_10146()] && !arg.field_14487[class_2350.field_11034.method_10146()].field_14485;
		}

		@Override
		public class_3366.class_3384 method_14768(class_2350 arg, class_3366.class_3388 arg2, Random random) {
			arg2.field_14485 = true;
			arg2.field_14487[class_2350.field_11034.method_10146()].field_14485 = true;
			return new class_3366.class_3377(arg, arg2);
		}
	}

	static class class_3368 implements class_3366.class_3375 {
		private class_3368() {
		}

		@Override
		public boolean method_14769(class_3366.class_3388 arg) {
			if (arg.field_14482[class_2350.field_11034.method_10146()]
				&& !arg.field_14487[class_2350.field_11034.method_10146()].field_14485
				&& arg.field_14482[class_2350.field_11036.method_10146()]
				&& !arg.field_14487[class_2350.field_11036.method_10146()].field_14485) {
				class_3366.class_3388 lv = arg.field_14487[class_2350.field_11034.method_10146()];
				return lv.field_14482[class_2350.field_11036.method_10146()] && !lv.field_14487[class_2350.field_11036.method_10146()].field_14485;
			} else {
				return false;
			}
		}

		@Override
		public class_3366.class_3384 method_14768(class_2350 arg, class_3366.class_3388 arg2, Random random) {
			arg2.field_14485 = true;
			arg2.field_14487[class_2350.field_11034.method_10146()].field_14485 = true;
			arg2.field_14487[class_2350.field_11036.method_10146()].field_14485 = true;
			arg2.field_14487[class_2350.field_11034.method_10146()].field_14487[class_2350.field_11036.method_10146()].field_14485 = true;
			return new class_3366.class_3378(arg, arg2);
		}
	}

	static class class_3369 implements class_3366.class_3375 {
		private class_3369() {
		}

		@Override
		public boolean method_14769(class_3366.class_3388 arg) {
			return arg.field_14482[class_2350.field_11036.method_10146()] && !arg.field_14487[class_2350.field_11036.method_10146()].field_14485;
		}

		@Override
		public class_3366.class_3384 method_14768(class_2350 arg, class_3366.class_3388 arg2, Random random) {
			arg2.field_14485 = true;
			arg2.field_14487[class_2350.field_11036.method_10146()].field_14485 = true;
			return new class_3366.class_3379(arg, arg2);
		}
	}

	static class class_3370 implements class_3366.class_3375 {
		private class_3370() {
		}

		@Override
		public boolean method_14769(class_3366.class_3388 arg) {
			if (arg.field_14482[class_2350.field_11043.method_10146()]
				&& !arg.field_14487[class_2350.field_11043.method_10146()].field_14485
				&& arg.field_14482[class_2350.field_11036.method_10146()]
				&& !arg.field_14487[class_2350.field_11036.method_10146()].field_14485) {
				class_3366.class_3388 lv = arg.field_14487[class_2350.field_11043.method_10146()];
				return lv.field_14482[class_2350.field_11036.method_10146()] && !lv.field_14487[class_2350.field_11036.method_10146()].field_14485;
			} else {
				return false;
			}
		}

		@Override
		public class_3366.class_3384 method_14768(class_2350 arg, class_3366.class_3388 arg2, Random random) {
			arg2.field_14485 = true;
			arg2.field_14487[class_2350.field_11043.method_10146()].field_14485 = true;
			arg2.field_14487[class_2350.field_11036.method_10146()].field_14485 = true;
			arg2.field_14487[class_2350.field_11043.method_10146()].field_14487[class_2350.field_11036.method_10146()].field_14485 = true;
			return new class_3366.class_3380(arg, arg2);
		}
	}

	static class class_3371 implements class_3366.class_3375 {
		private class_3371() {
		}

		@Override
		public boolean method_14769(class_3366.class_3388 arg) {
			return arg.field_14482[class_2350.field_11043.method_10146()] && !arg.field_14487[class_2350.field_11043.method_10146()].field_14485;
		}

		@Override
		public class_3366.class_3384 method_14768(class_2350 arg, class_3366.class_3388 arg2, Random random) {
			class_3366.class_3388 lv = arg2;
			if (!arg2.field_14482[class_2350.field_11043.method_10146()] || arg2.field_14487[class_2350.field_11043.method_10146()].field_14485) {
				lv = arg2.field_14487[class_2350.field_11035.method_10146()];
			}

			lv.field_14485 = true;
			lv.field_14487[class_2350.field_11043.method_10146()].field_14485 = true;
			return new class_3366.class_3381(arg, lv);
		}
	}

	static class class_3372 implements class_3366.class_3375 {
		private class_3372() {
		}

		@Override
		public boolean method_14769(class_3366.class_3388 arg) {
			return true;
		}

		@Override
		public class_3366.class_3384 method_14768(class_2350 arg, class_3366.class_3388 arg2, Random random) {
			arg2.field_14485 = true;
			return new class_3366.class_3385(arg, arg2, random);
		}
	}

	static class class_3373 implements class_3366.class_3375 {
		private class_3373() {
		}

		@Override
		public boolean method_14769(class_3366.class_3388 arg) {
			return !arg.field_14482[class_2350.field_11039.method_10146()]
				&& !arg.field_14482[class_2350.field_11034.method_10146()]
				&& !arg.field_14482[class_2350.field_11043.method_10146()]
				&& !arg.field_14482[class_2350.field_11035.method_10146()]
				&& !arg.field_14482[class_2350.field_11036.method_10146()];
		}

		@Override
		public class_3366.class_3384 method_14768(class_2350 arg, class_3366.class_3388 arg2, Random random) {
			arg2.field_14485 = true;
			return new class_3366.class_3386(arg, arg2);
		}
	}

	public static class class_3374 extends class_3366.class_3384 {
		private class_3366.class_3388 field_14464;
		private class_3366.class_3388 field_14466;
		private final List<class_3366.class_3384> field_14465 = Lists.<class_3366.class_3384>newArrayList();

		public class_3374(Random random, int i, int j, class_2350 arg) {
			super(class_3773.field_16922, 0);
			this.method_14926(arg);
			class_2350 lv = this.method_14934();
			if (lv.method_10166() == class_2350.class_2351.field_11051) {
				this.field_15315 = new class_3341(i, 39, j, i + 58 - 1, 61, j + 58 - 1);
			} else {
				this.field_15315 = new class_3341(i, 39, j, i + 58 - 1, 61, j + 58 - 1);
			}

			List<class_3366.class_3388> list = this.method_14760(random);
			this.field_14464.field_14485 = true;
			this.field_14465.add(new class_3366.class_3382(lv, this.field_14464));
			this.field_14465.add(new class_3366.class_3376(lv, this.field_14466));
			List<class_3366.class_3375> list2 = Lists.<class_3366.class_3375>newArrayList();
			list2.add(new class_3366.class_3368());
			list2.add(new class_3366.class_3370());
			list2.add(new class_3366.class_3371());
			list2.add(new class_3366.class_3367());
			list2.add(new class_3366.class_3369());
			list2.add(new class_3366.class_3373());
			list2.add(new class_3366.class_3372());

			for (class_3366.class_3388 lv2 : list) {
				if (!lv2.field_14485 && !lv2.method_14785()) {
					for (class_3366.class_3375 lv3 : list2) {
						if (lv3.method_14769(lv2)) {
							this.field_14465.add(lv3.method_14768(lv, lv2, random));
							break;
						}
					}
				}
			}

			int k = this.field_15315.field_14380;
			int l = this.method_14928(9, 22);
			int m = this.method_14941(9, 22);

			for (class_3366.class_3384 lv4 : this.field_14465) {
				lv4.method_14935().method_14661(l, k, m);
			}

			class_3341 lv5 = class_3341.method_14666(
				this.method_14928(1, 1), this.method_14924(1), this.method_14941(1, 1), this.method_14928(23, 21), this.method_14924(8), this.method_14941(23, 21)
			);
			class_3341 lv6 = class_3341.method_14666(
				this.method_14928(34, 1), this.method_14924(1), this.method_14941(34, 1), this.method_14928(56, 21), this.method_14924(8), this.method_14941(56, 21)
			);
			class_3341 lv7 = class_3341.method_14666(
				this.method_14928(22, 22), this.method_14924(13), this.method_14941(22, 22), this.method_14928(35, 35), this.method_14924(17), this.method_14941(35, 35)
			);
			int n = random.nextInt();
			this.field_14465.add(new class_3366.class_3387(lv, lv5, n++));
			this.field_14465.add(new class_3366.class_3387(lv, lv6, n++));
			this.field_14465.add(new class_3366.class_3383(lv, lv7));
		}

		public class_3374(class_3485 arg, class_2487 arg2) {
			super(class_3773.field_16922, arg2);
		}

		private List<class_3366.class_3388> method_14760(Random random) {
			class_3366.class_3388[] lvs = new class_3366.class_3388[75];

			for (int i = 0; i < 5; i++) {
				for (int j = 0; j < 4; j++) {
					int k = 0;
					int l = method_14770(i, 0, j);
					lvs[l] = new class_3366.class_3388(l);
				}
			}

			for (int i = 0; i < 5; i++) {
				for (int j = 0; j < 4; j++) {
					int k = 1;
					int l = method_14770(i, 1, j);
					lvs[l] = new class_3366.class_3388(l);
				}
			}

			for (int i = 1; i < 4; i++) {
				for (int j = 0; j < 2; j++) {
					int k = 2;
					int l = method_14770(i, 2, j);
					lvs[l] = new class_3366.class_3388(l);
				}
			}

			this.field_14464 = lvs[field_14469];

			for (int i = 0; i < 5; i++) {
				for (int j = 0; j < 5; j++) {
					for (int k = 0; k < 3; k++) {
						int l = method_14770(i, k, j);
						if (lvs[l] != null) {
							for (class_2350 lv : class_2350.values()) {
								int m = i + lv.method_10148();
								int n = k + lv.method_10164();
								int o = j + lv.method_10165();
								if (m >= 0 && m < 5 && o >= 0 && o < 5 && n >= 0 && n < 3) {
									int p = method_14770(m, n, o);
									if (lvs[p] != null) {
										if (o == j) {
											lvs[l].method_14786(lv, lvs[p]);
										} else {
											lvs[l].method_14786(lv.method_10153(), lvs[p]);
										}
									}
								}
							}
						}
					}
				}
			}

			class_3366.class_3388 lv2 = new class_3366.class_3388(1003);
			class_3366.class_3388 lv3 = new class_3366.class_3388(1001);
			class_3366.class_3388 lv4 = new class_3366.class_3388(1002);
			lvs[field_14468].method_14786(class_2350.field_11036, lv2);
			lvs[field_14478].method_14786(class_2350.field_11035, lv3);
			lvs[field_14477].method_14786(class_2350.field_11035, lv4);
			lv2.field_14485 = true;
			lv3.field_14485 = true;
			lv4.field_14485 = true;
			this.field_14464.field_14484 = true;
			this.field_14466 = lvs[method_14770(random.nextInt(4), 0, 2)];
			this.field_14466.field_14485 = true;
			this.field_14466.field_14487[class_2350.field_11034.method_10146()].field_14485 = true;
			this.field_14466.field_14487[class_2350.field_11043.method_10146()].field_14485 = true;
			this.field_14466.field_14487[class_2350.field_11034.method_10146()].field_14487[class_2350.field_11043.method_10146()].field_14485 = true;
			this.field_14466.field_14487[class_2350.field_11036.method_10146()].field_14485 = true;
			this.field_14466.field_14487[class_2350.field_11034.method_10146()].field_14487[class_2350.field_11036.method_10146()].field_14485 = true;
			this.field_14466.field_14487[class_2350.field_11043.method_10146()].field_14487[class_2350.field_11036.method_10146()].field_14485 = true;
			this.field_14466.field_14487[class_2350.field_11034.method_10146()].field_14487[class_2350.field_11043.method_10146()].field_14487[class_2350.field_11036
					.method_10146()]
				.field_14485 = true;
			List<class_3366.class_3388> list = Lists.<class_3366.class_3388>newArrayList();

			for (class_3366.class_3388 lv5 : lvs) {
				if (lv5 != null) {
					lv5.method_14780();
					list.add(lv5);
				}
			}

			lv2.method_14780();
			Collections.shuffle(list, random);
			int q = 1;

			for (class_3366.class_3388 lv6 : list) {
				int r = 0;
				int m = 0;

				while (r < 2 && m < 5) {
					m++;
					int n = random.nextInt(6);
					if (lv6.field_14482[n]) {
						int o = class_2350.method_10143(n).method_10153().method_10146();
						lv6.field_14482[n] = false;
						lv6.field_14487[n].field_14482[o] = false;
						if (lv6.method_14783(q++) && lv6.field_14487[n].method_14783(q++)) {
							r++;
						} else {
							lv6.field_14482[n] = true;
							lv6.field_14487[n].field_14482[o] = true;
						}
					}
				}
			}

			list.add(lv2);
			list.add(lv3);
			list.add(lv4);
			return list;
		}

		@Override
		public boolean method_14931(class_1936 arg, Random random, class_3341 arg2, class_1923 arg3) {
			int i = Math.max(arg.method_8615(), 64) - this.field_15315.field_14380;
			this.method_14773(arg, arg2, 0, 0, 0, 58, i, 58);
			this.method_14761(false, 0, arg, random, arg2);
			this.method_14761(true, 33, arg, random, arg2);
			this.method_14763(arg, random, arg2);
			this.method_14762(arg, random, arg2);
			this.method_14765(arg, random, arg2);
			this.method_14764(arg, random, arg2);
			this.method_14766(arg, random, arg2);
			this.method_14767(arg, random, arg2);

			for (int j = 0; j < 7; j++) {
				int k = 0;

				while (k < 7) {
					if (k == 0 && j == 3) {
						k = 6;
					}

					int l = j * 9;
					int m = k * 9;

					for (int n = 0; n < 4; n++) {
						for (int o = 0; o < 4; o++) {
							this.method_14917(arg, field_14476, l + n, 0, m + o, arg2);
							this.method_14936(arg, field_14476, l + n, -1, m + o, arg2);
						}
					}

					if (j != 0 && j != 6) {
						k += 6;
					} else {
						k++;
					}
				}
			}

			for (int j = 0; j < 5; j++) {
				this.method_14773(arg, arg2, -1 - j, 0 + j * 2, -1 - j, -1 - j, 23, 58 + j);
				this.method_14773(arg, arg2, 58 + j, 0 + j * 2, -1 - j, 58 + j, 23, 58 + j);
				this.method_14773(arg, arg2, 0 - j, 0 + j * 2, -1 - j, 57 + j, 23, -1 - j);
				this.method_14773(arg, arg2, 0 - j, 0 + j * 2, 58 + j, 57 + j, 23, 58 + j);
			}

			for (class_3366.class_3384 lv : this.field_14465) {
				if (lv.method_14935().method_14657(arg2)) {
					lv.method_14931(arg, random, arg2, arg3);
				}
			}

			return true;
		}

		private void method_14761(boolean bl, int i, class_1936 arg, Random random, class_3341 arg2) {
			int j = 24;
			if (this.method_14775(arg2, i, 0, i + 23, 20)) {
				this.method_14940(arg, arg2, i + 0, 0, 0, i + 24, 0, 20, field_14473, field_14473, false);
				this.method_14773(arg, arg2, i + 0, 1, 0, i + 24, 10, 20);

				for (int k = 0; k < 4; k++) {
					this.method_14940(arg, arg2, i + k, k + 1, k, i + k, k + 1, 20, field_14476, field_14476, false);
					this.method_14940(arg, arg2, i + k + 7, k + 5, k + 7, i + k + 7, k + 5, 20, field_14476, field_14476, false);
					this.method_14940(arg, arg2, i + 17 - k, k + 5, k + 7, i + 17 - k, k + 5, 20, field_14476, field_14476, false);
					this.method_14940(arg, arg2, i + 24 - k, k + 1, k, i + 24 - k, k + 1, 20, field_14476, field_14476, false);
					this.method_14940(arg, arg2, i + k + 1, k + 1, k, i + 23 - k, k + 1, k, field_14476, field_14476, false);
					this.method_14940(arg, arg2, i + k + 8, k + 5, k + 7, i + 16 - k, k + 5, k + 7, field_14476, field_14476, false);
				}

				this.method_14940(arg, arg2, i + 4, 4, 4, i + 6, 4, 20, field_14473, field_14473, false);
				this.method_14940(arg, arg2, i + 7, 4, 4, i + 17, 4, 6, field_14473, field_14473, false);
				this.method_14940(arg, arg2, i + 18, 4, 4, i + 20, 4, 20, field_14473, field_14473, false);
				this.method_14940(arg, arg2, i + 11, 8, 11, i + 13, 8, 20, field_14473, field_14473, false);
				this.method_14917(arg, field_14470, i + 12, 9, 12, arg2);
				this.method_14917(arg, field_14470, i + 12, 9, 15, arg2);
				this.method_14917(arg, field_14470, i + 12, 9, 18, arg2);
				int k = i + (bl ? 19 : 5);
				int l = i + (bl ? 5 : 19);

				for (int m = 20; m >= 5; m -= 3) {
					this.method_14917(arg, field_14470, k, 5, m, arg2);
				}

				for (int m = 19; m >= 7; m -= 3) {
					this.method_14917(arg, field_14470, l, 5, m, arg2);
				}

				for (int m = 0; m < 4; m++) {
					int n = bl ? i + 24 - (17 - m * 3) : i + 17 - m * 3;
					this.method_14917(arg, field_14470, n, 5, 5, arg2);
				}

				this.method_14917(arg, field_14470, l, 5, 5, arg2);
				this.method_14940(arg, arg2, i + 11, 1, 12, i + 13, 7, 12, field_14473, field_14473, false);
				this.method_14940(arg, arg2, i + 12, 1, 11, i + 12, 7, 13, field_14473, field_14473, false);
			}
		}

		private void method_14763(class_1936 arg, Random random, class_3341 arg2) {
			if (this.method_14775(arg2, 22, 5, 35, 17)) {
				this.method_14773(arg, arg2, 25, 0, 0, 32, 8, 20);

				for (int i = 0; i < 4; i++) {
					this.method_14940(arg, arg2, 24, 2, 5 + i * 4, 24, 4, 5 + i * 4, field_14476, field_14476, false);
					this.method_14940(arg, arg2, 22, 4, 5 + i * 4, 23, 4, 5 + i * 4, field_14476, field_14476, false);
					this.method_14917(arg, field_14476, 25, 5, 5 + i * 4, arg2);
					this.method_14917(arg, field_14476, 26, 6, 5 + i * 4, arg2);
					this.method_14917(arg, field_14471, 26, 5, 5 + i * 4, arg2);
					this.method_14940(arg, arg2, 33, 2, 5 + i * 4, 33, 4, 5 + i * 4, field_14476, field_14476, false);
					this.method_14940(arg, arg2, 34, 4, 5 + i * 4, 35, 4, 5 + i * 4, field_14476, field_14476, false);
					this.method_14917(arg, field_14476, 32, 5, 5 + i * 4, arg2);
					this.method_14917(arg, field_14476, 31, 6, 5 + i * 4, arg2);
					this.method_14917(arg, field_14471, 31, 5, 5 + i * 4, arg2);
					this.method_14940(arg, arg2, 27, 6, 5 + i * 4, 30, 6, 5 + i * 4, field_14473, field_14473, false);
				}
			}
		}

		private void method_14762(class_1936 arg, Random random, class_3341 arg2) {
			if (this.method_14775(arg2, 15, 20, 42, 21)) {
				this.method_14940(arg, arg2, 15, 0, 21, 42, 0, 21, field_14473, field_14473, false);
				this.method_14773(arg, arg2, 26, 1, 21, 31, 3, 21);
				this.method_14940(arg, arg2, 21, 12, 21, 36, 12, 21, field_14473, field_14473, false);
				this.method_14940(arg, arg2, 17, 11, 21, 40, 11, 21, field_14473, field_14473, false);
				this.method_14940(arg, arg2, 16, 10, 21, 41, 10, 21, field_14473, field_14473, false);
				this.method_14940(arg, arg2, 15, 7, 21, 42, 9, 21, field_14473, field_14473, false);
				this.method_14940(arg, arg2, 16, 6, 21, 41, 6, 21, field_14473, field_14473, false);
				this.method_14940(arg, arg2, 17, 5, 21, 40, 5, 21, field_14473, field_14473, false);
				this.method_14940(arg, arg2, 21, 4, 21, 36, 4, 21, field_14473, field_14473, false);
				this.method_14940(arg, arg2, 22, 3, 21, 26, 3, 21, field_14473, field_14473, false);
				this.method_14940(arg, arg2, 31, 3, 21, 35, 3, 21, field_14473, field_14473, false);
				this.method_14940(arg, arg2, 23, 2, 21, 25, 2, 21, field_14473, field_14473, false);
				this.method_14940(arg, arg2, 32, 2, 21, 34, 2, 21, field_14473, field_14473, false);
				this.method_14940(arg, arg2, 28, 4, 20, 29, 4, 21, field_14476, field_14476, false);
				this.method_14917(arg, field_14476, 27, 3, 21, arg2);
				this.method_14917(arg, field_14476, 30, 3, 21, arg2);
				this.method_14917(arg, field_14476, 26, 2, 21, arg2);
				this.method_14917(arg, field_14476, 31, 2, 21, arg2);
				this.method_14917(arg, field_14476, 25, 1, 21, arg2);
				this.method_14917(arg, field_14476, 32, 1, 21, arg2);

				for (int i = 0; i < 7; i++) {
					this.method_14917(arg, field_14474, 28 - i, 6 + i, 21, arg2);
					this.method_14917(arg, field_14474, 29 + i, 6 + i, 21, arg2);
				}

				for (int i = 0; i < 4; i++) {
					this.method_14917(arg, field_14474, 28 - i, 9 + i, 21, arg2);
					this.method_14917(arg, field_14474, 29 + i, 9 + i, 21, arg2);
				}

				this.method_14917(arg, field_14474, 28, 12, 21, arg2);
				this.method_14917(arg, field_14474, 29, 12, 21, arg2);

				for (int i = 0; i < 3; i++) {
					this.method_14917(arg, field_14474, 22 - i * 2, 8, 21, arg2);
					this.method_14917(arg, field_14474, 22 - i * 2, 9, 21, arg2);
					this.method_14917(arg, field_14474, 35 + i * 2, 8, 21, arg2);
					this.method_14917(arg, field_14474, 35 + i * 2, 9, 21, arg2);
				}

				this.method_14773(arg, arg2, 15, 13, 21, 42, 15, 21);
				this.method_14773(arg, arg2, 15, 1, 21, 15, 6, 21);
				this.method_14773(arg, arg2, 16, 1, 21, 16, 5, 21);
				this.method_14773(arg, arg2, 17, 1, 21, 20, 4, 21);
				this.method_14773(arg, arg2, 21, 1, 21, 21, 3, 21);
				this.method_14773(arg, arg2, 22, 1, 21, 22, 2, 21);
				this.method_14773(arg, arg2, 23, 1, 21, 24, 1, 21);
				this.method_14773(arg, arg2, 42, 1, 21, 42, 6, 21);
				this.method_14773(arg, arg2, 41, 1, 21, 41, 5, 21);
				this.method_14773(arg, arg2, 37, 1, 21, 40, 4, 21);
				this.method_14773(arg, arg2, 36, 1, 21, 36, 3, 21);
				this.method_14773(arg, arg2, 33, 1, 21, 34, 1, 21);
				this.method_14773(arg, arg2, 35, 1, 21, 35, 2, 21);
			}
		}

		private void method_14765(class_1936 arg, Random random, class_3341 arg2) {
			if (this.method_14775(arg2, 21, 21, 36, 36)) {
				this.method_14940(arg, arg2, 21, 0, 22, 36, 0, 36, field_14473, field_14473, false);
				this.method_14773(arg, arg2, 21, 1, 22, 36, 23, 36);

				for (int i = 0; i < 4; i++) {
					this.method_14940(arg, arg2, 21 + i, 13 + i, 21 + i, 36 - i, 13 + i, 21 + i, field_14476, field_14476, false);
					this.method_14940(arg, arg2, 21 + i, 13 + i, 36 - i, 36 - i, 13 + i, 36 - i, field_14476, field_14476, false);
					this.method_14940(arg, arg2, 21 + i, 13 + i, 22 + i, 21 + i, 13 + i, 35 - i, field_14476, field_14476, false);
					this.method_14940(arg, arg2, 36 - i, 13 + i, 22 + i, 36 - i, 13 + i, 35 - i, field_14476, field_14476, false);
				}

				this.method_14940(arg, arg2, 25, 16, 25, 32, 16, 32, field_14473, field_14473, false);
				this.method_14940(arg, arg2, 25, 17, 25, 25, 19, 25, field_14476, field_14476, false);
				this.method_14940(arg, arg2, 32, 17, 25, 32, 19, 25, field_14476, field_14476, false);
				this.method_14940(arg, arg2, 25, 17, 32, 25, 19, 32, field_14476, field_14476, false);
				this.method_14940(arg, arg2, 32, 17, 32, 32, 19, 32, field_14476, field_14476, false);
				this.method_14917(arg, field_14476, 26, 20, 26, arg2);
				this.method_14917(arg, field_14476, 27, 21, 27, arg2);
				this.method_14917(arg, field_14471, 27, 20, 27, arg2);
				this.method_14917(arg, field_14476, 26, 20, 31, arg2);
				this.method_14917(arg, field_14476, 27, 21, 30, arg2);
				this.method_14917(arg, field_14471, 27, 20, 30, arg2);
				this.method_14917(arg, field_14476, 31, 20, 31, arg2);
				this.method_14917(arg, field_14476, 30, 21, 30, arg2);
				this.method_14917(arg, field_14471, 30, 20, 30, arg2);
				this.method_14917(arg, field_14476, 31, 20, 26, arg2);
				this.method_14917(arg, field_14476, 30, 21, 27, arg2);
				this.method_14917(arg, field_14471, 30, 20, 27, arg2);
				this.method_14940(arg, arg2, 28, 21, 27, 29, 21, 27, field_14473, field_14473, false);
				this.method_14940(arg, arg2, 27, 21, 28, 27, 21, 29, field_14473, field_14473, false);
				this.method_14940(arg, arg2, 28, 21, 30, 29, 21, 30, field_14473, field_14473, false);
				this.method_14940(arg, arg2, 30, 21, 28, 30, 21, 29, field_14473, field_14473, false);
			}
		}

		private void method_14764(class_1936 arg, Random random, class_3341 arg2) {
			if (this.method_14775(arg2, 0, 21, 6, 58)) {
				this.method_14940(arg, arg2, 0, 0, 21, 6, 0, 57, field_14473, field_14473, false);
				this.method_14773(arg, arg2, 0, 1, 21, 6, 7, 57);
				this.method_14940(arg, arg2, 4, 4, 21, 6, 4, 53, field_14473, field_14473, false);

				for (int i = 0; i < 4; i++) {
					this.method_14940(arg, arg2, i, i + 1, 21, i, i + 1, 57 - i, field_14476, field_14476, false);
				}

				for (int i = 23; i < 53; i += 3) {
					this.method_14917(arg, field_14470, 5, 5, i, arg2);
				}

				this.method_14917(arg, field_14470, 5, 5, 52, arg2);

				for (int i = 0; i < 4; i++) {
					this.method_14940(arg, arg2, i, i + 1, 21, i, i + 1, 57 - i, field_14476, field_14476, false);
				}

				this.method_14940(arg, arg2, 4, 1, 52, 6, 3, 52, field_14473, field_14473, false);
				this.method_14940(arg, arg2, 5, 1, 51, 5, 3, 53, field_14473, field_14473, false);
			}

			if (this.method_14775(arg2, 51, 21, 58, 58)) {
				this.method_14940(arg, arg2, 51, 0, 21, 57, 0, 57, field_14473, field_14473, false);
				this.method_14773(arg, arg2, 51, 1, 21, 57, 7, 57);
				this.method_14940(arg, arg2, 51, 4, 21, 53, 4, 53, field_14473, field_14473, false);

				for (int i = 0; i < 4; i++) {
					this.method_14940(arg, arg2, 57 - i, i + 1, 21, 57 - i, i + 1, 57 - i, field_14476, field_14476, false);
				}

				for (int i = 23; i < 53; i += 3) {
					this.method_14917(arg, field_14470, 52, 5, i, arg2);
				}

				this.method_14917(arg, field_14470, 52, 5, 52, arg2);
				this.method_14940(arg, arg2, 51, 1, 52, 53, 3, 52, field_14473, field_14473, false);
				this.method_14940(arg, arg2, 52, 1, 51, 52, 3, 53, field_14473, field_14473, false);
			}

			if (this.method_14775(arg2, 0, 51, 57, 57)) {
				this.method_14940(arg, arg2, 7, 0, 51, 50, 0, 57, field_14473, field_14473, false);
				this.method_14773(arg, arg2, 7, 1, 51, 50, 10, 57);

				for (int i = 0; i < 4; i++) {
					this.method_14940(arg, arg2, i + 1, i + 1, 57 - i, 56 - i, i + 1, 57 - i, field_14476, field_14476, false);
				}
			}
		}

		private void method_14766(class_1936 arg, Random random, class_3341 arg2) {
			if (this.method_14775(arg2, 7, 21, 13, 50)) {
				this.method_14940(arg, arg2, 7, 0, 21, 13, 0, 50, field_14473, field_14473, false);
				this.method_14773(arg, arg2, 7, 1, 21, 13, 10, 50);
				this.method_14940(arg, arg2, 11, 8, 21, 13, 8, 53, field_14473, field_14473, false);

				for (int i = 0; i < 4; i++) {
					this.method_14940(arg, arg2, i + 7, i + 5, 21, i + 7, i + 5, 54, field_14476, field_14476, false);
				}

				for (int i = 21; i <= 45; i += 3) {
					this.method_14917(arg, field_14470, 12, 9, i, arg2);
				}
			}

			if (this.method_14775(arg2, 44, 21, 50, 54)) {
				this.method_14940(arg, arg2, 44, 0, 21, 50, 0, 50, field_14473, field_14473, false);
				this.method_14773(arg, arg2, 44, 1, 21, 50, 10, 50);
				this.method_14940(arg, arg2, 44, 8, 21, 46, 8, 53, field_14473, field_14473, false);

				for (int i = 0; i < 4; i++) {
					this.method_14940(arg, arg2, 50 - i, i + 5, 21, 50 - i, i + 5, 54, field_14476, field_14476, false);
				}

				for (int i = 21; i <= 45; i += 3) {
					this.method_14917(arg, field_14470, 45, 9, i, arg2);
				}
			}

			if (this.method_14775(arg2, 8, 44, 49, 54)) {
				this.method_14940(arg, arg2, 14, 0, 44, 43, 0, 50, field_14473, field_14473, false);
				this.method_14773(arg, arg2, 14, 1, 44, 43, 10, 50);

				for (int i = 12; i <= 45; i += 3) {
					this.method_14917(arg, field_14470, i, 9, 45, arg2);
					this.method_14917(arg, field_14470, i, 9, 52, arg2);
					if (i == 12 || i == 18 || i == 24 || i == 33 || i == 39 || i == 45) {
						this.method_14917(arg, field_14470, i, 9, 47, arg2);
						this.method_14917(arg, field_14470, i, 9, 50, arg2);
						this.method_14917(arg, field_14470, i, 10, 45, arg2);
						this.method_14917(arg, field_14470, i, 10, 46, arg2);
						this.method_14917(arg, field_14470, i, 10, 51, arg2);
						this.method_14917(arg, field_14470, i, 10, 52, arg2);
						this.method_14917(arg, field_14470, i, 11, 47, arg2);
						this.method_14917(arg, field_14470, i, 11, 50, arg2);
						this.method_14917(arg, field_14470, i, 12, 48, arg2);
						this.method_14917(arg, field_14470, i, 12, 49, arg2);
					}
				}

				for (int ix = 0; ix < 3; ix++) {
					this.method_14940(arg, arg2, 8 + ix, 5 + ix, 54, 49 - ix, 5 + ix, 54, field_14473, field_14473, false);
				}

				this.method_14940(arg, arg2, 11, 8, 54, 46, 8, 54, field_14476, field_14476, false);
				this.method_14940(arg, arg2, 14, 8, 44, 43, 8, 53, field_14473, field_14473, false);
			}
		}

		private void method_14767(class_1936 arg, Random random, class_3341 arg2) {
			if (this.method_14775(arg2, 14, 21, 20, 43)) {
				this.method_14940(arg, arg2, 14, 0, 21, 20, 0, 43, field_14473, field_14473, false);
				this.method_14773(arg, arg2, 14, 1, 22, 20, 14, 43);
				this.method_14940(arg, arg2, 18, 12, 22, 20, 12, 39, field_14473, field_14473, false);
				this.method_14940(arg, arg2, 18, 12, 21, 20, 12, 21, field_14476, field_14476, false);

				for (int i = 0; i < 4; i++) {
					this.method_14940(arg, arg2, i + 14, i + 9, 21, i + 14, i + 9, 43 - i, field_14476, field_14476, false);
				}

				for (int i = 23; i <= 39; i += 3) {
					this.method_14917(arg, field_14470, 19, 13, i, arg2);
				}
			}

			if (this.method_14775(arg2, 37, 21, 43, 43)) {
				this.method_14940(arg, arg2, 37, 0, 21, 43, 0, 43, field_14473, field_14473, false);
				this.method_14773(arg, arg2, 37, 1, 22, 43, 14, 43);
				this.method_14940(arg, arg2, 37, 12, 22, 39, 12, 39, field_14473, field_14473, false);
				this.method_14940(arg, arg2, 37, 12, 21, 39, 12, 21, field_14476, field_14476, false);

				for (int i = 0; i < 4; i++) {
					this.method_14940(arg, arg2, 43 - i, i + 9, 21, 43 - i, i + 9, 43 - i, field_14476, field_14476, false);
				}

				for (int i = 23; i <= 39; i += 3) {
					this.method_14917(arg, field_14470, 38, 13, i, arg2);
				}
			}

			if (this.method_14775(arg2, 15, 37, 42, 43)) {
				this.method_14940(arg, arg2, 21, 0, 37, 36, 0, 43, field_14473, field_14473, false);
				this.method_14773(arg, arg2, 21, 1, 37, 36, 14, 43);
				this.method_14940(arg, arg2, 21, 12, 37, 36, 12, 39, field_14473, field_14473, false);

				for (int i = 0; i < 4; i++) {
					this.method_14940(arg, arg2, 15 + i, i + 9, 43 - i, 42 - i, i + 9, 43 - i, field_14476, field_14476, false);
				}

				for (int i = 21; i <= 36; i += 3) {
					this.method_14917(arg, field_14470, i, 13, 38, arg2);
				}
			}
		}
	}

	interface class_3375 {
		boolean method_14769(class_3366.class_3388 arg);

		class_3366.class_3384 method_14768(class_2350 arg, class_3366.class_3388 arg2, Random random);
	}

	public static class class_3376 extends class_3366.class_3384 {
		public class_3376(class_2350 arg, class_3366.class_3388 arg2) {
			super(class_3773.field_16911, 1, arg, arg2, 2, 2, 2);
		}

		public class_3376(class_3485 arg, class_2487 arg2) {
			super(class_3773.field_16911, arg2);
		}

		@Override
		public boolean method_14931(class_1936 arg, Random random, class_3341 arg2, class_1923 arg3) {
			this.method_14771(arg, arg2, 1, 8, 0, 14, 8, 14, field_14473);
			int i = 7;
			class_2680 lv = field_14476;
			this.method_14940(arg, arg2, 0, 7, 0, 0, 7, 15, lv, lv, false);
			this.method_14940(arg, arg2, 15, 7, 0, 15, 7, 15, lv, lv, false);
			this.method_14940(arg, arg2, 1, 7, 0, 15, 7, 0, lv, lv, false);
			this.method_14940(arg, arg2, 1, 7, 15, 14, 7, 15, lv, lv, false);

			for (int ix = 1; ix <= 6; ix++) {
				lv = field_14476;
				if (ix == 2 || ix == 6) {
					lv = field_14473;
				}

				for (int j = 0; j <= 15; j += 15) {
					this.method_14940(arg, arg2, j, ix, 0, j, ix, 1, lv, lv, false);
					this.method_14940(arg, arg2, j, ix, 6, j, ix, 9, lv, lv, false);
					this.method_14940(arg, arg2, j, ix, 14, j, ix, 15, lv, lv, false);
				}

				this.method_14940(arg, arg2, 1, ix, 0, 1, ix, 0, lv, lv, false);
				this.method_14940(arg, arg2, 6, ix, 0, 9, ix, 0, lv, lv, false);
				this.method_14940(arg, arg2, 14, ix, 0, 14, ix, 0, lv, lv, false);
				this.method_14940(arg, arg2, 1, ix, 15, 14, ix, 15, lv, lv, false);
			}

			this.method_14940(arg, arg2, 6, 3, 6, 9, 6, 9, field_14474, field_14474, false);
			this.method_14940(arg, arg2, 7, 4, 7, 8, 5, 8, class_2246.field_10205.method_9564(), class_2246.field_10205.method_9564(), false);

			for (int ix = 3; ix <= 6; ix += 3) {
				for (int k = 6; k <= 9; k += 3) {
					this.method_14917(arg, field_14471, k, ix, 6, arg2);
					this.method_14917(arg, field_14471, k, ix, 9, arg2);
				}
			}

			this.method_14940(arg, arg2, 5, 1, 6, 5, 2, 6, field_14476, field_14476, false);
			this.method_14940(arg, arg2, 5, 1, 9, 5, 2, 9, field_14476, field_14476, false);
			this.method_14940(arg, arg2, 10, 1, 6, 10, 2, 6, field_14476, field_14476, false);
			this.method_14940(arg, arg2, 10, 1, 9, 10, 2, 9, field_14476, field_14476, false);
			this.method_14940(arg, arg2, 6, 1, 5, 6, 2, 5, field_14476, field_14476, false);
			this.method_14940(arg, arg2, 9, 1, 5, 9, 2, 5, field_14476, field_14476, false);
			this.method_14940(arg, arg2, 6, 1, 10, 6, 2, 10, field_14476, field_14476, false);
			this.method_14940(arg, arg2, 9, 1, 10, 9, 2, 10, field_14476, field_14476, false);
			this.method_14940(arg, arg2, 5, 2, 5, 5, 6, 5, field_14476, field_14476, false);
			this.method_14940(arg, arg2, 5, 2, 10, 5, 6, 10, field_14476, field_14476, false);
			this.method_14940(arg, arg2, 10, 2, 5, 10, 6, 5, field_14476, field_14476, false);
			this.method_14940(arg, arg2, 10, 2, 10, 10, 6, 10, field_14476, field_14476, false);
			this.method_14940(arg, arg2, 5, 7, 1, 5, 7, 6, field_14476, field_14476, false);
			this.method_14940(arg, arg2, 10, 7, 1, 10, 7, 6, field_14476, field_14476, false);
			this.method_14940(arg, arg2, 5, 7, 9, 5, 7, 14, field_14476, field_14476, false);
			this.method_14940(arg, arg2, 10, 7, 9, 10, 7, 14, field_14476, field_14476, false);
			this.method_14940(arg, arg2, 1, 7, 5, 6, 7, 5, field_14476, field_14476, false);
			this.method_14940(arg, arg2, 1, 7, 10, 6, 7, 10, field_14476, field_14476, false);
			this.method_14940(arg, arg2, 9, 7, 5, 14, 7, 5, field_14476, field_14476, false);
			this.method_14940(arg, arg2, 9, 7, 10, 14, 7, 10, field_14476, field_14476, false);
			this.method_14940(arg, arg2, 2, 1, 2, 2, 1, 3, field_14476, field_14476, false);
			this.method_14940(arg, arg2, 3, 1, 2, 3, 1, 2, field_14476, field_14476, false);
			this.method_14940(arg, arg2, 13, 1, 2, 13, 1, 3, field_14476, field_14476, false);
			this.method_14940(arg, arg2, 12, 1, 2, 12, 1, 2, field_14476, field_14476, false);
			this.method_14940(arg, arg2, 2, 1, 12, 2, 1, 13, field_14476, field_14476, false);
			this.method_14940(arg, arg2, 3, 1, 13, 3, 1, 13, field_14476, field_14476, false);
			this.method_14940(arg, arg2, 13, 1, 12, 13, 1, 13, field_14476, field_14476, false);
			this.method_14940(arg, arg2, 12, 1, 13, 12, 1, 13, field_14476, field_14476, false);
			return true;
		}
	}

	public static class class_3377 extends class_3366.class_3384 {
		public class_3377(class_2350 arg, class_3366.class_3388 arg2) {
			super(class_3773.field_16963, 1, arg, arg2, 2, 1, 1);
		}

		public class_3377(class_3485 arg, class_2487 arg2) {
			super(class_3773.field_16963, arg2);
		}

		@Override
		public boolean method_14931(class_1936 arg, Random random, class_3341 arg2, class_1923 arg3) {
			class_3366.class_3388 lv = this.field_14479.field_14487[class_2350.field_11034.method_10146()];
			class_3366.class_3388 lv2 = this.field_14479;
			if (this.field_14479.field_14486 / 25 > 0) {
				this.method_14774(arg, arg2, 8, 0, lv.field_14482[class_2350.field_11033.method_10146()]);
				this.method_14774(arg, arg2, 0, 0, lv2.field_14482[class_2350.field_11033.method_10146()]);
			}

			if (lv2.field_14487[class_2350.field_11036.method_10146()] == null) {
				this.method_14771(arg, arg2, 1, 4, 1, 7, 4, 6, field_14473);
			}

			if (lv.field_14487[class_2350.field_11036.method_10146()] == null) {
				this.method_14771(arg, arg2, 8, 4, 1, 14, 4, 6, field_14473);
			}

			this.method_14940(arg, arg2, 0, 3, 0, 0, 3, 7, field_14476, field_14476, false);
			this.method_14940(arg, arg2, 15, 3, 0, 15, 3, 7, field_14476, field_14476, false);
			this.method_14940(arg, arg2, 1, 3, 0, 15, 3, 0, field_14476, field_14476, false);
			this.method_14940(arg, arg2, 1, 3, 7, 14, 3, 7, field_14476, field_14476, false);
			this.method_14940(arg, arg2, 0, 2, 0, 0, 2, 7, field_14473, field_14473, false);
			this.method_14940(arg, arg2, 15, 2, 0, 15, 2, 7, field_14473, field_14473, false);
			this.method_14940(arg, arg2, 1, 2, 0, 15, 2, 0, field_14473, field_14473, false);
			this.method_14940(arg, arg2, 1, 2, 7, 14, 2, 7, field_14473, field_14473, false);
			this.method_14940(arg, arg2, 0, 1, 0, 0, 1, 7, field_14476, field_14476, false);
			this.method_14940(arg, arg2, 15, 1, 0, 15, 1, 7, field_14476, field_14476, false);
			this.method_14940(arg, arg2, 1, 1, 0, 15, 1, 0, field_14476, field_14476, false);
			this.method_14940(arg, arg2, 1, 1, 7, 14, 1, 7, field_14476, field_14476, false);
			this.method_14940(arg, arg2, 5, 1, 0, 10, 1, 4, field_14476, field_14476, false);
			this.method_14940(arg, arg2, 6, 2, 0, 9, 2, 3, field_14473, field_14473, false);
			this.method_14940(arg, arg2, 5, 3, 0, 10, 3, 4, field_14476, field_14476, false);
			this.method_14917(arg, field_14471, 6, 2, 3, arg2);
			this.method_14917(arg, field_14471, 9, 2, 3, arg2);
			if (lv2.field_14482[class_2350.field_11035.method_10146()]) {
				this.method_14773(arg, arg2, 3, 1, 0, 4, 2, 0);
			}

			if (lv2.field_14482[class_2350.field_11043.method_10146()]) {
				this.method_14773(arg, arg2, 3, 1, 7, 4, 2, 7);
			}

			if (lv2.field_14482[class_2350.field_11039.method_10146()]) {
				this.method_14773(arg, arg2, 0, 1, 3, 0, 2, 4);
			}

			if (lv.field_14482[class_2350.field_11035.method_10146()]) {
				this.method_14773(arg, arg2, 11, 1, 0, 12, 2, 0);
			}

			if (lv.field_14482[class_2350.field_11043.method_10146()]) {
				this.method_14773(arg, arg2, 11, 1, 7, 12, 2, 7);
			}

			if (lv.field_14482[class_2350.field_11034.method_10146()]) {
				this.method_14773(arg, arg2, 15, 1, 3, 15, 2, 4);
			}

			return true;
		}
	}

	public static class class_3378 extends class_3366.class_3384 {
		public class_3378(class_2350 arg, class_3366.class_3388 arg2) {
			super(class_3773.field_16927, 1, arg, arg2, 2, 2, 1);
		}

		public class_3378(class_3485 arg, class_2487 arg2) {
			super(class_3773.field_16927, arg2);
		}

		@Override
		public boolean method_14931(class_1936 arg, Random random, class_3341 arg2, class_1923 arg3) {
			class_3366.class_3388 lv = this.field_14479.field_14487[class_2350.field_11034.method_10146()];
			class_3366.class_3388 lv2 = this.field_14479;
			class_3366.class_3388 lv3 = lv2.field_14487[class_2350.field_11036.method_10146()];
			class_3366.class_3388 lv4 = lv.field_14487[class_2350.field_11036.method_10146()];
			if (this.field_14479.field_14486 / 25 > 0) {
				this.method_14774(arg, arg2, 8, 0, lv.field_14482[class_2350.field_11033.method_10146()]);
				this.method_14774(arg, arg2, 0, 0, lv2.field_14482[class_2350.field_11033.method_10146()]);
			}

			if (lv3.field_14487[class_2350.field_11036.method_10146()] == null) {
				this.method_14771(arg, arg2, 1, 8, 1, 7, 8, 6, field_14473);
			}

			if (lv4.field_14487[class_2350.field_11036.method_10146()] == null) {
				this.method_14771(arg, arg2, 8, 8, 1, 14, 8, 6, field_14473);
			}

			for (int i = 1; i <= 7; i++) {
				class_2680 lv5 = field_14476;
				if (i == 2 || i == 6) {
					lv5 = field_14473;
				}

				this.method_14940(arg, arg2, 0, i, 0, 0, i, 7, lv5, lv5, false);
				this.method_14940(arg, arg2, 15, i, 0, 15, i, 7, lv5, lv5, false);
				this.method_14940(arg, arg2, 1, i, 0, 15, i, 0, lv5, lv5, false);
				this.method_14940(arg, arg2, 1, i, 7, 14, i, 7, lv5, lv5, false);
			}

			this.method_14940(arg, arg2, 2, 1, 3, 2, 7, 4, field_14476, field_14476, false);
			this.method_14940(arg, arg2, 3, 1, 2, 4, 7, 2, field_14476, field_14476, false);
			this.method_14940(arg, arg2, 3, 1, 5, 4, 7, 5, field_14476, field_14476, false);
			this.method_14940(arg, arg2, 13, 1, 3, 13, 7, 4, field_14476, field_14476, false);
			this.method_14940(arg, arg2, 11, 1, 2, 12, 7, 2, field_14476, field_14476, false);
			this.method_14940(arg, arg2, 11, 1, 5, 12, 7, 5, field_14476, field_14476, false);
			this.method_14940(arg, arg2, 5, 1, 3, 5, 3, 4, field_14476, field_14476, false);
			this.method_14940(arg, arg2, 10, 1, 3, 10, 3, 4, field_14476, field_14476, false);
			this.method_14940(arg, arg2, 5, 7, 2, 10, 7, 5, field_14476, field_14476, false);
			this.method_14940(arg, arg2, 5, 5, 2, 5, 7, 2, field_14476, field_14476, false);
			this.method_14940(arg, arg2, 10, 5, 2, 10, 7, 2, field_14476, field_14476, false);
			this.method_14940(arg, arg2, 5, 5, 5, 5, 7, 5, field_14476, field_14476, false);
			this.method_14940(arg, arg2, 10, 5, 5, 10, 7, 5, field_14476, field_14476, false);
			this.method_14917(arg, field_14476, 6, 6, 2, arg2);
			this.method_14917(arg, field_14476, 9, 6, 2, arg2);
			this.method_14917(arg, field_14476, 6, 6, 5, arg2);
			this.method_14917(arg, field_14476, 9, 6, 5, arg2);
			this.method_14940(arg, arg2, 5, 4, 3, 6, 4, 4, field_14476, field_14476, false);
			this.method_14940(arg, arg2, 9, 4, 3, 10, 4, 4, field_14476, field_14476, false);
			this.method_14917(arg, field_14471, 5, 4, 2, arg2);
			this.method_14917(arg, field_14471, 5, 4, 5, arg2);
			this.method_14917(arg, field_14471, 10, 4, 2, arg2);
			this.method_14917(arg, field_14471, 10, 4, 5, arg2);
			if (lv2.field_14482[class_2350.field_11035.method_10146()]) {
				this.method_14773(arg, arg2, 3, 1, 0, 4, 2, 0);
			}

			if (lv2.field_14482[class_2350.field_11043.method_10146()]) {
				this.method_14773(arg, arg2, 3, 1, 7, 4, 2, 7);
			}

			if (lv2.field_14482[class_2350.field_11039.method_10146()]) {
				this.method_14773(arg, arg2, 0, 1, 3, 0, 2, 4);
			}

			if (lv.field_14482[class_2350.field_11035.method_10146()]) {
				this.method_14773(arg, arg2, 11, 1, 0, 12, 2, 0);
			}

			if (lv.field_14482[class_2350.field_11043.method_10146()]) {
				this.method_14773(arg, arg2, 11, 1, 7, 12, 2, 7);
			}

			if (lv.field_14482[class_2350.field_11034.method_10146()]) {
				this.method_14773(arg, arg2, 15, 1, 3, 15, 2, 4);
			}

			if (lv3.field_14482[class_2350.field_11035.method_10146()]) {
				this.method_14773(arg, arg2, 3, 5, 0, 4, 6, 0);
			}

			if (lv3.field_14482[class_2350.field_11043.method_10146()]) {
				this.method_14773(arg, arg2, 3, 5, 7, 4, 6, 7);
			}

			if (lv3.field_14482[class_2350.field_11039.method_10146()]) {
				this.method_14773(arg, arg2, 0, 5, 3, 0, 6, 4);
			}

			if (lv4.field_14482[class_2350.field_11035.method_10146()]) {
				this.method_14773(arg, arg2, 11, 5, 0, 12, 6, 0);
			}

			if (lv4.field_14482[class_2350.field_11043.method_10146()]) {
				this.method_14773(arg, arg2, 11, 5, 7, 12, 6, 7);
			}

			if (lv4.field_14482[class_2350.field_11034.method_10146()]) {
				this.method_14773(arg, arg2, 15, 5, 3, 15, 6, 4);
			}

			return true;
		}
	}

	public static class class_3379 extends class_3366.class_3384 {
		public class_3379(class_2350 arg, class_3366.class_3388 arg2) {
			super(class_3773.field_16946, 1, arg, arg2, 1, 2, 1);
		}

		public class_3379(class_3485 arg, class_2487 arg2) {
			super(class_3773.field_16946, arg2);
		}

		@Override
		public boolean method_14931(class_1936 arg, Random random, class_3341 arg2, class_1923 arg3) {
			if (this.field_14479.field_14486 / 25 > 0) {
				this.method_14774(arg, arg2, 0, 0, this.field_14479.field_14482[class_2350.field_11033.method_10146()]);
			}

			class_3366.class_3388 lv = this.field_14479.field_14487[class_2350.field_11036.method_10146()];
			if (lv.field_14487[class_2350.field_11036.method_10146()] == null) {
				this.method_14771(arg, arg2, 1, 8, 1, 6, 8, 6, field_14473);
			}

			this.method_14940(arg, arg2, 0, 4, 0, 0, 4, 7, field_14476, field_14476, false);
			this.method_14940(arg, arg2, 7, 4, 0, 7, 4, 7, field_14476, field_14476, false);
			this.method_14940(arg, arg2, 1, 4, 0, 6, 4, 0, field_14476, field_14476, false);
			this.method_14940(arg, arg2, 1, 4, 7, 6, 4, 7, field_14476, field_14476, false);
			this.method_14940(arg, arg2, 2, 4, 1, 2, 4, 2, field_14476, field_14476, false);
			this.method_14940(arg, arg2, 1, 4, 2, 1, 4, 2, field_14476, field_14476, false);
			this.method_14940(arg, arg2, 5, 4, 1, 5, 4, 2, field_14476, field_14476, false);
			this.method_14940(arg, arg2, 6, 4, 2, 6, 4, 2, field_14476, field_14476, false);
			this.method_14940(arg, arg2, 2, 4, 5, 2, 4, 6, field_14476, field_14476, false);
			this.method_14940(arg, arg2, 1, 4, 5, 1, 4, 5, field_14476, field_14476, false);
			this.method_14940(arg, arg2, 5, 4, 5, 5, 4, 6, field_14476, field_14476, false);
			this.method_14940(arg, arg2, 6, 4, 5, 6, 4, 5, field_14476, field_14476, false);
			class_3366.class_3388 lv2 = this.field_14479;

			for (int i = 1; i <= 5; i += 4) {
				int j = 0;
				if (lv2.field_14482[class_2350.field_11035.method_10146()]) {
					this.method_14940(arg, arg2, 2, i, j, 2, i + 2, j, field_14476, field_14476, false);
					this.method_14940(arg, arg2, 5, i, j, 5, i + 2, j, field_14476, field_14476, false);
					this.method_14940(arg, arg2, 3, i + 2, j, 4, i + 2, j, field_14476, field_14476, false);
				} else {
					this.method_14940(arg, arg2, 0, i, j, 7, i + 2, j, field_14476, field_14476, false);
					this.method_14940(arg, arg2, 0, i + 1, j, 7, i + 1, j, field_14473, field_14473, false);
				}

				int var10 = 7;
				if (lv2.field_14482[class_2350.field_11043.method_10146()]) {
					this.method_14940(arg, arg2, 2, i, var10, 2, i + 2, var10, field_14476, field_14476, false);
					this.method_14940(arg, arg2, 5, i, var10, 5, i + 2, var10, field_14476, field_14476, false);
					this.method_14940(arg, arg2, 3, i + 2, var10, 4, i + 2, var10, field_14476, field_14476, false);
				} else {
					this.method_14940(arg, arg2, 0, i, var10, 7, i + 2, var10, field_14476, field_14476, false);
					this.method_14940(arg, arg2, 0, i + 1, var10, 7, i + 1, var10, field_14473, field_14473, false);
				}

				int k = 0;
				if (lv2.field_14482[class_2350.field_11039.method_10146()]) {
					this.method_14940(arg, arg2, k, i, 2, k, i + 2, 2, field_14476, field_14476, false);
					this.method_14940(arg, arg2, k, i, 5, k, i + 2, 5, field_14476, field_14476, false);
					this.method_14940(arg, arg2, k, i + 2, 3, k, i + 2, 4, field_14476, field_14476, false);
				} else {
					this.method_14940(arg, arg2, k, i, 0, k, i + 2, 7, field_14476, field_14476, false);
					this.method_14940(arg, arg2, k, i + 1, 0, k, i + 1, 7, field_14473, field_14473, false);
				}

				int var11 = 7;
				if (lv2.field_14482[class_2350.field_11034.method_10146()]) {
					this.method_14940(arg, arg2, var11, i, 2, var11, i + 2, 2, field_14476, field_14476, false);
					this.method_14940(arg, arg2, var11, i, 5, var11, i + 2, 5, field_14476, field_14476, false);
					this.method_14940(arg, arg2, var11, i + 2, 3, var11, i + 2, 4, field_14476, field_14476, false);
				} else {
					this.method_14940(arg, arg2, var11, i, 0, var11, i + 2, 7, field_14476, field_14476, false);
					this.method_14940(arg, arg2, var11, i + 1, 0, var11, i + 1, 7, field_14473, field_14473, false);
				}

				lv2 = lv;
			}

			return true;
		}
	}

	public static class class_3380 extends class_3366.class_3384 {
		public class_3380(class_2350 arg, class_3366.class_3388 arg2) {
			super(class_3773.field_16970, 1, arg, arg2, 1, 2, 2);
		}

		public class_3380(class_3485 arg, class_2487 arg2) {
			super(class_3773.field_16970, arg2);
		}

		@Override
		public boolean method_14931(class_1936 arg, Random random, class_3341 arg2, class_1923 arg3) {
			class_3366.class_3388 lv = this.field_14479.field_14487[class_2350.field_11043.method_10146()];
			class_3366.class_3388 lv2 = this.field_14479;
			class_3366.class_3388 lv3 = lv.field_14487[class_2350.field_11036.method_10146()];
			class_3366.class_3388 lv4 = lv2.field_14487[class_2350.field_11036.method_10146()];
			if (this.field_14479.field_14486 / 25 > 0) {
				this.method_14774(arg, arg2, 0, 8, lv.field_14482[class_2350.field_11033.method_10146()]);
				this.method_14774(arg, arg2, 0, 0, lv2.field_14482[class_2350.field_11033.method_10146()]);
			}

			if (lv4.field_14487[class_2350.field_11036.method_10146()] == null) {
				this.method_14771(arg, arg2, 1, 8, 1, 6, 8, 7, field_14473);
			}

			if (lv3.field_14487[class_2350.field_11036.method_10146()] == null) {
				this.method_14771(arg, arg2, 1, 8, 8, 6, 8, 14, field_14473);
			}

			for (int i = 1; i <= 7; i++) {
				class_2680 lv5 = field_14476;
				if (i == 2 || i == 6) {
					lv5 = field_14473;
				}

				this.method_14940(arg, arg2, 0, i, 0, 0, i, 15, lv5, lv5, false);
				this.method_14940(arg, arg2, 7, i, 0, 7, i, 15, lv5, lv5, false);
				this.method_14940(arg, arg2, 1, i, 0, 6, i, 0, lv5, lv5, false);
				this.method_14940(arg, arg2, 1, i, 15, 6, i, 15, lv5, lv5, false);
			}

			for (int i = 1; i <= 7; i++) {
				class_2680 lv5 = field_14474;
				if (i == 2 || i == 6) {
					lv5 = field_14471;
				}

				this.method_14940(arg, arg2, 3, i, 7, 4, i, 8, lv5, lv5, false);
			}

			if (lv2.field_14482[class_2350.field_11035.method_10146()]) {
				this.method_14773(arg, arg2, 3, 1, 0, 4, 2, 0);
			}

			if (lv2.field_14482[class_2350.field_11034.method_10146()]) {
				this.method_14773(arg, arg2, 7, 1, 3, 7, 2, 4);
			}

			if (lv2.field_14482[class_2350.field_11039.method_10146()]) {
				this.method_14773(arg, arg2, 0, 1, 3, 0, 2, 4);
			}

			if (lv.field_14482[class_2350.field_11043.method_10146()]) {
				this.method_14773(arg, arg2, 3, 1, 15, 4, 2, 15);
			}

			if (lv.field_14482[class_2350.field_11039.method_10146()]) {
				this.method_14773(arg, arg2, 0, 1, 11, 0, 2, 12);
			}

			if (lv.field_14482[class_2350.field_11034.method_10146()]) {
				this.method_14773(arg, arg2, 7, 1, 11, 7, 2, 12);
			}

			if (lv4.field_14482[class_2350.field_11035.method_10146()]) {
				this.method_14773(arg, arg2, 3, 5, 0, 4, 6, 0);
			}

			if (lv4.field_14482[class_2350.field_11034.method_10146()]) {
				this.method_14773(arg, arg2, 7, 5, 3, 7, 6, 4);
				this.method_14940(arg, arg2, 5, 4, 2, 6, 4, 5, field_14476, field_14476, false);
				this.method_14940(arg, arg2, 6, 1, 2, 6, 3, 2, field_14476, field_14476, false);
				this.method_14940(arg, arg2, 6, 1, 5, 6, 3, 5, field_14476, field_14476, false);
			}

			if (lv4.field_14482[class_2350.field_11039.method_10146()]) {
				this.method_14773(arg, arg2, 0, 5, 3, 0, 6, 4);
				this.method_14940(arg, arg2, 1, 4, 2, 2, 4, 5, field_14476, field_14476, false);
				this.method_14940(arg, arg2, 1, 1, 2, 1, 3, 2, field_14476, field_14476, false);
				this.method_14940(arg, arg2, 1, 1, 5, 1, 3, 5, field_14476, field_14476, false);
			}

			if (lv3.field_14482[class_2350.field_11043.method_10146()]) {
				this.method_14773(arg, arg2, 3, 5, 15, 4, 6, 15);
			}

			if (lv3.field_14482[class_2350.field_11039.method_10146()]) {
				this.method_14773(arg, arg2, 0, 5, 11, 0, 6, 12);
				this.method_14940(arg, arg2, 1, 4, 10, 2, 4, 13, field_14476, field_14476, false);
				this.method_14940(arg, arg2, 1, 1, 10, 1, 3, 10, field_14476, field_14476, false);
				this.method_14940(arg, arg2, 1, 1, 13, 1, 3, 13, field_14476, field_14476, false);
			}

			if (lv3.field_14482[class_2350.field_11034.method_10146()]) {
				this.method_14773(arg, arg2, 7, 5, 11, 7, 6, 12);
				this.method_14940(arg, arg2, 5, 4, 10, 6, 4, 13, field_14476, field_14476, false);
				this.method_14940(arg, arg2, 6, 1, 10, 6, 3, 10, field_14476, field_14476, false);
				this.method_14940(arg, arg2, 6, 1, 13, 6, 3, 13, field_14476, field_14476, false);
			}

			return true;
		}
	}

	public static class class_3381 extends class_3366.class_3384 {
		public class_3381(class_2350 arg, class_3366.class_3388 arg2) {
			super(class_3773.field_16925, 1, arg, arg2, 1, 1, 2);
		}

		public class_3381(class_3485 arg, class_2487 arg2) {
			super(class_3773.field_16925, arg2);
		}

		@Override
		public boolean method_14931(class_1936 arg, Random random, class_3341 arg2, class_1923 arg3) {
			class_3366.class_3388 lv = this.field_14479.field_14487[class_2350.field_11043.method_10146()];
			class_3366.class_3388 lv2 = this.field_14479;
			if (this.field_14479.field_14486 / 25 > 0) {
				this.method_14774(arg, arg2, 0, 8, lv.field_14482[class_2350.field_11033.method_10146()]);
				this.method_14774(arg, arg2, 0, 0, lv2.field_14482[class_2350.field_11033.method_10146()]);
			}

			if (lv2.field_14487[class_2350.field_11036.method_10146()] == null) {
				this.method_14771(arg, arg2, 1, 4, 1, 6, 4, 7, field_14473);
			}

			if (lv.field_14487[class_2350.field_11036.method_10146()] == null) {
				this.method_14771(arg, arg2, 1, 4, 8, 6, 4, 14, field_14473);
			}

			this.method_14940(arg, arg2, 0, 3, 0, 0, 3, 15, field_14476, field_14476, false);
			this.method_14940(arg, arg2, 7, 3, 0, 7, 3, 15, field_14476, field_14476, false);
			this.method_14940(arg, arg2, 1, 3, 0, 7, 3, 0, field_14476, field_14476, false);
			this.method_14940(arg, arg2, 1, 3, 15, 6, 3, 15, field_14476, field_14476, false);
			this.method_14940(arg, arg2, 0, 2, 0, 0, 2, 15, field_14473, field_14473, false);
			this.method_14940(arg, arg2, 7, 2, 0, 7, 2, 15, field_14473, field_14473, false);
			this.method_14940(arg, arg2, 1, 2, 0, 7, 2, 0, field_14473, field_14473, false);
			this.method_14940(arg, arg2, 1, 2, 15, 6, 2, 15, field_14473, field_14473, false);
			this.method_14940(arg, arg2, 0, 1, 0, 0, 1, 15, field_14476, field_14476, false);
			this.method_14940(arg, arg2, 7, 1, 0, 7, 1, 15, field_14476, field_14476, false);
			this.method_14940(arg, arg2, 1, 1, 0, 7, 1, 0, field_14476, field_14476, false);
			this.method_14940(arg, arg2, 1, 1, 15, 6, 1, 15, field_14476, field_14476, false);
			this.method_14940(arg, arg2, 1, 1, 1, 1, 1, 2, field_14476, field_14476, false);
			this.method_14940(arg, arg2, 6, 1, 1, 6, 1, 2, field_14476, field_14476, false);
			this.method_14940(arg, arg2, 1, 3, 1, 1, 3, 2, field_14476, field_14476, false);
			this.method_14940(arg, arg2, 6, 3, 1, 6, 3, 2, field_14476, field_14476, false);
			this.method_14940(arg, arg2, 1, 1, 13, 1, 1, 14, field_14476, field_14476, false);
			this.method_14940(arg, arg2, 6, 1, 13, 6, 1, 14, field_14476, field_14476, false);
			this.method_14940(arg, arg2, 1, 3, 13, 1, 3, 14, field_14476, field_14476, false);
			this.method_14940(arg, arg2, 6, 3, 13, 6, 3, 14, field_14476, field_14476, false);
			this.method_14940(arg, arg2, 2, 1, 6, 2, 3, 6, field_14476, field_14476, false);
			this.method_14940(arg, arg2, 5, 1, 6, 5, 3, 6, field_14476, field_14476, false);
			this.method_14940(arg, arg2, 2, 1, 9, 2, 3, 9, field_14476, field_14476, false);
			this.method_14940(arg, arg2, 5, 1, 9, 5, 3, 9, field_14476, field_14476, false);
			this.method_14940(arg, arg2, 3, 2, 6, 4, 2, 6, field_14476, field_14476, false);
			this.method_14940(arg, arg2, 3, 2, 9, 4, 2, 9, field_14476, field_14476, false);
			this.method_14940(arg, arg2, 2, 2, 7, 2, 2, 8, field_14476, field_14476, false);
			this.method_14940(arg, arg2, 5, 2, 7, 5, 2, 8, field_14476, field_14476, false);
			this.method_14917(arg, field_14471, 2, 2, 5, arg2);
			this.method_14917(arg, field_14471, 5, 2, 5, arg2);
			this.method_14917(arg, field_14471, 2, 2, 10, arg2);
			this.method_14917(arg, field_14471, 5, 2, 10, arg2);
			this.method_14917(arg, field_14476, 2, 3, 5, arg2);
			this.method_14917(arg, field_14476, 5, 3, 5, arg2);
			this.method_14917(arg, field_14476, 2, 3, 10, arg2);
			this.method_14917(arg, field_14476, 5, 3, 10, arg2);
			if (lv2.field_14482[class_2350.field_11035.method_10146()]) {
				this.method_14773(arg, arg2, 3, 1, 0, 4, 2, 0);
			}

			if (lv2.field_14482[class_2350.field_11034.method_10146()]) {
				this.method_14773(arg, arg2, 7, 1, 3, 7, 2, 4);
			}

			if (lv2.field_14482[class_2350.field_11039.method_10146()]) {
				this.method_14773(arg, arg2, 0, 1, 3, 0, 2, 4);
			}

			if (lv.field_14482[class_2350.field_11043.method_10146()]) {
				this.method_14773(arg, arg2, 3, 1, 15, 4, 2, 15);
			}

			if (lv.field_14482[class_2350.field_11039.method_10146()]) {
				this.method_14773(arg, arg2, 0, 1, 11, 0, 2, 12);
			}

			if (lv.field_14482[class_2350.field_11034.method_10146()]) {
				this.method_14773(arg, arg2, 7, 1, 11, 7, 2, 12);
			}

			return true;
		}
	}

	public static class class_3382 extends class_3366.class_3384 {
		public class_3382(class_2350 arg, class_3366.class_3388 arg2) {
			super(class_3773.field_16905, 1, arg, arg2, 1, 1, 1);
		}

		public class_3382(class_3485 arg, class_2487 arg2) {
			super(class_3773.field_16905, arg2);
		}

		@Override
		public boolean method_14931(class_1936 arg, Random random, class_3341 arg2, class_1923 arg3) {
			this.method_14940(arg, arg2, 0, 3, 0, 2, 3, 7, field_14476, field_14476, false);
			this.method_14940(arg, arg2, 5, 3, 0, 7, 3, 7, field_14476, field_14476, false);
			this.method_14940(arg, arg2, 0, 2, 0, 1, 2, 7, field_14476, field_14476, false);
			this.method_14940(arg, arg2, 6, 2, 0, 7, 2, 7, field_14476, field_14476, false);
			this.method_14940(arg, arg2, 0, 1, 0, 0, 1, 7, field_14476, field_14476, false);
			this.method_14940(arg, arg2, 7, 1, 0, 7, 1, 7, field_14476, field_14476, false);
			this.method_14940(arg, arg2, 0, 1, 7, 7, 3, 7, field_14476, field_14476, false);
			this.method_14940(arg, arg2, 1, 1, 0, 2, 3, 0, field_14476, field_14476, false);
			this.method_14940(arg, arg2, 5, 1, 0, 6, 3, 0, field_14476, field_14476, false);
			if (this.field_14479.field_14482[class_2350.field_11043.method_10146()]) {
				this.method_14773(arg, arg2, 3, 1, 7, 4, 2, 7);
			}

			if (this.field_14479.field_14482[class_2350.field_11039.method_10146()]) {
				this.method_14773(arg, arg2, 0, 1, 3, 1, 2, 4);
			}

			if (this.field_14479.field_14482[class_2350.field_11034.method_10146()]) {
				this.method_14773(arg, arg2, 6, 1, 3, 7, 2, 4);
			}

			return true;
		}
	}

	public static class class_3383 extends class_3366.class_3384 {
		public class_3383(class_2350 arg, class_3341 arg2) {
			super(class_3773.field_16966, arg, arg2);
		}

		public class_3383(class_3485 arg, class_2487 arg2) {
			super(class_3773.field_16966, arg2);
		}

		@Override
		public boolean method_14931(class_1936 arg, Random random, class_3341 arg2, class_1923 arg3) {
			this.method_14940(arg, arg2, 2, -1, 2, 11, -1, 11, field_14476, field_14476, false);
			this.method_14940(arg, arg2, 0, -1, 0, 1, -1, 11, field_14473, field_14473, false);
			this.method_14940(arg, arg2, 12, -1, 0, 13, -1, 11, field_14473, field_14473, false);
			this.method_14940(arg, arg2, 2, -1, 0, 11, -1, 1, field_14473, field_14473, false);
			this.method_14940(arg, arg2, 2, -1, 12, 11, -1, 13, field_14473, field_14473, false);
			this.method_14940(arg, arg2, 0, 0, 0, 0, 0, 13, field_14476, field_14476, false);
			this.method_14940(arg, arg2, 13, 0, 0, 13, 0, 13, field_14476, field_14476, false);
			this.method_14940(arg, arg2, 1, 0, 0, 12, 0, 0, field_14476, field_14476, false);
			this.method_14940(arg, arg2, 1, 0, 13, 12, 0, 13, field_14476, field_14476, false);

			for (int i = 2; i <= 11; i += 3) {
				this.method_14917(arg, field_14471, 0, 0, i, arg2);
				this.method_14917(arg, field_14471, 13, 0, i, arg2);
				this.method_14917(arg, field_14471, i, 0, 0, arg2);
			}

			this.method_14940(arg, arg2, 2, 0, 3, 4, 0, 9, field_14476, field_14476, false);
			this.method_14940(arg, arg2, 9, 0, 3, 11, 0, 9, field_14476, field_14476, false);
			this.method_14940(arg, arg2, 4, 0, 9, 9, 0, 11, field_14476, field_14476, false);
			this.method_14917(arg, field_14476, 5, 0, 8, arg2);
			this.method_14917(arg, field_14476, 8, 0, 8, arg2);
			this.method_14917(arg, field_14476, 10, 0, 10, arg2);
			this.method_14917(arg, field_14476, 3, 0, 10, arg2);
			this.method_14940(arg, arg2, 3, 0, 3, 3, 0, 7, field_14474, field_14474, false);
			this.method_14940(arg, arg2, 10, 0, 3, 10, 0, 7, field_14474, field_14474, false);
			this.method_14940(arg, arg2, 6, 0, 10, 7, 0, 10, field_14474, field_14474, false);
			int i = 3;

			for (int j = 0; j < 2; j++) {
				for (int k = 2; k <= 8; k += 3) {
					this.method_14940(arg, arg2, i, 0, k, i, 2, k, field_14476, field_14476, false);
				}

				i = 10;
			}

			this.method_14940(arg, arg2, 5, 0, 10, 5, 2, 10, field_14476, field_14476, false);
			this.method_14940(arg, arg2, 8, 0, 10, 8, 2, 10, field_14476, field_14476, false);
			this.method_14940(arg, arg2, 6, -1, 7, 7, -1, 8, field_14474, field_14474, false);
			this.method_14773(arg, arg2, 6, -1, 3, 7, -1, 4);
			this.method_14772(arg, arg2, 6, 1, 6);
			return true;
		}
	}

	public abstract static class class_3384 extends class_3443 {
		protected static final class_2680 field_14473 = class_2246.field_10135.method_9564();
		protected static final class_2680 field_14476 = class_2246.field_10006.method_9564();
		protected static final class_2680 field_14474 = class_2246.field_10297.method_9564();
		protected static final class_2680 field_14470 = field_14476;
		protected static final class_2680 field_14471 = class_2246.field_10174.method_9564();
		protected static final class_2680 field_14475 = class_2246.field_10382.method_9564();
		protected static final Set<class_2248> field_14472 = ImmutableSet.<class_2248>builder()
			.add(class_2246.field_10295)
			.add(class_2246.field_10225)
			.add(class_2246.field_10384)
			.add(field_14475.method_11614())
			.build();
		protected static final int field_14469 = method_14770(2, 0, 0);
		protected static final int field_14468 = method_14770(2, 2, 0);
		protected static final int field_14478 = method_14770(0, 1, 0);
		protected static final int field_14477 = method_14770(4, 1, 0);
		protected class_3366.class_3388 field_14479;

		protected static final int method_14770(int i, int j, int k) {
			return j * 25 + k * 5 + i;
		}

		public class_3384(class_3773 arg, int i) {
			super(arg, i);
		}

		public class_3384(class_3773 arg, class_2350 arg2, class_3341 arg3) {
			super(arg, 1);
			this.method_14926(arg2);
			this.field_15315 = arg3;
		}

		protected class_3384(class_3773 arg, int i, class_2350 arg2, class_3366.class_3388 arg3, int j, int k, int l) {
			super(arg, i);
			this.method_14926(arg2);
			this.field_14479 = arg3;
			int m = arg3.field_14486;
			int n = m % 5;
			int o = m / 5 % 5;
			int p = m / 25;
			if (arg2 != class_2350.field_11043 && arg2 != class_2350.field_11035) {
				this.field_15315 = new class_3341(0, 0, 0, l * 8 - 1, k * 4 - 1, j * 8 - 1);
			} else {
				this.field_15315 = new class_3341(0, 0, 0, j * 8 - 1, k * 4 - 1, l * 8 - 1);
			}

			switch (arg2) {
				case field_11043:
					this.field_15315.method_14661(n * 8, p * 4, -(o + l) * 8 + 1);
					break;
				case field_11035:
					this.field_15315.method_14661(n * 8, p * 4, o * 8);
					break;
				case field_11039:
					this.field_15315.method_14661(-(o + l) * 8 + 1, p * 4, n * 8);
					break;
				default:
					this.field_15315.method_14661(o * 8, p * 4, n * 8);
			}
		}

		public class_3384(class_3773 arg, class_2487 arg2) {
			super(arg, arg2);
		}

		@Override
		protected void method_14943(class_2487 arg) {
		}

		protected void method_14773(class_1936 arg, class_3341 arg2, int i, int j, int k, int l, int m, int n) {
			for (int o = j; o <= m; o++) {
				for (int p = i; p <= l; p++) {
					for (int q = k; q <= n; q++) {
						class_2680 lv = this.method_14929(arg, p, o, q, arg2);
						if (!field_14472.contains(lv.method_11614())) {
							if (this.method_14924(o) >= arg.method_8615() && lv != field_14475) {
								this.method_14917(arg, class_2246.field_10124.method_9564(), p, o, q, arg2);
							} else {
								this.method_14917(arg, field_14475, p, o, q, arg2);
							}
						}
					}
				}
			}
		}

		protected void method_14774(class_1936 arg, class_3341 arg2, int i, int j, boolean bl) {
			if (bl) {
				this.method_14940(arg, arg2, i + 0, 0, j + 0, i + 2, 0, j + 8 - 1, field_14473, field_14473, false);
				this.method_14940(arg, arg2, i + 5, 0, j + 0, i + 8 - 1, 0, j + 8 - 1, field_14473, field_14473, false);
				this.method_14940(arg, arg2, i + 3, 0, j + 0, i + 4, 0, j + 2, field_14473, field_14473, false);
				this.method_14940(arg, arg2, i + 3, 0, j + 5, i + 4, 0, j + 8 - 1, field_14473, field_14473, false);
				this.method_14940(arg, arg2, i + 3, 0, j + 2, i + 4, 0, j + 2, field_14476, field_14476, false);
				this.method_14940(arg, arg2, i + 3, 0, j + 5, i + 4, 0, j + 5, field_14476, field_14476, false);
				this.method_14940(arg, arg2, i + 2, 0, j + 3, i + 2, 0, j + 4, field_14476, field_14476, false);
				this.method_14940(arg, arg2, i + 5, 0, j + 3, i + 5, 0, j + 4, field_14476, field_14476, false);
			} else {
				this.method_14940(arg, arg2, i + 0, 0, j + 0, i + 8 - 1, 0, j + 8 - 1, field_14473, field_14473, false);
			}
		}

		protected void method_14771(class_1936 arg, class_3341 arg2, int i, int j, int k, int l, int m, int n, class_2680 arg3) {
			for (int o = j; o <= m; o++) {
				for (int p = i; p <= l; p++) {
					for (int q = k; q <= n; q++) {
						if (this.method_14929(arg, p, o, q, arg2) == field_14475) {
							this.method_14917(arg, arg3, p, o, q, arg2);
						}
					}
				}
			}
		}

		protected boolean method_14775(class_3341 arg, int i, int j, int k, int l) {
			int m = this.method_14928(i, j);
			int n = this.method_14941(i, j);
			int o = this.method_14928(k, l);
			int p = this.method_14941(k, l);
			return arg.method_14669(Math.min(m, o), Math.min(n, p), Math.max(m, o), Math.max(n, p));
		}

		protected boolean method_14772(class_1936 arg, class_3341 arg2, int i, int j, int k) {
			int l = this.method_14928(i, k);
			int m = this.method_14924(j);
			int n = this.method_14941(i, k);
			if (arg2.method_14662(new class_2338(l, m, n))) {
				class_1550 lv = class_1299.field_6086.method_5883(arg.method_8410());
				lv.method_6025(lv.method_6063());
				lv.method_5808((double)l + 0.5, (double)m, (double)n + 0.5, 0.0F, 0.0F);
				lv.method_5943(arg, arg.method_8404(new class_2338(lv)), class_3730.field_16474, null, null);
				arg.method_8649(lv);
				return true;
			} else {
				return false;
			}
		}
	}

	public static class class_3385 extends class_3366.class_3384 {
		private int field_14480;

		public class_3385(class_2350 arg, class_3366.class_3388 arg2, Random random) {
			super(class_3773.field_16928, 1, arg, arg2, 1, 1, 1);
			this.field_14480 = random.nextInt(3);
		}

		public class_3385(class_3485 arg, class_2487 arg2) {
			super(class_3773.field_16928, arg2);
		}

		@Override
		public boolean method_14931(class_1936 arg, Random random, class_3341 arg2, class_1923 arg3) {
			if (this.field_14479.field_14486 / 25 > 0) {
				this.method_14774(arg, arg2, 0, 0, this.field_14479.field_14482[class_2350.field_11033.method_10146()]);
			}

			if (this.field_14479.field_14487[class_2350.field_11036.method_10146()] == null) {
				this.method_14771(arg, arg2, 1, 4, 1, 6, 4, 6, field_14473);
			}

			boolean bl = this.field_14480 != 0
				&& random.nextBoolean()
				&& !this.field_14479.field_14482[class_2350.field_11033.method_10146()]
				&& !this.field_14479.field_14482[class_2350.field_11036.method_10146()]
				&& this.field_14479.method_14781() > 1;
			if (this.field_14480 == 0) {
				this.method_14940(arg, arg2, 0, 1, 0, 2, 1, 2, field_14476, field_14476, false);
				this.method_14940(arg, arg2, 0, 3, 0, 2, 3, 2, field_14476, field_14476, false);
				this.method_14940(arg, arg2, 0, 2, 0, 0, 2, 2, field_14473, field_14473, false);
				this.method_14940(arg, arg2, 1, 2, 0, 2, 2, 0, field_14473, field_14473, false);
				this.method_14917(arg, field_14471, 1, 2, 1, arg2);
				this.method_14940(arg, arg2, 5, 1, 0, 7, 1, 2, field_14476, field_14476, false);
				this.method_14940(arg, arg2, 5, 3, 0, 7, 3, 2, field_14476, field_14476, false);
				this.method_14940(arg, arg2, 7, 2, 0, 7, 2, 2, field_14473, field_14473, false);
				this.method_14940(arg, arg2, 5, 2, 0, 6, 2, 0, field_14473, field_14473, false);
				this.method_14917(arg, field_14471, 6, 2, 1, arg2);
				this.method_14940(arg, arg2, 0, 1, 5, 2, 1, 7, field_14476, field_14476, false);
				this.method_14940(arg, arg2, 0, 3, 5, 2, 3, 7, field_14476, field_14476, false);
				this.method_14940(arg, arg2, 0, 2, 5, 0, 2, 7, field_14473, field_14473, false);
				this.method_14940(arg, arg2, 1, 2, 7, 2, 2, 7, field_14473, field_14473, false);
				this.method_14917(arg, field_14471, 1, 2, 6, arg2);
				this.method_14940(arg, arg2, 5, 1, 5, 7, 1, 7, field_14476, field_14476, false);
				this.method_14940(arg, arg2, 5, 3, 5, 7, 3, 7, field_14476, field_14476, false);
				this.method_14940(arg, arg2, 7, 2, 5, 7, 2, 7, field_14473, field_14473, false);
				this.method_14940(arg, arg2, 5, 2, 7, 6, 2, 7, field_14473, field_14473, false);
				this.method_14917(arg, field_14471, 6, 2, 6, arg2);
				if (this.field_14479.field_14482[class_2350.field_11035.method_10146()]) {
					this.method_14940(arg, arg2, 3, 3, 0, 4, 3, 0, field_14476, field_14476, false);
				} else {
					this.method_14940(arg, arg2, 3, 3, 0, 4, 3, 1, field_14476, field_14476, false);
					this.method_14940(arg, arg2, 3, 2, 0, 4, 2, 0, field_14473, field_14473, false);
					this.method_14940(arg, arg2, 3, 1, 0, 4, 1, 1, field_14476, field_14476, false);
				}

				if (this.field_14479.field_14482[class_2350.field_11043.method_10146()]) {
					this.method_14940(arg, arg2, 3, 3, 7, 4, 3, 7, field_14476, field_14476, false);
				} else {
					this.method_14940(arg, arg2, 3, 3, 6, 4, 3, 7, field_14476, field_14476, false);
					this.method_14940(arg, arg2, 3, 2, 7, 4, 2, 7, field_14473, field_14473, false);
					this.method_14940(arg, arg2, 3, 1, 6, 4, 1, 7, field_14476, field_14476, false);
				}

				if (this.field_14479.field_14482[class_2350.field_11039.method_10146()]) {
					this.method_14940(arg, arg2, 0, 3, 3, 0, 3, 4, field_14476, field_14476, false);
				} else {
					this.method_14940(arg, arg2, 0, 3, 3, 1, 3, 4, field_14476, field_14476, false);
					this.method_14940(arg, arg2, 0, 2, 3, 0, 2, 4, field_14473, field_14473, false);
					this.method_14940(arg, arg2, 0, 1, 3, 1, 1, 4, field_14476, field_14476, false);
				}

				if (this.field_14479.field_14482[class_2350.field_11034.method_10146()]) {
					this.method_14940(arg, arg2, 7, 3, 3, 7, 3, 4, field_14476, field_14476, false);
				} else {
					this.method_14940(arg, arg2, 6, 3, 3, 7, 3, 4, field_14476, field_14476, false);
					this.method_14940(arg, arg2, 7, 2, 3, 7, 2, 4, field_14473, field_14473, false);
					this.method_14940(arg, arg2, 6, 1, 3, 7, 1, 4, field_14476, field_14476, false);
				}
			} else if (this.field_14480 == 1) {
				this.method_14940(arg, arg2, 2, 1, 2, 2, 3, 2, field_14476, field_14476, false);
				this.method_14940(arg, arg2, 2, 1, 5, 2, 3, 5, field_14476, field_14476, false);
				this.method_14940(arg, arg2, 5, 1, 5, 5, 3, 5, field_14476, field_14476, false);
				this.method_14940(arg, arg2, 5, 1, 2, 5, 3, 2, field_14476, field_14476, false);
				this.method_14917(arg, field_14471, 2, 2, 2, arg2);
				this.method_14917(arg, field_14471, 2, 2, 5, arg2);
				this.method_14917(arg, field_14471, 5, 2, 5, arg2);
				this.method_14917(arg, field_14471, 5, 2, 2, arg2);
				this.method_14940(arg, arg2, 0, 1, 0, 1, 3, 0, field_14476, field_14476, false);
				this.method_14940(arg, arg2, 0, 1, 1, 0, 3, 1, field_14476, field_14476, false);
				this.method_14940(arg, arg2, 0, 1, 7, 1, 3, 7, field_14476, field_14476, false);
				this.method_14940(arg, arg2, 0, 1, 6, 0, 3, 6, field_14476, field_14476, false);
				this.method_14940(arg, arg2, 6, 1, 7, 7, 3, 7, field_14476, field_14476, false);
				this.method_14940(arg, arg2, 7, 1, 6, 7, 3, 6, field_14476, field_14476, false);
				this.method_14940(arg, arg2, 6, 1, 0, 7, 3, 0, field_14476, field_14476, false);
				this.method_14940(arg, arg2, 7, 1, 1, 7, 3, 1, field_14476, field_14476, false);
				this.method_14917(arg, field_14473, 1, 2, 0, arg2);
				this.method_14917(arg, field_14473, 0, 2, 1, arg2);
				this.method_14917(arg, field_14473, 1, 2, 7, arg2);
				this.method_14917(arg, field_14473, 0, 2, 6, arg2);
				this.method_14917(arg, field_14473, 6, 2, 7, arg2);
				this.method_14917(arg, field_14473, 7, 2, 6, arg2);
				this.method_14917(arg, field_14473, 6, 2, 0, arg2);
				this.method_14917(arg, field_14473, 7, 2, 1, arg2);
				if (!this.field_14479.field_14482[class_2350.field_11035.method_10146()]) {
					this.method_14940(arg, arg2, 1, 3, 0, 6, 3, 0, field_14476, field_14476, false);
					this.method_14940(arg, arg2, 1, 2, 0, 6, 2, 0, field_14473, field_14473, false);
					this.method_14940(arg, arg2, 1, 1, 0, 6, 1, 0, field_14476, field_14476, false);
				}

				if (!this.field_14479.field_14482[class_2350.field_11043.method_10146()]) {
					this.method_14940(arg, arg2, 1, 3, 7, 6, 3, 7, field_14476, field_14476, false);
					this.method_14940(arg, arg2, 1, 2, 7, 6, 2, 7, field_14473, field_14473, false);
					this.method_14940(arg, arg2, 1, 1, 7, 6, 1, 7, field_14476, field_14476, false);
				}

				if (!this.field_14479.field_14482[class_2350.field_11039.method_10146()]) {
					this.method_14940(arg, arg2, 0, 3, 1, 0, 3, 6, field_14476, field_14476, false);
					this.method_14940(arg, arg2, 0, 2, 1, 0, 2, 6, field_14473, field_14473, false);
					this.method_14940(arg, arg2, 0, 1, 1, 0, 1, 6, field_14476, field_14476, false);
				}

				if (!this.field_14479.field_14482[class_2350.field_11034.method_10146()]) {
					this.method_14940(arg, arg2, 7, 3, 1, 7, 3, 6, field_14476, field_14476, false);
					this.method_14940(arg, arg2, 7, 2, 1, 7, 2, 6, field_14473, field_14473, false);
					this.method_14940(arg, arg2, 7, 1, 1, 7, 1, 6, field_14476, field_14476, false);
				}
			} else if (this.field_14480 == 2) {
				this.method_14940(arg, arg2, 0, 1, 0, 0, 1, 7, field_14476, field_14476, false);
				this.method_14940(arg, arg2, 7, 1, 0, 7, 1, 7, field_14476, field_14476, false);
				this.method_14940(arg, arg2, 1, 1, 0, 6, 1, 0, field_14476, field_14476, false);
				this.method_14940(arg, arg2, 1, 1, 7, 6, 1, 7, field_14476, field_14476, false);
				this.method_14940(arg, arg2, 0, 2, 0, 0, 2, 7, field_14474, field_14474, false);
				this.method_14940(arg, arg2, 7, 2, 0, 7, 2, 7, field_14474, field_14474, false);
				this.method_14940(arg, arg2, 1, 2, 0, 6, 2, 0, field_14474, field_14474, false);
				this.method_14940(arg, arg2, 1, 2, 7, 6, 2, 7, field_14474, field_14474, false);
				this.method_14940(arg, arg2, 0, 3, 0, 0, 3, 7, field_14476, field_14476, false);
				this.method_14940(arg, arg2, 7, 3, 0, 7, 3, 7, field_14476, field_14476, false);
				this.method_14940(arg, arg2, 1, 3, 0, 6, 3, 0, field_14476, field_14476, false);
				this.method_14940(arg, arg2, 1, 3, 7, 6, 3, 7, field_14476, field_14476, false);
				this.method_14940(arg, arg2, 0, 1, 3, 0, 2, 4, field_14474, field_14474, false);
				this.method_14940(arg, arg2, 7, 1, 3, 7, 2, 4, field_14474, field_14474, false);
				this.method_14940(arg, arg2, 3, 1, 0, 4, 2, 0, field_14474, field_14474, false);
				this.method_14940(arg, arg2, 3, 1, 7, 4, 2, 7, field_14474, field_14474, false);
				if (this.field_14479.field_14482[class_2350.field_11035.method_10146()]) {
					this.method_14773(arg, arg2, 3, 1, 0, 4, 2, 0);
				}

				if (this.field_14479.field_14482[class_2350.field_11043.method_10146()]) {
					this.method_14773(arg, arg2, 3, 1, 7, 4, 2, 7);
				}

				if (this.field_14479.field_14482[class_2350.field_11039.method_10146()]) {
					this.method_14773(arg, arg2, 0, 1, 3, 0, 2, 4);
				}

				if (this.field_14479.field_14482[class_2350.field_11034.method_10146()]) {
					this.method_14773(arg, arg2, 7, 1, 3, 7, 2, 4);
				}
			}

			if (bl) {
				this.method_14940(arg, arg2, 3, 1, 3, 4, 1, 4, field_14476, field_14476, false);
				this.method_14940(arg, arg2, 3, 2, 3, 4, 2, 4, field_14473, field_14473, false);
				this.method_14940(arg, arg2, 3, 3, 3, 4, 3, 4, field_14476, field_14476, false);
			}

			return true;
		}
	}

	public static class class_3386 extends class_3366.class_3384 {
		public class_3386(class_2350 arg, class_3366.class_3388 arg2) {
			super(class_3773.field_16944, 1, arg, arg2, 1, 1, 1);
		}

		public class_3386(class_3485 arg, class_2487 arg2) {
			super(class_3773.field_16944, arg2);
		}

		@Override
		public boolean method_14931(class_1936 arg, Random random, class_3341 arg2, class_1923 arg3) {
			if (this.field_14479.field_14486 / 25 > 0) {
				this.method_14774(arg, arg2, 0, 0, this.field_14479.field_14482[class_2350.field_11033.method_10146()]);
			}

			if (this.field_14479.field_14487[class_2350.field_11036.method_10146()] == null) {
				this.method_14771(arg, arg2, 1, 4, 1, 6, 4, 6, field_14473);
			}

			for (int i = 1; i <= 6; i++) {
				for (int j = 1; j <= 6; j++) {
					if (random.nextInt(3) != 0) {
						int k = 2 + (random.nextInt(4) == 0 ? 0 : 1);
						class_2680 lv = class_2246.field_10562.method_9564();
						this.method_14940(arg, arg2, i, k, j, i, 3, j, lv, lv, false);
					}
				}
			}

			this.method_14940(arg, arg2, 0, 1, 0, 0, 1, 7, field_14476, field_14476, false);
			this.method_14940(arg, arg2, 7, 1, 0, 7, 1, 7, field_14476, field_14476, false);
			this.method_14940(arg, arg2, 1, 1, 0, 6, 1, 0, field_14476, field_14476, false);
			this.method_14940(arg, arg2, 1, 1, 7, 6, 1, 7, field_14476, field_14476, false);
			this.method_14940(arg, arg2, 0, 2, 0, 0, 2, 7, field_14474, field_14474, false);
			this.method_14940(arg, arg2, 7, 2, 0, 7, 2, 7, field_14474, field_14474, false);
			this.method_14940(arg, arg2, 1, 2, 0, 6, 2, 0, field_14474, field_14474, false);
			this.method_14940(arg, arg2, 1, 2, 7, 6, 2, 7, field_14474, field_14474, false);
			this.method_14940(arg, arg2, 0, 3, 0, 0, 3, 7, field_14476, field_14476, false);
			this.method_14940(arg, arg2, 7, 3, 0, 7, 3, 7, field_14476, field_14476, false);
			this.method_14940(arg, arg2, 1, 3, 0, 6, 3, 0, field_14476, field_14476, false);
			this.method_14940(arg, arg2, 1, 3, 7, 6, 3, 7, field_14476, field_14476, false);
			this.method_14940(arg, arg2, 0, 1, 3, 0, 2, 4, field_14474, field_14474, false);
			this.method_14940(arg, arg2, 7, 1, 3, 7, 2, 4, field_14474, field_14474, false);
			this.method_14940(arg, arg2, 3, 1, 0, 4, 2, 0, field_14474, field_14474, false);
			this.method_14940(arg, arg2, 3, 1, 7, 4, 2, 7, field_14474, field_14474, false);
			if (this.field_14479.field_14482[class_2350.field_11035.method_10146()]) {
				this.method_14773(arg, arg2, 3, 1, 0, 4, 2, 0);
			}

			return true;
		}
	}

	public static class class_3387 extends class_3366.class_3384 {
		private int field_14481;

		public class_3387(class_2350 arg, class_3341 arg2, int i) {
			super(class_3773.field_16957, arg, arg2);
			this.field_14481 = i & 1;
		}

		public class_3387(class_3485 arg, class_2487 arg2) {
			super(class_3773.field_16957, arg2);
		}

		@Override
		public boolean method_14931(class_1936 arg, Random random, class_3341 arg2, class_1923 arg3) {
			if (this.field_14481 == 0) {
				for (int i = 0; i < 4; i++) {
					this.method_14940(arg, arg2, 10 - i, 3 - i, 20 - i, 12 + i, 3 - i, 20, field_14476, field_14476, false);
				}

				this.method_14940(arg, arg2, 7, 0, 6, 15, 0, 16, field_14476, field_14476, false);
				this.method_14940(arg, arg2, 6, 0, 6, 6, 3, 20, field_14476, field_14476, false);
				this.method_14940(arg, arg2, 16, 0, 6, 16, 3, 20, field_14476, field_14476, false);
				this.method_14940(arg, arg2, 7, 1, 7, 7, 1, 20, field_14476, field_14476, false);
				this.method_14940(arg, arg2, 15, 1, 7, 15, 1, 20, field_14476, field_14476, false);
				this.method_14940(arg, arg2, 7, 1, 6, 9, 3, 6, field_14476, field_14476, false);
				this.method_14940(arg, arg2, 13, 1, 6, 15, 3, 6, field_14476, field_14476, false);
				this.method_14940(arg, arg2, 8, 1, 7, 9, 1, 7, field_14476, field_14476, false);
				this.method_14940(arg, arg2, 13, 1, 7, 14, 1, 7, field_14476, field_14476, false);
				this.method_14940(arg, arg2, 9, 0, 5, 13, 0, 5, field_14476, field_14476, false);
				this.method_14940(arg, arg2, 10, 0, 7, 12, 0, 7, field_14474, field_14474, false);
				this.method_14940(arg, arg2, 8, 0, 10, 8, 0, 12, field_14474, field_14474, false);
				this.method_14940(arg, arg2, 14, 0, 10, 14, 0, 12, field_14474, field_14474, false);

				for (int i = 18; i >= 7; i -= 3) {
					this.method_14917(arg, field_14471, 6, 3, i, arg2);
					this.method_14917(arg, field_14471, 16, 3, i, arg2);
				}

				this.method_14917(arg, field_14471, 10, 0, 10, arg2);
				this.method_14917(arg, field_14471, 12, 0, 10, arg2);
				this.method_14917(arg, field_14471, 10, 0, 12, arg2);
				this.method_14917(arg, field_14471, 12, 0, 12, arg2);
				this.method_14917(arg, field_14471, 8, 3, 6, arg2);
				this.method_14917(arg, field_14471, 14, 3, 6, arg2);
				this.method_14917(arg, field_14476, 4, 2, 4, arg2);
				this.method_14917(arg, field_14471, 4, 1, 4, arg2);
				this.method_14917(arg, field_14476, 4, 0, 4, arg2);
				this.method_14917(arg, field_14476, 18, 2, 4, arg2);
				this.method_14917(arg, field_14471, 18, 1, 4, arg2);
				this.method_14917(arg, field_14476, 18, 0, 4, arg2);
				this.method_14917(arg, field_14476, 4, 2, 18, arg2);
				this.method_14917(arg, field_14471, 4, 1, 18, arg2);
				this.method_14917(arg, field_14476, 4, 0, 18, arg2);
				this.method_14917(arg, field_14476, 18, 2, 18, arg2);
				this.method_14917(arg, field_14471, 18, 1, 18, arg2);
				this.method_14917(arg, field_14476, 18, 0, 18, arg2);
				this.method_14917(arg, field_14476, 9, 7, 20, arg2);
				this.method_14917(arg, field_14476, 13, 7, 20, arg2);
				this.method_14940(arg, arg2, 6, 0, 21, 7, 4, 21, field_14476, field_14476, false);
				this.method_14940(arg, arg2, 15, 0, 21, 16, 4, 21, field_14476, field_14476, false);
				this.method_14772(arg, arg2, 11, 2, 16);
			} else if (this.field_14481 == 1) {
				this.method_14940(arg, arg2, 9, 3, 18, 13, 3, 20, field_14476, field_14476, false);
				this.method_14940(arg, arg2, 9, 0, 18, 9, 2, 18, field_14476, field_14476, false);
				this.method_14940(arg, arg2, 13, 0, 18, 13, 2, 18, field_14476, field_14476, false);
				int i = 9;
				int j = 20;
				int k = 5;

				for (int l = 0; l < 2; l++) {
					this.method_14917(arg, field_14476, i, 6, 20, arg2);
					this.method_14917(arg, field_14471, i, 5, 20, arg2);
					this.method_14917(arg, field_14476, i, 4, 20, arg2);
					i = 13;
				}

				this.method_14940(arg, arg2, 7, 3, 7, 15, 3, 14, field_14476, field_14476, false);
				int var11 = 10;

				for (int l = 0; l < 2; l++) {
					this.method_14940(arg, arg2, var11, 0, 10, var11, 6, 10, field_14476, field_14476, false);
					this.method_14940(arg, arg2, var11, 0, 12, var11, 6, 12, field_14476, field_14476, false);
					this.method_14917(arg, field_14471, var11, 0, 10, arg2);
					this.method_14917(arg, field_14471, var11, 0, 12, arg2);
					this.method_14917(arg, field_14471, var11, 4, 10, arg2);
					this.method_14917(arg, field_14471, var11, 4, 12, arg2);
					var11 = 12;
				}

				var11 = 8;

				for (int l = 0; l < 2; l++) {
					this.method_14940(arg, arg2, var11, 0, 7, var11, 2, 7, field_14476, field_14476, false);
					this.method_14940(arg, arg2, var11, 0, 14, var11, 2, 14, field_14476, field_14476, false);
					var11 = 14;
				}

				this.method_14940(arg, arg2, 8, 3, 8, 8, 3, 13, field_14474, field_14474, false);
				this.method_14940(arg, arg2, 14, 3, 8, 14, 3, 13, field_14474, field_14474, false);
				this.method_14772(arg, arg2, 11, 5, 13);
			}

			return true;
		}
	}

	static class class_3388 {
		private final int field_14486;
		private final class_3366.class_3388[] field_14487 = new class_3366.class_3388[6];
		private final boolean[] field_14482 = new boolean[6];
		private boolean field_14485;
		private boolean field_14484;
		private int field_14483;

		public class_3388(int i) {
			this.field_14486 = i;
		}

		public void method_14786(class_2350 arg, class_3366.class_3388 arg2) {
			this.field_14487[arg.method_10146()] = arg2;
			arg2.field_14487[arg.method_10153().method_10146()] = this;
		}

		public void method_14780() {
			for (int i = 0; i < 6; i++) {
				this.field_14482[i] = this.field_14487[i] != null;
			}
		}

		public boolean method_14783(int i) {
			if (this.field_14484) {
				return true;
			} else {
				this.field_14483 = i;

				for (int j = 0; j < 6; j++) {
					if (this.field_14487[j] != null && this.field_14482[j] && this.field_14487[j].field_14483 != i && this.field_14487[j].method_14783(i)) {
						return true;
					}
				}

				return false;
			}
		}

		public boolean method_14785() {
			return this.field_14486 >= 75;
		}

		public int method_14781() {
			int i = 0;

			for (int j = 0; j < 6; j++) {
				if (this.field_14482[j]) {
					i++;
				}
			}

			return i;
		}
	}
}
