package net.minecraft;

import java.util.Random;
import javax.annotation.Nullable;

public class class_2279 extends class_2248 {
	public static final class_2758 field_10762 = class_2741.field_12482;
	private final class_2283 field_10763;

	protected class_2279(class_2283 arg, class_2248.class_2251 arg2) {
		super(arg2);
		this.field_10763 = arg;
		this.method_9590(this.field_10647.method_11664().method_11657(field_10762, Integer.valueOf(0)));
	}

	@Override
	public void method_9588(class_2680 arg, class_1937 arg2, class_2338 arg3, Random random) {
		if (!arg.method_11591(arg2, arg3)) {
			arg2.method_8651(arg3, true);
		} else {
			class_2338 lv = arg3.method_10084();
			if (arg2.method_8623(lv) && lv.method_10264() < 256) {
				int i = (Integer)arg.method_11654(field_10762);
				if (i < 5) {
					boolean bl = false;
					boolean bl2 = false;
					class_2680 lv2 = arg2.method_8320(arg3.method_10074());
					class_2248 lv3 = lv2.method_11614();
					if (lv3 == class_2246.field_10471) {
						bl = true;
					} else if (lv3 == this.field_10763) {
						int j = 1;

						for (int k = 0; k < 4; k++) {
							class_2248 lv4 = arg2.method_8320(arg3.method_10087(j + 1)).method_11614();
							if (lv4 != this.field_10763) {
								if (lv4 == class_2246.field_10471) {
									bl2 = true;
								}
								break;
							}

							j++;
						}

						if (j < 2 || j <= random.nextInt(bl2 ? 5 : 4)) {
							bl = true;
						}
					} else if (lv2.method_11588()) {
						bl = true;
					}

					if (bl && method_9746(arg2, lv, null) && arg2.method_8623(arg3.method_10086(2))) {
						arg2.method_8652(arg3, this.field_10763.method_9759(arg2, arg3), 2);
						this.method_9745(arg2, lv, i);
					} else if (i < 4) {
						int j = random.nextInt(4);
						if (bl2) {
							j++;
						}

						boolean bl3 = false;

						for (int l = 0; l < j; l++) {
							class_2350 lv5 = class_2350.class_2353.field_11062.method_10183(random);
							class_2338 lv6 = arg3.method_10093(lv5);
							if (arg2.method_8623(lv6) && arg2.method_8623(lv6.method_10074()) && method_9746(arg2, lv6, lv5.method_10153())) {
								this.method_9745(arg2, lv6, i + 1);
								bl3 = true;
							}
						}

						if (bl3) {
							arg2.method_8652(arg3, this.field_10763.method_9759(arg2, arg3), 2);
						} else {
							this.method_9747(arg2, arg3);
						}
					} else {
						this.method_9747(arg2, arg3);
					}
				}
			}
		}
	}

	private void method_9745(class_1937 arg, class_2338 arg2, int i) {
		arg.method_8652(arg2, this.method_9564().method_11657(field_10762, Integer.valueOf(i)), 2);
		arg.method_20290(1033, arg2, 0);
	}

	private void method_9747(class_1937 arg, class_2338 arg2) {
		arg.method_8652(arg2, this.method_9564().method_11657(field_10762, Integer.valueOf(5)), 2);
		arg.method_20290(1034, arg2, 0);
	}

	private static boolean method_9746(class_1941 arg, class_2338 arg2, @Nullable class_2350 arg3) {
		for (class_2350 lv : class_2350.class_2353.field_11062) {
			if (lv != arg3 && !arg.method_8623(arg2.method_10093(lv))) {
				return false;
			}
		}

		return true;
	}

	@Override
	public class_2680 method_9559(class_2680 arg, class_2350 arg2, class_2680 arg3, class_1936 arg4, class_2338 arg5, class_2338 arg6) {
		if (arg2 != class_2350.field_11036 && !arg.method_11591(arg4, arg5)) {
			arg4.method_8397().method_8676(arg5, this, 1);
		}

		return super.method_9559(arg, arg2, arg3, arg4, arg5, arg6);
	}

	@Override
	public boolean method_9558(class_2680 arg, class_1941 arg2, class_2338 arg3) {
		class_2680 lv = arg2.method_8320(arg3.method_10074());
		class_2248 lv2 = lv.method_11614();
		if (lv2 != this.field_10763 && lv2 != class_2246.field_10471) {
			if (!lv.method_11588()) {
				return false;
			} else {
				boolean bl = false;

				for (class_2350 lv3 : class_2350.class_2353.field_11062) {
					class_2680 lv4 = arg2.method_8320(arg3.method_10093(lv3));
					if (lv4.method_11614() == this.field_10763) {
						if (bl) {
							return false;
						}

						bl = true;
					} else if (!lv4.method_11588()) {
						return false;
					}
				}

				return bl;
			}
		} else {
			return true;
		}
	}

	@Override
	public class_1921 method_9551() {
		return class_1921.field_9174;
	}

	@Override
	protected void method_9515(class_2689.class_2690<class_2248, class_2680> arg) {
		arg.method_11667(field_10762);
	}

	public static void method_9744(class_1936 arg, class_2338 arg2, Random random, int i) {
		arg.method_8652(arg2, ((class_2283)class_2246.field_10021).method_9759(arg, arg2), 2);
		method_9748(arg, arg2, random, arg2, i, 0);
	}

	private static void method_9748(class_1936 arg, class_2338 arg2, Random random, class_2338 arg3, int i, int j) {
		class_2283 lv = (class_2283)class_2246.field_10021;
		int k = random.nextInt(4) + 1;
		if (j == 0) {
			k++;
		}

		for (int l = 0; l < k; l++) {
			class_2338 lv2 = arg2.method_10086(l + 1);
			if (!method_9746(arg, lv2, null)) {
				return;
			}

			arg.method_8652(lv2, lv.method_9759(arg, lv2), 2);
			arg.method_8652(lv2.method_10074(), lv.method_9759(arg, lv2.method_10074()), 2);
		}

		boolean bl = false;
		if (j < 4) {
			int m = random.nextInt(4);
			if (j == 0) {
				m++;
			}

			for (int n = 0; n < m; n++) {
				class_2350 lv3 = class_2350.class_2353.field_11062.method_10183(random);
				class_2338 lv4 = arg2.method_10086(k).method_10093(lv3);
				if (Math.abs(lv4.method_10263() - arg3.method_10263()) < i
					&& Math.abs(lv4.method_10260() - arg3.method_10260()) < i
					&& arg.method_8623(lv4)
					&& arg.method_8623(lv4.method_10074())
					&& method_9746(arg, lv4, lv3.method_10153())) {
					bl = true;
					arg.method_8652(lv4, lv.method_9759(arg, lv4), 2);
					arg.method_8652(lv4.method_10093(lv3.method_10153()), lv.method_9759(arg, lv4.method_10093(lv3.method_10153())), 2);
					method_9748(arg, lv4, random, arg3, i, j + 1);
				}
			}
		}

		if (!bl) {
			arg.method_8652(arg2.method_10086(k), class_2246.field_10528.method_9564().method_11657(field_10762, Integer.valueOf(5)), 2);
		}
	}

	@Override
	public void method_19286(class_1937 arg, class_2680 arg2, class_3965 arg3, class_1297 arg4) {
		class_2338 lv = arg3.method_17777();
		method_9577(arg, lv, new class_1799(this));
		arg.method_8651(lv, true);
	}
}
