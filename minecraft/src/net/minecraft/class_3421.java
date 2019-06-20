package net.minecraft;

import com.google.common.collect.Lists;
import java.util.List;
import java.util.Random;
import javax.annotation.Nullable;

public class class_3421 {
	private static final class_3421.class_3427[] field_15265 = new class_3421.class_3427[]{
		new class_3421.class_3427(class_3421.class_3435.class, 40, 0),
		new class_3421.class_3427(class_3421.class_3429.class, 5, 5),
		new class_3421.class_3427(class_3421.class_3425.class, 20, 0),
		new class_3421.class_3427(class_3421.class_3430.class, 20, 0),
		new class_3421.class_3427(class_3421.class_3431.class, 10, 6),
		new class_3421.class_3427(class_3421.class_3436.class, 5, 5),
		new class_3421.class_3427(class_3421.class_3433.class, 5, 5),
		new class_3421.class_3427(class_3421.class_3424.class, 5, 4),
		new class_3421.class_3427(class_3421.class_3422.class, 5, 4),
		new class_3421.class_3427(class_3421.class_3426.class, 10, 2) {
			@Override
			public boolean method_14862(int i) {
				return super.method_14862(i) && i > 4;
			}
		},
		new class_3421.class_3427(class_3421.class_3428.class, 20, 1) {
			@Override
			public boolean method_14862(int i) {
				return super.method_14862(i) && i > 5;
			}
		}
	};
	private static List<class_3421.class_3427> field_15267;
	private static Class<? extends class_3421.class_3437> field_15266;
	private static int field_15264;
	private static final class_3421.class_3432 field_15263 = new class_3421.class_3432();

	public static void method_14855() {
		field_15267 = Lists.<class_3421.class_3427>newArrayList();

		for (class_3421.class_3427 lv : field_15265) {
			lv.field_15277 = 0;
			field_15267.add(lv);
		}

		field_15266 = null;
	}

	private static boolean method_14852() {
		boolean bl = false;
		field_15264 = 0;

		for (class_3421.class_3427 lv : field_15267) {
			if (lv.field_15275 > 0 && lv.field_15277 < lv.field_15275) {
				bl = true;
			}

			field_15264 = field_15264 + lv.field_15278;
		}

		return bl;
	}

	private static class_3421.class_3437 method_14847(
		Class<? extends class_3421.class_3437> class_, List<class_3443> list, Random random, int i, int j, int k, @Nullable class_2350 arg, int l
	) {
		class_3421.class_3437 lv = null;
		if (class_ == class_3421.class_3435.class) {
			lv = class_3421.class_3435.method_14867(list, random, i, j, k, arg, l);
		} else if (class_ == class_3421.class_3429.class) {
			lv = class_3421.class_3429.method_14864(list, random, i, j, k, arg, l);
		} else if (class_ == class_3421.class_3425.class) {
			lv = class_3421.class_3425.method_14859(list, random, i, j, k, arg, l);
		} else if (class_ == class_3421.class_3430.class) {
			lv = class_3421.class_3430.method_16652(list, random, i, j, k, arg, l);
		} else if (class_ == class_3421.class_3431.class) {
			lv = class_3421.class_3431.method_14865(list, random, i, j, k, arg, l);
		} else if (class_ == class_3421.class_3436.class) {
			lv = class_3421.class_3436.method_14868(list, random, i, j, k, arg, l);
		} else if (class_ == class_3421.class_3433.class) {
			lv = class_3421.class_3433.method_14866(list, random, i, j, k, arg, l);
		} else if (class_ == class_3421.class_3424.class) {
			lv = class_3421.class_3424.method_14858(list, random, i, j, k, arg, l);
		} else if (class_ == class_3421.class_3422.class) {
			lv = class_3421.class_3422.method_14856(list, random, i, j, k, arg, l);
		} else if (class_ == class_3421.class_3426.class) {
			lv = class_3421.class_3426.method_14860(list, random, i, j, k, arg, l);
		} else if (class_ == class_3421.class_3428.class) {
			lv = class_3421.class_3428.method_14863(list, i, j, k, arg, l);
		}

		return lv;
	}

	private static class_3421.class_3437 method_14851(class_3421.class_3434 arg, List<class_3443> list, Random random, int i, int j, int k, class_2350 arg2, int l) {
		if (!method_14852()) {
			return null;
		} else {
			if (field_15266 != null) {
				class_3421.class_3437 lv = method_14847(field_15266, list, random, i, j, k, arg2, l);
				field_15266 = null;
				if (lv != null) {
					return lv;
				}
			}

			int m = 0;

			while (m < 5) {
				m++;
				int n = random.nextInt(field_15264);

				for (class_3421.class_3427 lv2 : field_15267) {
					n -= lv2.field_15278;
					if (n < 0) {
						if (!lv2.method_14862(l) || lv2 == arg.field_15284) {
							break;
						}

						class_3421.class_3437 lv3 = method_14847(lv2.field_15276, list, random, i, j, k, arg2, l);
						if (lv3 != null) {
							lv2.field_15277++;
							arg.field_15284 = lv2;
							if (!lv2.method_14861()) {
								field_15267.remove(lv2);
							}

							return lv3;
						}
					}
				}
			}

			class_3341 lv4 = class_3421.class_3423.method_14857(list, random, i, j, k, arg2);
			return lv4 != null && lv4.field_14380 > 1 ? new class_3421.class_3423(l, lv4, arg2) : null;
		}
	}

	private static class_3443 method_14854(class_3421.class_3434 arg, List<class_3443> list, Random random, int i, int j, int k, @Nullable class_2350 arg2, int l) {
		if (l > 50) {
			return null;
		} else if (Math.abs(i - arg.method_14935().field_14381) <= 112 && Math.abs(k - arg.method_14935().field_14379) <= 112) {
			class_3443 lv = method_14851(arg, list, random, i, j, k, arg2, l + 1);
			if (lv != null) {
				list.add(lv);
				arg.field_15282.add(lv);
			}

			return lv;
		} else {
			return null;
		}
	}

	public static class class_3422 extends class_3421.class_3437 {
		private boolean field_15268;

		public class_3422(int i, Random random, class_3341 arg, class_2350 arg2) {
			super(class_3773.field_16955, i);
			this.method_14926(arg2);
			this.field_15287 = this.method_14869(random);
			this.field_15315 = arg;
		}

		public class_3422(class_3485 arg, class_2487 arg2) {
			super(class_3773.field_16955, arg2);
			this.field_15268 = arg2.method_10577("Chest");
		}

		@Override
		protected void method_14943(class_2487 arg) {
			super.method_14943(arg);
			arg.method_10556("Chest", this.field_15268);
		}

		@Override
		public void method_14918(class_3443 arg, List<class_3443> list, Random random) {
			this.method_14874((class_3421.class_3434)arg, list, random, 1, 1);
		}

		public static class_3421.class_3422 method_14856(List<class_3443> list, Random random, int i, int j, int k, class_2350 arg, int l) {
			class_3341 lv = class_3341.method_14667(i, j, k, -1, -1, 0, 5, 5, 7, arg);
			return method_14871(lv) && class_3443.method_14932(list, lv) == null ? new class_3421.class_3422(l, random, lv, arg) : null;
		}

		@Override
		public boolean method_14931(class_1936 arg, Random random, class_3341 arg2, class_1923 arg3) {
			this.method_14938(arg, arg2, 0, 0, 0, 4, 4, 6, true, random, class_3421.field_15263);
			this.method_14872(arg, random, arg2, this.field_15287, 1, 1, 0);
			this.method_14872(arg, random, arg2, class_3421.class_3437.class_3438.field_15288, 1, 1, 6);
			this.method_14940(arg, arg2, 3, 1, 2, 3, 1, 4, class_2246.field_10056.method_9564(), class_2246.field_10056.method_9564(), false);
			this.method_14917(arg, class_2246.field_10131.method_9564(), 3, 1, 1, arg2);
			this.method_14917(arg, class_2246.field_10131.method_9564(), 3, 1, 5, arg2);
			this.method_14917(arg, class_2246.field_10131.method_9564(), 3, 2, 2, arg2);
			this.method_14917(arg, class_2246.field_10131.method_9564(), 3, 2, 4, arg2);

			for (int i = 2; i <= 4; i++) {
				this.method_14917(arg, class_2246.field_10131.method_9564(), 2, 1, i, arg2);
			}

			if (!this.field_15268 && arg2.method_14662(new class_2338(this.method_14928(3, 3), this.method_14924(2), this.method_14941(3, 3)))) {
				this.field_15268 = true;
				this.method_14915(arg, arg2, random, 3, 2, 3, class_39.field_842);
			}

			return true;
		}
	}

	public static class class_3423 extends class_3421.class_3437 {
		private final int field_15269;

		public class_3423(int i, class_3341 arg, class_2350 arg2) {
			super(class_3773.field_16965, i);
			this.method_14926(arg2);
			this.field_15315 = arg;
			this.field_15269 = arg2 != class_2350.field_11043 && arg2 != class_2350.field_11035 ? arg.method_14660() : arg.method_14664();
		}

		public class_3423(class_3485 arg, class_2487 arg2) {
			super(class_3773.field_16965, arg2);
			this.field_15269 = arg2.method_10550("Steps");
		}

		@Override
		protected void method_14943(class_2487 arg) {
			super.method_14943(arg);
			arg.method_10569("Steps", this.field_15269);
		}

