package net.minecraft;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Random;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

public class class_2404 extends class_2248 implements class_2263 {
	public static final class_2758 field_11278 = class_2741.field_12538;
	protected final class_3609 field_11279;
	private final List<class_3610> field_11276;
	private final Map<class_2680, class_265> field_11277 = Maps.<class_2680, class_265>newIdentityHashMap();

	protected class_2404(class_3609 arg, class_2248.class_2251 arg2) {
		super(arg2);
		this.field_11279 = arg;
		this.field_11276 = Lists.<class_3610>newArrayList();
		this.field_11276.add(arg.method_15729(false));

		for (int i = 1; i < 8; i++) {
			this.field_11276.add(arg.method_15728(8 - i, false));
		}

		this.field_11276.add(arg.method_15728(8, true));
		this.method_9590(this.field_10647.method_11664().method_11657(field_11278, Integer.valueOf(0)));
	}

	@Override
	public void method_9514(class_2680 arg, class_1937 arg2, class_2338 arg3, Random random) {
		arg2.method_8316(arg3).method_15757(arg2, arg3, random);
	}

	@Override
	public boolean method_9579(class_2680 arg, class_1922 arg2, class_2338 arg3) {
		return false;
	}

	@Override
	public boolean method_9516(class_2680 arg, class_1922 arg2, class_2338 arg3, class_10 arg4) {
		return !this.field_11279.method_15791(class_3486.field_15518);
	}

	@Override
	public class_3610 method_9545(class_2680 arg) {
		int i = (Integer)arg.method_11654(field_11278);
		return (class_3610)this.field_11276.get(Math.min(i, 8));
	}

	@Override
	public boolean method_9532(class_2680 arg) {
		return false;
	}

	@Environment(EnvType.CLIENT)
	@Override
	public boolean method_9522(class_2680 arg, class_2680 arg2, class_2350 arg3) {
		return arg2.method_11618().method_15772().method_15780(this.field_11279) ? true : super.method_9601(arg);
	}

	@Override
	public class_265 method_9529(class_2680 arg, class_1922 arg2, class_2338 arg3) {
		class_3610 lv = arg2.method_8316(arg3.method_10084());
		return lv.method_15772().method_15780(this.field_11279) ? class_259.method_1077() : (class_265)this.field_11277.computeIfAbsent(arg, argx -> {
			class_3610 lvx = argx.method_11618();
			return class_259.method_1081(0.0, 0.0, 0.0, 1.0, (double)lvx.method_15763(), 1.0);
		});
	}

	@Override
	public class_2464 method_9604(class_2680 arg) {
		return class_2464.field_11455;
	}

	@Override
	public List<class_1799> method_9560(class_2680 arg, class_47.class_48 arg2) {
		return Collections.emptyList();
	}

	@Override
	public int method_9563(class_1941 arg) {
		return this.field_11279.method_15789(arg);
	}

	@Override
	public void method_9615(class_2680 arg, class_1937 arg2, class_2338 arg3, class_2680 arg4) {
		if (this.method_10316(arg2, arg3, arg)) {
			arg2.method_8405().method_8676(arg3, arg.method_11618().method_15772(), this.method_9563(arg2));
		}
	}

	@Override
	public class_2680 method_9559(class_2680 arg, class_2350 arg2, class_2680 arg3, class_1936 arg4, class_2338 arg5, class_2338 arg6) {
		if (arg.method_11618().method_15771() || arg3.method_11618().method_15771()) {
			arg4.method_8405().method_8676(arg5, arg.method_11618().method_15772(), this.method_9563(arg4));
		}

		return super.method_9559(arg, arg2, arg3, arg4, arg5, arg6);
	}

	@Override
	public void method_9612(class_2680 arg, class_1937 arg2, class_2338 arg3, class_2248 arg4, class_2338 arg5) {
		if (this.method_10316(arg2, arg3, arg)) {
			arg2.method_8405().method_8676(arg3, arg.method_11618().method_15772(), this.method_9563(arg2));
		}
	}

	public boolean method_10316(class_1937 arg, class_2338 arg2, class_2680 arg3) {
		if (this.field_11279.method_15791(class_3486.field_15518)) {
			boolean bl = false;

			for (class_2350 lv : class_2350.values()) {
				if (lv != class_2350.field_11033 && arg.method_8316(arg2.method_10093(lv)).method_15767(class_3486.field_15517)) {
					bl = true;
					break;
				}
			}

			if (bl) {
				class_3610 lv2 = arg.method_8316(arg2);
				if (lv2.method_15771()) {
					arg.method_8501(arg2, class_2246.field_10540.method_9564());
					this.method_10318(arg, arg2);
					return false;
				}

				if (lv2.method_15763() >= 0.44444445F) {
					arg.method_8501(arg2, class_2246.field_10445.method_9564());
					this.method_10318(arg, arg2);
					return false;
				}
			}
		}

		return true;
	}

	protected void method_10318(class_1936 arg, class_2338 arg2) {
		double d = (double)arg2.method_10263();
		double e = (double)arg2.method_10264();
		double f = (double)arg2.method_10260();
		arg.method_8396(
			null, arg2, class_3417.field_15112, class_3419.field_15245, 0.5F, 2.6F + (arg.method_8409().nextFloat() - arg.method_8409().nextFloat()) * 0.8F
		);

		for (int i = 0; i < 8; i++) {
			arg.method_8406(class_2398.field_11237, d + Math.random(), e + 1.2, f + Math.random(), 0.0, 0.0, 0.0);
		}
	}

	@Override
	protected void method_9515(class_2689.class_2690<class_2248, class_2680> arg) {
		arg.method_11667(field_11278);
	}

	@Override
	public class_3611 method_9700(class_1936 arg, class_2338 arg2, class_2680 arg3) {
		if ((Integer)arg3.method_11654(field_11278) == 0) {
			arg.method_8652(arg2, class_2246.field_10124.method_9564(), 11);
			return this.field_11279;
		} else {
			return class_3612.field_15906;
		}
	}
}
