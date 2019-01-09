package net.minecraft;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import java.util.EnumSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

public class class_2457 extends class_2248 {
	public static final class_2754<class_2773> field_11440 = class_2741.field_12495;
	public static final class_2754<class_2773> field_11436 = class_2741.field_12523;
	public static final class_2754<class_2773> field_11437 = class_2741.field_12551;
	public static final class_2754<class_2773> field_11439 = class_2741.field_12504;
	public static final class_2758 field_11432 = class_2741.field_12511;
	public static final Map<class_2350, class_2754<class_2773>> field_11435 = Maps.newEnumMap(
		ImmutableMap.of(
			class_2350.field_11043, field_11440, class_2350.field_11034, field_11436, class_2350.field_11035, field_11437, class_2350.field_11039, field_11439
		)
	);
	protected static final class_265[] field_11433 = new class_265[]{
		class_2248.method_9541(3.0, 0.0, 3.0, 13.0, 1.0, 13.0),
		class_2248.method_9541(3.0, 0.0, 3.0, 13.0, 1.0, 16.0),
		class_2248.method_9541(0.0, 0.0, 3.0, 13.0, 1.0, 13.0),
		class_2248.method_9541(0.0, 0.0, 3.0, 13.0, 1.0, 16.0),
		class_2248.method_9541(3.0, 0.0, 0.0, 13.0, 1.0, 13.0),
		class_2248.method_9541(3.0, 0.0, 0.0, 13.0, 1.0, 16.0),
		class_2248.method_9541(0.0, 0.0, 0.0, 13.0, 1.0, 13.0),
		class_2248.method_9541(0.0, 0.0, 0.0, 13.0, 1.0, 16.0),
		class_2248.method_9541(3.0, 0.0, 3.0, 16.0, 1.0, 13.0),
		class_2248.method_9541(3.0, 0.0, 3.0, 16.0, 1.0, 16.0),
		class_2248.method_9541(0.0, 0.0, 3.0, 16.0, 1.0, 13.0),
		class_2248.method_9541(0.0, 0.0, 3.0, 16.0, 1.0, 16.0),
		class_2248.method_9541(3.0, 0.0, 0.0, 16.0, 1.0, 13.0),
		class_2248.method_9541(3.0, 0.0, 0.0, 16.0, 1.0, 16.0),
		class_2248.method_9541(0.0, 0.0, 0.0, 16.0, 1.0, 13.0),
		class_2248.method_9541(0.0, 0.0, 0.0, 16.0, 1.0, 16.0)
	};
	private boolean field_11438 = true;
	private final Set<class_2338> field_11434 = Sets.<class_2338>newHashSet();

	public class_2457(class_2248.class_2251 arg) {
		super(arg);
		this.method_9590(
			this.field_10647
				.method_11664()
				.method_11657(field_11440, class_2773.field_12687)
				.method_11657(field_11436, class_2773.field_12687)
				.method_11657(field_11437, class_2773.field_12687)
				.method_11657(field_11439, class_2773.field_12687)
				.method_11657(field_11432, Integer.valueOf(0))
		);
	}

	@Override
	public class_265 method_9529(class_2680 arg, class_1922 arg2, class_2338 arg3) {
		return field_11433[method_10480(arg)];
	}

	private static int method_10480(class_2680 arg) {
		int i = 0;
		boolean bl = arg.method_11654(field_11440) != class_2773.field_12687;
		boolean bl2 = arg.method_11654(field_11436) != class_2773.field_12687;
		boolean bl3 = arg.method_11654(field_11437) != class_2773.field_12687;
		boolean bl4 = arg.method_11654(field_11439) != class_2773.field_12687;
		if (bl || bl3 && !bl && !bl2 && !bl4) {
			i |= 1 << class_2350.field_11043.method_10161();
		}

		if (bl2 || bl4 && !bl && !bl2 && !bl3) {
			i |= 1 << class_2350.field_11034.method_10161();
		}

		if (bl3 || bl && !bl2 && !bl3 && !bl4) {
			i |= 1 << class_2350.field_11035.method_10161();
		}

		if (bl4 || bl2 && !bl && !bl3 && !bl4) {
			i |= 1 << class_2350.field_11039.method_10161();
		}

		return i;
	}

