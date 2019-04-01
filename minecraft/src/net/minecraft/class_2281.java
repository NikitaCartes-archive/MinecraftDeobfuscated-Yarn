package net.minecraft;

import java.util.List;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

public class class_2281 extends class_2237 implements class_3737 {
	public static final class_2753 field_10768 = class_2383.field_11177;
	public static final class_2754<class_2745> field_10770 = class_2741.field_12506;
	public static final class_2746 field_10772 = class_2741.field_12508;
	protected static final class_265 field_10767 = class_2248.method_9541(1.0, 0.0, 0.0, 15.0, 14.0, 15.0);
	protected static final class_265 field_10771 = class_2248.method_9541(1.0, 0.0, 1.0, 15.0, 14.0, 16.0);
	protected static final class_265 field_10773 = class_2248.method_9541(0.0, 0.0, 1.0, 15.0, 14.0, 15.0);
	protected static final class_265 field_10769 = class_2248.method_9541(1.0, 0.0, 1.0, 16.0, 14.0, 15.0);
	protected static final class_265 field_10774 = class_2248.method_9541(1.0, 0.0, 1.0, 15.0, 14.0, 15.0);
	private static final class_2281.class_3923<class_1263> field_17356 = new class_2281.class_3923<class_1263>() {
		public class_1263 method_17461(class_2595 arg, class_2595 arg2) {
			return new class_1258(arg, arg2);
		}

		public class_1263 method_17460(class_2595 arg) {
			return arg;
		}
	};
	private static final class_2281.class_3923<class_3908> field_17357 = new class_2281.class_3923<class_3908>() {
		public class_3908 method_17463(class_2595 arg, class_2595 arg2) {
			final class_1263 lv = new class_1258(arg, arg2);
			return new class_3908() {
				@Nullable
				@Override
				public class_1703 createMenu(int i, class_1661 arg, class_1657 arg2) {
					if (arg.method_17489(arg2) && arg2.method_17489(arg2)) {
						arg.method_11289(arg.field_7546);
						arg2.method_11289(arg.field_7546);
						return class_1707.method_19247(i, arg, lv);
					} else {
						return null;
					}
				}

				@Override
				public class_2561 method_5476() {
					return new class_2588("container.chestDouble");
				}
			};
		}

		public class_3908 method_17462(class_2595 arg) {
			return arg;
		}
	};

	protected class_2281(class_2248.class_2251 arg) {
		super(arg);
		this.method_9590(
			this.field_10647
				.method_11664()
				.method_11657(field_10768, class_2350.field_11043)
				.method_11657(field_10770, class_2745.field_12569)
				.method_11657(field_10772, Boolean.valueOf(false))
		);
	}

	@Environment(EnvType.CLIENT)
	@Override
	public boolean method_9589(class_2680 arg) {
		return true;
	}

	@Override
	public class_2464 method_9604(class_2680 arg) {
		return class_2464.field_11456;
	}

	@Override
	public class_2680 method_9559(class_2680 arg, class_2350 arg2, class_2680 arg3, class_1936 arg4, class_2338 arg5, class_2338 arg6) {
		if ((Boolean)arg.method_11654(field_10772)) {
			arg4.method_8405().method_8676(arg5, class_3612.field_15910, class_3612.field_15910.method_15789(arg4));
		}

		if (arg3.method_11614() == this && arg2.method_10166().method_10179()) {
			class_2745 lv = arg3.method_11654(field_10770);
			if (arg.method_11654(field_10770) == class_2745.field_12569
				&& lv != class_2745.field_12569
				&& arg.method_11654(field_10768) == arg3.method_11654(field_10768)
				&& method_9758(arg3) == arg2.method_10153()) {
				return arg.method_11657(field_10770, lv.method_11824());
			}
		} else if (method_9758(arg) == arg2) {
			return arg.method_11657(field_10770, class_2745.field_12569);
		}

		return super.method_9559(arg, arg2, arg3, arg4, arg5, arg6);
	}

