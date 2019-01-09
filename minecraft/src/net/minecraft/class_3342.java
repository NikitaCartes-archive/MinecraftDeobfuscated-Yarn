package net.minecraft;

import com.google.common.collect.Lists;
import java.util.List;
import java.util.Random;

public class class_3342 {
	private static final class_3492 field_14383 = new class_3492().method_15133(true).method_16184(class_3793.field_16718);
	private static final class_3492 field_14389 = new class_3492().method_15133(true).method_16184(class_3793.field_16721);
	private static final class_3342.class_3344 field_14390 = new class_3342.class_3344() {
		@Override
		public void method_14688() {
		}

		@Override
		public boolean method_14687(class_3485 arg, int i, class_3342.class_3343 arg2, class_2338 arg3, List<class_3443> list, Random random) {
			if (i > 8) {
				return false;
			} else {
				class_2470 lv = arg2.field_15434.method_15113();
				class_3342.class_3343 lv2 = class_3342.method_14681(list, class_3342.method_14684(arg, arg2, arg3, "base_floor", lv, true));
				int j = random.nextInt(3);
				if (j == 0) {
					lv2 = class_3342.method_14681(list, class_3342.method_14684(arg, lv2, new class_2338(-1, 4, -1), "base_roof", lv, true));
				} else if (j == 1) {
					lv2 = class_3342.method_14681(list, class_3342.method_14684(arg, lv2, new class_2338(-1, 0, -1), "second_floor_2", lv, false));
					lv2 = class_3342.method_14681(list, class_3342.method_14684(arg, lv2, new class_2338(-1, 8, -1), "second_roof", lv, false));
					class_3342.method_14673(arg, class_3342.field_14386, i + 1, lv2, null, list, random);
				} else if (j == 2) {
					lv2 = class_3342.method_14681(list, class_3342.method_14684(arg, lv2, new class_2338(-1, 0, -1), "second_floor_2", lv, false));
					lv2 = class_3342.method_14681(list, class_3342.method_14684(arg, lv2, new class_2338(-1, 4, -1), "third_floor_2", lv, false));
					lv2 = class_3342.method_14681(list, class_3342.method_14684(arg, lv2, new class_2338(-1, 8, -1), "third_roof", lv, true));
					class_3342.method_14673(arg, class_3342.field_14386, i + 1, lv2, null, list, random);
				}

				return true;
			}
		}
	};
	private static final List<class_3545<class_2470, class_2338>> field_14385 = Lists.<class_3545<class_2470, class_2338>>newArrayList(
		new class_3545<>(class_2470.field_11467, new class_2338(1, -1, 0)),
		new class_3545<>(class_2470.field_11463, new class_2338(6, -1, 1)),
		new class_3545<>(class_2470.field_11465, new class_2338(0, -1, 5)),
		new class_3545<>(class_2470.field_11464, new class_2338(5, -1, 6))
	);
	private static final class_3342.class_3344 field_14386 = new class_3342.class_3344() {
		@Override
		public void method_14688() {
		}

		@Override
		public boolean method_14687(class_3485 arg, int i, class_3342.class_3343 arg2, class_2338 arg3, List<class_3443> list, Random random) {
			class_2470 lv = arg2.field_15434.method_15113();
			class_3342.class_3343 lv2 = class_3342.method_14681(
				list, class_3342.method_14684(arg, arg2, new class_2338(3 + random.nextInt(2), -3, 3 + random.nextInt(2)), "tower_base", lv, true)
			);
			lv2 = class_3342.method_14681(list, class_3342.method_14684(arg, lv2, new class_2338(0, 7, 0), "tower_piece", lv, true));
			class_3342.class_3343 lv3 = random.nextInt(3) == 0 ? lv2 : null;
			int j = 1 + random.nextInt(3);

			for (int k = 0; k < j; k++) {
				lv2 = class_3342.method_14681(list, class_3342.method_14684(arg, lv2, new class_2338(0, 4, 0), "tower_piece", lv, true));
				if (k < j - 1 && random.nextBoolean()) {
					lv3 = lv2;
				}
			}

			if (lv3 != null) {
				for (class_3545<class_2470, class_2338> lv4 : class_3342.field_14385) {
					if (random.nextBoolean()) {
						class_3342.class_3343 lv5 = class_3342.method_14681(
							list, class_3342.method_14684(arg, lv3, lv4.method_15441(), "bridge_end", lv.method_10501(lv4.method_15442()), true)
						);
						class_3342.method_14673(arg, class_3342.field_14387, i + 1, lv5, null, list, random);
					}
				}

				lv2 = class_3342.method_14681(list, class_3342.method_14684(arg, lv2, new class_2338(-1, 4, -1), "tower_top", lv, true));
			} else {
				if (i != 7) {
					return class_3342.method_14673(arg, class_3342.field_14384, i + 1, lv2, null, list, random);
				}

				lv2 = class_3342.method_14681(list, class_3342.method_14684(arg, lv2, new class_2338(-1, 4, -1), "tower_top", lv, true));
			}

			return true;
		}
	};
	private static final class_3342.class_3344 field_14387 = new class_3342.class_3344() {
		public boolean field_14394;

		@Override
		public void method_14688() {
			this.field_14394 = false;
		}

		@Override
		public boolean method_14687(class_3485 arg, int i, class_3342.class_3343 arg2, class_2338 arg3, List<class_3443> list, Random random) {
			class_2470 lv = arg2.field_15434.method_15113();
			int j = random.nextInt(4) + 1;
			class_3342.class_3343 lv2 = class_3342.method_14681(list, class_3342.method_14684(arg, arg2, new class_2338(0, 0, -4), "bridge_piece", lv, true));
			lv2.field_15316 = -1;
			int k = 0;

			for (int l = 0; l < j; l++) {
				if (random.nextBoolean()) {
					lv2 = class_3342.method_14681(list, class_3342.method_14684(arg, lv2, new class_2338(0, k, -4), "bridge_piece", lv, true));
					k = 0;
				} else {
					if (random.nextBoolean()) {
						lv2 = class_3342.method_14681(list, class_3342.method_14684(arg, lv2, new class_2338(0, k, -4), "bridge_steep_stairs", lv, true));
					} else {
						lv2 = class_3342.method_14681(list, class_3342.method_14684(arg, lv2, new class_2338(0, k, -8), "bridge_gentle_stairs", lv, true));
					}

					k = 4;
				}
			}

			if (!this.field_14394 && random.nextInt(10 - i) == 0) {
				class_3342.method_14681(list, class_3342.method_14684(arg, lv2, new class_2338(-8 + random.nextInt(8), k, -70 + random.nextInt(10)), "ship", lv, true));
				this.field_14394 = true;
			} else if (!class_3342.method_14673(arg, class_3342.field_14390, i + 1, lv2, new class_2338(-3, k + 1, -11), list, random)) {
				return false;
			}

			lv2 = class_3342.method_14681(list, class_3342.method_14684(arg, lv2, new class_2338(4, k, 0), "bridge_end", lv.method_10501(class_2470.field_11464), true));
			lv2.field_15316 = -1;
			return true;
		}
	};
	private static final List<class_3545<class_2470, class_2338>> field_14388 = Lists.<class_3545<class_2470, class_2338>>newArrayList(
		new class_3545<>(class_2470.field_11467, new class_2338(4, -1, 0)),
		new class_3545<>(class_2470.field_11463, new class_2338(12, -1, 4)),
		new class_3545<>(class_2470.field_11465, new class_2338(0, -1, 8)),
		new class_3545<>(class_2470.field_11464, new class_2338(8, -1, 12))
	);
	private static final class_3342.class_3344 field_14384 = new class_3342.class_3344() {
		@Override
		public void method_14688() {
		}

		@Override
		public boolean method_14687(class_3485 arg, int i, class_3342.class_3343 arg2, class_2338 arg3, List<class_3443> list, Random random) {
			class_2470 lv = arg2.field_15434.method_15113();
			class_3342.class_3343 lv2 = class_3342.method_14681(list, class_3342.method_14684(arg, arg2, new class_2338(-3, 4, -3), "fat_tower_base", lv, true));
			lv2 = class_3342.method_14681(list, class_3342.method_14684(arg, lv2, new class_2338(0, 4, 0), "fat_tower_middle", lv, true));

			for (int j = 0; j < 2 && random.nextInt(3) != 0; j++) {
				lv2 = class_3342.method_14681(list, class_3342.method_14684(arg, lv2, new class_2338(0, 8, 0), "fat_tower_middle", lv, true));

				for (class_3545<class_2470, class_2338> lv3 : class_3342.field_14388) {
					if (random.nextBoolean()) {
						class_3342.class_3343 lv4 = class_3342.method_14681(
							list, class_3342.method_14684(arg, lv2, lv3.method_15441(), "bridge_end", lv.method_10501(lv3.method_15442()), true)
						);
						class_3342.method_14673(arg, class_3342.field_14387, i + 1, lv4, null, list, random);
					}
				}
			}

			lv2 = class_3342.method_14681(list, class_3342.method_14684(arg, lv2, new class_2338(-2, 8, -2), "fat_tower_top", lv, true));
			return true;
		}
	};

