package net.minecraft;

import java.util.Random;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

public class class_3865 extends class_2363 {
	protected class_3865(class_2248.class_2251 arg) {
		super(arg);
	}

	@Override
	public class_2586 method_10123(class_1922 arg) {
		return new class_3866();
	}

	@Override
	protected void method_17025(class_1937 arg, class_2338 arg2, class_1657 arg3) {
		class_2586 lv = arg.method_8321(arg2);
		if (lv instanceof class_3866) {
			arg3.method_17355((class_3908)lv);
			arg3.method_7281(class_3468.field_15379);
		}
	}

	@Environment(EnvType.CLIENT)
	@Override
	public void method_9496(class_2680 arg, class_1937 arg2, class_2338 arg3, Random random) {
		if ((Boolean)arg.method_11654(field_11105)) {
			double d = (double)arg3.method_10263() + 0.5;
			double e = (double)arg3.method_10264();
			double f = (double)arg3.method_10260() + 0.5;
			if (random.nextDouble() < 0.1) {
				arg2.method_8486(d, e, f, class_3417.field_15006, class_3419.field_15245, 1.0F, 1.0F, false);
			}

			class_2350 lv = arg.method_11654(field_11104);
			class_2350.class_2351 lv2 = lv.method_10166();
			double g = 0.52;
			double h = random.nextDouble() * 0.6 - 0.3;
			double i = lv2 == class_2350.class_2351.field_11048 ? (double)lv.method_10148() * 0.52 : h;
			double j = random.nextDouble() * 6.0 / 16.0;
			double k = lv2 == class_2350.class_2351.field_11051 ? (double)lv.method_10165() * 0.52 : h;
			arg2.method_8406(class_2398.field_11251, d + i, e + j, f + k, 0.0, 0.0, 0.0);
			arg2.method_8406(class_2398.field_11240, d + i, e + j, f + k, 0.0, 0.0, 0.0);
		}
	}
}