	@Override
	public class_265 method_9530(class_2680 arg, class_1922 arg2, class_2338 arg3, class_3726 arg4) {
		if (arg.method_11654(field_10770) == class_2745.field_12569) {
			return field_10774;
		} else {
			switch (method_9758(arg)) {
				case field_11043:
				default:
					return field_10767;
				case field_11035:
					return field_10771;
				case field_11039:
					return field_10773;
				case field_11034:
					return field_10769;
			}
		}
	}

	public static class_2350 method_9758(class_2680 arg) {
		class_2350 lv = arg.method_11654(field_10768);
		return arg.method_11654(field_10770) == class_2745.field_12574 ? lv.method_10170() : lv.method_10160();
	}

	@Override
	public class_2680 method_9605(class_1750 arg) {
		class_2745 lv = class_2745.field_12569;
		class_2350 lv2 = arg.method_8042().method_10153();
		class_3610 lv3 = arg.method_8045().method_8316(arg.method_8037());
		boolean bl = arg.method_8046();
		class_2350 lv4 = arg.method_8038();
		if (lv4.method_10166().method_10179() && bl) {
			class_2350 lv5 = this.method_9753(arg, lv4.method_10153());
			if (lv5 != null && lv5.method_10166() != lv4.method_10166()) {
				lv2 = lv5;
				lv = lv5.method_10160() == lv4.method_10153() ? class_2745.field_12571 : class_2745.field_12574;
			}
		}

		if (lv == class_2745.field_12569 && !bl) {
			if (lv2 == this.method_9753(arg, lv2.method_10170())) {
				lv = class_2745.field_12574;
			} else if (lv2 == this.method_9753(arg, lv2.method_10160())) {
				lv = class_2745.field_12571;
			}
		}

		return this.method_9564()
			.method_11657(field_10768, lv2)
			.method_11657(field_10770, lv)
			.method_11657(field_10772, Boolean.valueOf(lv3.method_15772() == class_3612.field_15910));
	}

	@Override
	public class_3610 method_9545(class_2680 arg) {
		return arg.method_11654(field_10772) ? class_3612.field_15910.method_15729(false) : super.method_9545(arg);
	}

	@Nullable
	private class_2350 method_9753(class_1750 arg, class_2350 arg2) {
		class_2680 lv = arg.method_8045().method_8320(arg.method_8037().method_10093(arg2));
		return lv.method_11614() == this && lv.method_11654(field_10770) == class_2745.field_12569 ? lv.method_11654(field_10768) : null;
	}

	@Override
	public void method_9567(class_1937 arg, class_2338 arg2, class_2680 arg3, class_1309 arg4, class_1799 arg5) {
		if (arg5.method_7938()) {
			class_2586 lv = arg.method_8321(arg2);
			if (lv instanceof class_2595) {
				((class_2595)lv).method_17488(arg5.method_7964());
			}
		}
	}

	@Override
	public void method_9536(class_2680 arg, class_1937 arg2, class_2338 arg3, class_2680 arg4, boolean bl) {
		if (arg.method_11614() != arg4.method_11614()) {
			class_2586 lv = arg2.method_8321(arg3);
			if (lv instanceof class_1263) {
				class_1264.method_5451(arg2, arg3, (class_1263)lv);
				arg2.method_8455(arg3, this);
			}

			super.method_9536(arg, arg2, arg3, arg4, bl);
		}
	}

	@Override
	public boolean method_9534(class_2680 arg, class_1937 arg2, class_2338 arg3, class_1657 arg4, class_1268 arg5, class_3965 arg6) {
		if (arg2.field_9236) {
			return true;
		} else {
			class_3908 lv = this.method_17454(arg, arg2, arg3);
			if (lv != null) {
				arg4.method_17355(lv);
				arg4.method_7259(this.method_9755());
			}

			return true;
		}
	}

	protected class_3445<class_2960> method_9755() {
		return class_3468.field_15419.method_14956(class_3468.field_15395);
	}

