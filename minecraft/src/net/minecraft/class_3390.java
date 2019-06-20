package net.minecraft;

import com.google.common.collect.Lists;
import java.util.List;
import java.util.Random;
import javax.annotation.Nullable;

public class class_3390 {
	private static final class_3390.class_3404[] field_14494 = new class_3390.class_3404[]{
		new class_3390.class_3404(class_3390.class_3393.class, 30, 0, true),
		new class_3390.class_3404(class_3390.class_3391.class, 10, 4),
		new class_3390.class_3404(class_3390.class_3405.class, 10, 4),
		new class_3390.class_3404(class_3390.class_3406.class, 10, 3),
		new class_3390.class_3404(class_3390.class_3402.class, 5, 2),
		new class_3390.class_3404(class_3390.class_3396.class, 5, 1)
	};
	private static final class_3390.class_3404[] field_14493 = new class_3390.class_3404[]{
		new class_3390.class_3404(class_3390.class_3399.class, 25, 0, true),
		new class_3390.class_3404(class_3390.class_3397.class, 15, 5),
		new class_3390.class_3404(class_3390.class_3400.class, 5, 10),
		new class_3390.class_3404(class_3390.class_3398.class, 5, 10),
		new class_3390.class_3404(class_3390.class_3394.class, 10, 3, true),
		new class_3390.class_3404(class_3390.class_3395.class, 7, 2),
		new class_3390.class_3404(class_3390.class_3401.class, 5, 2)
	};

	private static class_3390.class_3403 method_14795(class_3390.class_3404 arg, List<class_3443> list, Random random, int i, int j, int k, class_2350 arg2, int l) {
		Class<? extends class_3390.class_3403> class_ = arg.field_14501;
		class_3390.class_3403 lv = null;
		if (class_ == class_3390.class_3393.class) {
			lv = class_3390.class_3393.method_14798(list, random, i, j, k, arg2, l);
		} else if (class_ == class_3390.class_3391.class) {
			lv = class_3390.class_3391.method_14796(list, i, j, k, arg2, l);
		} else if (class_ == class_3390.class_3405.class) {
			lv = class_3390.class_3405.method_14817(list, i, j, k, arg2, l);
		} else if (class_ == class_3390.class_3406.class) {
			lv = class_3390.class_3406.method_14818(list, i, j, k, l, arg2);
		} else if (class_ == class_3390.class_3402.class) {
			lv = class_3390.class_3402.method_14807(list, i, j, k, l, arg2);
		} else if (class_ == class_3390.class_3396.class) {
			lv = class_3390.class_3396.method_14801(list, random, i, j, k, arg2, l);
		} else if (class_ == class_3390.class_3399.class) {
			lv = class_3390.class_3399.method_14804(list, i, j, k, arg2, l);
		} else if (class_ == class_3390.class_3400.class) {
			lv = class_3390.class_3400.method_14805(list, random, i, j, k, arg2, l);
		} else if (class_ == class_3390.class_3398.class) {
			lv = class_3390.class_3398.method_14803(list, random, i, j, k, arg2, l);
		} else if (class_ == class_3390.class_3394.class) {
			lv = class_3390.class_3394.method_14799(list, i, j, k, arg2, l);
		} else if (class_ == class_3390.class_3395.class) {
			lv = class_3390.class_3395.method_14800(list, i, j, k, arg2, l);
		} else if (class_ == class_3390.class_3397.class) {
			lv = class_3390.class_3397.method_14802(list, i, j, k, arg2, l);
		} else if (class_ == class_3390.class_3401.class) {
			lv = class_3390.class_3401.method_14806(list, i, j, k, arg2, l);
		}

		return lv;
	}

	public static class class_3391 extends class_3390.class_3403 {
		public class_3391(int i, class_3341 arg, class_2350 arg2) {
			super(class_3773.field_16926, i);
			this.method_14926(arg2);
			this.field_15315 = arg;
		}

		protected class_3391(Random random, int i, int j) {
			super(class_3773.field_16926, 0);
			this.method_14926(class_2350.class_2353.field_11062.method_10183(random));
			if (this.method_14934().method_10166() == class_2350.class_2351.field_11051) {
				this.field_15315 = new class_3341(i, 64, j, i + 19 - 1, 73, j + 19 - 1);
			} else {
				this.field_15315 = new class_3341(i, 64, j, i + 19 - 1, 73, j + 19 - 1);
			}
		}

		protected class_3391(class_3773 arg, class_2487 arg2) {
			super(arg, arg2);
		}

		public class_3391(class_3485 arg, class_2487 arg2) {
			this(class_3773.field_16926, arg2);
		}

		@Override
		public void method_14918(class_3443 arg, List<class_3443> list, Random random) {
			this.method_14814((class_3390.class_3407)arg, list, random, 8, 3, false);
			this.method_14812((class_3390.class_3407)arg, list, random, 3, 8, false);
			this.method_14808((class_3390.class_3407)arg, list, random, 3, 8, false);
		}

		public static class_3390.class_3391 method_14796(List<class_3443> list, int i, int j, int k, class_2350 arg, int l) {
			class_3341 lv = class_3341.method_14667(i, j, k, -8, -3, 0, 19, 10, 19, arg);
			return method_14809(lv) && class_3443.method_14932(list, lv) == null ? new class_3390.class_3391(l, lv, arg) : null;
		}

		@Override
		public boolean method_14931(class_1936 arg, Random random, class_3341 arg2, class_1923 arg3) {
			this.method_14940(arg, arg2, 7, 3, 0, 11, 4, 18, class_2246.field_10266.method_9564(), class_2246.field_10266.method_9564(), false);
			this.method_14940(arg, arg2, 0, 3, 7, 18, 4, 11, class_2246.field_10266.method_9564(), class_2246.field_10266.method_9564(), false);
			this.method_14940(arg, arg2, 8, 5, 0, 10, 7, 18, class_2246.field_10124.method_9564(), class_2246.field_10124.method_9564(), false);
			this.method_14940(arg, arg2, 0, 5, 8, 18, 7, 10, class_2246.field_10124.method_9564(), class_2246.field_10124.method_9564(), false);
			this.method_14940(arg, arg2, 7, 5, 0, 7, 5, 7, class_2246.field_10266.method_9564(), class_2246.field_10266.method_9564(), false);
			this.method_14940(arg, arg2, 7, 5, 11, 7, 5, 18, class_2246.field_10266.method_9564(), class_2246.field_10266.method_9564(), false);
			this.method_14940(arg, arg2, 11, 5, 0, 11, 5, 7, class_2246.field_10266.method_9564(), class_2246.field_10266.method_9564(), false);
			this.method_14940(arg, arg2, 11, 5, 11, 11, 5, 18, class_2246.field_10266.method_9564(), class_2246.field_10266.method_9564(), false);
			this.method_14940(arg, arg2, 0, 5, 7, 7, 5, 7, class_2246.field_10266.method_9564(), class_2246.field_10266.method_9564(), false);
			this.method_14940(arg, arg2, 11, 5, 7, 18, 5, 7, class_2246.field_10266.method_9564(), class_2246.field_10266.method_9564(), false);
			this.method_14940(arg, arg2, 0, 5, 11, 7, 5, 11, class_2246.field_10266.method_9564(), class_2246.field_10266.method_9564(), false);
			this.method_14940(arg, arg2, 11, 5, 11, 18, 5, 11, class_2246.field_10266.method_9564(), class_2246.field_10266.method_9564(), false);
			this.method_14940(arg, arg2, 7, 2, 0, 11, 2, 5, class_2246.field_10266.method_9564(), class_2246.field_10266.method_9564(), false);
			this.method_14940(arg, arg2, 7, 2, 13, 11, 2, 18, class_2246.field_10266.method_9564(), class_2246.field_10266.method_9564(), false);
			this.method_14940(arg, arg2, 7, 0, 0, 11, 1, 3, class_2246.field_10266.method_9564(), class_2246.field_10266.method_9564(), false);
			this.method_14940(arg, arg2, 7, 0, 15, 11, 1, 18, class_2246.field_10266.method_9564(), class_2246.field_10266.method_9564(), false);

			for (int i = 7; i <= 11; i++) {
				for (int j = 0; j <= 2; j++) {
					this.method_14936(arg, class_2246.field_10266.method_9564(), i, -1, j, arg2);
					this.method_14936(arg, class_2246.field_10266.method_9564(), i, -1, 18 - j, arg2);
				}
			}

			this.method_14940(arg, arg2, 0, 2, 7, 5, 2, 11, class_2246.field_10266.method_9564(), class_2246.field_10266.method_9564(), false);
			this.method_14940(arg, arg2, 13, 2, 7, 18, 2, 11, class_2246.field_10266.method_9564(), class_2246.field_10266.method_9564(), false);
			this.method_14940(arg, arg2, 0, 0, 7, 3, 1, 11, class_2246.field_10266.method_9564(), class_2246.field_10266.method_9564(), false);
			this.method_14940(arg, arg2, 15, 0, 7, 18, 1, 11, class_2246.field_10266.method_9564(), class_2246.field_10266.method_9564(), false);

			for (int i = 0; i <= 2; i++) {
				for (int j = 7; j <= 11; j++) {
					this.method_14936(arg, class_2246.field_10266.method_9564(), i, -1, j, arg2);
					this.method_14936(arg, class_2246.field_10266.method_9564(), 18 - i, -1, j, arg2);
				}
			}

			return true;
		}
	}

	public static class class_3392 extends class_3390.class_3403 {
		private final int field_14495;

		public class_3392(int i, Random random, class_3341 arg, class_2350 arg2) {
			super(class_3773.field_16903, i);
			this.method_14926(arg2);
			this.field_15315 = arg;
			this.field_14495 = random.nextInt();
		}

		public class_3392(class_3485 arg, class_2487 arg2) {
			super(class_3773.field_16903, arg2);
			this.field_14495 = arg2.method_10550("Seed");
		}

		public static class_3390.class_3392 method_14797(List<class_3443> list, Random random, int i, int j, int k, class_2350 arg, int l) {
			class_3341 lv = class_3341.method_14667(i, j, k, -1, -3, 0, 5, 10, 8, arg);
			return method_14809(lv) && class_3443.method_14932(list, lv) == null ? new class_3390.class_3392(l, random, lv, arg) : null;
		}

