package net.minecraft;

import java.util.Random;
import java.util.stream.IntStream;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

public class class_2510 extends class_2248 implements class_3737 {
	public static final class_2753 field_11571 = class_2383.field_11177;
	public static final class_2754<class_2760> field_11572 = class_2741.field_12518;
	public static final class_2754<class_2778> field_11565 = class_2741.field_12503;
	public static final class_2746 field_11573 = class_2741.field_12508;
	protected static final class_265 field_11562 = class_2482.field_11499;
	protected static final class_265 field_11576 = class_2482.field_11500;
	protected static final class_265 field_11561 = class_2248.method_9541(0.0, 0.0, 0.0, 8.0, 8.0, 8.0);
	protected static final class_265 field_11578 = class_2248.method_9541(0.0, 0.0, 8.0, 8.0, 8.0, 16.0);
	protected static final class_265 field_11568 = class_2248.method_9541(0.0, 8.0, 0.0, 8.0, 16.0, 8.0);
	protected static final class_265 field_11563 = class_2248.method_9541(0.0, 8.0, 8.0, 8.0, 16.0, 16.0);
	protected static final class_265 field_11575 = class_2248.method_9541(8.0, 0.0, 0.0, 16.0, 8.0, 8.0);
	protected static final class_265 field_11569 = class_2248.method_9541(8.0, 0.0, 8.0, 16.0, 8.0, 16.0);
	protected static final class_265 field_11577 = class_2248.method_9541(8.0, 8.0, 0.0, 16.0, 16.0, 8.0);
	protected static final class_265 field_11567 = class_2248.method_9541(8.0, 8.0, 8.0, 16.0, 16.0, 16.0);
	protected static final class_265[] field_11566 = method_10672(field_11562, field_11561, field_11575, field_11578, field_11569);
	protected static final class_265[] field_11564 = method_10672(field_11576, field_11568, field_11577, field_11563, field_11567);
	private static final int[] field_11570 = new int[]{12, 5, 3, 10, 14, 13, 7, 11, 13, 7, 11, 14, 8, 4, 1, 2, 4, 1, 2, 8};
	private final class_2248 field_11579;
	private final class_2680 field_11574;

	private static class_265[] method_10672(class_265 arg, class_265 arg2, class_265 arg3, class_265 arg4, class_265 arg5) {
		return (class_265[])IntStream.range(0, 16).mapToObj(i -> method_10671(i, arg, arg2, arg3, arg4, arg5)).toArray(class_265[]::new);
	}

	private static class_265 method_10671(int i, class_265 arg, class_265 arg2, class_265 arg3, class_265 arg4, class_265 arg5) {
		class_265 lv = arg;
		if ((i & 1) != 0) {
			lv = class_259.method_1084(arg, arg2);
		}

		if ((i & 2) != 0) {
			lv = class_259.method_1084(lv, arg3);
		}

		if ((i & 4) != 0) {
			lv = class_259.method_1084(lv, arg4);
		}

		if ((i & 8) != 0) {
			lv = class_259.method_1084(lv, arg5);
		}

		return lv;
	}

	protected class_2510(class_2680 arg, class_2248.class_2251 arg2) {
		super(arg2);
		this.method_9590(
			this.field_10647
				.method_11664()
				.method_11657(field_11571, class_2350.field_11043)
				.method_11657(field_11572, class_2760.field_12617)
				.method_11657(field_11565, class_2778.field_12710)
				.method_11657(field_11573, Boolean.valueOf(false))
		);
		this.field_11579 = arg.method_11614();
		this.field_11574 = arg;
	}

	@Override
	public boolean method_9526(class_2680 arg) {
		return true;
	}

	@Override
	public class_265 method_9530(class_2680 arg, class_1922 arg2, class_2338 arg3, class_3726 arg4) {
		return (arg.method_11654(field_11572) == class_2760.field_12619 ? field_11566 : field_11564)[field_11570[this.method_10673(arg)]];
	}

	private int method_10673(class_2680 arg) {
		return ((class_2778)arg.method_11654(field_11565)).ordinal() * 4 + ((class_2350)arg.method_11654(field_11571)).method_10161();
	}