	@Override
	public class_2680 method_9605(class_1750 arg) {
		class_1922 lv = arg.method_8045();
		class_2338 lv2 = arg.method_8037();
		return this.method_9564()
			.method_11657(field_11439, this.method_10477(lv, lv2, class_2350.field_11039))
			.method_11657(field_11436, this.method_10477(lv, lv2, class_2350.field_11034))
			.method_11657(field_11440, this.method_10477(lv, lv2, class_2350.field_11043))
			.method_11657(field_11437, this.method_10477(lv, lv2, class_2350.field_11035));
	}

	@Override
	public class_2680 method_9559(class_2680 arg, class_2350 arg2, class_2680 arg3, class_1936 arg4, class_2338 arg5, class_2338 arg6) {
		if (arg2 == class_2350.field_11033) {
			return arg;
		} else {
			return arg2 == class_2350.field_11036
				? arg.method_11657(field_11439, this.method_10477(arg4, arg5, class_2350.field_11039))
					.method_11657(field_11436, this.method_10477(arg4, arg5, class_2350.field_11034))
					.method_11657(field_11440, this.method_10477(arg4, arg5, class_2350.field_11043))
					.method_11657(field_11437, this.method_10477(arg4, arg5, class_2350.field_11035))
				: arg.method_11657((class_2769)field_11435.get(arg2), this.method_10477(arg4, arg5, arg2));
		}
	}

	@Override
	public void method_9517(class_2680 arg, class_1936 arg2, class_2338 arg3, int i) {
		try (class_2338.class_2340 lv = class_2338.class_2340.method_10109()) {
			for (class_2350 lv2 : class_2350.class_2353.field_11062) {
				class_2773 lv3 = arg.method_11654((class_2769<class_2773>)field_11435.get(lv2));
				if (lv3 != class_2773.field_12687 && arg2.method_8320(lv.method_10114(arg3).method_10118(lv2)).method_11614() != this) {
					lv.method_10118(class_2350.field_11033);
					class_2680 lv4 = arg2.method_8320(lv);
					if (lv4.method_11614() != class_2246.field_10282) {
						class_2338 lv5 = lv.method_10093(lv2.method_10153());
						class_2680 lv6 = lv4.method_11578(lv2.method_10153(), arg2.method_8320(lv5), arg2, lv, lv5);
						method_9611(lv4, lv6, arg2, lv, i);
					}

					lv.method_10114(arg3).method_10118(lv2).method_10118(class_2350.field_11036);
					class_2680 lv7 = arg2.method_8320(lv);
					if (lv7.method_11614() != class_2246.field_10282) {
						class_2338 lv8 = lv.method_10093(lv2.method_10153());
						class_2680 lv9 = lv7.method_11578(lv2.method_10153(), arg2.method_8320(lv8), arg2, lv, lv8);
						method_9611(lv7, lv9, arg2, lv, i);
					}
				}
			}
		}
	}

	private class_2773 method_10477(class_1922 arg, class_2338 arg2, class_2350 arg3) {
		class_2338 lv = arg2.method_10093(arg3);
		class_2680 lv2 = arg.method_8320(lv);
		class_2338 lv3 = arg2.method_10084();
		class_2680 lv4 = arg.method_8320(lv3);
		if (!lv4.method_11621(arg, lv3)) {
			boolean bl = arg.method_8320(lv).method_11631(arg, lv) || arg.method_8320(lv).method_11614() == class_2246.field_10171;
			if (bl && method_10484(arg.method_8320(lv.method_10084()))) {
				if (lv2.method_11603(arg, lv)) {
					return class_2773.field_12686;
				}

				return class_2773.field_12689;
			}
		}

		return !method_10482(arg.method_8320(lv), arg3) && (lv2.method_11621(arg, lv) || !method_10484(arg.method_8320(lv.method_10074())))
			? class_2773.field_12687
			: class_2773.field_12689;
	}

