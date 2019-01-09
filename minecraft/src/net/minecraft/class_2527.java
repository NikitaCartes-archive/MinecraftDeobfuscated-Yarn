package net.minecraft;

import java.util.Random;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

public class class_2527 extends class_2248 {
	protected static final class_265 field_11618 = class_2248.method_9541(6.0, 0.0, 6.0, 10.0, 10.0, 10.0);

	protected class_2527(class_2248.class_2251 arg) {
		super(arg);
	}

	@Override
	public class_265 method_9529(class_2680 arg, class_1922 arg2, class_2338 arg3) {
		return field_11618;
	}

	@Override
	public class_2680 method_9559(class_2680 arg, class_2350 arg2, class_2680 arg3, class_1936 arg4, class_2338 arg5, class_2338 arg6) {
		return arg2 == class_2350.field_11033 && !this.method_9558(arg, arg4, arg5)
			? class_2246.field_10124.method_9564()
			: super.method_9559(arg, arg2, arg3, arg4, arg5, arg6);
	}

	@Override
	public boolean method_9558(class_2680 arg, class_1941 arg2, class_2338 arg3) {
		class_2338 lv = arg3.method_10074();
		class_2680 lv2 = arg2.method_8320(lv);
		class_2248 lv3 = lv2.method_11614();
		boolean bl = lv3.method_9525(class_3481.field_16584)
			|| lv3 instanceof class_2506
			|| lv3 == class_2246.field_10033
			|| lv3.method_9525(class_3481.field_15504)
			|| lv2.method_11631(arg2, lv);
		return bl && lv3 != class_2246.field_10613;
	}

	@Environment(EnvType.CLIENT)
	@Override
	public void method_9496(class_2680 arg, class_1937 arg2, class_2338 arg3, Random random) {
		double d = (double)arg3.method_10263() + 0.5;
		double e = (double)arg3.method_10264() + 0.7;
		double f = (double)arg3.method_10260() + 0.5;
		arg2.method_8406(class_2398.field_11251, d, e, f, 0.0, 0.0, 0.0);
		arg2.method_8406(class_2398.field_11240, d, e, f, 0.0, 0.0, 0.0);
	}

	@Override
	public class_1921 method_9551() {
		return class_1921.field_9174;
	}
}
