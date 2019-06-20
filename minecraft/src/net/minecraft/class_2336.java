package net.minecraft;

import java.util.Random;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

public class class_2336 extends class_2237 implements class_3737 {
	public static final class_2753 field_10966 = class_2383.field_11177;
	public static final class_2746 field_10968 = class_2741.field_12508;
	protected static final class_265 field_10967 = class_2248.method_9541(1.0, 0.0, 1.0, 15.0, 14.0, 15.0);
	public static final class_2588 field_17363 = new class_2588("container.enderchest");

	protected class_2336(class_2248.class_2251 arg) {
		super(arg);
		this.method_9590(this.field_10647.method_11664().method_11657(field_10966, class_2350.field_11043).method_11657(field_10968, Boolean.valueOf(false)));
	}

	@Override
	public class_265 method_9530(class_2680 arg, class_1922 arg2, class_2338 arg3, class_3726 arg4) {
		return field_10967;
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
	public class_2680 method_9605(class_1750 arg) {
		class_3610 lv = arg.method_8045().method_8316(arg.method_8037());
		return this.method_9564()
			.method_11657(field_10966, arg.method_8042().method_10153())
			.method_11657(field_10968, Boolean.valueOf(lv.method_15772() == class_3612.field_15910));
	}

	@Override
	public boolean method_9534(class_2680 arg, class_1937 arg2, class_2338 arg3, class_1657 arg4, class_1268 arg5, class_3965 arg6) {
		class_1730 lv = arg4.method_7274();
		class_2586 lv2 = arg2.method_8321(arg3);
		if (lv != null && lv2 instanceof class_2611) {
			class_2338 lv3 = arg3.method_10084();
			if (arg2.method_8320(lv3).method_11621(arg2, lv3)) {
				return true;
			} else if (arg2.field_9236) {
				return true;
			} else {
				class_2611 lv4 = (class_2611)lv2;
				lv.method_7661(lv4);
				arg4.method_17355(new class_747((i, arg2x, arg3x) -> class_1707.method_19245(i, arg2x, lv), field_17363));
				arg4.method_7281(class_3468.field_15424);
				return true;
			}
		} else {
			return true;
		}
	}

	@Override
	public class_2586 method_10123(class_1922 arg) {
		return new class_2611();
	}

	@Environment(EnvType.CLIENT)
	@Override
	public void method_9496(class_2680 arg, class_1937 arg2, class_2338 arg3, Random random) {
		for (int i = 0; i < 3; i++) {
			int j = random.nextInt(2) * 2 - 1;
			int k = random.nextInt(2) * 2 - 1;
			double d = (double)arg3.method_10263() + 0.5 + 0.25 * (double)j;
			double e = (double)((float)arg3.method_10264() + random.nextFloat());
			double f = (double)arg3.method_10260() + 0.5 + 0.25 * (double)k;
			double g = (double)(random.nextFloat() * (float)j);
			double h = ((double)random.nextFloat() - 0.5) * 0.125;
			double l = (double)(random.nextFloat() * (float)k);
			arg2.method_8406(class_2398.field_11214, d, e, f, g, h, l);
		}
	}

	@Override
	public class_2680 method_9598(class_2680 arg, class_2470 arg2) {
		return arg.method_11657(field_10966, arg2.method_10503(arg.method_11654(field_10966)));
	}

	@Override
	public class_2680 method_9569(class_2680 arg, class_2415 arg2) {
		return arg.method_11626(arg2.method_10345(arg.method_11654(field_10966)));
	}

	@Override
	protected void method_9515(class_2689.class_2690<class_2248, class_2680> arg) {
		arg.method_11667(field_10966, field_10968);
	}

	@Override
	public class_3610 method_9545(class_2680 arg) {
		return arg.method_11654(field_10968) ? class_3612.field_15910.method_15729(false) : super.method_9545(arg);
	}

	@Override
	public class_2680 method_9559(class_2680 arg, class_2350 arg2, class_2680 arg3, class_1936 arg4, class_2338 arg5, class_2338 arg6) {
		if ((Boolean)arg.method_11654(field_10968)) {
			arg4.method_8405().method_8676(arg5, class_3612.field_15910, class_3612.field_15910.method_15789(arg4));
		}

		return super.method_9559(arg, arg2, arg3, arg4, arg5, arg6);
	}

	@Override
	public boolean method_9516(class_2680 arg, class_1922 arg2, class_2338 arg3, class_10 arg4) {
		return false;
	}
}
