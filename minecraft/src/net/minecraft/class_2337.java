package net.minecraft;

import java.util.Random;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

public class class_2337 extends class_2318 {
	protected static final class_265 field_10971 = class_2248.method_9541(6.0, 0.0, 6.0, 10.0, 16.0, 10.0);
	protected static final class_265 field_10970 = class_2248.method_9541(6.0, 6.0, 0.0, 10.0, 10.0, 16.0);
	protected static final class_265 field_10969 = class_2248.method_9541(0.0, 6.0, 6.0, 16.0, 10.0, 10.0);

	protected class_2337(class_2248.class_2251 arg) {
		super(arg);
		this.method_9590(this.field_10647.method_11664().method_11657(field_10927, class_2350.field_11036));
	}

	@Override
	public class_2680 method_9598(class_2680 arg, class_2470 arg2) {
		return arg.method_11657(field_10927, arg2.method_10503(arg.method_11654(field_10927)));
	}

	@Override
	public class_2680 method_9569(class_2680 arg, class_2415 arg2) {
		return arg.method_11657(field_10927, arg2.method_10343(arg.method_11654(field_10927)));
	}

	@Override
	public class_265 method_9529(class_2680 arg, class_1922 arg2, class_2338 arg3) {
		switch (((class_2350)arg.method_11654(field_10927)).method_10166()) {
			case field_11048:
			default:
				return field_10969;
			case field_11051:
				return field_10970;
			case field_11052:
				return field_10971;
		}
	}

	@Override
	public class_2680 method_9605(class_1750 arg) {
		class_2350 lv = arg.method_8038();
		class_2680 lv2 = arg.method_8045().method_8320(arg.method_8037().method_10093(lv.method_10153()));
		return lv2.method_11614() == this && lv2.method_11654(field_10927) == lv
			? this.method_9564().method_11657(field_10927, lv.method_10153())
			: this.method_9564().method_11657(field_10927, lv);
	}

	@Environment(EnvType.CLIENT)
	@Override
	public void method_9496(class_2680 arg, class_1937 arg2, class_2338 arg3, Random random) {
		class_2350 lv = arg.method_11654(field_10927);
		double d = (double)arg3.method_10263() + 0.55 - (double)(random.nextFloat() * 0.1F);
		double e = (double)arg3.method_10264() + 0.55 - (double)(random.nextFloat() * 0.1F);
		double f = (double)arg3.method_10260() + 0.55 - (double)(random.nextFloat() * 0.1F);
		double g = (double)(0.4F - (random.nextFloat() + random.nextFloat()) * 0.4F);
		if (random.nextInt(5) == 0) {
			arg2.method_8406(
				class_2398.field_11207,
				d + (double)lv.method_10148() * g,
				e + (double)lv.method_10164() * g,
				f + (double)lv.method_10165() * g,
				random.nextGaussian() * 0.005,
				random.nextGaussian() * 0.005,
				random.nextGaussian() * 0.005
			);
		}
	}

	@Override
	public class_1921 method_9551() {
		return class_1921.field_9174;
	}

	@Override
	protected void method_9515(class_2689.class_2690<class_2248, class_2680> arg) {
		arg.method_11667(field_10927);
	}

	@Override
	public class_3619 method_9527(class_2680 arg) {
		return class_3619.field_15974;
	}
}