		public static class_3341 method_14857(List<class_3443> list, Random random, int i, int j, int k, class_2350 arg) {
			int l = 3;
			class_3341 lv = class_3341.method_14667(i, j, k, -1, -1, 0, 5, 5, 4, arg);
			class_3443 lv2 = class_3443.method_14932(list, lv);
			if (lv2 == null) {
				return null;
			} else {
				if (lv2.method_14935().field_14380 == lv.field_14380) {
					for (int m = 3; m >= 1; m--) {
						lv = class_3341.method_14667(i, j, k, -1, -1, 0, 5, 5, m - 1, arg);
						if (!lv2.method_14935().method_14657(lv)) {
							return class_3341.method_14667(i, j, k, -1, -1, 0, 5, 5, m, arg);
						}
					}
				}

				return null;
			}
		}

		@Override
		public boolean method_14931(class_1936 arg, Random random, class_3341 arg2, class_1923 arg3) {
			for (int i = 0; i < this.field_15269; i++) {
				this.method_14917(arg, class_2246.field_10056.method_9564(), 0, 0, i, arg2);
				this.method_14917(arg, class_2246.field_10056.method_9564(), 1, 0, i, arg2);
				this.method_14917(arg, class_2246.field_10056.method_9564(), 2, 0, i, arg2);
				this.method_14917(arg, class_2246.field_10056.method_9564(), 3, 0, i, arg2);
				this.method_14917(arg, class_2246.field_10056.method_9564(), 4, 0, i, arg2);

				for (int j = 1; j <= 3; j++) {
					this.method_14917(arg, class_2246.field_10056.method_9564(), 0, j, i, arg2);
					this.method_14917(arg, class_2246.field_10543.method_9564(), 1, j, i, arg2);
					this.method_14917(arg, class_2246.field_10543.method_9564(), 2, j, i, arg2);
					this.method_14917(arg, class_2246.field_10543.method_9564(), 3, j, i, arg2);
					this.method_14917(arg, class_2246.field_10056.method_9564(), 4, j, i, arg2);
				}

				this.method_14917(arg, class_2246.field_10056.method_9564(), 0, 4, i, arg2);
				this.method_14917(arg, class_2246.field_10056.method_9564(), 1, 4, i, arg2);
				this.method_14917(arg, class_2246.field_10056.method_9564(), 2, 4, i, arg2);
				this.method_14917(arg, class_2246.field_10056.method_9564(), 3, 4, i, arg2);
				this.method_14917(arg, class_2246.field_10056.method_9564(), 4, 4, i, arg2);
			}

			return true;
		}
	}

	public static class class_3424 extends class_3421.class_3437 {
		private final boolean field_15273;
		private final boolean field_15272;
		private final boolean field_15271;
		private final boolean field_15270;

		public class_3424(int i, Random random, class_3341 arg, class_2350 arg2) {
			super(class_3773.field_16937, i);
			this.method_14926(arg2);
			this.field_15287 = this.method_14869(random);
			this.field_15315 = arg;
			this.field_15273 = random.nextBoolean();
			this.field_15272 = random.nextBoolean();
			this.field_15271 = random.nextBoolean();
			this.field_15270 = random.nextInt(3) > 0;
		}

		public class_3424(class_3485 arg, class_2487 arg2) {
			super(class_3773.field_16937, arg2);
			this.field_15273 = arg2.method_10577("leftLow");
			this.field_15272 = arg2.method_10577("leftHigh");
			this.field_15271 = arg2.method_10577("rightLow");
			this.field_15270 = arg2.method_10577("rightHigh");
		}

		@Override
		protected void method_14943(class_2487 arg) {
			super.method_14943(arg);
			arg.method_10556("leftLow", this.field_15273);
			arg.method_10556("leftHigh", this.field_15272);
			arg.method_10556("rightLow", this.field_15271);
			arg.method_10556("rightHigh", this.field_15270);
		}

		@Override
		public void method_14918(class_3443 arg, List<class_3443> list, Random random) {
			int i = 3;
			int j = 5;
			class_2350 lv = this.method_14934();
			if (lv == class_2350.field_11039 || lv == class_2350.field_11043) {
				i = 8 - i;
				j = 8 - j;
			}

			this.method_14874((class_3421.class_3434)arg, list, random, 5, 1);
			if (this.field_15273) {
				this.method_14870((class_3421.class_3434)arg, list, random, i, 1);
			}

			if (this.field_15272) {
				this.method_14870((class_3421.class_3434)arg, list, random, j, 7);
			}

			if (this.field_15271) {
				this.method_14873((class_3421.class_3434)arg, list, random, i, 1);
			}

			if (this.field_15270) {
				this.method_14873((class_3421.class_3434)arg, list, random, j, 7);
			}
		}

		public static class_3421.class_3424 method_14858(List<class_3443> list, Random random, int i, int j, int k, class_2350 arg, int l) {
			class_3341 lv = class_3341.method_14667(i, j, k, -4, -3, 0, 10, 9, 11, arg);
			return method_14871(lv) && class_3443.method_14932(list, lv) == null ? new class_3421.class_3424(l, random, lv, arg) : null;
		}

		@Override
		public boolean method_14931(class_1936 arg, Random random, class_3341 arg2, class_1923 arg3) {
			this.method_14938(arg, arg2, 0, 0, 0, 9, 8, 10, true, random, class_3421.field_15263);
			this.method_14872(arg, random, arg2, this.field_15287, 4, 3, 0);
			if (this.field_15273) {
				this.method_14940(arg, arg2, 0, 3, 1, 0, 5, 3, field_15314, field_15314, false);
			}

			if (this.field_15271) {
				this.method_14940(arg, arg2, 9, 3, 1, 9, 5, 3, field_15314, field_15314, false);
			}

			if (this.field_15272) {
				this.method_14940(arg, arg2, 0, 5, 7, 0, 7, 9, field_15314, field_15314, false);
			}

			if (this.field_15270) {
				this.method_14940(arg, arg2, 9, 5, 7, 9, 7, 9, field_15314, field_15314, false);
			}

			this.method_14940(arg, arg2, 5, 1, 10, 7, 3, 10, field_15314, field_15314, false);
			this.method_14938(arg, arg2, 1, 2, 1, 8, 2, 6, false, random, class_3421.field_15263);
			this.method_14938(arg, arg2, 4, 1, 5, 4, 4, 9, false, random, class_3421.field_15263);
			this.method_14938(arg, arg2, 8, 1, 5, 8, 4, 9, false, random, class_3421.field_15263);
			this.method_14938(arg, arg2, 1, 4, 7, 3, 4, 9, false, random, class_3421.field_15263);
			this.method_14938(arg, arg2, 1, 3, 5, 3, 3, 6, false, random, class_3421.field_15263);
			this.method_14940(arg, arg2, 1, 3, 4, 3, 3, 4, class_2246.field_10136.method_9564(), class_2246.field_10136.method_9564(), false);
			this.method_14940(arg, arg2, 1, 4, 6, 3, 4, 6, class_2246.field_10136.method_9564(), class_2246.field_10136.method_9564(), false);
			this.method_14938(arg, arg2, 5, 1, 7, 7, 1, 8, false, random, class_3421.field_15263);
			this.method_14940(arg, arg2, 5, 1, 9, 7, 1, 9, class_2246.field_10136.method_9564(), class_2246.field_10136.method_9564(), false);
			this.method_14940(arg, arg2, 5, 2, 7, 7, 2, 7, class_2246.field_10136.method_9564(), class_2246.field_10136.method_9564(), false);
			this.method_14940(arg, arg2, 4, 5, 7, 4, 5, 9, class_2246.field_10136.method_9564(), class_2246.field_10136.method_9564(), false);
			this.method_14940(arg, arg2, 8, 5, 7, 8, 5, 9, class_2246.field_10136.method_9564(), class_2246.field_10136.method_9564(), false);
			this.method_14940(
				arg,
				arg2,
				5,
				5,
				7,
				7,
				5,
				9,
				class_2246.field_10136.method_9564().method_11657(class_2482.field_11501, class_2771.field_12682),
				class_2246.field_10136.method_9564().method_11657(class_2482.field_11501, class_2771.field_12682),
				false
			);
			this.method_14917(arg, class_2246.field_10099.method_9564().method_11657(class_2555.field_11731, class_2350.field_11035), 6, 5, 6, arg2);
			return true;
		}
	}

	public static class class_3425 extends class_3421.class_3466 {
		public class_3425(int i, Random random, class_3341 arg, class_2350 arg2) {
			super(class_3773.field_16906, i);
			this.method_14926(arg2);
			this.field_15287 = this.method_14869(random);
			this.field_15315 = arg;
		}

		public class_3425(class_3485 arg, class_2487 arg2) {
			super(class_3773.field_16906, arg2);
		}

		@Override
		public void method_14918(class_3443 arg, List<class_3443> list, Random random) {
			class_2350 lv = this.method_14934();
			if (lv != class_2350.field_11043 && lv != class_2350.field_11034) {
				this.method_14873((class_3421.class_3434)arg, list, random, 1, 1);
			} else {
				this.method_14870((class_3421.class_3434)arg, list, random, 1, 1);
			}
		}