	@Override
	public boolean method_9558(class_2680 arg, class_1941 arg2, class_2338 arg3) {
		class_2338 lv = arg3.method_10074();
		class_2680 lv2 = arg2.method_8320(lv);
		return lv2.method_11631(arg2, lv) || lv2.method_11614() == class_2246.field_10171;
	}

	private class_2680 method_10485(class_1937 arg, class_2338 arg2, class_2680 arg3) {
		arg3 = this.method_10481(arg, arg2, arg3);
		List<class_2338> list = Lists.<class_2338>newArrayList(this.field_11434);
		this.field_11434.clear();

		for (class_2338 lv : list) {
			arg.method_8452(lv, this);
		}

		return arg3;
	}

	private class_2680 method_10481(class_1937 arg, class_2338 arg2, class_2680 arg3) {
		class_2680 lv = arg3;
		int i = (Integer)arg3.method_11654(field_11432);
		this.field_11438 = false;
		int j = arg.method_8482(arg2);
		this.field_11438 = true;
		int k = 0;
		if (j < 15) {
			for (class_2350 lv2 : class_2350.class_2353.field_11062) {
				class_2338 lv3 = arg2.method_10093(lv2);
				class_2680 lv4 = arg.method_8320(lv3);
				k = this.method_10486(k, lv4);
				class_2338 lv5 = arg2.method_10084();
				if (lv4.method_11621(arg, lv3) && !arg.method_8320(lv5).method_11621(arg, lv5)) {
					k = this.method_10486(k, arg.method_8320(lv3.method_10084()));
				} else if (!lv4.method_11621(arg, lv3)) {
					k = this.method_10486(k, arg.method_8320(lv3.method_10074()));
				}
			}
		}

		int l = k - 1;
		if (j > l) {
			l = j;
		}

		if (i != l) {
			arg3 = arg3.method_11657(field_11432, Integer.valueOf(l));
			if (arg.method_8320(arg2) == lv) {
				arg.method_8652(arg2, arg3, 2);
			}

			this.field_11434.add(arg2);

			for (class_2350 lv6 : class_2350.values()) {
				this.field_11434.add(arg2.method_10093(lv6));
			}
		}

		return arg3;
	}

	private void method_10479(class_1937 arg, class_2338 arg2) {
		if (arg.method_8320(arg2).method_11614() == this) {
			arg.method_8452(arg2, this);

			for (class_2350 lv : class_2350.values()) {
				arg.method_8452(arg2.method_10093(lv), this);
			}
		}
	}

	@Override
	public void method_9615(class_2680 arg, class_1937 arg2, class_2338 arg3, class_2680 arg4) {
		if (arg4.method_11614() != arg.method_11614() && !arg2.field_9236) {
			this.method_10485(arg2, arg3, arg);

			for (class_2350 lv : class_2350.class_2353.field_11064) {
				arg2.method_8452(arg3.method_10093(lv), this);
			}

			for (class_2350 lv : class_2350.class_2353.field_11062) {
				this.method_10479(arg2, arg3.method_10093(lv));
			}

			for (class_2350 lv : class_2350.class_2353.field_11062) {
				class_2338 lv2 = arg3.method_10093(lv);
				if (arg2.method_8320(lv2).method_11621(arg2, lv2)) {
					this.method_10479(arg2, lv2.method_10084());
				} else {
					this.method_10479(arg2, lv2.method_10074());
				}
			}
		}
	}