	@Nullable
	public static <T> T method_17459(class_2680 arg, class_1936 arg2, class_2338 arg3, boolean bl, class_2281.class_3923<T> arg4) {
		class_2586 lv = arg2.method_8321(arg3);
		if (!(lv instanceof class_2595)) {
			return null;
		} else if (!bl && method_9756(arg2, arg3)) {
			return null;
		} else {
			class_2595 lv2 = (class_2595)lv;
			class_2745 lv3 = arg.method_11654(field_10770);
			if (lv3 == class_2745.field_12569) {
				return arg4.method_17464(lv2);
			} else {
				class_2338 lv4 = arg3.method_10093(method_9758(arg));
				class_2680 lv5 = arg2.method_8320(lv4);
				if (lv5.method_11614() == arg.method_11614()) {
					class_2745 lv6 = lv5.method_11654(field_10770);
					if (lv6 != class_2745.field_12569 && lv3 != lv6 && lv5.method_11654(field_10768) == arg.method_11654(field_10768)) {
						if (!bl && method_9756(arg2, lv4)) {
							return null;
						}

						class_2586 lv7 = arg2.method_8321(lv4);
						if (lv7 instanceof class_2595) {
							class_2595 lv8 = lv3 == class_2745.field_12571 ? lv2 : (class_2595)lv7;
							class_2595 lv9 = lv3 == class_2745.field_12571 ? (class_2595)lv7 : lv2;
							return arg4.method_17465(lv8, lv9);
						}
					}
				}

				return arg4.method_17464(lv2);
			}
		}
	}

	@Nullable
	public static class_1263 method_17458(class_2680 arg, class_1937 arg2, class_2338 arg3, boolean bl) {
		return method_17459(arg, arg2, arg3, bl, field_17356);
	}

	@Nullable
	@Override
	public class_3908 method_17454(class_2680 arg, class_1937 arg2, class_2338 arg3) {
		return method_17459(arg, arg2, arg3, false, field_17357);
	}

	@Override
	public class_2586 method_10123(class_1922 arg) {
		return new class_2595();
	}

	private static boolean method_9756(class_1936 arg, class_2338 arg2) {
		return method_9757(arg, arg2) || method_9754(arg, arg2);
	}

	private static boolean method_9757(class_1922 arg, class_2338 arg2) {
		class_2338 lv = arg2.method_10084();
		return arg.method_8320(lv).method_11621(arg, lv);
	}

	private static boolean method_9754(class_1936 arg, class_2338 arg2) {
		List<class_1451> list = arg.method_18467(
			class_1451.class,
			new class_238(
				(double)arg2.method_10263(),
				(double)(arg2.method_10264() + 1),
				(double)arg2.method_10260(),
				(double)(arg2.method_10263() + 1),
				(double)(arg2.method_10264() + 2),
				(double)(arg2.method_10260() + 1)
			)
		);
		if (!list.isEmpty()) {
			for (class_1451 lv : list) {
				if (lv.method_6172()) {
					return true;
				}
			}
		}

		return false;
	}

	@Override
	public boolean method_9498(class_2680 arg) {
		return true;
	}

	@Override
	public int method_9572(class_2680 arg, class_1937 arg2, class_2338 arg3) {
		return class_1703.method_7618(method_17458(arg, arg2, arg3, false));
	}

	@Override
	public class_2680 method_9598(class_2680 arg, class_2470 arg2) {
		return arg.method_11657(field_10768, arg2.method_10503(arg.method_11654(field_10768)));
	}

	@Override
	public class_2680 method_9569(class_2680 arg, class_2415 arg2) {
		return arg.method_11626(arg2.method_10345(arg.method_11654(field_10768)));
	}

	@Override
	protected void method_9515(class_2689.class_2690<class_2248, class_2680> arg) {
		arg.method_11667(field_10768, field_10770, field_10772);
	}

	@Override
	public boolean method_9516(class_2680 arg, class_1922 arg2, class_2338 arg3, class_10 arg4) {
		return false;
	}

	interface class_3923<T> {
		T method_17465(class_2595 arg, class_2595 arg2);

		T method_17464(class_2595 arg);
	}
}