		public static class_3421.class_3425 method_14859(List<class_3443> list, Random random, int i, int j, int k, class_2350 arg, int l) {
			class_3341 lv = class_3341.method_14667(i, j, k, -1, -1, 0, 5, 5, 5, arg);
			return method_14871(lv) && class_3443.method_14932(list, lv) == null ? new class_3421.class_3425(l, random, lv, arg) : null;
		}

		@Override
		public boolean method_14931(class_1936 arg, Random random, class_3341 arg2, class_1923 arg3) {
			this.method_14938(arg, arg2, 0, 0, 0, 4, 4, 4, true, random, class_3421.field_15263);
			this.method_14872(arg, random, arg2, this.field_15287, 1, 1, 0);
			class_2350 lv = this.method_14934();
			if (lv != class_2350.field_11043 && lv != class_2350.field_11034) {
				this.method_14940(arg, arg2, 4, 1, 1, 4, 3, 3, field_15314, field_15314, false);
			} else {
				this.method_14940(arg, arg2, 0, 1, 1, 0, 3, 3, field_15314, field_15314, false);
			}

			return true;
		}
	}

	public static class class_3426 extends class_3421.class_3437 {
		private final boolean field_15274;

		public class_3426(int i, Random random, class_3341 arg, class_2350 arg2) {
			super(class_3773.field_16959, i);
			this.method_14926(arg2);
			this.field_15287 = this.method_14869(random);
			this.field_15315 = arg;
			this.field_15274 = arg.method_14663() > 6;
		}

		public class_3426(class_3485 arg, class_2487 arg2) {
			super(class_3773.field_16959, arg2);
			this.field_15274 = arg2.method_10577("Tall");
		}

		@Override
		protected void method_14943(class_2487 arg) {
			super.method_14943(arg);
			arg.method_10556("Tall", this.field_15274);
		}

		public static class_3421.class_3426 method_14860(List<class_3443> list, Random random, int i, int j, int k, class_2350 arg, int l) {
			class_3341 lv = class_3341.method_14667(i, j, k, -4, -1, 0, 14, 11, 15, arg);
			if (!method_14871(lv) || class_3443.method_14932(list, lv) != null) {
				lv = class_3341.method_14667(i, j, k, -4, -1, 0, 14, 6, 15, arg);
				if (!method_14871(lv) || class_3443.method_14932(list, lv) != null) {
					return null;
				}
			}

			return new class_3421.class_3426(l, random, lv, arg);
		}

		@Override
		public boolean method_14931(class_1936 arg, Random random, class_3341 arg2, class_1923 arg3) {
			int i = 11;
			if (!this.field_15274) {
				i = 6;
			}

			this.method_14938(arg, arg2, 0, 0, 0, 13, i - 1, 14, true, random, class_3421.field_15263);
			this.method_14872(arg, random, arg2, this.field_15287, 4, 1, 0);
			this.method_14933(arg, arg2, random, 0.07F, 2, 1, 1, 11, 4, 13, class_2246.field_10343.method_9564(), class_2246.field_10343.method_9564(), false, false);
			int j = 1;
			int k = 12;

			for (int l = 1; l <= 13; l++) {
				if ((l - 1) % 4 == 0) {
					this.method_14940(arg, arg2, 1, 1, l, 1, 4, l, class_2246.field_10161.method_9564(), class_2246.field_10161.method_9564(), false);
					this.method_14940(arg, arg2, 12, 1, l, 12, 4, l, class_2246.field_10161.method_9564(), class_2246.field_10161.method_9564(), false);
					this.method_14917(arg, class_2246.field_10099.method_9564().method_11657(class_2555.field_11731, class_2350.field_11034), 2, 3, l, arg2);
					this.method_14917(arg, class_2246.field_10099.method_9564().method_11657(class_2555.field_11731, class_2350.field_11039), 11, 3, l, arg2);
					if (this.field_15274) {
						this.method_14940(arg, arg2, 1, 6, l, 1, 9, l, class_2246.field_10161.method_9564(), class_2246.field_10161.method_9564(), false);
						this.method_14940(arg, arg2, 12, 6, l, 12, 9, l, class_2246.field_10161.method_9564(), class_2246.field_10161.method_9564(), false);
					}
				} else {
					this.method_14940(arg, arg2, 1, 1, l, 1, 4, l, class_2246.field_10504.method_9564(), class_2246.field_10504.method_9564(), false);
					this.method_14940(arg, arg2, 12, 1, l, 12, 4, l, class_2246.field_10504.method_9564(), class_2246.field_10504.method_9564(), false);
					if (this.field_15274) {
						this.method_14940(arg, arg2, 1, 6, l, 1, 9, l, class_2246.field_10504.method_9564(), class_2246.field_10504.method_9564(), false);
						this.method_14940(arg, arg2, 12, 6, l, 12, 9, l, class_2246.field_10504.method_9564(), class_2246.field_10504.method_9564(), false);
					}
				}
			}

			for (int lx = 3; lx < 12; lx += 2) {
				this.method_14940(arg, arg2, 3, 1, lx, 4, 3, lx, class_2246.field_10504.method_9564(), class_2246.field_10504.method_9564(), false);
				this.method_14940(arg, arg2, 6, 1, lx, 7, 3, lx, class_2246.field_10504.method_9564(), class_2246.field_10504.method_9564(), false);
				this.method_14940(arg, arg2, 9, 1, lx, 10, 3, lx, class_2246.field_10504.method_9564(), class_2246.field_10504.method_9564(), false);
			}

			if (this.field_15274) {
				this.method_14940(arg, arg2, 1, 5, 1, 3, 5, 13, class_2246.field_10161.method_9564(), class_2246.field_10161.method_9564(), false);
				this.method_14940(arg, arg2, 10, 5, 1, 12, 5, 13, class_2246.field_10161.method_9564(), class_2246.field_10161.method_9564(), false);
				this.method_14940(arg, arg2, 4, 5, 1, 9, 5, 2, class_2246.field_10161.method_9564(), class_2246.field_10161.method_9564(), false);
				this.method_14940(arg, arg2, 4, 5, 12, 9, 5, 13, class_2246.field_10161.method_9564(), class_2246.field_10161.method_9564(), false);
				this.method_14917(arg, class_2246.field_10161.method_9564(), 9, 5, 11, arg2);
				this.method_14917(arg, class_2246.field_10161.method_9564(), 8, 5, 11, arg2);
				this.method_14917(arg, class_2246.field_10161.method_9564(), 9, 5, 10, arg2);
				class_2680 lv = class_2246.field_10620
					.method_9564()
					.method_11657(class_2354.field_10903, Boolean.valueOf(true))
					.method_11657(class_2354.field_10907, Boolean.valueOf(true));
				class_2680 lv2 = class_2246.field_10620
					.method_9564()
					.method_11657(class_2354.field_10905, Boolean.valueOf(true))
					.method_11657(class_2354.field_10904, Boolean.valueOf(true));
				this.method_14940(arg, arg2, 3, 6, 3, 3, 6, 11, lv2, lv2, false);
				this.method_14940(arg, arg2, 10, 6, 3, 10, 6, 9, lv2, lv2, false);
				this.method_14940(arg, arg2, 4, 6, 2, 9, 6, 2, lv, lv, false);
				this.method_14940(arg, arg2, 4, 6, 12, 7, 6, 12, lv, lv, false);
				this.method_14917(
					arg,
					class_2246.field_10620
						.method_9564()
						.method_11657(class_2354.field_10905, Boolean.valueOf(true))
						.method_11657(class_2354.field_10907, Boolean.valueOf(true)),
					3,
					6,
					2,
					arg2
				);
				this.method_14917(
					arg,
					class_2246.field_10620
						.method_9564()
						.method_11657(class_2354.field_10904, Boolean.valueOf(true))
						.method_11657(class_2354.field_10907, Boolean.valueOf(true)),
					3,
					6,
					12,
					arg2
				);
				this.method_14917(
					arg,
					class_2246.field_10620
						.method_9564()
						.method_11657(class_2354.field_10905, Boolean.valueOf(true))
						.method_11657(class_2354.field_10903, Boolean.valueOf(true)),
					10,
					6,
					2,
					arg2
				);

				for (int m = 0; m <= 2; m++) {
					this.method_14917(
						arg,
						class_2246.field_10620
							.method_9564()
							.method_11657(class_2354.field_10904, Boolean.valueOf(true))
							.method_11657(class_2354.field_10903, Boolean.valueOf(true)),
						8 + m,
						6,
						12 - m,
						arg2
					);
					if (m != 2) {
						this.method_14917(
							arg,
							class_2246.field_10620
								.method_9564()
								.method_11657(class_2354.field_10905, Boolean.valueOf(true))
								.method_11657(class_2354.field_10907, Boolean.valueOf(true)),
							8 + m,
							6,
							11 - m,
							arg2
						);
					}
				}

				class_2680 lv3 = class_2246.field_9983.method_9564().method_11657(class_2399.field_11253, class_2350.field_11035);
				this.method_14917(arg, lv3, 10, 1, 13, arg2);
				this.method_14917(arg, lv3, 10, 2, 13, arg2);
				this.method_14917(arg, lv3, 10, 3, 13, arg2);
				this.method_14917(arg, lv3, 10, 4, 13, arg2);
				this.method_14917(arg, lv3, 10, 5, 13, arg2);
				this.method_14917(arg, lv3, 10, 6, 13, arg2);
				this.method_14917(arg, lv3, 10, 7, 13, arg2);
				int n = 7;
				int o = 7;
				class_2680 lv4 = class_2246.field_10620.method_9564().method_11657(class_2354.field_10907, Boolean.valueOf(true));
				this.method_14917(arg, lv4, 6, 9, 7, arg2);
				class_2680 lv5 = class_2246.field_10620.method_9564().method_11657(class_2354.field_10903, Boolean.valueOf(true));
				this.method_14917(arg, lv5, 7, 9, 7, arg2);
				this.method_14917(arg, lv4, 6, 8, 7, arg2);
				this.method_14917(arg, lv5, 7, 8, 7, arg2);
				class_2680 lv6 = lv2.method_11657(class_2354.field_10903, Boolean.valueOf(true)).method_11657(class_2354.field_10907, Boolean.valueOf(true));
				this.method_14917(arg, lv6, 6, 7, 7, arg2);
				this.method_14917(arg, lv6, 7, 7, 7, arg2);
				this.method_14917(arg, lv4, 5, 7, 7, arg2);
				this.method_14917(arg, lv5, 8, 7, 7, arg2);
				this.method_14917(arg, lv4.method_11657(class_2354.field_10905, Boolean.valueOf(true)), 6, 7, 6, arg2);
				this.method_14917(arg, lv4.method_11657(class_2354.field_10904, Boolean.valueOf(true)), 6, 7, 8, arg2);
				this.method_14917(arg, lv5.method_11657(class_2354.field_10905, Boolean.valueOf(true)), 7, 7, 6, arg2);
				this.method_14917(arg, lv5.method_11657(class_2354.field_10904, Boolean.valueOf(true)), 7, 7, 8, arg2);
				class_2680 lv7 = class_2246.field_10336.method_9564();
				this.method_14917(arg, lv7, 5, 8, 7, arg2);
				this.method_14917(arg, lv7, 8, 8, 7, arg2);
				this.method_14917(arg, lv7, 6, 8, 6, arg2);
				this.method_14917(arg, lv7, 6, 8, 8, arg2);
				this.method_14917(arg, lv7, 7, 8, 6, arg2);
				this.method_14917(arg, lv7, 7, 8, 8, arg2);
			}

			this.method_14915(arg, arg2, random, 3, 3, 5, class_39.field_683);
			if (this.field_15274) {
				this.method_14917(arg, field_15314, 12, 9, 1, arg2);
				this.method_14915(arg, arg2, random, 12, 8, 1, class_39.field_683);
			}

			return true;
		}
	}