		@Override
		protected void method_14943(class_2487 arg) {
			super.method_14943(arg);
			arg.method_10569("Seed", this.field_14495);
		}

		@Override
		public boolean method_14931(class_1936 arg, Random random, class_3341 arg2, class_1923 arg3) {
			Random random2 = new Random((long)this.field_14495);

			for (int i = 0; i <= 4; i++) {
				for (int j = 3; j <= 4; j++) {
					int k = random2.nextInt(8);
					this.method_14940(arg, arg2, i, j, 0, i, j, k, class_2246.field_10266.method_9564(), class_2246.field_10266.method_9564(), false);
				}
			}

			int i = random2.nextInt(8);
			this.method_14940(arg, arg2, 0, 5, 0, 0, 5, i, class_2246.field_10266.method_9564(), class_2246.field_10266.method_9564(), false);
			i = random2.nextInt(8);
			this.method_14940(arg, arg2, 4, 5, 0, 4, 5, i, class_2246.field_10266.method_9564(), class_2246.field_10266.method_9564(), false);

			for (int ix = 0; ix <= 4; ix++) {
				int j = random2.nextInt(5);
				this.method_14940(arg, arg2, ix, 2, 0, ix, 2, j, class_2246.field_10266.method_9564(), class_2246.field_10266.method_9564(), false);
			}

			for (int ix = 0; ix <= 4; ix++) {
				for (int j = 0; j <= 1; j++) {
					int k = random2.nextInt(3);
					this.method_14940(arg, arg2, ix, j, 0, ix, j, k, class_2246.field_10266.method_9564(), class_2246.field_10266.method_9564(), false);
				}
			}

			return true;
		}
	}

	public static class class_3393 extends class_3390.class_3403 {
		public class_3393(int i, Random random, class_3341 arg, class_2350 arg2) {
			super(class_3773.field_16917, i);
			this.method_14926(arg2);
			this.field_15315 = arg;
		}

		public class_3393(class_3485 arg, class_2487 arg2) {
			super(class_3773.field_16917, arg2);
		}

		@Override
		public void method_14918(class_3443 arg, List<class_3443> list, Random random) {
			this.method_14814((class_3390.class_3407)arg, list, random, 1, 3, false);
		}

		public static class_3390.class_3393 method_14798(List<class_3443> list, Random random, int i, int j, int k, class_2350 arg, int l) {
			class_3341 lv = class_3341.method_14667(i, j, k, -1, -3, 0, 5, 10, 19, arg);
			return method_14809(lv) && class_3443.method_14932(list, lv) == null ? new class_3390.class_3393(l, random, lv, arg) : null;
		}

		@Override
		public boolean method_14931(class_1936 arg, Random random, class_3341 arg2, class_1923 arg3) {
			this.method_14940(arg, arg2, 0, 3, 0, 4, 4, 18, class_2246.field_10266.method_9564(), class_2246.field_10266.method_9564(), false);
			this.method_14940(arg, arg2, 1, 5, 0, 3, 7, 18, class_2246.field_10124.method_9564(), class_2246.field_10124.method_9564(), false);
			this.method_14940(arg, arg2, 0, 5, 0, 0, 5, 18, class_2246.field_10266.method_9564(), class_2246.field_10266.method_9564(), false);
			this.method_14940(arg, arg2, 4, 5, 0, 4, 5, 18, class_2246.field_10266.method_9564(), class_2246.field_10266.method_9564(), false);
			this.method_14940(arg, arg2, 0, 2, 0, 4, 2, 5, class_2246.field_10266.method_9564(), class_2246.field_10266.method_9564(), false);
			this.method_14940(arg, arg2, 0, 2, 13, 4, 2, 18, class_2246.field_10266.method_9564(), class_2246.field_10266.method_9564(), false);
			this.method_14940(arg, arg2, 0, 0, 0, 4, 1, 3, class_2246.field_10266.method_9564(), class_2246.field_10266.method_9564(), false);
			this.method_14940(arg, arg2, 0, 0, 15, 4, 1, 18, class_2246.field_10266.method_9564(), class_2246.field_10266.method_9564(), false);

			for (int i = 0; i <= 4; i++) {
				for (int j = 0; j <= 2; j++) {
					this.method_14936(arg, class_2246.field_10266.method_9564(), i, -1, j, arg2);
					this.method_14936(arg, class_2246.field_10266.method_9564(), i, -1, 18 - j, arg2);
				}
			}

			class_2680 lv = class_2246.field_10364
				.method_9564()
				.method_11657(class_2354.field_10905, Boolean.valueOf(true))
				.method_11657(class_2354.field_10904, Boolean.valueOf(true));
			class_2680 lv2 = lv.method_11657(class_2354.field_10907, Boolean.valueOf(true));
			class_2680 lv3 = lv.method_11657(class_2354.field_10903, Boolean.valueOf(true));
			this.method_14940(arg, arg2, 0, 1, 1, 0, 4, 1, lv2, lv2, false);
			this.method_14940(arg, arg2, 0, 3, 4, 0, 4, 4, lv2, lv2, false);
			this.method_14940(arg, arg2, 0, 3, 14, 0, 4, 14, lv2, lv2, false);
			this.method_14940(arg, arg2, 0, 1, 17, 0, 4, 17, lv2, lv2, false);
			this.method_14940(arg, arg2, 4, 1, 1, 4, 4, 1, lv3, lv3, false);
			this.method_14940(arg, arg2, 4, 3, 4, 4, 4, 4, lv3, lv3, false);
			this.method_14940(arg, arg2, 4, 3, 14, 4, 4, 14, lv3, lv3, false);
			this.method_14940(arg, arg2, 4, 1, 17, 4, 4, 17, lv3, lv3, false);
			return true;
		}
	}

	public static class class_3394 extends class_3390.class_3403 {
		public class_3394(int i, class_3341 arg, class_2350 arg2) {
			super(class_3773.field_16930, i);
			this.method_14926(arg2);
			this.field_15315 = arg;
		}

		public class_3394(class_3485 arg, class_2487 arg2) {
			super(class_3773.field_16930, arg2);
		}

		@Override
		public void method_14918(class_3443 arg, List<class_3443> list, Random random) {
			this.method_14814((class_3390.class_3407)arg, list, random, 1, 0, true);
		}

		public static class_3390.class_3394 method_14799(List<class_3443> list, int i, int j, int k, class_2350 arg, int l) {
			class_3341 lv = class_3341.method_14667(i, j, k, -1, -7, 0, 5, 14, 10, arg);
			return method_14809(lv) && class_3443.method_14932(list, lv) == null ? new class_3390.class_3394(l, lv, arg) : null;
		}

		@Override
		public boolean method_14931(class_1936 arg, Random random, class_3341 arg2, class_1923 arg3) {
			class_2680 lv = class_2246.field_10159.method_9564().method_11657(class_2510.field_11571, class_2350.field_11035);
			class_2680 lv2 = class_2246.field_10364
				.method_9564()
				.method_11657(class_2354.field_10905, Boolean.valueOf(true))
				.method_11657(class_2354.field_10904, Boolean.valueOf(true));

			for (int i = 0; i <= 9; i++) {
				int j = Math.max(1, 7 - i);
				int k = Math.min(Math.max(j + 5, 14 - i), 13);
				int l = i;
				this.method_14940(arg, arg2, 0, 0, i, 4, j, i, class_2246.field_10266.method_9564(), class_2246.field_10266.method_9564(), false);
				this.method_14940(arg, arg2, 1, j + 1, i, 3, k - 1, i, class_2246.field_10124.method_9564(), class_2246.field_10124.method_9564(), false);
				if (i <= 6) {
					this.method_14917(arg, lv, 1, j + 1, i, arg2);
					this.method_14917(arg, lv, 2, j + 1, i, arg2);
					this.method_14917(arg, lv, 3, j + 1, i, arg2);
				}

				this.method_14940(arg, arg2, 0, k, i, 4, k, i, class_2246.field_10266.method_9564(), class_2246.field_10266.method_9564(), false);
				this.method_14940(arg, arg2, 0, j + 1, i, 0, k - 1, i, class_2246.field_10266.method_9564(), class_2246.field_10266.method_9564(), false);
				this.method_14940(arg, arg2, 4, j + 1, i, 4, k - 1, i, class_2246.field_10266.method_9564(), class_2246.field_10266.method_9564(), false);
				if ((i & 1) == 0) {
					this.method_14940(arg, arg2, 0, j + 2, i, 0, j + 3, i, lv2, lv2, false);
					this.method_14940(arg, arg2, 4, j + 2, i, 4, j + 3, i, lv2, lv2, false);
				}

				for (int m = 0; m <= 4; m++) {
					this.method_14936(arg, class_2246.field_10266.method_9564(), m, -1, l, arg2);
				}
			}

			return true;
		}
	}

	public static class class_3395 extends class_3390.class_3403 {
		public class_3395(int i, class_3341 arg, class_2350 arg2) {
			super(class_3773.field_16943, i);
			this.method_14926(arg2);
			this.field_15315 = arg;
		}

		public class_3395(class_3485 arg, class_2487 arg2) {
			super(class_3773.field_16943, arg2);
		}

		@Override
		public void method_14918(class_3443 arg, List<class_3443> list, Random random) {
			int i = 1;
			class_2350 lv = this.method_14934();
			if (lv == class_2350.field_11039 || lv == class_2350.field_11043) {
				i = 5;
			}

			this.method_14812((class_3390.class_3407)arg, list, random, 0, i, random.nextInt(8) > 0);
			this.method_14808((class_3390.class_3407)arg, list, random, 0, i, random.nextInt(8) > 0);
		}

		public static class_3390.class_3395 method_14800(List<class_3443> list, int i, int j, int k, class_2350 arg, int l) {
			class_3341 lv = class_3341.method_14667(i, j, k, -3, 0, 0, 9, 7, 9, arg);
			return method_14809(lv) && class_3443.method_14932(list, lv) == null ? new class_3390.class_3395(l, lv, arg) : null;
		}

