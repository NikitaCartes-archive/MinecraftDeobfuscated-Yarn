package net.minecraft;

import java.util.Map;
import java.util.Random;
import javax.annotation.Nullable;

public class class_2541 extends class_2248 {
	public static final class_2746 field_11703 = class_2429.field_11327;
	public static final class_2746 field_11706 = class_2429.field_11332;
	public static final class_2746 field_11702 = class_2429.field_11335;
	public static final class_2746 field_11699 = class_2429.field_11331;
	public static final class_2746 field_11696 = class_2429.field_11328;
	public static final Map<class_2350, class_2746> field_11697 = (Map<class_2350, class_2746>)class_2429.field_11329
		.entrySet()
		.stream()
		.filter(entry -> entry.getKey() != class_2350.field_11033)
		.collect(class_156.method_664());
	protected static final class_265 field_11698 = class_2248.method_9541(0.0, 15.0, 0.0, 16.0, 16.0, 16.0);
	protected static final class_265 field_11705 = class_2248.method_9541(0.0, 0.0, 0.0, 1.0, 16.0, 16.0);
	protected static final class_265 field_11704 = class_2248.method_9541(15.0, 0.0, 0.0, 16.0, 16.0, 16.0);
	protected static final class_265 field_11700 = class_2248.method_9541(0.0, 0.0, 0.0, 16.0, 16.0, 1.0);
	protected static final class_265 field_11701 = class_2248.method_9541(0.0, 0.0, 15.0, 16.0, 16.0, 16.0);

	public class_2541(class_2248.class_2251 arg) {
		super(arg);
		this.method_9590(
			this.field_10647
				.method_11664()
				.method_11657(field_11703, Boolean.valueOf(false))
				.method_11657(field_11706, Boolean.valueOf(false))
				.method_11657(field_11702, Boolean.valueOf(false))
				.method_11657(field_11699, Boolean.valueOf(false))
				.method_11657(field_11696, Boolean.valueOf(false))
		);
	}

	@Override
	public class_265 method_9529(class_2680 arg, class_1922 arg2, class_2338 arg3) {
		class_265 lv = class_259.method_1073();
		if ((Boolean)arg.method_11654(field_11703)) {
			lv = class_259.method_1084(lv, field_11698);
		}

		if ((Boolean)arg.method_11654(field_11706)) {
			lv = class_259.method_1084(lv, field_11700);
		}

		if ((Boolean)arg.method_11654(field_11702)) {
			lv = class_259.method_1084(lv, field_11704);
		}

		if ((Boolean)arg.method_11654(field_11699)) {
			lv = class_259.method_1084(lv, field_11701);
		}

		if ((Boolean)arg.method_11654(field_11696)) {
			lv = class_259.method_1084(lv, field_11705);
		}

		return lv;
	}

	@Override
	public boolean method_9558(class_2680 arg, class_1941 arg2, class_2338 arg3) {
		return this.method_10823(this.method_10827(arg, arg2, arg3));
	}

	private boolean method_10823(class_2680 arg) {
		return this.method_10822(arg) > 0;
	}

	private int method_10822(class_2680 arg) {
		int i = 0;

		for (class_2746 lv : field_11697.values()) {
			if ((Boolean)arg.method_11654(lv)) {
				i++;
			}
		}

		return i;
	}

	private boolean method_10829(class_1922 arg, class_2338 arg2, class_2350 arg3) {
		if (arg3 == class_2350.field_11033) {
			return false;
		} else {
			class_2338 lv = arg2.method_10093(arg3);
			if (method_10821(arg, lv, arg3)) {
				return true;
			} else if (arg3.method_10166() == class_2350.class_2351.field_11052) {
				return false;
			} else {
				class_2746 lv2 = (class_2746)field_11697.get(arg3);
				class_2680 lv3 = arg.method_8320(arg2.method_10084());
				return lv3.method_11614() == this && (Boolean)lv3.method_11654(lv2);
			}
		}
	}

	public static boolean method_10821(class_1922 arg, class_2338 arg2, class_2350 arg3) {
		class_2680 lv = arg.method_8320(arg2);
		return class_2248.method_9501(lv.method_11628(arg, arg2), arg3.method_10153()) && !method_10825(lv.method_11614());
	}

	protected static boolean method_10825(class_2248 arg) {
		return arg instanceof class_2480
			|| arg instanceof class_2506
			|| arg == class_2246.field_10327
			|| arg == class_2246.field_10593
			|| arg == class_2246.field_10033
			|| arg == class_2246.field_10560
			|| arg == class_2246.field_10615
			|| arg == class_2246.field_10379
			|| arg.method_9525(class_3481.field_15491);
	}