	static class class_3427 {
		public final Class<? extends class_3421.class_3437> field_15276;
		public final int field_15278;
		public int field_15277;
		public final int field_15275;

		public class_3427(Class<? extends class_3421.class_3437> class_, int i, int j) {
			this.field_15276 = class_;
			this.field_15278 = i;
			this.field_15275 = j;
		}

		public boolean method_14862(int i) {
			return this.field_15275 == 0 || this.field_15277 < this.field_15275;
		}

		public boolean method_14861() {
			return this.field_15275 == 0 || this.field_15277 < this.field_15275;
		}
	}

	public static class class_3428 extends class_3421.class_3437 {
		private boolean field_15279;

		public class_3428(int i, class_3341 arg, class_2350 arg2) {
			super(class_3773.field_16939, i);
			this.method_14926(arg2);
			this.field_15315 = arg;
		}

		public class_3428(class_3485 arg, class_2487 arg2) {
			super(class_3773.field_16939, arg2);
			this.field_15279 = arg2.method_10577("Mob");
		}

		@Override
		protected void method_14943(class_2487 arg) {
			super.method_14943(arg);
			arg.method_10556("Mob", this.field_15279);
		}

		@Override
		public void method_14918(class_3443 arg, List<class_3443> list, Random random) {
			if (arg != null) {
				((class_3421.class_3434)arg).field_15283 = this;
			}
		}

		public static class_3421.class_3428 method_14863(List<class_3443> list, int i, int j, int k, class_2350 arg, int l) {
			class_3341 lv = class_3341.method_14667(i, j, k, -4, -1, 0, 11, 8, 16, arg);
			return method_14871(lv) && class_3443.method_14932(list, lv) == null ? new class_3421.class_3428(l, lv, arg) : null;
		}

		@Override
		public boolean method_14931(class_1936 arg, Random random, class_3341 arg2, class_1923 arg3) {
			this.method_14938(arg, arg2, 0, 0, 0, 10, 7, 15, false, random, class_3421.field_15263);
			this.method_14872(arg, random, arg2, class_3421.class_3437.class_3438.field_15289, 4, 1, 0);
			int i = 6;
			this.method_14938(arg, arg2, 1, i, 1, 1, i, 14, false, random, class_3421.field_15263);
			this.method_14938(arg, arg2, 9, i, 1, 9, i, 14, false, random, class_3421.field_15263);
			this.method_14938(arg, arg2, 2, i, 1, 8, i, 2, false, random, class_3421.field_15263);
			this.method_14938(arg, arg2, 2, i, 14, 8, i, 14, false, random, class_3421.field_15263);
			this.method_14938(arg, arg2, 1, 1, 1, 2, 1, 4, false, random, class_3421.field_15263);
			this.method_14938(arg, arg2, 8, 1, 1, 9, 1, 4, false, random, class_3421.field_15263);
			this.method_14940(arg, arg2, 1, 1, 1, 1, 1, 3, class_2246.field_10164.method_9564(), class_2246.field_10164.method_9564(), false);
			this.method_14940(arg, arg2, 9, 1, 1, 9, 1, 3, class_2246.field_10164.method_9564(), class_2246.field_10164.method_9564(), false);
			this.method_14938(arg, arg2, 3, 1, 8, 7, 1, 12, false, random, class_3421.field_15263);
			this.method_14940(arg, arg2, 4, 1, 9, 6, 1, 11, class_2246.field_10164.method_9564(), class_2246.field_10164.method_9564(), false);
			class_2680 lv = class_2246.field_10576
				.method_9564()
				.method_11657(class_2389.field_10905, Boolean.valueOf(true))
				.method_11657(class_2389.field_10904, Boolean.valueOf(true));
			class_2680 lv2 = class_2246.field_10576
				.method_9564()
				.method_11657(class_2389.field_10903, Boolean.valueOf(true))
				.method_11657(class_2389.field_10907, Boolean.valueOf(true));

			for (int j = 3; j < 14; j += 2) {
				this.method_14940(arg, arg2, 0, 3, j, 0, 4, j, lv, lv, false);
				this.method_14940(arg, arg2, 10, 3, j, 10, 4, j, lv, lv, false);
			}

			for (int j = 2; j < 9; j += 2) {
				this.method_14940(arg, arg2, j, 3, 15, j, 4, 15, lv2, lv2, false);
			}

			class_2680 lv3 = class_2246.field_10392.method_9564().method_11657(class_2510.field_11571, class_2350.field_11043);
			this.method_14938(arg, arg2, 4, 1, 5, 6, 1, 7, false, random, class_3421.field_15263);
			this.method_14938(arg, arg2, 4, 2, 6, 6, 2, 7, false, random, class_3421.field_15263);
			this.method_14938(arg, arg2, 4, 3, 7, 6, 3, 7, false, random, class_3421.field_15263);

			for (int k = 4; k <= 6; k++) {
				this.method_14917(arg, lv3, k, 1, 4, arg2);
				this.method_14917(arg, lv3, k, 2, 5, arg2);
				this.method_14917(arg, lv3, k, 3, 6, arg2);
			}

			class_2680 lv4 = class_2246.field_10398.method_9564().method_11657(class_2333.field_10954, class_2350.field_11043);
			class_2680 lv5 = class_2246.field_10398.method_9564().method_11657(class_2333.field_10954, class_2350.field_11035);
			class_2680 lv6 = class_2246.field_10398.method_9564().method_11657(class_2333.field_10954, class_2350.field_11034);
			class_2680 lv7 = class_2246.field_10398.method_9564().method_11657(class_2333.field_10954, class_2350.field_11039);
			boolean bl = true;
			boolean[] bls = new boolean[12];

			for (int l = 0; l < bls.length; l++) {
				bls[l] = random.nextFloat() > 0.9F;
				bl &= bls[l];
			}

			this.method_14917(arg, lv4.method_11657(class_2333.field_10958, Boolean.valueOf(bls[0])), 4, 3, 8, arg2);
			this.method_14917(arg, lv4.method_11657(class_2333.field_10958, Boolean.valueOf(bls[1])), 5, 3, 8, arg2);
			this.method_14917(arg, lv4.method_11657(class_2333.field_10958, Boolean.valueOf(bls[2])), 6, 3, 8, arg2);
			this.method_14917(arg, lv5.method_11657(class_2333.field_10958, Boolean.valueOf(bls[3])), 4, 3, 12, arg2);
			this.method_14917(arg, lv5.method_11657(class_2333.field_10958, Boolean.valueOf(bls[4])), 5, 3, 12, arg2);
			this.method_14917(arg, lv5.method_11657(class_2333.field_10958, Boolean.valueOf(bls[5])), 6, 3, 12, arg2);
			this.method_14917(arg, lv6.method_11657(class_2333.field_10958, Boolean.valueOf(bls[6])), 3, 3, 9, arg2);
			this.method_14917(arg, lv6.method_11657(class_2333.field_10958, Boolean.valueOf(bls[7])), 3, 3, 10, arg2);
			this.method_14917(arg, lv6.method_11657(class_2333.field_10958, Boolean.valueOf(bls[8])), 3, 3, 11, arg2);
			this.method_14917(arg, lv7.method_11657(class_2333.field_10958, Boolean.valueOf(bls[9])), 7, 3, 9, arg2);
			this.method_14917(arg, lv7.method_11657(class_2333.field_10958, Boolean.valueOf(bls[10])), 7, 3, 10, arg2);
			this.method_14917(arg, lv7.method_11657(class_2333.field_10958, Boolean.valueOf(bls[11])), 7, 3, 11, arg2);
			if (bl) {
				class_2680 lv8 = class_2246.field_10027.method_9564();
				this.method_14917(arg, lv8, 4, 3, 9, arg2);
				this.method_14917(arg, lv8, 5, 3, 9, arg2);
				this.method_14917(arg, lv8, 6, 3, 9, arg2);
				this.method_14917(arg, lv8, 4, 3, 10, arg2);
				this.method_14917(arg, lv8, 5, 3, 10, arg2);
				this.method_14917(arg, lv8, 6, 3, 10, arg2);
				this.method_14917(arg, lv8, 4, 3, 11, arg2);
				this.method_14917(arg, lv8, 5, 3, 11, arg2);
				this.method_14917(arg, lv8, 6, 3, 11, arg2);
			}

			if (!this.field_15279) {
				i = this.method_14924(3);
				class_2338 lv9 = new class_2338(this.method_14928(5, 6), i, this.method_14941(5, 6));
				if (arg2.method_14662(lv9)) {
					this.field_15279 = true;
					arg.method_8652(lv9, class_2246.field_10260.method_9564(), 2);
					class_2586 lv10 = arg.method_8321(lv9);
					if (lv10 instanceof class_2636) {
						((class_2636)lv10).method_11390().method_8274(class_1299.field_6125);
					}
				}
			}

			return true;
		}
	}