		@Override
		public boolean method_14931(class_1936 arg, Random random, class_3341 arg2, class_1923 arg3) {
			class_2680 lv = class_2246.field_10364
				.method_9564()
				.method_11657(class_2354.field_10905, Boolean.valueOf(true))
				.method_11657(class_2354.field_10904, Boolean.valueOf(true));
			class_2680 lv2 = class_2246.field_10364
				.method_9564()
				.method_11657(class_2354.field_10903, Boolean.valueOf(true))
				.method_11657(class_2354.field_10907, Boolean.valueOf(true));
			this.method_14940(arg, arg2, 0, 0, 0, 8, 1, 8, class_2246.field_10266.method_9564(), class_2246.field_10266.method_9564(), false);
			this.method_14940(arg, arg2, 0, 2, 0, 8, 5, 8, class_2246.field_10124.method_9564(), class_2246.field_10124.method_9564(), false);
			this.method_14940(arg, arg2, 0, 6, 0, 8, 6, 5, class_2246.field_10266.method_9564(), class_2246.field_10266.method_9564(), false);
			this.method_14940(arg, arg2, 0, 2, 0, 2, 5, 0, class_2246.field_10266.method_9564(), class_2246.field_10266.method_9564(), false);
			this.method_14940(arg, arg2, 6, 2, 0, 8, 5, 0, class_2246.field_10266.method_9564(), class_2246.field_10266.method_9564(), false);
			this.method_14940(arg, arg2, 1, 3, 0, 1, 4, 0, lv2, lv2, false);
			this.method_14940(arg, arg2, 7, 3, 0, 7, 4, 0, lv2, lv2, false);
			this.method_14940(arg, arg2, 0, 2, 4, 8, 2, 8, class_2246.field_10266.method_9564(), class_2246.field_10266.method_9564(), false);
			this.method_14940(arg, arg2, 1, 1, 4, 2, 2, 4, class_2246.field_10124.method_9564(), class_2246.field_10124.method_9564(), false);
			this.method_14940(arg, arg2, 6, 1, 4, 7, 2, 4, class_2246.field_10124.method_9564(), class_2246.field_10124.method_9564(), false);
			this.method_14940(arg, arg2, 1, 3, 8, 7, 3, 8, lv2, lv2, false);
			this.method_14917(
				arg,
				class_2246.field_10364
					.method_9564()
					.method_11657(class_2354.field_10907, Boolean.valueOf(true))
					.method_11657(class_2354.field_10904, Boolean.valueOf(true)),
				0,
				3,
				8,
				arg2
			);
			this.method_14917(
				arg,
				class_2246.field_10364
					.method_9564()
					.method_11657(class_2354.field_10903, Boolean.valueOf(true))
					.method_11657(class_2354.field_10904, Boolean.valueOf(true)),
				8,
				3,
				8,
				arg2
			);
			this.method_14940(arg, arg2, 0, 3, 6, 0, 3, 7, lv, lv, false);
			this.method_14940(arg, arg2, 8, 3, 6, 8, 3, 7, lv, lv, false);
			this.method_14940(arg, arg2, 0, 3, 4, 0, 5, 5, class_2246.field_10266.method_9564(), class_2246.field_10266.method_9564(), false);
			this.method_14940(arg, arg2, 8, 3, 4, 8, 5, 5, class_2246.field_10266.method_9564(), class_2246.field_10266.method_9564(), false);
			this.method_14940(arg, arg2, 1, 3, 5, 2, 5, 5, class_2246.field_10266.method_9564(), class_2246.field_10266.method_9564(), false);
			this.method_14940(arg, arg2, 6, 3, 5, 7, 5, 5, class_2246.field_10266.method_9564(), class_2246.field_10266.method_9564(), false);
			this.method_14940(arg, arg2, 1, 4, 5, 1, 5, 5, lv2, lv2, false);
			this.method_14940(arg, arg2, 7, 4, 5, 7, 5, 5, lv2, lv2, false);

			for (int i = 0; i <= 5; i++) {
				for (int j = 0; j <= 8; j++) {
					this.method_14936(arg, class_2246.field_10266.method_9564(), j, -1, i, arg2);
				}
			}

			return true;
		}
	}

	public static class class_3396 extends class_3390.class_3403 {
		public class_3396(int i, Random random, class_3341 arg, class_2350 arg2) {
			super(class_3773.field_16952, i);
			this.method_14926(arg2);
			this.field_15315 = arg;
		}

		public class_3396(class_3485 arg, class_2487 arg2) {
			super(class_3773.field_16952, arg2);
		}

		@Override
		public void method_14918(class_3443 arg, List<class_3443> list, Random random) {
			this.method_14814((class_3390.class_3407)arg, list, random, 5, 3, true);
		}

		public static class_3390.class_3396 method_14801(List<class_3443> list, Random random, int i, int j, int k, class_2350 arg, int l) {
			class_3341 lv = class_3341.method_14667(i, j, k, -5, -3, 0, 13, 14, 13, arg);
			return method_14809(lv) && class_3443.method_14932(list, lv) == null ? new class_3390.class_3396(l, random, lv, arg) : null;
		}

		@Override
		public boolean method_14931(class_1936 arg, Random random, class_3341 arg2, class_1923 arg3) {
			this.method_14940(arg, arg2, 0, 3, 0, 12, 4, 12, class_2246.field_10266.method_9564(), class_2246.field_10266.method_9564(), false);
			this.method_14940(arg, arg2, 0, 5, 0, 12, 13, 12, class_2246.field_10124.method_9564(), class_2246.field_10124.method_9564(), false);
			this.method_14940(arg, arg2, 0, 5, 0, 1, 12, 12, class_2246.field_10266.method_9564(), class_2246.field_10266.method_9564(), false);
			this.method_14940(arg, arg2, 11, 5, 0, 12, 12, 12, class_2246.field_10266.method_9564(), class_2246.field_10266.method_9564(), false);
			this.method_14940(arg, arg2, 2, 5, 11, 4, 12, 12, class_2246.field_10266.method_9564(), class_2246.field_10266.method_9564(), false);
			this.method_14940(arg, arg2, 8, 5, 11, 10, 12, 12, class_2246.field_10266.method_9564(), class_2246.field_10266.method_9564(), false);
			this.method_14940(arg, arg2, 5, 9, 11, 7, 12, 12, class_2246.field_10266.method_9564(), class_2246.field_10266.method_9564(), false);
			this.method_14940(arg, arg2, 2, 5, 0, 4, 12, 1, class_2246.field_10266.method_9564(), class_2246.field_10266.method_9564(), false);
			this.method_14940(arg, arg2, 8, 5, 0, 10, 12, 1, class_2246.field_10266.method_9564(), class_2246.field_10266.method_9564(), false);
			this.method_14940(arg, arg2, 5, 9, 0, 7, 12, 1, class_2246.field_10266.method_9564(), class_2246.field_10266.method_9564(), false);
			this.method_14940(arg, arg2, 2, 11, 2, 10, 12, 10, class_2246.field_10266.method_9564(), class_2246.field_10266.method_9564(), false);
			this.method_14940(arg, arg2, 5, 8, 0, 7, 8, 0, class_2246.field_10364.method_9564(), class_2246.field_10364.method_9564(), false);
			class_2680 lv = class_2246.field_10364
				.method_9564()
				.method_11657(class_2354.field_10903, Boolean.valueOf(true))
				.method_11657(class_2354.field_10907, Boolean.valueOf(true));
			class_2680 lv2 = class_2246.field_10364
				.method_9564()
				.method_11657(class_2354.field_10905, Boolean.valueOf(true))
				.method_11657(class_2354.field_10904, Boolean.valueOf(true));

			for (int i = 1; i <= 11; i += 2) {
				this.method_14940(arg, arg2, i, 10, 0, i, 11, 0, lv, lv, false);
				this.method_14940(arg, arg2, i, 10, 12, i, 11, 12, lv, lv, false);
				this.method_14940(arg, arg2, 0, 10, i, 0, 11, i, lv2, lv2, false);
				this.method_14940(arg, arg2, 12, 10, i, 12, 11, i, lv2, lv2, false);
				this.method_14917(arg, class_2246.field_10266.method_9564(), i, 13, 0, arg2);
				this.method_14917(arg, class_2246.field_10266.method_9564(), i, 13, 12, arg2);
				this.method_14917(arg, class_2246.field_10266.method_9564(), 0, 13, i, arg2);
				this.method_14917(arg, class_2246.field_10266.method_9564(), 12, 13, i, arg2);
				if (i != 11) {
					this.method_14917(arg, lv, i + 1, 13, 0, arg2);
					this.method_14917(arg, lv, i + 1, 13, 12, arg2);
					this.method_14917(arg, lv2, 0, 13, i + 1, arg2);
					this.method_14917(arg, lv2, 12, 13, i + 1, arg2);
				}
			}

			this.method_14917(
				arg,
				class_2246.field_10364
					.method_9564()
					.method_11657(class_2354.field_10905, Boolean.valueOf(true))
					.method_11657(class_2354.field_10907, Boolean.valueOf(true)),
				0,
				13,
				0,
				arg2
			);
			this.method_14917(
				arg,
				class_2246.field_10364
					.method_9564()
					.method_11657(class_2354.field_10904, Boolean.valueOf(true))
					.method_11657(class_2354.field_10907, Boolean.valueOf(true)),
				0,
				13,
				12,
				arg2
			);
			this.method_14917(
				arg,
				class_2246.field_10364
					.method_9564()
					.method_11657(class_2354.field_10904, Boolean.valueOf(true))
					.method_11657(class_2354.field_10903, Boolean.valueOf(true)),
				12,
				13,
				12,
				arg2
			);
			this.method_14917(
				arg,
				class_2246.field_10364
					.method_9564()
					.method_11657(class_2354.field_10905, Boolean.valueOf(true))
					.method_11657(class_2354.field_10903, Boolean.valueOf(true)),
				12,
				13,
				0,
				arg2
			);

			for (int ix = 3; ix <= 9; ix += 2) {
				this.method_14940(
					arg,
					arg2,
					1,
					7,
					ix,
					1,
					8,
					ix,
					lv2.method_11657(class_2354.field_10903, Boolean.valueOf(true)),
					lv2.method_11657(class_2354.field_10903, Boolean.valueOf(true)),
					false
				);
				this.method_14940(
					arg,
					arg2,
					11,
					7,
					ix,
					11,
					8,
					ix,
					lv2.method_11657(class_2354.field_10907, Boolean.valueOf(true)),
					lv2.method_11657(class_2354.field_10907, Boolean.valueOf(true)),
					false
				);
			}

			this.method_14940(arg, arg2, 4, 2, 0, 8, 2, 12, class_2246.field_10266.method_9564(), class_2246.field_10266.method_9564(), false);
			this.method_14940(arg, arg2, 0, 2, 4, 12, 2, 8, class_2246.field_10266.method_9564(), class_2246.field_10266.method_9564(), false);
			this.method_14940(arg, arg2, 4, 0, 0, 8, 1, 3, class_2246.field_10266.method_9564(), class_2246.field_10266.method_9564(), false);
			this.method_14940(arg, arg2, 4, 0, 9, 8, 1, 12, class_2246.field_10266.method_9564(), class_2246.field_10266.method_9564(), false);
			this.method_14940(arg, arg2, 0, 0, 4, 3, 1, 8, class_2246.field_10266.method_9564(), class_2246.field_10266.method_9564(), false);
			this.method_14940(arg, arg2, 9, 0, 4, 12, 1, 8, class_2246.field_10266.method_9564(), class_2246.field_10266.method_9564(), false);

			for (int ix = 4; ix <= 8; ix++) {
				for (int j = 0; j <= 2; j++) {
					this.method_14936(arg, class_2246.field_10266.method_9564(), ix, -1, j, arg2);
					this.method_14936(arg, class_2246.field_10266.method_9564(), ix, -1, 12 - j, arg2);
				}
			}

			for (int ix = 0; ix <= 2; ix++) {
				for (int j = 4; j <= 8; j++) {
					this.method_14936(arg, class_2246.field_10266.method_9564(), ix, -1, j, arg2);
					this.method_14936(arg, class_2246.field_10266.method_9564(), 12 - ix, -1, j, arg2);
				}
			}

			this.method_14940(arg, arg2, 5, 5, 5, 7, 5, 7, class_2246.field_10266.method_9564(), class_2246.field_10266.method_9564(), false);
			this.method_14940(arg, arg2, 6, 1, 6, 6, 4, 6, class_2246.field_10124.method_9564(), class_2246.field_10124.method_9564(), false);
			this.method_14917(arg, class_2246.field_10266.method_9564(), 6, 0, 6, arg2);
			this.method_14917(arg, class_2246.field_10164.method_9564(), 6, 5, 6, arg2);
			class_2338 lv3 = new class_2338(this.method_14928(6, 6), this.method_14924(5), this.method_14941(6, 6));
			if (arg2.method_14662(lv3)) {
				arg.method_8405().method_8676(lv3, class_3612.field_15908, 0);
			}

			return true;
		}
	}

