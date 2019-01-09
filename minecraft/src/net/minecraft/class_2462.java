package net.minecraft;

import java.util.Random;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

public class class_2462 extends class_2312 {
	public static final class_2746 field_11452 = class_2741.field_12502;
	public static final class_2758 field_11451 = class_2741.field_12494;

	protected class_2462(class_2248.class_2251 arg) {
		super(arg);
		this.method_9590(
			this.field_10647
				.method_11664()
				.method_11657(field_11177, class_2350.field_11043)
				.method_11657(field_11451, Integer.valueOf(1))
				.method_11657(field_11452, Boolean.valueOf(false))
				.method_11657(field_10911, Boolean.valueOf(false))
		);
	}

	@Override
	public boolean method_9534(class_2680 arg, class_1937 arg2, class_2338 arg3, class_1657 arg4, class_1268 arg5, class_2350 arg6, float f, float g, float h) {
		if (!arg4.field_7503.field_7476) {
			return false;
		} else {
			arg2.method_8652(arg3, arg.method_11572(field_11451), 3);
			return true;
		}
	}

	@Override
	protected int method_9992(class_2680 arg) {
		return (Integer)arg.method_11654(field_11451) * 2;
	}

	@Override
	public class_2680 method_9605(class_1750 arg) {
		class_2680 lv = super.method_9605(arg);
		return lv.method_11657(field_11452, Boolean.valueOf(this.method_9996(arg.method_8045(), arg.method_8037(), lv)));
	}

	@Override
	public class_2680 method_9559(class_2680 arg, class_2350 arg2, class_2680 arg3, class_1936 arg4, class_2338 arg5, class_2338 arg6) {
		return !arg4.method_8608() && arg2.method_10166() != ((class_2350)arg.method_11654(field_11177)).method_10166()
			? arg.method_11657(field_11452, Boolean.valueOf(this.method_9996(arg4, arg5, arg)))
			: super.method_9559(arg, arg2, arg3, arg4, arg5, arg6);
	}

	@Override
	public boolean method_9996(class_1941 arg, class_2338 arg2, class_2680 arg3) {
		return this.method_10000(arg, arg2, arg3) > 0;
	}

	@Override
	protected boolean method_9989(class_2680 arg) {
		return method_9999(arg);
	}

	@Environment(EnvType.CLIENT)
	@Override
	public void method_9496(class_2680 arg, class_1937 arg2, class_2338 arg3, Random random) {
		if ((Boolean)arg.method_11654(field_10911)) {
			class_2350 lv = arg.method_11654(field_11177);
			double d = (double)((float)arg3.method_10263() + 0.5F) + (double)(random.nextFloat() - 0.5F) * 0.2;
			double e = (double)((float)arg3.method_10264() + 0.4F) + (double)(random.nextFloat() - 0.5F) * 0.2;
			double f = (double)((float)arg3.method_10260() + 0.5F) + (double)(random.nextFloat() - 0.5F) * 0.2;
			float g = -5.0F;
			if (random.nextBoolean()) {
				g = (float)((Integer)arg.method_11654(field_11451) * 2 - 1);
			}

			g /= 16.0F;
			double h = (double)(g * (float)lv.method_10148());
			double i = (double)(g * (float)lv.method_10165());
			arg2.method_8406(class_2390.field_11188, d + h, e, f + i, 0.0, 0.0, 0.0);
		}
	}

	@Override
	protected void method_9515(class_2689.class_2690<class_2248, class_2680> arg) {
		arg.method_11667(field_11177, field_11451, field_11452, field_10911);
	}
}