	public static class class_3429 extends class_3421.class_3437 {
		public class_3429(int i, Random random, class_3341 arg, class_2350 arg2) {
			super(class_3773.field_16948, i);
			this.method_14926(arg2);
			this.field_15287 = this.method_14869(random);
			this.field_15315 = arg;
		}

		public class_3429(class_3485 arg, class_2487 arg2) {
			super(class_3773.field_16948, arg2);
		}

		@Override
		public void method_14918(class_3443 arg, List<class_3443> list, Random random) {
			this.method_14874((class_3421.class_3434)arg, list, random, 1, 1);
		}

		public static class_3421.class_3429 method_14864(List<class_3443> list, Random random, int i, int j, int k, class_2350 arg, int l) {
			class_3341 lv = class_3341.method_14667(i, j, k, -1, -1, 0, 9, 5, 11, arg);
			return method_14871(lv) && class_3443.method_14932(list, lv) == null ? new class_3421.class_3429(l, random, lv, arg) : null;
		}

		@Override
		public boolean method_14931(class_1936 arg, Random random, class_3341 arg2, class_1923 arg3) {
			this.method_14938(arg, arg2, 0, 0, 0, 8, 4, 10, true, random, class_3421.field_15263);
			this.method_14872(arg, random, arg2, this.field_15287, 1, 1, 0);
			this.method_14940(arg, arg2, 1, 1, 10, 3, 3, 10, field_15314, field_15314, false);
			this.method_14938(arg, arg2, 4, 1, 1, 4, 3, 1, false, random, class_3421.field_15263);
			this.method_14938(arg, arg2, 4, 1, 3, 4, 3, 3, false, random, class_3421.field_15263);
			this.method_14938(arg, arg2, 4, 1, 7, 4, 3, 7, false, random, class_3421.field_15263);
			this.method_14938(arg, arg2, 4, 1, 9, 4, 3, 9, false, random, class_3421.field_15263);

			for (int i = 1; i <= 3; i++) {
				this.method_14917(
					arg,
					class_2246.field_10576
						.method_9564()
						.method_11657(class_2389.field_10905, Boolean.valueOf(true))
						.method_11657(class_2389.field_10904, Boolean.valueOf(true)),
					4,
					i,
					4,
					arg2
				);
				this.method_14917(
					arg,
					class_2246.field_10576
						.method_9564()
						.method_11657(class_2389.field_10905, Boolean.valueOf(true))
						.method_11657(class_2389.field_10904, Boolean.valueOf(true))
						.method_11657(class_2389.field_10907, Boolean.valueOf(true)),
					4,
					i,
					5,
					arg2
				);
				this.method_14917(
					arg,
					class_2246.field_10576
						.method_9564()
						.method_11657(class_2389.field_10905, Boolean.valueOf(true))
						.method_11657(class_2389.field_10904, Boolean.valueOf(true)),
					4,
					i,
					6,
					arg2
				);
				this.method_14917(
					arg,
					class_2246.field_10576
						.method_9564()
						.method_11657(class_2389.field_10903, Boolean.valueOf(true))
						.method_11657(class_2389.field_10907, Boolean.valueOf(true)),
					5,
					i,
					5,
					arg2
				);
				this.method_14917(
					arg,
					class_2246.field_10576
						.method_9564()
						.method_11657(class_2389.field_10903, Boolean.valueOf(true))
						.method_11657(class_2389.field_10907, Boolean.valueOf(true)),
					6,
					i,
					5,
					arg2
				);
				this.method_14917(
					arg,
					class_2246.field_10576
						.method_9564()
						.method_11657(class_2389.field_10903, Boolean.valueOf(true))
						.method_11657(class_2389.field_10907, Boolean.valueOf(true)),
					7,
					i,
					5,
					arg2
				);
			}

			this.method_14917(
				arg,
				class_2246.field_10576
					.method_9564()
					.method_11657(class_2389.field_10905, Boolean.valueOf(true))
					.method_11657(class_2389.field_10904, Boolean.valueOf(true)),
				4,
				3,
				2,
				arg2
			);
			this.method_14917(
				arg,
				class_2246.field_10576
					.method_9564()
					.method_11657(class_2389.field_10905, Boolean.valueOf(true))
					.method_11657(class_2389.field_10904, Boolean.valueOf(true)),
				4,
				3,
				8,
				arg2
			);
			class_2680 lv = class_2246.field_9973.method_9564().method_11657(class_2323.field_10938, class_2350.field_11039);
			class_2680 lv2 = class_2246.field_9973
				.method_9564()
				.method_11657(class_2323.field_10938, class_2350.field_11039)
				.method_11657(class_2323.field_10946, class_2756.field_12609);
			this.method_14917(arg, lv, 4, 1, 2, arg2);
			this.method_14917(arg, lv2, 4, 2, 2, arg2);
			this.method_14917(arg, lv, 4, 1, 8, arg2);
			this.method_14917(arg, lv2, 4, 2, 8, arg2);
			return true;
		}
	}

	public static class class_3430 extends class_3421.class_3466 {
		public class_3430(int i, Random random, class_3341 arg, class_2350 arg2) {
			super(class_3773.field_16958, i);
			this.method_14926(arg2);
			this.field_15287 = this.method_14869(random);
			this.field_15315 = arg;
		}

		public class_3430(class_3485 arg, class_2487 arg2) {
			super(class_3773.field_16958, arg2);
		}

		@Override
		public void method_14918(class_3443 arg, List<class_3443> list, Random random) {
			class_2350 lv = this.method_14934();
			if (lv != class_2350.field_11043 && lv != class_2350.field_11034) {
				this.method_14870((class_3421.class_3434)arg, list, random, 1, 1);
			} else {
				this.method_14873((class_3421.class_3434)arg, list, random, 1, 1);
			}
		}

		public static class_3421.class_3430 method_16652(List<class_3443> list, Random random, int i, int j, int k, class_2350 arg, int l) {
			class_3341 lv = class_3341.method_14667(i, j, k, -1, -1, 0, 5, 5, 5, arg);
			return method_14871(lv) && class_3443.method_14932(list, lv) == null ? new class_3421.class_3430(l, random, lv, arg) : null;
		}

		@Override
		public boolean method_14931(class_1936 arg, Random random, class_3341 arg2, class_1923 arg3) {
			this.method_14938(arg, arg2, 0, 0, 0, 4, 4, 4, true, random, class_3421.field_15263);
			this.method_14872(arg, random, arg2, this.field_15287, 1, 1, 0);
			class_2350 lv = this.method_14934();
			if (lv != class_2350.field_11043 && lv != class_2350.field_11034) {
				this.method_14940(arg, arg2, 0, 1, 1, 0, 3, 3, field_15314, field_15314, false);
			} else {
				this.method_14940(arg, arg2, 4, 1, 1, 4, 3, 3, field_15314, field_15314, false);
			}

			return true;
		}
	}

	public static class class_3431 extends class_3421.class_3437 {
		protected final int field_15280;

		public class_3431(int i, Random random, class_3341 arg, class_2350 arg2) {
			super(class_3773.field_16941, i);
			this.method_14926(arg2);
			this.field_15287 = this.method_14869(random);
			this.field_15315 = arg;
			this.field_15280 = random.nextInt(5);
		}

		public class_3431(class_3485 arg, class_2487 arg2) {
			super(class_3773.field_16941, arg2);
			this.field_15280 = arg2.method_10550("Type");
		}