	public static class class_3397 extends class_3390.class_3403 {
		public class_3397(int i, class_3341 arg, class_2350 arg2) {
			super(class_3773.field_16929, i);
			this.method_14926(arg2);
			this.field_15315 = arg;
		}

		public class_3397(class_3485 arg, class_2487 arg2) {
			super(class_3773.field_16929, arg2);
		}

		@Override
		public void method_14918(class_3443 arg, List<class_3443> list, Random random) {
			this.method_14814((class_3390.class_3407)arg, list, random, 1, 0, true);
			this.method_14812((class_3390.class_3407)arg, list, random, 0, 1, true);
			this.method_14808((class_3390.class_3407)arg, list, random, 0, 1, true);
		}

		public static class_3390.class_3397 method_14802(List<class_3443> list, int i, int j, int k, class_2350 arg, int l) {
			class_3341 lv = class_3341.method_14667(i, j, k, -1, 0, 0, 5, 7, 5, arg);
			return method_14809(lv) && class_3443.method_14932(list, lv) == null ? new class_3390.class_3397(l, lv, arg) : null;
		}

		@Override
		public boolean method_14931(class_1936 arg, Random random, class_3341 arg2, class_1923 arg3) {
			this.method_14940(arg, arg2, 0, 0, 0, 4, 1, 4, class_2246.field_10266.method_9564(), class_2246.field_10266.method_9564(), false);
			this.method_14940(arg, arg2, 0, 2, 0, 4, 5, 4, class_2246.field_10124.method_9564(), class_2246.field_10124.method_9564(), false);
			this.method_14940(arg, arg2, 0, 2, 0, 0, 5, 0, class_2246.field_10266.method_9564(), class_2246.field_10266.method_9564(), false);
			this.method_14940(arg, arg2, 4, 2, 0, 4, 5, 0, class_2246.field_10266.method_9564(), class_2246.field_10266.method_9564(), false);
			this.method_14940(arg, arg2, 0, 2, 4, 0, 5, 4, class_2246.field_10266.method_9564(), class_2246.field_10266.method_9564(), false);
			this.method_14940(arg, arg2, 4, 2, 4, 4, 5, 4, class_2246.field_10266.method_9564(), class_2246.field_10266.method_9564(), false);
			this.method_14940(arg, arg2, 0, 6, 0, 4, 6, 4, class_2246.field_10266.method_9564(), class_2246.field_10266.method_9564(), false);

			for (int i = 0; i <= 4; i++) {
				for (int j = 0; j <= 4; j++) {
					this.method_14936(arg, class_2246.field_10266.method_9564(), i, -1, j, arg2);
				}
			}

			return true;
		}
	}

	public static class class_3398 extends class_3390.class_3403 {
		private boolean field_14496;

		public class_3398(int i, Random random, class_3341 arg, class_2350 arg2) {
			super(class_3773.field_16962, i);
			this.method_14926(arg2);
			this.field_15315 = arg;
			this.field_14496 = random.nextInt(3) == 0;
		}

		public class_3398(class_3485 arg, class_2487 arg2) {
			super(class_3773.field_16962, arg2);
			this.field_14496 = arg2.method_10577("Chest");
		}

		@Override
		protected void method_14943(class_2487 arg) {
			super.method_14943(arg);
			arg.method_10556("Chest", this.field_14496);
		}

		@Override
		public void method_14918(class_3443 arg, List<class_3443> list, Random random) {
			this.method_14812((class_3390.class_3407)arg, list, random, 0, 1, true);
		}

		public static class_3390.class_3398 method_14803(List<class_3443> list, Random random, int i, int j, int k, class_2350 arg, int l) {
			class_3341 lv = class_3341.method_14667(i, j, k, -1, 0, 0, 5, 7, 5, arg);
			return method_14809(lv) && class_3443.method_14932(list, lv) == null ? new class_3390.class_3398(l, random, lv, arg) : null;
		}

		@Override
		public boolean method_14931(class_1936 arg, Random random, class_3341 arg2, class_1923 arg3) {
			this.method_14940(arg, arg2, 0, 0, 0, 4, 1, 4, class_2246.field_10266.method_9564(), class_2246.field_10266.method_9564(), false);
			this.method_14940(arg, arg2, 0, 2, 0, 4, 5, 4, class_2246.field_10124.method_9564(), class_2246.field_10124.method_9564(), false);
			class_2680 lv = class_2246.field_10364
				.method_9564()
				.method_11657(class_2354.field_10903, Boolean.valueOf(true))
				.method_11657(class_2354.field_10907, Boolean.valueOf(true));
			class_2680 lv2 = class_2246.field_10364
				.method_9564()
				.method_11657(class_2354.field_10905, Boolean.valueOf(true))
				.method_11657(class_2354.field_10904, Boolean.valueOf(true));
			this.method_14940(arg, arg2, 4, 2, 0, 4, 5, 4, class_2246.field_10266.method_9564(), class_2246.field_10266.method_9564(), false);
			this.method_14940(arg, arg2, 4, 3, 1, 4, 4, 1, lv2, lv2, false);
			this.method_14940(arg, arg2, 4, 3, 3, 4, 4, 3, lv2, lv2, false);
			this.method_14940(arg, arg2, 0, 2, 0, 0, 5, 0, class_2246.field_10266.method_9564(), class_2246.field_10266.method_9564(), false);
			this.method_14940(arg, arg2, 0, 2, 4, 3, 5, 4, class_2246.field_10266.method_9564(), class_2246.field_10266.method_9564(), false);
			this.method_14940(arg, arg2, 1, 3, 4, 1, 4, 4, lv, lv, false);
			this.method_14940(arg, arg2, 3, 3, 4, 3, 4, 4, lv, lv, false);
			if (this.field_14496 && arg2.method_14662(new class_2338(this.method_14928(3, 3), this.method_14924(2), this.method_14941(3, 3)))) {
				this.field_14496 = false;
				this.method_14915(arg, arg2, random, 3, 2, 3, class_39.field_615);
			}

			this.method_14940(arg, arg2, 0, 6, 0, 4, 6, 4, class_2246.field_10266.method_9564(), class_2246.field_10266.method_9564(), false);

			for (int i = 0; i <= 4; i++) {
				for (int j = 0; j <= 4; j++) {
					this.method_14936(arg, class_2246.field_10266.method_9564(), i, -1, j, arg2);
				}
			}

			return true;
		}
	}

	public static class class_3399 extends class_3390.class_3403 {
		public class_3399(int i, class_3341 arg, class_2350 arg2) {
			super(class_3773.field_16921, i);
			this.method_14926(arg2);
			this.field_15315 = arg;
		}

		public class_3399(class_3485 arg, class_2487 arg2) {
			super(class_3773.field_16921, arg2);
		}

		@Override
		public void method_14918(class_3443 arg, List<class_3443> list, Random random) {
			this.method_14814((class_3390.class_3407)arg, list, random, 1, 0, true);
		}

		public static class_3390.class_3399 method_14804(List<class_3443> list, int i, int j, int k, class_2350 arg, int l) {
			class_3341 lv = class_3341.method_14667(i, j, k, -1, 0, 0, 5, 7, 5, arg);
			return method_14809(lv) && class_3443.method_14932(list, lv) == null ? new class_3390.class_3399(l, lv, arg) : null;
		}

