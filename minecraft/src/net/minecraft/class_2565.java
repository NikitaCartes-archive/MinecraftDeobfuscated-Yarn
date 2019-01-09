package net.minecraft;

import java.util.Random;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

public class class_2565 extends class_2248 {
	protected class_2565(class_2248.class_2251 arg) {
		super(arg);
	}

	@Environment(EnvType.CLIENT)
	@Override
	public void method_9496(class_2680 arg, class_1937 arg2, class_2338 arg3, Random random) {
		class_2350 lv = class_2350.method_10162(random);
		class_2338 lv2 = arg3.method_10093(lv);
		if (lv != class_2350.field_11036 && !arg2.method_8320(lv2).method_11631(arg2, lv2)) {
			double d = (double)arg3.method_10263();
			double e = (double)arg3.method_10264();
			double f = (double)arg3.method_10260();
			if (lv == class_2350.field_11033) {
				e -= 0.05;
				d += random.nextDouble();
				f += random.nextDouble();
			} else {
				e += random.nextDouble() * 0.8;
				if (lv.method_10166() == class_2350.class_2351.field_11048) {
					f += random.nextDouble();
					if (lv == class_2350.field_11034) {
						d++;
					} else {
						d += 0.05;
					}
				} else {
					d += random.nextDouble();
					if (lv == class_2350.field_11035) {
						f++;
					} else {
						f += 0.05;
					}
				}
			}

			arg2.method_8406(class_2398.field_11232, d, e, f, 0.0, 0.0, 0.0);
		}
	}
}