		@Override
		protected void method_14943(class_2487 arg) {
			super.method_14943(arg);
			arg.method_10569("Type", this.field_15280);
		}

		@Override
		public void method_14918(class_3443 arg, List<class_3443> list, Random random) {
			this.method_14874((class_3421.class_3434)arg, list, random, 4, 1);
			this.method_14870((class_3421.class_3434)arg, list, random, 1, 4);
			this.method_14873((class_3421.class_3434)arg, list, random, 1, 4);
		}

		public static class_3421.class_3431 method_14865(List<class_3443> list, Random random, int i, int j, int k, class_2350 arg, int l) {
			class_3341 lv = class_3341.method_14667(i, j, k, -4, -1, 0, 11, 7, 11, arg);
			return method_14871(lv) && class_3443.method_14932(list, lv) == null ? new class_3421.class_3431(l, random, lv, arg) : null;
		}

		@Override
		public boolean method_14931(class_1936 arg, Random random, class_3341 arg2, class_1923 arg3) {
			this.method_14938(arg, arg2, 0, 0, 0, 10, 6, 10, true, random, class_3421.field_15263);
			this.method_14872(arg, random, arg2, this.field_15287, 4, 1, 0);
			this.method_14940(arg, arg2, 4, 1, 10, 6, 3, 10, field_15314, field_15314, false);
			this.method_14940(arg, arg2, 0, 1, 4, 0, 3, 6, field_15314, field_15314, false);
			this.method_14940(arg, arg2, 10, 1, 4, 10, 3, 6, field_15314, field_15314, false);
			switch (this.field_15280) {
				case 0:
					this.method_14917(arg, class_2246.field_10056.method_9564(), 5, 1, 5, arg2);
					this.method_14917(arg, class_2246.field_10056.method_9564(), 5, 2, 5, arg2);
					this.method_14917(arg, class_2246.field_10056.method_9564(), 5, 3, 5, arg2);
					this.method_14917(arg, class_2246.field_10099.method_9564().method_11657(class_2555.field_11731, class_2350.field_11039), 4, 3, 5, arg2);
					this.method_14917(arg, class_2246.field_10099.method_9564().method_11657(class_2555.field_11731, class_2350.field_11034), 6, 3, 5, arg2);
					this.method_14917(arg, class_2246.field_10099.method_9564().method_11657(class_2555.field_11731, class_2350.field_11035), 5, 3, 4, arg2);
					this.method_14917(arg, class_2246.field_10099.method_9564().method_11657(class_2555.field_11731, class_2350.field_11043), 5, 3, 6, arg2);
					this.method_14917(arg, class_2246.field_10136.method_9564(), 4, 1, 4, arg2);
					this.method_14917(arg, class_2246.field_10136.method_9564(), 4, 1, 5, arg2);
					this.method_14917(arg, class_2246.field_10136.method_9564(), 4, 1, 6, arg2);
					this.method_14917(arg, class_2246.field_10136.method_9564(), 6, 1, 4, arg2);
					this.method_14917(arg, class_2246.field_10136.method_9564(), 6, 1, 5, arg2);
					this.method_14917(arg, class_2246.field_10136.method_9564(), 6, 1, 6, arg2);
					this.method_14917(arg, class_2246.field_10136.method_9564(), 5, 1, 4, arg2);
					this.method_14917(arg, class_2246.field_10136.method_9564(), 5, 1, 6, arg2);
					break;
				case 1:
					for (int i = 0; i < 5; i++) {
						this.method_14917(arg, class_2246.field_10056.method_9564(), 3, 1, 3 + i, arg2);
						this.method_14917(arg, class_2246.field_10056.method_9564(), 7, 1, 3 + i, arg2);
						this.method_14917(arg, class_2246.field_10056.method_9564(), 3 + i, 1, 3, arg2);
						this.method_14917(arg, class_2246.field_10056.method_9564(), 3 + i, 1, 7, arg2);
					}

					this.method_14917(arg, class_2246.field_10056.method_9564(), 5, 1, 5, arg2);
					this.method_14917(arg, class_2246.field_10056.method_9564(), 5, 2, 5, arg2);
					this.method_14917(arg, class_2246.field_10056.method_9564(), 5, 3, 5, arg2);
					this.method_14917(arg, class_2246.field_10382.method_9564(), 5, 4, 5, arg2);
					break;
				case 2:
					for (int i = 1; i <= 9; i++) {
						this.method_14917(arg, class_2246.field_10445.method_9564(), 1, 3, i, arg2);
						this.method_14917(arg, class_2246.field_10445.method_9564(), 9, 3, i, arg2);
					}

					for (int i = 1; i <= 9; i++) {
						this.method_14917(arg, class_2246.field_10445.method_9564(), i, 3, 1, arg2);
						this.method_14917(arg, class_2246.field_10445.method_9564(), i, 3, 9, arg2);
					}

					this.method_14917(arg, class_2246.field_10445.method_9564(), 5, 1, 4, arg2);
					this.method_14917(arg, class_2246.field_10445.method_9564(), 5, 1, 6, arg2);
					this.method_14917(arg, class_2246.field_10445.method_9564(), 5, 3, 4, arg2);
					this.method_14917(arg, class_2246.field_10445.method_9564(), 5, 3, 6, arg2);
					this.method_14917(arg, class_2246.field_10445.method_9564(), 4, 1, 5, arg2);
					this.method_14917(arg, class_2246.field_10445.method_9564(), 6, 1, 5, arg2);
					this.method_14917(arg, class_2246.field_10445.method_9564(), 4, 3, 5, arg2);
					this.method_14917(arg, class_2246.field_10445.method_9564(), 6, 3, 5, arg2);

					for (int i = 1; i <= 3; i++) {
						this.method_14917(arg, class_2246.field_10445.method_9564(), 4, i, 4, arg2);
						this.method_14917(arg, class_2246.field_10445.method_9564(), 6, i, 4, arg2);
						this.method_14917(arg, class_2246.field_10445.method_9564(), 4, i, 6, arg2);
						this.method_14917(arg, class_2246.field_10445.method_9564(), 6, i, 6, arg2);
					}

					this.method_14917(arg, class_2246.field_10336.method_9564(), 5, 3, 5, arg2);

					for (int i = 2; i <= 8; i++) {
						this.method_14917(arg, class_2246.field_10161.method_9564(), 2, 3, i, arg2);
						this.method_14917(arg, class_2246.field_10161.method_9564(), 3, 3, i, arg2);
						if (i <= 3 || i >= 7) {
							this.method_14917(arg, class_2246.field_10161.method_9564(), 4, 3, i, arg2);
							this.method_14917(arg, class_2246.field_10161.method_9564(), 5, 3, i, arg2);
							this.method_14917(arg, class_2246.field_10161.method_9564(), 6, 3, i, arg2);
						}

						this.method_14917(arg, class_2246.field_10161.method_9564(), 7, 3, i, arg2);
						this.method_14917(arg, class_2246.field_10161.method_9564(), 8, 3, i, arg2);
					}

					class_2680 lv = class_2246.field_9983.method_9564().method_11657(class_2399.field_11253, class_2350.field_11039);
					this.method_14917(arg, lv, 9, 1, 3, arg2);
					this.method_14917(arg, lv, 9, 2, 3, arg2);
					this.method_14917(arg, lv, 9, 3, 3, arg2);
					this.method_14915(arg, arg2, random, 3, 4, 8, class_39.field_800);
			}

			return true;
		}
	}

	static class class_3432 extends class_3443.class_3444 {
		private class_3432() {
		}

		@Override
		public void method_14948(Random random, int i, int j, int k, boolean bl) {
			if (bl) {
				float f = random.nextFloat();
				if (f < 0.2F) {
					this.field_15317 = class_2246.field_10416.method_9564();
				} else if (f < 0.5F) {
					this.field_15317 = class_2246.field_10065.method_9564();
				} else if (f < 0.55F) {
					this.field_15317 = class_2246.field_10387.method_9564();
				} else {
					this.field_15317 = class_2246.field_10056.method_9564();
				}
			} else {
				this.field_15317 = class_2246.field_10543.method_9564();
			}
		}
	}

	public static class class_3433 extends class_3421.class_3437 {
		private final boolean field_15281;

		public class_3433(class_3773 arg, int i, Random random, int j, int k) {
			super(arg, i);
			this.field_15281 = true;
			this.method_14926(class_2350.class_2353.field_11062.method_10183(random));
			this.field_15287 = class_3421.class_3437.class_3438.field_15288;
			if (this.method_14934().method_10166() == class_2350.class_2351.field_11051) {
				this.field_15315 = new class_3341(j, 64, k, j + 5 - 1, 74, k + 5 - 1);
			} else {
				this.field_15315 = new class_3341(j, 64, k, j + 5 - 1, 74, k + 5 - 1);
			}
		}

		public class_3433(int i, Random random, class_3341 arg, class_2350 arg2) {
			super(class_3773.field_16904, i);
			this.field_15281 = false;
			this.method_14926(arg2);
			this.field_15287 = this.method_14869(random);
			this.field_15315 = arg;
		}

		public class_3433(class_3773 arg, class_2487 arg2) {
			super(arg, arg2);
			this.field_15281 = arg2.method_10577("Source");
		}

		public class_3433(class_3485 arg, class_2487 arg2) {
			this(class_3773.field_16904, arg2);
		}