	private class_2680 method_10827(class_2680 arg, class_1922 arg2, class_2338 arg3) {
		class_2338 lv = arg3.method_10084();
		if ((Boolean)arg.method_11654(field_11703)) {
			arg = arg.method_11657(field_11703, Boolean.valueOf(method_10821(arg2, lv, class_2350.field_11033)));
		}

		class_2680 lv2 = null;

		for (class_2350 lv3 : class_2350.class_2353.field_11062) {
			class_2746 lv4 = method_10828(lv3);
			if ((Boolean)arg.method_11654(lv4)) {
				boolean bl = this.method_10829(arg2, arg3, lv3);
				if (!bl) {
					if (lv2 == null) {
						lv2 = arg2.method_8320(lv);
					}

					bl = lv2.method_11614() == this && (Boolean)lv2.method_11654(lv4);
				}

				arg = arg.method_11657(lv4, Boolean.valueOf(bl));
			}
		}

		return arg;
	}

	@Override
	public class_2680 method_9559(class_2680 arg, class_2350 arg2, class_2680 arg3, class_1936 arg4, class_2338 arg5, class_2338 arg6) {
		if (arg2 == class_2350.field_11033) {
			return super.method_9559(arg, arg2, arg3, arg4, arg5, arg6);
		} else {
			class_2680 lv = this.method_10827(arg, arg4, arg5);
			return !this.method_10823(lv) ? class_2246.field_10124.method_9564() : lv;
		}
	}

	@Override
	public void method_9588(class_2680 arg, class_1937 arg2, class_2338 arg3, Random random) {
		if (!arg2.field_9236) {
			class_2680 lv = this.method_10827(arg, arg2, arg3);
			if (lv != arg) {
				if (this.method_10823(lv)) {
					arg2.method_8652(arg3, lv, 2);
				} else {
					method_9497(arg, arg2, arg3);
					arg2.method_8650(arg3);
				}
			} else if (arg2.field_9229.nextInt(4) == 0) {
				class_2350 lv2 = class_2350.method_10162(random);
				class_2338 lv3 = arg3.method_10084();
				if (lv2.method_10166().method_10179() && !(Boolean)arg.method_11654(method_10828(lv2))) {
					if (this.method_10824(arg2, arg3)) {
						class_2338 lv4 = arg3.method_10093(lv2);
						class_2680 lv5 = arg2.method_8320(lv4);
						if (lv5.method_11588()) {
							class_2350 lv6 = lv2.method_10170();
							class_2350 lv7 = lv2.method_10160();
							boolean bl = (Boolean)arg.method_11654(method_10828(lv6));
							boolean bl2 = (Boolean)arg.method_11654(method_10828(lv7));
							class_2338 lv8 = lv4.method_10093(lv6);
							class_2338 lv9 = lv4.method_10093(lv7);
							if (bl && method_10821(arg2, lv8, lv6)) {
								arg2.method_8652(lv4, this.method_9564().method_11657(method_10828(lv6), Boolean.valueOf(true)), 2);
							} else if (bl2 && method_10821(arg2, lv9, lv7)) {
								arg2.method_8652(lv4, this.method_9564().method_11657(method_10828(lv7), Boolean.valueOf(true)), 2);
							} else {
								class_2350 lv10 = lv2.method_10153();
								if (bl && arg2.method_8623(lv8) && method_10821(arg2, arg3.method_10093(lv6), lv10)) {
									arg2.method_8652(lv8, this.method_9564().method_11657(method_10828(lv10), Boolean.valueOf(true)), 2);
								} else if (bl2 && arg2.method_8623(lv9) && method_10821(arg2, arg3.method_10093(lv7), lv10)) {
									arg2.method_8652(lv9, this.method_9564().method_11657(method_10828(lv10), Boolean.valueOf(true)), 2);
								} else if ((double)arg2.field_9229.nextFloat() < 0.05 && method_10821(arg2, lv4.method_10084(), class_2350.field_11036)) {
									arg2.method_8652(lv4, this.method_9564().method_11657(field_11703, Boolean.valueOf(true)), 2);
								}
							}
						} else if (method_10821(arg2, lv4, lv2)) {
							arg2.method_8652(arg3, arg.method_11657(method_10828(lv2), Boolean.valueOf(true)), 2);
						}
					}
				} else {
					if (lv2 == class_2350.field_11036 && arg3.method_10264() < 255) {
						if (this.method_10829(arg2, arg3, lv2)) {
							arg2.method_8652(arg3, arg.method_11657(field_11703, Boolean.valueOf(true)), 2);
							return;
						}

						if (arg2.method_8623(lv3)) {
							if (!this.method_10824(arg2, arg3)) {
								return;
							}

							class_2680 lv11 = arg;

							for (class_2350 lv6 : class_2350.class_2353.field_11062) {
								if (random.nextBoolean() || !method_10821(arg2, lv3.method_10093(lv6), class_2350.field_11036)) {
									lv11 = lv11.method_11657(method_10828(lv6), Boolean.valueOf(false));
								}
							}

							if (this.method_10830(lv11)) {
								arg2.method_8652(lv3, lv11, 2);
							}

							return;
						}
					}

					if (arg3.method_10264() > 0) {
						class_2338 lv4 = arg3.method_10074();
						class_2680 lv5 = arg2.method_8320(lv4);
						if (lv5.method_11588() || lv5.method_11614() == this) {
							class_2680 lv12 = lv5.method_11588() ? this.method_9564() : lv5;
							class_2680 lv13 = this.method_10820(arg, lv12, random);
							if (lv12 != lv13 && this.method_10830(lv13)) {
								arg2.method_8652(lv4, lv13, 2);
							}
						}
					}
				}
			}
		}
	}