	private static class_3342.class_3343 method_14684(class_3485 arg, class_3342.class_3343 arg2, class_2338 arg3, String string, class_2470 arg4, boolean bl) {
		class_3342.class_3343 lv = new class_3342.class_3343(arg, string, arg2.field_15432, arg4, bl);
		class_2338 lv2 = arg2.field_15433.method_15180(arg2.field_15434, arg3, lv.field_15434, class_2338.field_10980);
		lv.method_14922(lv2.method_10263(), lv2.method_10264(), lv2.method_10260());
		return lv;
	}

	public static void method_14679(class_3485 arg, class_2338 arg2, class_2470 arg3, List<class_3443> list, Random random) {
		field_14384.method_14688();
		field_14390.method_14688();
		field_14387.method_14688();
		field_14386.method_14688();
		class_3342.class_3343 lv = method_14681(list, new class_3342.class_3343(arg, "base_floor", arg2, arg3, true));
		lv = method_14681(list, method_14684(arg, lv, new class_2338(-1, 0, -1), "second_floor_1", arg3, false));
		lv = method_14681(list, method_14684(arg, lv, new class_2338(-1, 4, -1), "third_floor_1", arg3, false));
		lv = method_14681(list, method_14684(arg, lv, new class_2338(-1, 8, -1), "third_roof", arg3, true));
		method_14673(arg, field_14386, 1, lv, null, list, random);
	}

