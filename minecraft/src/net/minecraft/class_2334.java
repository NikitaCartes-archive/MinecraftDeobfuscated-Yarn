package net.minecraft;

import java.util.Random;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

public class class_2334 extends class_2237 {
	protected static final class_265 field_10959 = class_2248.method_9541(0.0, 0.0, 0.0, 16.0, 12.0, 16.0);

	protected class_2334(class_2248.class_2251 arg) {
		super(arg);
	}

	@Override
	public class_2586 method_10123(class_1922 arg) {
		return new class_2640();
	}

	@Override
	public class_265 method_9529(class_2680 arg, class_1922 arg2, class_2338 arg3) {
		return field_10959;
	}

	@Override
	public void method_9548(class_2680 arg, class_1937 arg2, class_2338 arg3, class_1297 arg4) {
		if (!arg2.field_9236
			&& !arg4.method_5765()
			&& !arg4.method_5782()
			&& arg4.method_5822()
			&& class_259.method_1074(
				class_259.method_1078(arg4.method_5829().method_989((double)(-arg3.method_10263()), (double)(-arg3.method_10264()), (double)(-arg3.method_10260()))),
				arg.method_11606(arg2, arg3),
				class_247.field_16896
			)) {
			arg4.method_5731(class_2874.field_13078);
		}
	}

	@Environment(EnvType.CLIENT)
	@Override
	public void method_9496(class_2680 arg, class_1937 arg2, class_2338 arg3, Random random) {
		double d = (double)((float)arg3.method_10263() + random.nextFloat());
		double e = (double)((float)arg3.method_10264() + 0.8F);
		double f = (double)((float)arg3.method_10260() + random.nextFloat());
		double g = 0.0;
		double h = 0.0;
		double i = 0.0;
		arg2.method_8406(class_2398.field_11251, d, e, f, 0.0, 0.0, 0.0);
	}

	@Environment(EnvType.CLIENT)
	@Override
	public class_1799 method_9574(class_1922 arg, class_2338 arg2, class_2680 arg3) {
		return class_1799.field_8037;
	}
}