	private class_2680 method_10820(class_2680 arg, class_2680 arg2, Random random) {
		for (class_2350 lv : class_2350.class_2353.field_11062) {
			if (random.nextBoolean()) {
				class_2746 lv2 = method_10828(lv);
				if ((Boolean)arg.method_11654(lv2)) {
					arg2 = arg2.method_11657(lv2, Boolean.valueOf(true));
				}
			}
		}

		return arg2;
	}

	private boolean method_10830(class_2680 arg) {
		return (Boolean)arg.method_11654(field_11706)
			|| (Boolean)arg.method_11654(field_11702)
			|| (Boolean)arg.method_11654(field_11699)
			|| (Boolean)arg.method_11654(field_11696);
	}

	private boolean method_10824(class_1922 arg, class_2338 arg2) {
		int i = 4;
		Iterable<class_2338.class_2339> iterable = class_2338.class_2339.method_10068(
			arg2.method_10263() - 4, arg2.method_10264() - 1, arg2.method_10260() - 4, arg2.method_10263() + 4, arg2.method_10264() + 1, arg2.method_10260() + 4
		);
		int j = 5;

		for (class_2338 lv : iterable) {
			if (arg.method_8320(lv).method_11614() == this) {
				if (--j <= 0) {
					return false;
				}
			}
		}

		return true;
	}

	@Override
	public boolean method_9616(class_2680 arg, class_1750 arg2) {
		class_2680 lv = arg2.method_8045().method_8320(arg2.method_8037());
		return lv.method_11614() == this ? this.method_10822(lv) < field_11697.size() : super.method_9616(arg, arg2);
	}

	@Nullable
	@Override
	public class_2680 method_9605(class_1750 arg) {
		class_2680 lv = arg.method_8045().method_8320(arg.method_8037());
		boolean bl = lv.method_11614() == this;
		class_2680 lv2 = bl ? lv : this.method_9564();

		for (class_2350 lv3 : arg.method_7718()) {
			if (lv3 != class_2350.field_11033) {
				class_2746 lv4 = method_10828(lv3);
				boolean bl2 = bl && (Boolean)lv.method_11654(lv4);
				if (!bl2 && this.method_10829(arg.method_8045(), arg.method_8037(), lv3)) {
					return lv2.method_11657(lv4, Boolean.valueOf(true));
				}
			}
		}

		return bl ? lv2 : null;
	}

	@Override
	public class_1921 method_9551() {
		return class_1921.field_9174;
	}

	@Override
	protected void method_9515(class_2689.class_2690<class_2248, class_2680> arg) {
		arg.method_11667(field_11703, field_11706, field_11702, field_11699, field_11696);
	}

	@Override
	public class_2680 method_9598(class_2680 arg, class_2470 arg2) {
		switch (arg2) {
			case field_11464:
				return arg.method_11657(field_11706, arg.method_11654(field_11699))
					.method_11657(field_11702, arg.method_11654(field_11696))
					.method_11657(field_11699, arg.method_11654(field_11706))
					.method_11657(field_11696, arg.method_11654(field_11702));
			case field_11465:
				return arg.method_11657(field_11706, arg.method_11654(field_11702))
					.method_11657(field_11702, arg.method_11654(field_11699))
					.method_11657(field_11699, arg.method_11654(field_11696))
					.method_11657(field_11696, arg.method_11654(field_11706));
			case field_11463:
				return arg.method_11657(field_11706, arg.method_11654(field_11696))
					.method_11657(field_11702, arg.method_11654(field_11706))
					.method_11657(field_11699, arg.method_11654(field_11702))
					.method_11657(field_11696, arg.method_11654(field_11699));
			default:
				return arg;
		}
	}

	@Override
	public class_2680 method_9569(class_2680 arg, class_2415 arg2) {
		switch (arg2) {
			case field_11300:
				return arg.method_11657(field_11706, arg.method_11654(field_11699)).method_11657(field_11699, arg.method_11654(field_11706));
			case field_11301:
				return arg.method_11657(field_11702, arg.method_11654(field_11696)).method_11657(field_11696, arg.method_11654(field_11702));
			default:
				return super.method_9569(arg, arg2);
		}
	}

	public static class_2746 method_10828(class_2350 arg) {
		return (class_2746)field_11697.get(arg);
	}
}