	@Override
	public void method_9536(class_2680 arg, class_1937 arg2, class_2338 arg3, class_2680 arg4, boolean bl) {
		if (!bl && arg.method_11614() != arg4.method_11614()) {
			super.method_9536(arg, arg2, arg3, arg4, bl);
			if (!arg2.field_9236) {
				for (class_2350 lv : class_2350.values()) {
					arg2.method_8452(arg3.method_10093(lv), this);
				}

				this.method_10485(arg2, arg3, arg);

				for (class_2350 lv2 : class_2350.class_2353.field_11062) {
					this.method_10479(arg2, arg3.method_10093(lv2));
				}

				for (class_2350 lv2 : class_2350.class_2353.field_11062) {
					class_2338 lv3 = arg3.method_10093(lv2);
					if (arg2.method_8320(lv3).method_11621(arg2, lv3)) {
						this.method_10479(arg2, lv3.method_10084());
					} else {
						this.method_10479(arg2, lv3.method_10074());
					}
				}
			}
		}
	}

	private int method_10486(int i, class_2680 arg) {
		if (arg.method_11614() != this) {
			return i;
		} else {
			int j = (Integer)arg.method_11654(field_11432);
			return j > i ? j : i;
		}
	}

	@Override
	public void method_9612(class_2680 arg, class_1937 arg2, class_2338 arg3, class_2248 arg4, class_2338 arg5) {
		if (!arg2.field_9236) {
			if (arg.method_11591(arg2, arg3)) {
				this.method_10485(arg2, arg3, arg);
			} else {
				method_9497(arg, arg2, arg3);
				arg2.method_8650(arg3);
			}
		}
	}

	@Override
	public int method_9603(class_2680 arg, class_1922 arg2, class_2338 arg3, class_2350 arg4) {
		return !this.field_11438 ? 0 : arg.method_11597(arg2, arg3, arg4);
	}

	@Override
	public int method_9524(class_2680 arg, class_1922 arg2, class_2338 arg3, class_2350 arg4) {
		if (!this.field_11438) {
			return 0;
		} else {
			int i = (Integer)arg.method_11654(field_11432);
			if (i == 0) {
				return 0;
			} else if (arg4 == class_2350.field_11036) {
				return i;
			} else {
				EnumSet<class_2350> enumSet = EnumSet.noneOf(class_2350.class);

				for (class_2350 lv : class_2350.class_2353.field_11062) {
					if (this.method_10478(arg2, arg3, lv)) {
						enumSet.add(lv);
					}
				}

				if (arg4.method_10166().method_10179() && enumSet.isEmpty()) {
					return i;
				} else {
					return enumSet.contains(arg4) && !enumSet.contains(arg4.method_10160()) && !enumSet.contains(arg4.method_10170()) ? i : 0;
				}
			}
		}
	}

	private boolean method_10478(class_1922 arg, class_2338 arg2, class_2350 arg3) {
		class_2338 lv = arg2.method_10093(arg3);
		class_2680 lv2 = arg.method_8320(lv);
		boolean bl = lv2.method_11621(arg, lv);
		class_2338 lv3 = arg2.method_10084();
		boolean bl2 = arg.method_8320(lv3).method_11621(arg, lv3);
		if (!bl2 && bl && method_10483(arg, lv.method_10084())) {
			return true;
		} else if (method_10482(lv2, arg3)) {
			return true;
		} else {
			return lv2.method_11614() == class_2246.field_10450 && lv2.method_11654(class_2312.field_10911) && lv2.method_11654(class_2312.field_11177) == arg3
				? true
				: !bl && method_10483(arg, lv.method_10074());
		}
	}

	protected static boolean method_10483(class_1922 arg, class_2338 arg2) {
		return method_10484(arg.method_8320(arg2));
	}

	protected static boolean method_10484(class_2680 arg) {
		return method_10482(arg, null);
	}

