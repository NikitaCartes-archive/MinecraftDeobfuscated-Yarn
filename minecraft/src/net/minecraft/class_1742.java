package net.minecraft;

import java.util.List;
import java.util.Random;

public class class_1742 extends class_1792 {
	public class_1742(class_1792.class_1793 arg) {
		super(arg);
	}

	@Override
	public class_1269 method_7884(class_1838 arg) {
		class_2350 lv = arg.method_8038();
		if (lv == class_2350.field_11033) {
			return class_1269.field_5814;
		} else {
			class_1937 lv2 = arg.method_8045();
			class_1750 lv3 = new class_1750(arg);
			class_2338 lv4 = lv3.method_8037();
			class_2338 lv5 = lv4.method_10084();
			if (lv3.method_7716() && lv2.method_8320(lv5).method_11587(lv3)) {
				double d = (double)lv4.method_10263();
				double e = (double)lv4.method_10264();
				double f = (double)lv4.method_10260();
				List<class_1297> list = lv2.method_8335(null, new class_238(d, e, f, d + 1.0, e + 2.0, f + 1.0));
				if (!list.isEmpty()) {
					return class_1269.field_5814;
				} else {
					class_1799 lv6 = arg.method_8041();
					if (!lv2.field_9236) {
						lv2.method_8650(lv4, false);
						lv2.method_8650(lv5, false);
						class_1531 lv7 = new class_1531(lv2, d + 0.5, e, f + 0.5);
						float g = (float)class_3532.method_15375((class_3532.method_15393(arg.method_8044() - 180.0F) + 22.5F) / 45.0F) * 45.0F;
						lv7.method_5808(d + 0.5, e, f + 0.5, g, 0.0F);
						this.method_7701(lv7, lv2.field_9229);
						class_1299.method_5881(lv2, arg.method_8036(), lv7, lv6.method_7969());
						lv2.method_8649(lv7);
						lv2.method_8465(null, lv7.field_5987, lv7.field_6010, lv7.field_6035, class_3417.field_14969, class_3419.field_15245, 0.75F, 0.8F);
					}

					lv6.method_7934(1);
					return class_1269.field_5812;
				}
			} else {
				return class_1269.field_5814;
			}
		}
	}

	private void method_7701(class_1531 arg, Random random) {
		class_2379 lv = arg.method_6921();
		float f = random.nextFloat() * 5.0F;
		float g = random.nextFloat() * 20.0F - 10.0F;
		class_2379 lv2 = new class_2379(lv.method_10256() + f, lv.method_10257() + g, lv.method_10258());
		arg.method_6919(lv2);
		lv = arg.method_6923();
		f = random.nextFloat() * 10.0F - 5.0F;
		lv2 = new class_2379(lv.method_10256(), lv.method_10257() + f, lv.method_10258());
		arg.method_6927(lv2);
	}
}
