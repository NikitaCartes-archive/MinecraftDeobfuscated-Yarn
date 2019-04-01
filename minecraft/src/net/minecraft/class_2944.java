package net.minecraft;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.mojang.datafixers.Dynamic;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.function.Function;

public abstract class class_2944<T extends class_3037> extends class_3031<T> {
	public class_2944(Function<Dynamic<?>, ? extends T> function, boolean bl) {
		super(function, bl);
	}

	protected static boolean method_16432(class_3746 arg, class_2338 arg2) {
		return arg.method_16358(
			arg2,
			argx -> {
				class_2248 lv = argx.method_11614();
				return argx.method_11588()
					|| argx.method_11602(class_3481.field_15503)
					|| lv == class_2246.field_10219
					|| class_2248.method_9519(lv)
					|| lv.method_9525(class_3481.field_15475)
					|| lv.method_9525(class_3481.field_15462)
					|| lv == class_2246.field_10597;
			}
		);
	}

	protected static boolean method_16424(class_3746 arg, class_2338 arg2) {
		return arg.method_16358(arg2, class_2680::method_11588);
	}

	protected static boolean method_16419(class_3746 arg, class_2338 arg2) {
		return arg.method_16358(arg2, argx -> class_2248.method_9519(argx.method_11614()));
	}

	protected static boolean method_16422(class_3746 arg, class_2338 arg2) {
		return arg.method_16358(arg2, argx -> argx.method_11614() == class_2246.field_10382);
	}

	protected static boolean method_16416(class_3746 arg, class_2338 arg2) {
		return arg.method_16358(arg2, argx -> argx.method_11602(class_3481.field_15503));
	}

	protected static boolean method_16420(class_3746 arg, class_2338 arg2) {
		return arg.method_16358(arg2, argx -> argx.method_11588() || argx.method_11602(class_3481.field_15503));
	}

	protected static boolean method_16430(class_3746 arg, class_2338 arg2) {
		return arg.method_16358(arg2, argx -> {
			class_2248 lv = argx.method_11614();
			return class_2248.method_9519(lv) || lv == class_2246.field_10219;
		});
	}

	protected static boolean method_16433(class_3746 arg, class_2338 arg2) {
		return arg.method_16358(arg2, argx -> {
			class_2248 lv = argx.method_11614();
			return class_2248.method_9519(lv) || lv == class_2246.field_10219 || lv == class_2246.field_10362;
		});
	}

	protected static boolean method_16425(class_3746 arg, class_2338 arg2) {
		return arg.method_16358(arg2, argx -> {
			class_3614 lv = argx.method_11620();
			return lv == class_3614.field_15956;
		});
	}

	protected void method_16427(class_3747 arg, class_2338 arg2) {
		if (!method_16419(arg, arg2)) {
			this.method_13153(arg, arg2, class_2246.field_10566.method_9564());
		}
	}

	@Override
	protected void method_13153(class_1945 arg, class_2338 arg2, class_2680 arg3) {
		this.method_12774(arg, arg2, arg3);
	}

	protected final void method_12773(Set<class_2338> set, class_1945 arg, class_2338 arg2, class_2680 arg3) {
		this.method_12774(arg, arg2, arg3);
		if (class_3481.field_15475.method_15141(arg3.method_11614())) {
			set.add(arg2.method_10062());
		}
	}

	private void method_12774(class_1945 arg, class_2338 arg2, class_2680 arg3) {
		if (this.field_13556) {
			arg.method_8652(arg2, arg3, 19);
		} else {
			arg.method_8652(arg2, arg3, 18);
		}
	}

	@Override
	public final boolean method_13151(class_1936 arg, class_2794<? extends class_2888> arg2, Random random, class_2338 arg3, T arg4) {
		Set<class_2338> set = Sets.<class_2338>newHashSet();
		boolean bl = this.method_12775(set, arg, random, arg3);
		List<Set<class_2338>> list = Lists.<Set<class_2338>>newArrayList();
		int i = 6;

		for (int j = 0; j < 6; j++) {
			list.add(Sets.newHashSet());
		}

		try (class_2338.class_2340 lv = class_2338.class_2340.method_10109()) {
			if (bl && !set.isEmpty()) {
				for (class_2338 lv2 : Lists.newArrayList(set)) {
					for (class_2350 lv3 : class_2350.values()) {
						lv.method_10114(lv2).method_10118(lv3);
						if (!set.contains(lv)) {
							class_2680 lv4 = arg.method_8320(lv);
							if (lv4.method_11570(class_2741.field_12541)) {
								((Set)list.get(0)).add(lv.method_10062());
								this.method_12774(arg, lv, lv4.method_11657(class_2741.field_12541, Integer.valueOf(1)));
							}
						}
					}
				}
			}

			for (int k = 1; k < 6; k++) {
				Set<class_2338> set2 = (Set<class_2338>)list.get(k - 1);
				Set<class_2338> set3 = (Set<class_2338>)list.get(k);

				for (class_2338 lv5 : set2) {
					for (class_2350 lv6 : class_2350.values()) {
						lv.method_10114(lv5).method_10118(lv6);
						if (!set2.contains(lv) && !set3.contains(lv)) {
							class_2680 lv7 = arg.method_8320(lv);
							if (lv7.method_11570(class_2741.field_12541)) {
								int l = (Integer)lv7.method_11654(class_2741.field_12541);
								if (l > k + 1) {
									class_2680 lv8 = lv7.method_11657(class_2741.field_12541, Integer.valueOf(k + 1));
									this.method_12774(arg, lv, lv8);
									set3.add(lv.method_10062());
								}
							}
						}
					}
				}
			}
		}

		return bl;
	}

	protected abstract boolean method_12775(Set<class_2338> set, class_3747 arg, Random random, class_2338 arg2);
}