		@Override
		protected void method_14943(class_2487 arg) {
			super.method_14943(arg);
			arg.method_10556("Source", this.field_15281);
		}

		@Override
		public void method_14918(class_3443 arg, List<class_3443> list, Random random) {
			if (this.field_15281) {
				class_3421.field_15266 = class_3421.class_3424.class;
			}

			this.method_14874((class_3421.class_3434)arg, list, random, 1, 1);
		}

		public static class_3421.class_3433 method_14866(List<class_3443> list, Random random, int i, int j, int k, class_2350 arg, int l) {
			class_3341 lv = class_3341.method_14667(i, j, k, -1, -7, 0, 5, 11, 5, arg);
			return method_14871(lv) && class_3443.method_14932(list, lv) == null ? new class_3421.class_3433(l, random, lv, arg) : null;
		}

		@Override
		public boolean method_14931(class_1936 arg, Random random, class_3341 arg2, class_1923 arg3) {
			this.method_14938(arg, arg2, 0, 0, 0, 4, 10, 4, true, random, class_3421.field_15263);
			this.method_14872(arg, random, arg2, this.field_15287, 1, 7, 0);
			this.method_14872(arg, random, arg2, class_3421.class_3437.class_3438.field_15288, 1, 1, 4);
			this.method_14917(arg, class_2246.field_10056.method_9564(), 2, 6, 1, arg2);
			this.method_14917(arg, class_2246.field_10056.method_9564(), 1, 5, 1, arg2);
			this.method_14917(arg, class_2246.field_10136.method_9564(), 1, 6, 1, arg2);
			this.method_14917(arg, class_2246.field_10056.method_9564(), 1, 5, 2, arg2);
			this.method_14917(arg, class_2246.field_10056.method_9564(), 1, 4, 3, arg2);
			this.method_14917(arg, class_2246.field_10136.method_9564(), 1, 5, 3, arg2);
			this.method_14917(arg, class_2246.field_10056.method_9564(), 2, 4, 3, arg2);
			this.method_14917(arg, class_2246.field_10056.method_9564(), 3, 3, 3, arg2);
			this.method_14917(arg, class_2246.field_10136.method_9564(), 3, 4, 3, arg2);
			this.method_14917(arg, class_2246.field_10056.method_9564(), 3, 3, 2, arg2);
			this.method_14917(arg, class_2246.field_10056.method_9564(), 3, 2, 1, arg2);
			this.method_14917(arg, class_2246.field_10136.method_9564(), 3, 3, 1, arg2);
			this.method_14917(arg, class_2246.field_10056.method_9564(), 2, 2, 1, arg2);
			this.method_14917(arg, class_2246.field_10056.method_9564(), 1, 1, 1, arg2);
			this.method_14917(arg, class_2246.field_10136.method_9564(), 1, 2, 1, arg2);
			this.method_14917(arg, class_2246.field_10056.method_9564(), 1, 1, 2, arg2);
			this.method_14917(arg, class_2246.field_10136.method_9564(), 1, 1, 3, arg2);
			return true;
		}
	}

	public static class class_3434 extends class_3421.class_3433 {
		public class_3421.class_3427 field_15284;
		@Nullable
		public class_3421.class_3428 field_15283;
		public final List<class_3443> field_15282 = Lists.<class_3443>newArrayList();

		public class_3434(Random random, int i, int j) {
			super(class_3773.field_16914, 0, random, i, j);
		}

		public class_3434(class_3485 arg, class_2487 arg2) {
			super(class_3773.field_16914, arg2);
		}
	}

	public static class class_3435 extends class_3421.class_3437 {
		private final boolean field_15286;
		private final boolean field_15285;

		public class_3435(int i, Random random, class_3341 arg, class_2350 arg2) {
			super(class_3773.field_16934, i);
			this.method_14926(arg2);
			this.field_15287 = this.method_14869(random);
			this.field_15315 = arg;
			this.field_15286 = random.nextInt(2) == 0;
			this.field_15285 = random.nextInt(2) == 0;
		}

		public class_3435(class_3485 arg, class_2487 arg2) {
			super(class_3773.field_16934, arg2);
			this.field_15286 = arg2.method_10577("Left");
			this.field_15285 = arg2.method_10577("Right");
		}

		@Override
		protected void method_14943(class_2487 arg) {
			super.method_14943(arg);
			arg.method_10556("Left", this.field_15286);
			arg.method_10556("Right", this.field_15285);
		}

		@Override
		public void method_14918(class_3443 arg, List<class_3443> list, Random random) {
			this.method_14874((class_3421.class_3434)arg, list, random, 1, 1);
			if (this.field_15286) {
				this.method_14870((class_3421.class_3434)arg, list, random, 1, 2);
			}

			if (this.field_15285) {
				this.method_14873((class_3421.class_3434)arg, list, random, 1, 2);
			}
		}

		public static class_3421.class_3435 method_14867(List<class_3443> list, Random random, int i, int j, int k, class_2350 arg, int l) {
			class_3341 lv = class_3341.method_14667(i, j, k, -1, -1, 0, 5, 5, 7, arg);
			return method_14871(lv) && class_3443.method_14932(list, lv) == null ? new class_3421.class_3435(l, random, lv, arg) : null;
		}

		@Override
		public boolean method_14931(class_1936 arg, Random random, class_3341 arg2, class_1923 arg3) {
			this.method_14938(arg, arg2, 0, 0, 0, 4, 4, 6, true, random, class_3421.field_15263);
			this.method_14872(arg, random, arg2, this.field_15287, 1, 1, 0);
			this.method_14872(arg, random, arg2, class_3421.class_3437.class_3438.field_15288, 1, 1, 6);
			class_2680 lv = class_2246.field_10099.method_9564().method_11657(class_2555.field_11731, class_2350.field_11034);
			class_2680 lv2 = class_2246.field_10099.method_9564().method_11657(class_2555.field_11731, class_2350.field_11039);
			this.method_14945(arg, arg2, random, 0.1F, 1, 2, 1, lv);
			this.method_14945(arg, arg2, random, 0.1F, 3, 2, 1, lv2);
			this.method_14945(arg, arg2, random, 0.1F, 1, 2, 5, lv);
			this.method_14945(arg, arg2, random, 0.1F, 3, 2, 5, lv2);
			if (this.field_15286) {
				this.method_14940(arg, arg2, 0, 1, 2, 0, 3, 4, field_15314, field_15314, false);
			}

			if (this.field_15285) {
				this.method_14940(arg, arg2, 4, 1, 2, 4, 3, 4, field_15314, field_15314, false);
			}

			return true;
		}
	}

	public static class class_3436 extends class_3421.class_3437 {
		public class_3436(int i, Random random, class_3341 arg, class_2350 arg2) {
			super(class_3773.field_16949, i);
			this.method_14926(arg2);
			this.field_15287 = this.method_14869(random);
			this.field_15315 = arg;
		}

		public class_3436(class_3485 arg, class_2487 arg2) {
			super(class_3773.field_16949, arg2);
		}

		@Override
		public void method_14918(class_3443 arg, List<class_3443> list, Random random) {
			this.method_14874((class_3421.class_3434)arg, list, random, 1, 1);
		}

		public static class_3421.class_3436 method_14868(List<class_3443> list, Random random, int i, int j, int k, class_2350 arg, int l) {
			class_3341 lv = class_3341.method_14667(i, j, k, -1, -7, 0, 5, 11, 8, arg);
			return method_14871(lv) && class_3443.method_14932(list, lv) == null ? new class_3421.class_3436(l, random, lv, arg) : null;
		}

		@Override
		public boolean method_14931(class_1936 arg, Random random, class_3341 arg2, class_1923 arg3) {
			this.method_14938(arg, arg2, 0, 0, 0, 4, 10, 7, true, random, class_3421.field_15263);
			this.method_14872(arg, random, arg2, this.field_15287, 1, 7, 0);
			this.method_14872(arg, random, arg2, class_3421.class_3437.class_3438.field_15288, 1, 1, 7);
			class_2680 lv = class_2246.field_10596.method_9564().method_11657(class_2510.field_11571, class_2350.field_11035);

			for (int i = 0; i < 6; i++) {
				this.method_14917(arg, lv, 1, 6 - i, 1 + i, arg2);
				this.method_14917(arg, lv, 2, 6 - i, 1 + i, arg2);
				this.method_14917(arg, lv, 3, 6 - i, 1 + i, arg2);
				if (i < 5) {
					this.method_14917(arg, class_2246.field_10056.method_9564(), 1, 5 - i, 1 + i, arg2);
					this.method_14917(arg, class_2246.field_10056.method_9564(), 2, 5 - i, 1 + i, arg2);
					this.method_14917(arg, class_2246.field_10056.method_9564(), 3, 5 - i, 1 + i, arg2);
				}
			}

			return true;
		}
	}

	abstract static class class_3437 extends class_3443 {
		protected class_3421.class_3437.class_3438 field_15287 = class_3421.class_3437.class_3438.field_15288;

		protected class_3437(class_3773 arg, int i) {
			super(arg, i);
		}

		public class_3437(class_3773 arg, class_2487 arg2) {
			super(arg, arg2);
			this.field_15287 = class_3421.class_3437.class_3438.valueOf(arg2.method_10558("EntryDoor"));
		}

		@Override
		protected void method_14943(class_2487 arg) {
			arg.method_10582("EntryDoor", this.field_15287.name());
		}