		@Override
		public boolean method_14931(class_1936 arg, Random random, class_3341 arg2, class_1923 arg3) {
			this.method_14940(arg, arg2, 0, 0, 0, 4, 1, 4, class_2246.field_10266.method_9564(), class_2246.field_10266.method_9564(), false);
			this.method_14940(arg, arg2, 0, 2, 0, 4, 5, 4, class_2246.field_10124.method_9564(), class_2246.field_10124.method_9564(), false);
			class_2680 lv = class_2246.field_10364
				.method_9564()
				.method_11657(class_2354.field_10905, Boolean.valueOf(true))
				.method_11657(class_2354.field_10904, Boolean.valueOf(true));
			this.method_14940(arg, arg2, 0, 2, 0, 0, 5, 4, class_2246.field_10266.method_9564(), class_2246.field_10266.method_9564(), false);
			this.method_14940(arg, arg2, 4, 2, 0, 4, 5, 4, class_2246.field_10266.method_9564(), class_2246.field_10266.method_9564(), false);
			this.method_14940(arg, arg2, 0, 3, 1, 0, 4, 1, lv, lv, false);
			this.method_14940(arg, arg2, 0, 3, 3, 0, 4, 3, lv, lv, false);
			this.method_14940(arg, arg2, 4, 3, 1, 4, 4, 1, lv, lv, false);
			this.method_14940(arg, arg2, 4, 3, 3, 4, 4, 3, lv, lv, false);
			this.method_14940(arg, arg2, 0, 6, 0, 4, 6, 4, class_2246.field_10266.method_9564(), class_2246.field_10266.method_9564(), false);

			for (int i = 0; i <= 4; i++) {
				for (int j = 0; j <= 4; j++) {
					this.method_14936(arg, class_2246.field_10266.method_9564(), i, -1, j, arg2);
				}
			}

			return true;
		}
	}

	public static class class_3400 extends class_3390.class_3403 {
		private boolean field_14497;

		public class_3400(int i, Random random, class_3341 arg, class_2350 arg2) {
			super(class_3773.field_16945, i);
			this.method_14926(arg2);
			this.field_15315 = arg;
			this.field_14497 = random.nextInt(3) == 0;
		}

		public class_3400(class_3485 arg, class_2487 arg2) {
			super(class_3773.field_16945, arg2);
			this.field_14497 = arg2.method_10577("Chest");
		}

		@Override
		protected void method_14943(class_2487 arg) {
			super.method_14943(arg);
			arg.method_10556("Chest", this.field_14497);
		}

		@Override
		public void method_14918(class_3443 arg, List<class_3443> list, Random random) {
			this.method_14808((class_3390.class_3407)arg, list, random, 0, 1, true);
		}

		public static class_3390.class_3400 method_14805(List<class_3443> list, Random random, int i, int j, int k, class_2350 arg, int l) {
			class_3341 lv = class_3341.method_14667(i, j, k, -1, 0, 0, 5, 7, 5, arg);
			return method_14809(lv) && class_3443.method_14932(list, lv) == null ? new class_3390.class_3400(l, random, lv, arg) : null;
		}

		@Override
		public boolean method_14931(class_1936 arg, Random random, class_3341 arg2, class_1923 arg3) {
			this.method_14940(arg, arg2, 0, 0, 0, 4, 1, 4, class_2246.field_10266.method_9564(), class_2246.field_10266.method_9564(), false);
			this.method_14940(arg, arg2, 0, 2, 0, 4, 5, 4, class_2246.field_10124.method_9564(), class_2246.field_10124.method_9564(), false);
			class_2680 lv = class_2246.field_10364
				.method_9564()
				.method_11657(class_2354.field_10903, Boolean.valueOf(true))
				.method_11657(class_2354.field_10907, Boolean.valueOf(true));
			class_2680 lv2 = class_2246.field_10364
				.method_9564()
				.method_11657(class_2354.field_10905, Boolean.valueOf(true))
				.method_11657(class_2354.field_10904, Boolean.valueOf(true));
			this.method_14940(arg, arg2, 0, 2, 0, 0, 5, 4, class_2246.field_10266.method_9564(), class_2246.field_10266.method_9564(), false);
			this.method_14940(arg, arg2, 0, 3, 1, 0, 4, 1, lv2, lv2, false);
			this.method_14940(arg, arg2, 0, 3, 3, 0, 4, 3, lv2, lv2, false);
			this.method_14940(arg, arg2, 4, 2, 0, 4, 5, 0, class_2246.field_10266.method_9564(), class_2246.field_10266.method_9564(), false);
			this.method_14940(arg, arg2, 1, 2, 4, 4, 5, 4, class_2246.field_10266.method_9564(), class_2246.field_10266.method_9564(), false);
			this.method_14940(arg, arg2, 1, 3, 4, 1, 4, 4, lv, lv, false);
			this.method_14940(arg, arg2, 3, 3, 4, 3, 4, 4, lv, lv, false);
			if (this.field_14497 && arg2.method_14662(new class_2338(this.method_14928(1, 3), this.method_14924(2), this.method_14941(1, 3)))) {
				this.field_14497 = false;
				this.method_14915(arg, arg2, random, 1, 2, 3, class_39.field_615);
			}

			this.method_14940(arg, arg2, 0, 6, 0, 4, 6, 4, class_2246.field_10266.method_9564(), class_2246.field_10266.method_9564(), false);

			for (int i = 0; i <= 4; i++) {
				for (int j = 0; j <= 4; j++) {
					this.method_14936(arg, class_2246.field_10266.method_9564(), i, -1, j, arg2);
				}
			}

			return true;
		}
	}

	public static class class_3401 extends class_3390.class_3403 {
		public class_3401(int i, class_3341 arg, class_2350 arg2) {
			super(class_3773.field_16961, i);
			this.method_14926(arg2);
			this.field_15315 = arg;
		}

		public class_3401(class_3485 arg, class_2487 arg2) {
			super(class_3773.field_16961, arg2);
		}

		@Override
		public void method_14918(class_3443 arg, List<class_3443> list, Random random) {
			this.method_14814((class_3390.class_3407)arg, list, random, 5, 3, true);
			this.method_14814((class_3390.class_3407)arg, list, random, 5, 11, true);
		}

		public static class_3390.class_3401 method_14806(List<class_3443> list, int i, int j, int k, class_2350 arg, int l) {
			class_3341 lv = class_3341.method_14667(i, j, k, -5, -3, 0, 13, 14, 13, arg);
			return method_14809(lv) && class_3443.method_14932(list, lv) == null ? new class_3390.class_3401(l, lv, arg) : null;
		}