	protected static boolean method_10482(class_2680 arg, @Nullable class_2350 arg2) {
		class_2248 lv = arg.method_11614();
		if (lv == class_2246.field_10091) {
			return true;
		} else if (arg.method_11614() == class_2246.field_10450) {
			class_2350 lv2 = arg.method_11654(class_2462.field_11177);
			return lv2 == arg2 || lv2.method_10153() == arg2;
		} else {
			return class_2246.field_10282 == arg.method_11614() ? arg2 == arg.method_11654(class_2426.field_10927) : arg.method_11634() && arg2 != null;
		}
	}

	@Override
	public boolean method_9506(class_2680 arg) {
		return this.field_11438;
	}

	@Environment(EnvType.CLIENT)
	public static int method_10487(int i) {
		float f = (float)i / 15.0F;
		float g = f * 0.6F + 0.4F;
		if (i == 0) {
			g = 0.3F;
		}

		float h = f * f * 0.7F - 0.5F;
		float j = f * f * 0.6F - 0.7F;
		if (h < 0.0F) {
			h = 0.0F;
		}

		if (j < 0.0F) {
			j = 0.0F;
		}

		int k = class_3532.method_15340((int)(g * 255.0F), 0, 255);
		int l = class_3532.method_15340((int)(h * 255.0F), 0, 255);
		int m = class_3532.method_15340((int)(j * 255.0F), 0, 255);
		return 0xFF000000 | k << 16 | l << 8 | m;
	}

	@Environment(EnvType.CLIENT)
	@Override
	public void method_9496(class_2680 arg, class_1937 arg2, class_2338 arg3, Random random) {
		int i = (Integer)arg.method_11654(field_11432);
		if (i != 0) {
			double d = (double)arg3.method_10263() + 0.5 + ((double)random.nextFloat() - 0.5) * 0.2;
			double e = (double)((float)arg3.method_10264() + 0.0625F);
			double f = (double)arg3.method_10260() + 0.5 + ((double)random.nextFloat() - 0.5) * 0.2;
			float g = (float)i / 15.0F;
			float h = g * 0.6F + 0.4F;
			float j = Math.max(0.0F, g * g * 0.7F - 0.5F);
			float k = Math.max(0.0F, g * g * 0.6F - 0.7F);
			arg2.method_8406(new class_2390(h, j, k, 1.0F), d, e, f, 0.0, 0.0, 0.0);
		}
	}

	@Override
	public class_1921 method_9551() {
		return class_1921.field_9174;
	}

	@Override
	public class_2680 method_9598(class_2680 arg, class_2470 arg2) {
		switch (arg2) {
			case field_11464:
				return arg.method_11657(field_11440, arg.method_11654(field_11437))
					.method_11657(field_11436, arg.method_11654(field_11439))
					.method_11657(field_11437, arg.method_11654(field_11440))
					.method_11657(field_11439, arg.method_11654(field_11436));
			case field_11465:
				return arg.method_11657(field_11440, arg.method_11654(field_11436))
					.method_11657(field_11436, arg.method_11654(field_11437))
					.method_11657(field_11437, arg.method_11654(field_11439))
					.method_11657(field_11439, arg.method_11654(field_11440));
			case field_11463:
				return arg.method_11657(field_11440, arg.method_11654(field_11439))
					.method_11657(field_11436, arg.method_11654(field_11440))
					.method_11657(field_11437, arg.method_11654(field_11436))
					.method_11657(field_11439, arg.method_11654(field_11437));
			default:
				return arg;
		}
	}

	@Override
	public class_2680 method_9569(class_2680 arg, class_2415 arg2) {
		switch (arg2) {
			case field_11300:
				return arg.method_11657(field_11440, arg.method_11654(field_11437)).method_11657(field_11437, arg.method_11654(field_11440));
			case field_11301:
				return arg.method_11657(field_11436, arg.method_11654(field_11439)).method_11657(field_11439, arg.method_11654(field_11436));
			default:
				return super.method_9569(arg, arg2);
		}
	}

	@Override
	protected void method_9515(class_2689.class_2690<class_2248, class_2680> arg) {
		arg.method_11667(field_11440, field_11436, field_11437, field_11439, field_11432);
	}
}