	@Environment(EnvType.CLIENT)
	@Override
	public void method_9496(class_2680 arg, class_1937 arg2, class_2338 arg3, Random random) {
		this.field_11579.method_9496(arg, arg2, arg3, random);
	}

	@Override
	public void method_9606(class_2680 arg, class_1937 arg2, class_2338 arg3, class_1657 arg4) {
		this.field_11574.method_11636(arg2, arg3, arg4);
	}

	@Override
	public void method_9585(class_1936 arg, class_2338 arg2, class_2680 arg3) {
		this.field_11579.method_9585(arg, arg2, arg3);
	}

	@Override
	public float method_9520() {
		return this.field_11579.method_9520();
	}

	@Override
	public class_1921 method_9551() {
		return this.field_11579.method_9551();
	}

	@Override
	public int method_9563(class_1941 arg) {
		return this.field_11579.method_9563(arg);
	}

	@Override
	public void method_9615(class_2680 arg, class_1937 arg2, class_2338 arg3, class_2680 arg4, boolean bl) {
		if (arg.method_11614() != arg.method_11614()) {
			this.field_11574.method_11622(arg2, arg3, class_2246.field_10124, arg3, false);
			this.field_11579.method_9615(this.field_11574, arg2, arg3, arg4, false);
		}
	}

	@Override
	public void method_9536(class_2680 arg, class_1937 arg2, class_2338 arg3, class_2680 arg4, boolean bl) {
		if (arg.method_11614() != arg4.method_11614()) {
			this.field_11574.method_11600(arg2, arg3, arg4, bl);
		}
	}

	@Override
	public void method_9591(class_1937 arg, class_2338 arg2, class_1297 arg3) {
		this.field_11579.method_9591(arg, arg2, arg3);
	}

	@Override
	public void method_9588(class_2680 arg, class_1937 arg2, class_2338 arg3, Random random) {
		this.field_11579.method_9588(arg, arg2, arg3, random);
	}

	@Override
	public boolean method_9534(class_2680 arg, class_1937 arg2, class_2338 arg3, class_1657 arg4, class_1268 arg5, class_3965 arg6) {
		return this.field_11574.method_11629(arg2, arg4, arg5, arg6);
	}

	@Override
	public void method_9586(class_1937 arg, class_2338 arg2, class_1927 arg3) {
		this.field_11579.method_9586(arg, arg2, arg3);
	}

	@Override
	public class_2680 method_9605(class_1750 arg) {
		class_2350 lv = arg.method_8038();
		class_2338 lv2 = arg.method_8037();
		class_3610 lv3 = arg.method_8045().method_8316(lv2);
		class_2680 lv4 = this.method_9564()
			.method_11657(field_11571, arg.method_8042())
			.method_11657(
				field_11572,
				lv != class_2350.field_11033 && (lv == class_2350.field_11036 || !(arg.method_17698().field_1351 - (double)lv2.method_10264() > 0.5))
					? class_2760.field_12617
					: class_2760.field_12619
			)
			.method_11657(field_11573, Boolean.valueOf(lv3.method_15772() == class_3612.field_15910));
		return lv4.method_11657(field_11565, method_10675(lv4, arg.method_8045(), lv2));
	}

	@Override
	public class_2680 method_9559(class_2680 arg, class_2350 arg2, class_2680 arg3, class_1936 arg4, class_2338 arg5, class_2338 arg6) {
		if ((Boolean)arg.method_11654(field_11573)) {
			arg4.method_8405().method_8676(arg5, class_3612.field_15910, class_3612.field_15910.method_15789(arg4));
		}

		return arg2.method_10166().method_10179()
			? arg.method_11657(field_11565, method_10675(arg, arg4, arg5))
			: super.method_9559(arg, arg2, arg3, arg4, arg5, arg6);
	}