		@Override
		public boolean method_14931(class_1936 arg, Random random, class_3341 arg2, class_1923 arg3) {
			this.method_14940(arg, arg2, 0, 3, 0, 12, 4, 12, class_2246.field_10266.method_9564(), class_2246.field_10266.method_9564(), false);
			this.method_14940(arg, arg2, 0, 5, 0, 12, 13, 12, class_2246.field_10124.method_9564(), class_2246.field_10124.method_9564(), false);
			this.method_14940(arg, arg2, 0, 5, 0, 1, 12, 12, class_2246.field_10266.method_9564(), class_2246.field_10266.method_9564(), false);
			this.method_14940(arg, arg2, 11, 5, 0, 12, 12, 12, class_2246.field_10266.method_9564(), class_2246.field_10266.method_9564(), false);
			this.method_14940(arg, arg2, 2, 5, 11, 4, 12, 12, class_2246.field_10266.method_9564(), class_2246.field_10266.method_9564(), false);
			this.method_14940(arg, arg2, 8, 5, 11, 10, 12, 12, class_2246.field_10266.method_9564(), class_2246.field_10266.method_9564(), false);
			this.method_14940(arg, arg2, 5, 9, 11, 7, 12, 12, class_2246.field_10266.method_9564(), class_2246.field_10266.method_9564(), false);
			this.method_14940(arg, arg2, 2, 5, 0, 4, 12, 1, class_2246.field_10266.method_9564(), class_2246.field_10266.method_9564(), false);
			this.method_14940(arg, arg2, 8, 5, 0, 10, 12, 1, class_2246.field_10266.method_9564(), class_2246.field_10266.method_9564(), false);
			this.method_14940(arg, arg2, 5, 9, 0, 7, 12, 1, class_2246.field_10266.method_9564(), class_2246.field_10266.method_9564(), false);
			this.method_14940(arg, arg2, 2, 11, 2, 10, 12, 10, class_2246.field_10266.method_9564(), class_2246.field_10266.method_9564(), false);
			class_2680 lv = class_2246.field_10364
				.method_9564()
				.method_11657(class_2354.field_10903, Boolean.valueOf(true))
				.method_11657(class_2354.field_10907, Boolean.valueOf(true));
			class_2680 lv2 = class_2246.field_10364
				.method_9564()
				.method_11657(class_2354.field_10905, Boolean.valueOf(true))
				.method_11657(class_2354.field_10904, Boolean.valueOf(true));
			class_2680 lv3 = lv2.method_11657(class_2354.field_10903, Boolean.valueOf(true));
			class_2680 lv4 = lv2.method_11657(class_2354.field_10907, Boolean.valueOf(true));

			for (int i = 1; i <= 11; i += 2) {
				this.method_14940(arg, arg2, i, 10, 0, i, 11, 0, lv, lv, false);
				this.method_14940(arg, arg2, i, 10, 12, i, 11, 12, lv, lv, false);
				this.method_14940(arg, arg2, 0, 10, i, 0, 11, i, lv2, lv2, false);
				this.method_14940(arg, arg2, 12, 10, i, 12, 11, i, lv2, lv2, false);
				this.method_14917(arg, class_2246.field_10266.method_9564(), i, 13, 0, arg2);
				this.method_14917(arg, class_2246.field_10266.method_9564(), i, 13, 12, arg2);
				this.method_14917(arg, class_2246.field_10266.method_9564(), 0, 13, i, arg2);
				this.method_14917(arg, class_2246.field_10266.method_9564(), 12, 13, i, arg2);
				if (i != 11) {
					this.method_14917(arg, lv, i + 1, 13, 0, arg2);
					this.method_14917(arg, lv, i + 1, 13, 12, arg2);
					this.method_14917(arg, lv2, 0, 13, i + 1, arg2);
					this.method_14917(arg, lv2, 12, 13, i + 1, arg2);
				}
			}

			this.method_14917(
				arg,
				class_2246.field_10364
					.method_9564()
					.method_11657(class_2354.field_10905, Boolean.valueOf(true))
					.method_11657(class_2354.field_10907, Boolean.valueOf(true)),
				0,
				13,
				0,
				arg2
			);
			this.method_14917(
				arg,
				class_2246.field_10364
					.method_9564()
					.method_11657(class_2354.field_10904, Boolean.valueOf(true))
					.method_11657(class_2354.field_10907, Boolean.valueOf(true)),
				0,
				13,
				12,
				arg2
			);
			this.method_14917(
				arg,
				class_2246.field_10364
					.method_9564()
					.method_11657(class_2354.field_10904, Boolean.valueOf(true))
					.method_11657(class_2354.field_10903, Boolean.valueOf(true)),
				12,
				13,
				12,
				arg2
			);
			this.method_14917(
				arg,
				class_2246.field_10364
					.method_9564()
					.method_11657(class_2354.field_10905, Boolean.valueOf(true))
					.method_11657(class_2354.field_10903, Boolean.valueOf(true)),
				12,
				13,
				0,
				arg2
			);

			for (int ix = 3; ix <= 9; ix += 2) {
				this.method_14940(arg, arg2, 1, 7, ix, 1, 8, ix, lv3, lv3, false);
				this.method_14940(arg, arg2, 11, 7, ix, 11, 8, ix, lv4, lv4, false);
			}

			class_2680 lv5 = class_2246.field_10159.method_9564().method_11657(class_2510.field_11571, class_2350.field_11043);

			for (int j = 0; j <= 6; j++) {
				int k = j + 4;

				for (int l = 5; l <= 7; l++) {
					this.method_14917(arg, lv5, l, 5 + j, k, arg2);
				}

				if (k >= 5 && k <= 8) {
					this.method_14940(arg, arg2, 5, 5, k, 7, j + 4, k, class_2246.field_10266.method_9564(), class_2246.field_10266.method_9564(), false);
				} else if (k >= 9 && k <= 10) {
					this.method_14940(arg, arg2, 5, 8, k, 7, j + 4, k, class_2246.field_10266.method_9564(), class_2246.field_10266.method_9564(), false);
				}

				if (j >= 1) {
					this.method_14940(arg, arg2, 5, 6 + j, k, 7, 9 + j, k, class_2246.field_10124.method_9564(), class_2246.field_10124.method_9564(), false);
				}
			}

			for (int j = 5; j <= 7; j++) {
				this.method_14917(arg, lv5, j, 12, 11, arg2);
			}

			this.method_14940(arg, arg2, 5, 6, 7, 5, 7, 7, lv4, lv4, false);
			this.method_14940(arg, arg2, 7, 6, 7, 7, 7, 7, lv3, lv3, false);
			this.method_14940(arg, arg2, 5, 13, 12, 7, 13, 12, class_2246.field_10124.method_9564(), class_2246.field_10124.method_9564(), false);
			this.method_14940(arg, arg2, 2, 5, 2, 3, 5, 3, class_2246.field_10266.method_9564(), class_2246.field_10266.method_9564(), false);
			this.method_14940(arg, arg2, 2, 5, 9, 3, 5, 10, class_2246.field_10266.method_9564(), class_2246.field_10266.method_9564(), false);
			this.method_14940(arg, arg2, 2, 5, 4, 2, 5, 8, class_2246.field_10266.method_9564(), class_2246.field_10266.method_9564(), false);
			this.method_14940(arg, arg2, 9, 5, 2, 10, 5, 3, class_2246.field_10266.method_9564(), class_2246.field_10266.method_9564(), false);
			this.method_14940(arg, arg2, 9, 5, 9, 10, 5, 10, class_2246.field_10266.method_9564(), class_2246.field_10266.method_9564(), false);
			this.method_14940(arg, arg2, 10, 5, 4, 10, 5, 8, class_2246.field_10266.method_9564(), class_2246.field_10266.method_9564(), false);
			class_2680 lv6 = lv5.method_11657(class_2510.field_11571, class_2350.field_11034);
			class_2680 lv7 = lv5.method_11657(class_2510.field_11571, class_2350.field_11039);
			this.method_14917(arg, lv7, 4, 5, 2, arg2);
			this.method_14917(arg, lv7, 4, 5, 3, arg2);
			this.method_14917(arg, lv7, 4, 5, 9, arg2);
			this.method_14917(arg, lv7, 4, 5, 10, arg2);
			this.method_14917(arg, lv6, 8, 5, 2, arg2);
			this.method_14917(arg, lv6, 8, 5, 3, arg2);
			this.method_14917(arg, lv6, 8, 5, 9, arg2);
			this.method_14917(arg, lv6, 8, 5, 10, arg2);
			this.method_14940(arg, arg2, 3, 4, 4, 4, 4, 8, class_2246.field_10114.method_9564(), class_2246.field_10114.method_9564(), false);
			this.method_14940(arg, arg2, 8, 4, 4, 9, 4, 8, class_2246.field_10114.method_9564(), class_2246.field_10114.method_9564(), false);
			this.method_14940(arg, arg2, 3, 5, 4, 4, 5, 8, class_2246.field_9974.method_9564(), class_2246.field_9974.method_9564(), false);
			this.method_14940(arg, arg2, 8, 5, 4, 9, 5, 8, class_2246.field_9974.method_9564(), class_2246.field_9974.method_9564(), false);
			this.method_14940(arg, arg2, 4, 2, 0, 8, 2, 12, class_2246.field_10266.method_9564(), class_2246.field_10266.method_9564(), false);
			this.method_14940(arg, arg2, 0, 2, 4, 12, 2, 8, class_2246.field_10266.method_9564(), class_2246.field_10266.method_9564(), false);
			this.method_14940(arg, arg2, 4, 0, 0, 8, 1, 3, class_2246.field_10266.method_9564(), class_2246.field_10266.method_9564(), false);
			this.method_14940(arg, arg2, 4, 0, 9, 8, 1, 12, class_2246.field_10266.method_9564(), class_2246.field_10266.method_9564(), false);
			this.method_14940(arg, arg2, 0, 0, 4, 3, 1, 8, class_2246.field_10266.method_9564(), class_2246.field_10266.method_9564(), false);
			this.method_14940(arg, arg2, 9, 0, 4, 12, 1, 8, class_2246.field_10266.method_9564(), class_2246.field_10266.method_9564(), false);

			for (int l = 4; l <= 8; l++) {
				for (int m = 0; m <= 2; m++) {
					this.method_14936(arg, class_2246.field_10266.method_9564(), l, -1, m, arg2);
					this.method_14936(arg, class_2246.field_10266.method_9564(), l, -1, 12 - m, arg2);
				}
			}

			for (int l = 0; l <= 2; l++) {
				for (int m = 4; m <= 8; m++) {
					this.method_14936(arg, class_2246.field_10266.method_9564(), l, -1, m, arg2);
					this.method_14936(arg, class_2246.field_10266.method_9564(), 12 - l, -1, m, arg2);
				}
			}

			return true;
		}
	}

	public static class class_3402 extends class_3390.class_3403 {
		private boolean field_14498;

		public class_3402(int i, class_3341 arg, class_2350 arg2) {
			super(class_3773.field_16931, i);
			this.method_14926(arg2);
			this.field_15315 = arg;
		}

		public class_3402(class_3485 arg, class_2487 arg2) {
			super(class_3773.field_16931, arg2);
			this.field_14498 = arg2.method_10577("Mob");
		}

		@Override
		protected void method_14943(class_2487 arg) {
			super.method_14943(arg);
			arg.method_10556("Mob", this.field_14498);
		}

		public static class_3390.class_3402 method_14807(List<class_3443> list, int i, int j, int k, int l, class_2350 arg) {
			class_3341 lv = class_3341.method_14667(i, j, k, -2, 0, 0, 7, 8, 9, arg);
			return method_14809(lv) && class_3443.method_14932(list, lv) == null ? new class_3390.class_3402(l, lv, arg) : null;
		}