		protected void method_14872(class_1936 arg, Random random, class_3341 arg2, class_3421.class_3437.class_3438 arg3, int i, int j, int k) {
			switch (arg3) {
				case field_15288:
					this.method_14940(arg, arg2, i, j, k, i + 3 - 1, j + 3 - 1, k, field_15314, field_15314, false);
					break;
				case field_15290:
					this.method_14917(arg, class_2246.field_10056.method_9564(), i, j, k, arg2);
					this.method_14917(arg, class_2246.field_10056.method_9564(), i, j + 1, k, arg2);
					this.method_14917(arg, class_2246.field_10056.method_9564(), i, j + 2, k, arg2);
					this.method_14917(arg, class_2246.field_10056.method_9564(), i + 1, j + 2, k, arg2);
					this.method_14917(arg, class_2246.field_10056.method_9564(), i + 2, j + 2, k, arg2);
					this.method_14917(arg, class_2246.field_10056.method_9564(), i + 2, j + 1, k, arg2);
					this.method_14917(arg, class_2246.field_10056.method_9564(), i + 2, j, k, arg2);
					this.method_14917(arg, class_2246.field_10149.method_9564(), i + 1, j, k, arg2);
					this.method_14917(arg, class_2246.field_10149.method_9564().method_11657(class_2323.field_10946, class_2756.field_12609), i + 1, j + 1, k, arg2);
					break;
				case field_15289:
					this.method_14917(arg, class_2246.field_10543.method_9564(), i + 1, j, k, arg2);
					this.method_14917(arg, class_2246.field_10543.method_9564(), i + 1, j + 1, k, arg2);
					this.method_14917(arg, class_2246.field_10576.method_9564().method_11657(class_2389.field_10903, Boolean.valueOf(true)), i, j, k, arg2);
					this.method_14917(arg, class_2246.field_10576.method_9564().method_11657(class_2389.field_10903, Boolean.valueOf(true)), i, j + 1, k, arg2);
					this.method_14917(
						arg,
						class_2246.field_10576
							.method_9564()
							.method_11657(class_2389.field_10907, Boolean.valueOf(true))
							.method_11657(class_2389.field_10903, Boolean.valueOf(true)),
						i,
						j + 2,
						k,
						arg2
					);
					this.method_14917(
						arg,
						class_2246.field_10576
							.method_9564()
							.method_11657(class_2389.field_10907, Boolean.valueOf(true))
							.method_11657(class_2389.field_10903, Boolean.valueOf(true)),
						i + 1,
						j + 2,
						k,
						arg2
					);
					this.method_14917(
						arg,
						class_2246.field_10576
							.method_9564()
							.method_11657(class_2389.field_10907, Boolean.valueOf(true))
							.method_11657(class_2389.field_10903, Boolean.valueOf(true)),
						i + 2,
						j + 2,
						k,
						arg2
					);
					this.method_14917(arg, class_2246.field_10576.method_9564().method_11657(class_2389.field_10907, Boolean.valueOf(true)), i + 2, j + 1, k, arg2);
					this.method_14917(arg, class_2246.field_10576.method_9564().method_11657(class_2389.field_10907, Boolean.valueOf(true)), i + 2, j, k, arg2);
					break;
				case field_15291:
					this.method_14917(arg, class_2246.field_10056.method_9564(), i, j, k, arg2);
					this.method_14917(arg, class_2246.field_10056.method_9564(), i, j + 1, k, arg2);
					this.method_14917(arg, class_2246.field_10056.method_9564(), i, j + 2, k, arg2);
					this.method_14917(arg, class_2246.field_10056.method_9564(), i + 1, j + 2, k, arg2);
					this.method_14917(arg, class_2246.field_10056.method_9564(), i + 2, j + 2, k, arg2);
					this.method_14917(arg, class_2246.field_10056.method_9564(), i + 2, j + 1, k, arg2);
					this.method_14917(arg, class_2246.field_10056.method_9564(), i + 2, j, k, arg2);
					this.method_14917(arg, class_2246.field_9973.method_9564(), i + 1, j, k, arg2);
					this.method_14917(arg, class_2246.field_9973.method_9564().method_11657(class_2323.field_10946, class_2756.field_12609), i + 1, j + 1, k, arg2);
					this.method_14917(arg, class_2246.field_10494.method_9564().method_11657(class_2269.field_11177, class_2350.field_11043), i + 2, j + 1, k + 1, arg2);
					this.method_14917(arg, class_2246.field_10494.method_9564().method_11657(class_2269.field_11177, class_2350.field_11035), i + 2, j + 1, k - 1, arg2);
			}
		}

		protected class_3421.class_3437.class_3438 method_14869(Random random) {
			int i = random.nextInt(5);
			switch (i) {
				case 0:
				case 1:
				default:
					return class_3421.class_3437.class_3438.field_15288;
				case 2:
					return class_3421.class_3437.class_3438.field_15290;
				case 3:
					return class_3421.class_3437.class_3438.field_15289;
				case 4:
					return class_3421.class_3437.class_3438.field_15291;
			}
		}

		@Nullable
		protected class_3443 method_14874(class_3421.class_3434 arg, List<class_3443> list, Random random, int i, int j) {
			class_2350 lv = this.method_14934();
			if (lv != null) {
				switch (lv) {
					case field_11043:
						return class_3421.method_14854(
							arg, list, random, this.field_15315.field_14381 + i, this.field_15315.field_14380 + j, this.field_15315.field_14379 - 1, lv, this.method_14923()
						);
					case field_11035:
						return class_3421.method_14854(
							arg, list, random, this.field_15315.field_14381 + i, this.field_15315.field_14380 + j, this.field_15315.field_14376 + 1, lv, this.method_14923()
						);
					case field_11039:
						return class_3421.method_14854(
							arg, list, random, this.field_15315.field_14381 - 1, this.field_15315.field_14380 + j, this.field_15315.field_14379 + i, lv, this.method_14923()
						);
					case field_11034:
						return class_3421.method_14854(
							arg, list, random, this.field_15315.field_14378 + 1, this.field_15315.field_14380 + j, this.field_15315.field_14379 + i, lv, this.method_14923()
						);
				}
			}

			return null;
		}

		@Nullable
		protected class_3443 method_14870(class_3421.class_3434 arg, List<class_3443> list, Random random, int i, int j) {
			class_2350 lv = this.method_14934();
			if (lv != null) {
				switch (lv) {
					case field_11043:
						return class_3421.method_14854(
							arg,
							list,
							random,
							this.field_15315.field_14381 - 1,
							this.field_15315.field_14380 + i,
							this.field_15315.field_14379 + j,
							class_2350.field_11039,
							this.method_14923()
						);
					case field_11035:
						return class_3421.method_14854(
							arg,
							list,
							random,
							this.field_15315.field_14381 - 1,
							this.field_15315.field_14380 + i,
							this.field_15315.field_14379 + j,
							class_2350.field_11039,
							this.method_14923()
						);
					case field_11039:
						return class_3421.method_14854(
							arg,
							list,
							random,
							this.field_15315.field_14381 + j,
							this.field_15315.field_14380 + i,
							this.field_15315.field_14379 - 1,
							class_2350.field_11043,
							this.method_14923()
						);
					case field_11034:
						return class_3421.method_14854(
							arg,
							list,
							random,
							this.field_15315.field_14381 + j,
							this.field_15315.field_14380 + i,
							this.field_15315.field_14379 - 1,
							class_2350.field_11043,
							this.method_14923()
						);
				}
			}

			return null;
		}

		@Nullable
		protected class_3443 method_14873(class_3421.class_3434 arg, List<class_3443> list, Random random, int i, int j) {
			class_2350 lv = this.method_14934();
			if (lv != null) {
				switch (lv) {
					case field_11043:
						return class_3421.method_14854(
							arg,
							list,
							random,
							this.field_15315.field_14378 + 1,
							this.field_15315.field_14380 + i,
							this.field_15315.field_14379 + j,
							class_2350.field_11034,
							this.method_14923()
						);
					case field_11035:
						return class_3421.method_14854(
							arg,
							list,
							random,
							this.field_15315.field_14378 + 1,
							this.field_15315.field_14380 + i,
							this.field_15315.field_14379 + j,
							class_2350.field_11034,
							this.method_14923()
						);
					case field_11039:
						return class_3421.method_14854(
							arg,
							list,
							random,
							this.field_15315.field_14381 + j,
							this.field_15315.field_14380 + i,
							this.field_15315.field_14376 + 1,
							class_2350.field_11035,
							this.method_14923()
						);
					case field_11034:
						return class_3421.method_14854(
							arg,
							list,
							random,
							this.field_15315.field_14381 + j,
							this.field_15315.field_14380 + i,
							this.field_15315.field_14376 + 1,
							class_2350.field_11035,
							this.method_14923()
						);
				}
			}

			return null;
		}

		protected static boolean method_14871(class_3341 arg) {
			return arg != null && arg.field_14380 > 10;
		}

		public static enum class_3438 {
			field_15288,
			field_15290,
			field_15289,
			field_15291;
		}
	}

	public abstract static class class_3466 extends class_3421.class_3437 {
		protected class_3466(class_3773 arg, int i) {
			super(arg, i);
		}

		public class_3466(class_3773 arg, class_2487 arg2) {
			super(arg, arg2);
		}
	}
}