	private static class_2778 method_10675(class_2680 arg, class_1922 arg2, class_2338 arg3) {
		class_2350 lv = arg.method_11654(field_11571);
		class_2680 lv2 = arg2.method_8320(arg3.method_10093(lv));
		if (method_10676(lv2) && arg.method_11654(field_11572) == lv2.method_11654(field_11572)) {
			class_2350 lv3 = lv2.method_11654(field_11571);
			if (lv3.method_10166() != ((class_2350)arg.method_11654(field_11571)).method_10166() && method_10678(arg, arg2, arg3, lv3.method_10153())) {
				if (lv3 == lv.method_10160()) {
					return class_2778.field_12708;
				}

				return class_2778.field_12709;
			}
		}

		class_2680 lv4 = arg2.method_8320(arg3.method_10093(lv.method_10153()));
		if (method_10676(lv4) && arg.method_11654(field_11572) == lv4.method_11654(field_11572)) {
			class_2350 lv5 = lv4.method_11654(field_11571);
			if (lv5.method_10166() != ((class_2350)arg.method_11654(field_11571)).method_10166() && method_10678(arg, arg2, arg3, lv5)) {
				if (lv5 == lv.method_10160()) {
					return class_2778.field_12712;
				}

				return class_2778.field_12713;
			}
		}

		return class_2778.field_12710;
	}

	private static boolean method_10678(class_2680 arg, class_1922 arg2, class_2338 arg3, class_2350 arg4) {
		class_2680 lv = arg2.method_8320(arg3.method_10093(arg4));
		return !method_10676(lv) || lv.method_11654(field_11571) != arg.method_11654(field_11571) || lv.method_11654(field_11572) != arg.method_11654(field_11572);
	}

	public static boolean method_10676(class_2680 arg) {
		return arg.method_11614() instanceof class_2510;
	}

	@Override
	public class_2680 method_9598(class_2680 arg, class_2470 arg2) {
		return arg.method_11657(field_11571, arg2.method_10503(arg.method_11654(field_11571)));
	}

	@Override
	public class_2680 method_9569(class_2680 arg, class_2415 arg2) {
		class_2350 lv = arg.method_11654(field_11571);
		class_2778 lv2 = arg.method_11654(field_11565);
		switch (arg2) {
			case field_11300:
				if (lv.method_10166() == class_2350.class_2351.field_11051) {
					switch (lv2) {
						case field_12712:
							return arg.method_11626(class_2470.field_11464).method_11657(field_11565, class_2778.field_12713);
						case field_12713:
							return arg.method_11626(class_2470.field_11464).method_11657(field_11565, class_2778.field_12712);
						case field_12708:
							return arg.method_11626(class_2470.field_11464).method_11657(field_11565, class_2778.field_12709);
						case field_12709:
							return arg.method_11626(class_2470.field_11464).method_11657(field_11565, class_2778.field_12708);
						default:
							return arg.method_11626(class_2470.field_11464);
					}
				}
				break;
			case field_11301:
				if (lv.method_10166() == class_2350.class_2351.field_11048) {
					switch (lv2) {
						case field_12712:
							return arg.method_11626(class_2470.field_11464).method_11657(field_11565, class_2778.field_12712);
						case field_12713:
							return arg.method_11626(class_2470.field_11464).method_11657(field_11565, class_2778.field_12713);
						case field_12708:
							return arg.method_11626(class_2470.field_11464).method_11657(field_11565, class_2778.field_12709);
						case field_12709:
							return arg.method_11626(class_2470.field_11464).method_11657(field_11565, class_2778.field_12708);
						case field_12710:
							return arg.method_11626(class_2470.field_11464);
					}
				}
		}

		return super.method_9569(arg, arg2);
	}

	@Override
	protected void method_9515(class_2689.class_2690<class_2248, class_2680> arg) {
		arg.method_11667(field_11571, field_11572, field_11565, field_11573);
	}

	@Override
	public class_3610 method_9545(class_2680 arg) {
		return arg.method_11654(field_11573) ? class_3612.field_15910.method_15729(false) : super.method_9545(arg);
	}

	@Override
	public boolean method_9516(class_2680 arg, class_1922 arg2, class_2338 arg3, class_10 arg4) {
		return false;
	}
}