		@Override
		public boolean method_14931(class_1936 arg, Random random, class_3341 arg2, class_1923 arg3) {
			this.method_14940(arg, arg2, 0, 2, 0, 6, 7, 7, class_2246.field_10124.method_9564(), class_2246.field_10124.method_9564(), false);
			this.method_14940(arg, arg2, 1, 0, 0, 5, 1, 7, class_2246.field_10266.method_9564(), class_2246.field_10266.method_9564(), false);
			this.method_14940(arg, arg2, 1, 2, 1, 5, 2, 7, class_2246.field_10266.method_9564(), class_2246.field_10266.method_9564(), false);
			this.method_14940(arg, arg2, 1, 3, 2, 5, 3, 7, class_2246.field_10266.method_9564(), class_2246.field_10266.method_9564(), false);
			this.method_14940(arg, arg2, 1, 4, 3, 5, 4, 7, class_2246.field_10266.method_9564(), class_2246.field_10266.method_9564(), false);
			this.method_14940(arg, arg2, 1, 2, 0, 1, 4, 2, class_2246.field_10266.method_9564(), class_2246.field_10266.method_9564(), false);
			this.method_14940(arg, arg2, 5, 2, 0, 5, 4, 2, class_2246.field_10266.method_9564(), class_2246.field_10266.method_9564(), false);
			this.method_14940(arg, arg2, 1, 5, 2, 1, 5, 3, class_2246.field_10266.method_9564(), class_2246.field_10266.method_9564(), false);
			this.method_14940(arg, arg2, 5, 5, 2, 5, 5, 3, class_2246.field_10266.method_9564(), class_2246.field_10266.method_9564(), false);
			this.method_14940(arg, arg2, 0, 5, 3, 0, 5, 8, class_2246.field_10266.method_9564(), class_2246.field_10266.method_9564(), false);
			this.method_14940(arg, arg2, 6, 5, 3, 6, 5, 8, class_2246.field_10266.method_9564(), class_2246.field_10266.method_9564(), false);
			this.method_14940(arg, arg2, 1, 5, 8, 5, 5, 8, class_2246.field_10266.method_9564(), class_2246.field_10266.method_9564(), false);
			class_2680 lv = class_2246.field_10364
				.method_9564()
				.method_11657(class_2354.field_10903, Boolean.valueOf(true))
				.method_11657(class_2354.field_10907, Boolean.valueOf(true));
			class_2680 lv2 = class_2246.field_10364
				.method_9564()
				.method_11657(class_2354.field_10905, Boolean.valueOf(true))
				.method_11657(class_2354.field_10904, Boolean.valueOf(true));
			this.method_14917(arg, class_2246.field_10364.method_9564().method_11657(class_2354.field_10903, Boolean.valueOf(true)), 1, 6, 3, arg2);
			this.method_14917(arg, class_2246.field_10364.method_9564().method_11657(class_2354.field_10907, Boolean.valueOf(true)), 5, 6, 3, arg2);
			this.method_14917(
				arg,
				class_2246.field_10364
					.method_9564()
					.method_11657(class_2354.field_10907, Boolean.valueOf(true))
					.method_11657(class_2354.field_10905, Boolean.valueOf(true)),
				0,
				6,
				3,
				arg2
			);
			this.method_14917(
				arg,
				class_2246.field_10364
					.method_9564()
					.method_11657(class_2354.field_10903, Boolean.valueOf(true))
					.method_11657(class_2354.field_10905, Boolean.valueOf(true)),
				6,
				6,
				3,
				arg2
			);
			this.method_14940(arg, arg2, 0, 6, 4, 0, 6, 7, lv2, lv2, false);
			this.method_14940(arg, arg2, 6, 6, 4, 6, 6, 7, lv2, lv2, false);
			this.method_14917(
				arg,
				class_2246.field_10364
					.method_9564()
					.method_11657(class_2354.field_10907, Boolean.valueOf(true))
					.method_11657(class_2354.field_10904, Boolean.valueOf(true)),
				0,
				6,
				8,
				arg2
			);
			this.method_14917(
				arg,
				class_2246.field_10364
					.method_9564()
					.method_11657(class_2354.field_10903, Boolean.valueOf(true))
					.method_11657(class_2354.field_10904, Boolean.valueOf(true)),
				6,
				6,
				8,
				arg2
			);
			this.method_14940(arg, arg2, 1, 6, 8, 5, 6, 8, lv, lv, false);
			this.method_14917(arg, class_2246.field_10364.method_9564().method_11657(class_2354.field_10907, Boolean.valueOf(true)), 1, 7, 8, arg2);
			this.method_14940(arg, arg2, 2, 7, 8, 4, 7, 8, lv, lv, false);
			this.method_14917(arg, class_2246.field_10364.method_9564().method_11657(class_2354.field_10903, Boolean.valueOf(true)), 5, 7, 8, arg2);
			this.method_14917(arg, class_2246.field_10364.method_9564().method_11657(class_2354.field_10907, Boolean.valueOf(true)), 2, 8, 8, arg2);
			this.method_14917(arg, lv, 3, 8, 8, arg2);
			this.method_14917(arg, class_2246.field_10364.method_9564().method_11657(class_2354.field_10903, Boolean.valueOf(true)), 4, 8, 8, arg2);
			if (!this.field_14498) {
				class_2338 lv3 = new class_2338(this.method_14928(3, 5), this.method_14924(5), this.method_14941(3, 5));
				if (arg2.method_14662(lv3)) {
					this.field_14498 = true;
					arg.method_8652(lv3, class_2246.field_10260.method_9564(), 2);
					class_2586 lv4 = arg.method_8321(lv3);
					if (lv4 instanceof class_2636) {
						((class_2636)lv4).method_11390().method_8274(class_1299.field_6099);
					}
				}
			}

			for (int i = 0; i <= 6; i++) {
				for (int j = 0; j <= 6; j++) {
					this.method_14936(arg, class_2246.field_10266.method_9564(), i, -1, j, arg2);
				}
			}

			return true;
		}
	}

	abstract static class class_3403 extends class_3443 {
		protected class_3403(class_3773 arg, int i) {
			super(arg, i);
		}

		public class_3403(class_3773 arg, class_2487 arg2) {
			super(arg, arg2);
		}

		@Override
		protected void method_14943(class_2487 arg) {
		}

		private int method_14810(List<class_3390.class_3404> list) {
			boolean bl = false;
			int i = 0;

			for (class_3390.class_3404 lv : list) {
				if (lv.field_14499 > 0 && lv.field_14502 < lv.field_14499) {
					bl = true;
				}

				i += lv.field_14503;
			}

			return bl ? i : -1;
		}

		private class_3390.class_3403 method_14811(
			class_3390.class_3407 arg, List<class_3390.class_3404> list, List<class_3443> list2, Random random, int i, int j, int k, class_2350 arg2, int l
		) {
			int m = this.method_14810(list);
			boolean bl = m > 0 && l <= 30;
			int n = 0;

			while (n < 5 && bl) {
				n++;
				int o = random.nextInt(m);

				for (class_3390.class_3404 lv : list) {
					o -= lv.field_14503;
					if (o < 0) {
						if (!lv.method_14816(l) || lv == arg.field_14506 && !lv.field_14500) {
							break;
						}

						class_3390.class_3403 lv2 = class_3390.method_14795(lv, list2, random, i, j, k, arg2, l);
						if (lv2 != null) {
							lv.field_14502++;
							arg.field_14506 = lv;
							if (!lv.method_14815()) {
								list.remove(lv);
							}

							return lv2;
						}
					}
				}
			}

			return class_3390.class_3392.method_14797(list2, random, i, j, k, arg2, l);
		}

		private class_3443 method_14813(
			class_3390.class_3407 arg, List<class_3443> list, Random random, int i, int j, int k, @Nullable class_2350 arg2, int l, boolean bl
		) {
			if (Math.abs(i - arg.method_14935().field_14381) <= 112 && Math.abs(k - arg.method_14935().field_14379) <= 112) {
				List<class_3390.class_3404> list2 = arg.field_14507;
				if (bl) {
					list2 = arg.field_14504;
				}

				class_3443 lv = this.method_14811(arg, list2, list, random, i, j, k, arg2, l + 1);
				if (lv != null) {
					list.add(lv);
					arg.field_14505.add(lv);
				}

				return lv;
			} else {
				return class_3390.class_3392.method_14797(list, random, i, j, k, arg2, l);
			}
		}

		@Nullable
		protected class_3443 method_14814(class_3390.class_3407 arg, List<class_3443> list, Random random, int i, int j, boolean bl) {
			class_2350 lv = this.method_14934();
			if (lv != null) {
				switch (lv) {
					case field_11043:
						return this.method_14813(
							arg, list, random, this.field_15315.field_14381 + i, this.field_15315.field_14380 + j, this.field_15315.field_14379 - 1, lv, this.method_14923(), bl
						);
					case field_11035:
						return this.method_14813(
							arg, list, random, this.field_15315.field_14381 + i, this.field_15315.field_14380 + j, this.field_15315.field_14376 + 1, lv, this.method_14923(), bl
						);
					case field_11039:
						return this.method_14813(
							arg, list, random, this.field_15315.field_14381 - 1, this.field_15315.field_14380 + j, this.field_15315.field_14379 + i, lv, this.method_14923(), bl
						);
					case field_11034:
						return this.method_14813(
							arg, list, random, this.field_15315.field_14378 + 1, this.field_15315.field_14380 + j, this.field_15315.field_14379 + i, lv, this.method_14923(), bl
						);
				}
			}

			return null;
		}

		@Nullable
		protected class_3443 method_14812(class_3390.class_3407 arg, List<class_3443> list, Random random, int i, int j, boolean bl) {
			class_2350 lv = this.method_14934();
			if (lv != null) {
				switch (lv) {
					case field_11043:
						return this.method_14813(
							arg,
							list,
							random,
							this.field_15315.field_14381 - 1,
							this.field_15315.field_14380 + i,
							this.field_15315.field_14379 + j,
							class_2350.field_11039,
							this.method_14923(),
							bl
						);
					case field_11035:
						return this.method_14813(
							arg,
							list,
							random,
							this.field_15315.field_14381 - 1,
							this.field_15315.field_14380 + i,
							this.field_15315.field_14379 + j,
							class_2350.field_11039,
							this.method_14923(),
							bl
						);
					case field_11039:
						return this.method_14813(
							arg,
							list,
							random,
							this.field_15315.field_14381 + j,
							this.field_15315.field_14380 + i,
							this.field_15315.field_14379 - 1,
							class_2350.field_11043,
							this.method_14923(),
							bl
						);
					case field_11034:
						return this.method_14813(
							arg,
							list,
							random,
							this.field_15315.field_14381 + j,
							this.field_15315.field_14380 + i,
							this.field_15315.field_14379 - 1,
							class_2350.field_11043,
							this.method_14923(),
							bl
						);
				}
			}

			return null;
		}

		@Nullable
		protected class_3443 method_14808(class_3390.class_3407 arg, List<class_3443> list, Random random, int i, int j, boolean bl) {
			class_2350 lv = this.method_14934();
			if (lv != null) {
				switch (lv) {
					case field_11043:
						return this.method_14813(
							arg,
							list,
							random,
							this.field_15315.field_14378 + 1,
							this.field_15315.field_14380 + i,
							this.field_15315.field_14379 + j,
							class_2350.field_11034,
							this.method_14923(),
							bl
						);
					case field_11035:
						return this.method_14813(
							arg,
							list,
							random,
							this.field_15315.field_14378 + 1,
							this.field_15315.field_14380 + i,
							this.field_15315.field_14379 + j,
							class_2350.field_11034,
							this.method_14923(),
							bl
						);
					case field_11039:
						return this.method_14813(
							arg,
							list,
							random,
							this.field_15315.field_14381 + j,
							this.field_15315.field_14380 + i,
							this.field_15315.field_14376 + 1,
							class_2350.field_11035,
							this.method_14923(),
							bl
						);
					case field_11034:
						return this.method_14813(
							arg,
							list,
							random,
							this.field_15315.field_14381 + j,
							this.field_15315.field_14380 + i,
							this.field_15315.field_14376 + 1,
							class_2350.field_11035,
							this.method_14923(),
							bl
						);
				}
			}

			return null;
		}

		protected static boolean method_14809(class_3341 arg) {
			return arg != null && arg.field_14380 > 10;
		}
	}

	static class class_3404 {
		public final Class<? extends class_3390.class_3403> field_14501;
		public final int field_14503;
		public int field_14502;
		public final int field_14499;
		public final boolean field_14500;

		public class_3404(Class<? extends class_3390.class_3403> class_, int i, int j, boolean bl) {
			this.field_14501 = class_;
			this.field_14503 = i;
			this.field_14499 = j;
			this.field_14500 = bl;
		}

		public class_3404(Class<? extends class_3390.class_3403> class_, int i, int j) {
			this(class_, i, j, false);
		}

		public boolean method_14816(int i) {
			return this.field_14499 == 0 || this.field_14502 < this.field_14499;
		}