	private static class_3342.class_3343 method_14681(List<class_3443> list, class_3342.class_3343 arg) {
		list.add(arg);
		return arg;
	}

	private static boolean method_14673(
		class_3485 arg, class_3342.class_3344 arg2, int i, class_3342.class_3343 arg3, class_2338 arg4, List<class_3443> list, Random random
	) {
		if (i > 8) {
			return false;
		} else {
			List<class_3443> list2 = Lists.<class_3443>newArrayList();
			if (arg2.method_14687(arg, i, arg3, arg4, list2, random)) {
				boolean bl = false;
				int j = random.nextInt();

				for (class_3443 lv : list2) {
					lv.field_15316 = j;
					class_3443 lv2 = class_3443.method_14932(list, lv.method_14935());
					if (lv2 != null && lv2.field_15316 != arg3.field_15316) {
						bl = true;
						break;
					}
				}

				if (!bl) {
					list.addAll(list2);
					return true;
				}
			}

			return false;
		}
	}

	public static class class_3343 extends class_3470 {
		private final String field_14391;
		private final class_2470 field_14393;
		private final boolean field_14392;

		public class_3343(class_3485 arg, String string, class_2338 arg2, class_2470 arg3, boolean bl) {
			super(class_3773.field_16936, 0);
			this.field_14391 = string;
			this.field_15432 = arg2;
			this.field_14393 = arg3;
			this.field_14392 = bl;
			this.method_14686(arg);
		}

		public class_3343(class_3485 arg, class_2487 arg2) {
			super(class_3773.field_16936, arg2);
			this.field_14391 = arg2.method_10558("Template");
			this.field_14393 = class_2470.valueOf(arg2.method_10558("Rot"));
			this.field_14392 = arg2.method_10577("OW");
			this.method_14686(arg);
		}

		private void method_14686(class_3485 arg) {
			class_3499 lv = arg.method_15091(new class_2960("end_city/" + this.field_14391));
			class_3492 lv2 = (this.field_14392 ? class_3342.field_14383 : class_3342.field_14389).method_15128().method_15123(this.field_14393);
			this.method_15027(lv, this.field_15432, lv2);
		}

		@Override
		protected void method_14943(class_2487 arg) {
			super.method_14943(arg);
			arg.method_10582("Template", this.field_14391);
			arg.method_10582("Rot", this.field_14393.name());
			arg.method_10556("OW", this.field_14392);
		}

		@Override
		protected void method_15026(String string, class_2338 arg, class_1936 arg2, Random random, class_3341 arg3) {
			if (string.startsWith("Chest")) {
				class_2338 lv = arg.method_10074();
				if (arg3.method_14662(lv)) {
					class_2621.method_11287(arg2, random, lv, class_39.field_274);
				}
			} else if (string.startsWith("Sentry")) {
				class_1606 lv2 = new class_1606(arg2.method_8410());
				lv2.method_5814((double)arg.method_10263() + 0.5, (double)arg.method_10264() + 0.5, (double)arg.method_10260() + 0.5);
				lv2.method_7125(arg);
				arg2.method_8649(lv2);
			} else if (string.startsWith("Elytra")) {
				class_1533 lv3 = new class_1533(arg2.method_8410(), arg, this.field_14393.method_10503(class_2350.field_11035));
				lv3.method_6933(new class_1799(class_1802.field_8833), false);
				arg2.method_8649(lv3);
			}
		}
	}

	interface class_3344 {
		void method_14688();

		boolean method_14687(class_3485 arg, int i, class_3342.class_3343 arg2, class_2338 arg3, List<class_3443> list, Random random);
	}
}