		public boolean method_14815() {
			return this.field_14499 == 0 || this.field_14502 < this.field_14499;
		}
	}

	public static class class_3405 extends class_3390.class_3403 {
		public class_3405(int i, class_3341 arg, class_2350 arg2) {
			super(class_3773.field_16908, i);
			this.method_14926(arg2);
			this.field_15315 = arg;
		}

		public class_3405(class_3485 arg, class_2487 arg2) {
			super(class_3773.field_16908, arg2);
		}

		@Override
		public void method_14918(class_3443 arg, List<class_3443> list, Random random) {
			this.method_14814((class_3390.class_3407)arg, list, random, 2, 0, false);
			this.method_14812((class_3390.class_3407)arg, list, random, 0, 2, false);
			this.method_14808((class_3390.class_3407)arg, list, random, 0, 2, false);
		}

		public static class_3390.class_3405 method_14817(List<class_3443> list, int i, int j, int k, class_2350 arg, int l) {
			class_3341 lv = class_3341.method_14667(i, j, k, -2, 0, 0, 7, 9, 7, arg);
			return method_14809(lv) && class_3443.method_14932(list, lv) == null ? new class_3390.class_3405(l, lv, arg) : null;
		}

		@Override
		public boolean method_14931(class_1936 arg, Random random, class_3341 arg2, class_1923 arg3) {
			this.method_14940(arg, arg2, 0, 0, 0, 6, 1, 6, class_2246.field_10266.method_9564(), class_2246.field_10266.method_9564(), false);
			this.method_14940(arg, arg2, 0, 2, 0, 6, 7, 6, class_2246.field_10124.method_9564(), class_2246.field_10124.method_9564(), false);
			this.method_14940(arg, arg2, 0, 2, 0, 1, 6, 0, class_2246.field_10266.method_9564(), class_2246.field_10266.method_9564(), false);
			this.method_14940(arg, arg2, 0, 2, 6, 1, 6, 6, class_2246.field_10266.method_9564(), class_2246.field_10266.method_9564(), false);
			this.method_14940(arg, arg2, 5, 2, 0, 6, 6, 0, class_2246.field_10266.method_9564(), class_2246.field_10266.method_9564(), false);
			this.method_14940(arg, arg2, 5, 2, 6, 6, 6, 6, class_2246.field_10266.method_9564(), class_2246.field_10266.method_9564(), false);
			this.method_14940(arg, arg2, 0, 2, 0, 0, 6, 1, class_2246.field_10266.method_9564(), class_2246.field_10266.method_9564(), false);
			this.method_14940(arg, arg2, 0, 2, 5, 0, 6, 6, class_2246.field_10266.method_9564(), class_2246.field_10266.method_9564(), false);
			this.method_14940(arg, arg2, 6, 2, 0, 6, 6, 1, class_2246.field_10266.method_9564(), class_2246.field_10266.method_9564(), false);
			this.method_14940(arg, arg2, 6, 2, 5, 6, 6, 6, class_2246.field_10266.method_9564(), class_2246.field_10266.method_9564(), false);
			class_2680 lv = class_2246.field_10364
				.method_9564()
				.method_11657(class_2354.field_10903, Boolean.valueOf(true))
				.method_11657(class_2354.field_10907, Boolean.valueOf(true));
			class_2680 lv2 = class_2246.field_10364
				.method_9564()
				.method_11657(class_2354.field_10905, Boolean.valueOf(true))
				.method_11657(class_2354.field_10904, Boolean.valueOf(true));
			this.method_14940(arg, arg2, 2, 6, 0, 4, 6, 0, class_2246.field_10266.method_9564(), class_2246.field_10266.method_9564(), false);
			this.method_14940(arg, arg2, 2, 5, 0, 4, 5, 0, lv, lv, false);
			this.method_14940(arg, arg2, 2, 6, 6, 4, 6, 6, class_2246.field_10266.method_9564(), class_2246.field_10266.method_9564(), false);
			this.method_14940(arg, arg2, 2, 5, 6, 4, 5, 6, lv, lv, false);
			this.method_14940(arg, arg2, 0, 6, 2, 0, 6, 4, class_2246.field_10266.method_9564(), class_2246.field_10266.method_9564(), false);
			this.method_14940(arg, arg2, 0, 5, 2, 0, 5, 4, lv2, lv2, false);
			this.method_14940(arg, arg2, 6, 6, 2, 6, 6, 4, class_2246.field_10266.method_9564(), class_2246.field_10266.method_9564(), false);
			this.method_14940(arg, arg2, 6, 5, 2, 6, 5, 4, lv2, lv2, false);

			for (int i = 0; i <= 6; i++) {
				for (int j = 0; j <= 6; j++) {
					this.method_14936(arg, class_2246.field_10266.method_9564(), i, -1, j, arg2);
				}
			}

			return true;
		}
	}

	public static class class_3406 extends class_3390.class_3403 {
		public class_3406(int i, class_3341 arg, class_2350 arg2) {
			super(class_3773.field_16967, i);
			this.method_14926(arg2);
			this.field_15315 = arg;
		}

		public class_3406(class_3485 arg, class_2487 arg2) {
			super(class_3773.field_16967, arg2);
		}

		@Override
		public void method_14918(class_3443 arg, List<class_3443> list, Random random) {
			this.method_14808((class_3390.class_3407)arg, list, random, 6, 2, false);
		}

		public static class_3390.class_3406 method_14818(List<class_3443> list, int i, int j, int k, int l, class_2350 arg) {
			class_3341 lv = class_3341.method_14667(i, j, k, -2, 0, 0, 7, 11, 7, arg);
			return method_14809(lv) && class_3443.method_14932(list, lv) == null ? new class_3390.class_3406(l, lv, arg) : null;
		}

		@Override
		public boolean method_14931(class_1936 arg, Random random, class_3341 arg2, class_1923 arg3) {
			this.method_14940(arg, arg2, 0, 0, 0, 6, 1, 6, class_2246.field_10266.method_9564(), class_2246.field_10266.method_9564(), false);
			this.method_14940(arg, arg2, 0, 2, 0, 6, 10, 6, class_2246.field_10124.method_9564(), class_2246.field_10124.method_9564(), false);
			this.method_14940(arg, arg2, 0, 2, 0, 1, 8, 0, class_2246.field_10266.method_9564(), class_2246.field_10266.method_9564(), false);
			this.method_14940(arg, arg2, 5, 2, 0, 6, 8, 0, class_2246.field_10266.method_9564(), class_2246.field_10266.method_9564(), false);
			this.method_14940(arg, arg2, 0, 2, 1, 0, 8, 6, class_2246.field_10266.method_9564(), class_2246.field_10266.method_9564(), false);
			this.method_14940(arg, arg2, 6, 2, 1, 6, 8, 6, class_2246.field_10266.method_9564(), class_2246.field_10266.method_9564(), false);
			this.method_14940(arg, arg2, 1, 2, 6, 5, 8, 6, class_2246.field_10266.method_9564(), class_2246.field_10266.method_9564(), false);
			class_2680 lv = class_2246.field_10364
				.method_9564()
				.method_11657(class_2354.field_10903, Boolean.valueOf(true))
				.method_11657(class_2354.field_10907, Boolean.valueOf(true));
			class_2680 lv2 = class_2246.field_10364
				.method_9564()
				.method_11657(class_2354.field_10905, Boolean.valueOf(true))
				.method_11657(class_2354.field_10904, Boolean.valueOf(true));
			this.method_14940(arg, arg2, 0, 3, 2, 0, 5, 4, lv2, lv2, false);
			this.method_14940(arg, arg2, 6, 3, 2, 6, 5, 2, lv2, lv2, false);
			this.method_14940(arg, arg2, 6, 3, 4, 6, 5, 4, lv2, lv2, false);
			this.method_14917(arg, class_2246.field_10266.method_9564(), 5, 2, 5, arg2);
			this.method_14940(arg, arg2, 4, 2, 5, 4, 3, 5, class_2246.field_10266.method_9564(), class_2246.field_10266.method_9564(), false);
			this.method_14940(arg, arg2, 3, 2, 5, 3, 4, 5, class_2246.field_10266.method_9564(), class_2246.field_10266.method_9564(), false);
			this.method_14940(arg, arg2, 2, 2, 5, 2, 5, 5, class_2246.field_10266.method_9564(), class_2246.field_10266.method_9564(), false);
			this.method_14940(arg, arg2, 1, 2, 5, 1, 6, 5, class_2246.field_10266.method_9564(), class_2246.field_10266.method_9564(), false);
			this.method_14940(arg, arg2, 1, 7, 1, 5, 7, 4, class_2246.field_10266.method_9564(), class_2246.field_10266.method_9564(), false);
			this.method_14940(arg, arg2, 6, 8, 2, 6, 8, 4, class_2246.field_10124.method_9564(), class_2246.field_10124.method_9564(), false);
			this.method_14940(arg, arg2, 2, 6, 0, 4, 8, 0, class_2246.field_10266.method_9564(), class_2246.field_10266.method_9564(), false);
			this.method_14940(arg, arg2, 2, 5, 0, 4, 5, 0, lv, lv, false);

			for (int i = 0; i <= 6; i++) {
				for (int j = 0; j <= 6; j++) {
					this.method_14936(arg, class_2246.field_10266.method_9564(), i, -1, j, arg2);
				}
			}

			return true;
		}
	}

	public static class class_3407 extends class_3390.class_3391 {
		public class_3390.class_3404 field_14506;
		public List<class_3390.class_3404> field_14507;
		public List<class_3390.class_3404> field_14504;
		public final List<class_3443> field_14505 = Lists.<class_3443>newArrayList();

		public class_3407(Random random, int i, int j) {
			super(random, i, j);
			this.field_14507 = Lists.<class_3390.class_3404>newArrayList();

			for (class_3390.class_3404 lv : class_3390.field_14494) {
				lv.field_14502 = 0;
				this.field_14507.add(lv);
			}

			this.field_14504 = Lists.<class_3390.class_3404>newArrayList();

			for (class_3390.class_3404 lv : class_3390.field_14493) {
				lv.field_14502 = 0;
				this.field_14504.add(lv);
			}
		}

		public class_3407(class_3485 arg, class_2487 arg2) {
			super(class_3773.field_16924, arg2);
		}
	}
}
