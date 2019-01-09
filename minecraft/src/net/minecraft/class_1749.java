package net.minecraft;

import java.util.List;

public class class_1749 extends class_1792 {
	private final class_1690.class_1692 field_7902;

	public class_1749(class_1690.class_1692 arg, class_1792.class_1793 arg2) {
		super(arg2);
		this.field_7902 = arg;
	}

	@Override
	public class_1271<class_1799> method_7836(class_1937 arg, class_1657 arg2, class_1268 arg3) {
		class_1799 lv = arg2.method_5998(arg3);
		float f = 1.0F;
		float g = class_3532.method_16439(1.0F, arg2.field_6004, arg2.field_5965);
		float h = class_3532.method_16439(1.0F, arg2.field_5982, arg2.field_6031);
		double d = class_3532.method_16436(1.0, arg2.field_6014, arg2.field_5987);
		double e = class_3532.method_16436(1.0, arg2.field_6036, arg2.field_6010) + (double)arg2.method_5751();
		double i = class_3532.method_16436(1.0, arg2.field_5969, arg2.field_6035);
		class_243 lv2 = new class_243(d, e, i);
		float j = class_3532.method_15362(-h * (float) (Math.PI / 180.0) - (float) Math.PI);
		float k = class_3532.method_15374(-h * (float) (Math.PI / 180.0) - (float) Math.PI);
		float l = -class_3532.method_15362(-g * (float) (Math.PI / 180.0));
		float m = class_3532.method_15374(-g * (float) (Math.PI / 180.0));
		float n = k * l;
		float p = j * l;
		double q = 5.0;
		class_243 lv3 = lv2.method_1031((double)n * 5.0, (double)m * 5.0, (double)p * 5.0);
		class_239 lv4 = arg.method_8418(lv2, lv3, class_242.field_1347);
		if (lv4 == null) {
			return new class_1271<>(class_1269.field_5811, lv);
		} else {
			class_243 lv5 = arg2.method_5828(1.0F);
			boolean bl = false;
			List<class_1297> list = arg.method_8335(
				arg2, arg2.method_5829().method_1012(lv5.field_1352 * 5.0, lv5.field_1351 * 5.0, lv5.field_1350 * 5.0).method_1014(1.0)
			);

			for (int r = 0; r < list.size(); r++) {
				class_1297 lv6 = (class_1297)list.get(r);
				if (lv6.method_5863()) {
					class_238 lv7 = lv6.method_5829().method_1014((double)lv6.method_5871());
					if (lv7.method_1006(lv2)) {
						bl = true;
					}
				}
			}

			if (bl) {
				return new class_1271<>(class_1269.field_5811, lv);
			} else if (lv4.field_1330 == class_239.class_240.field_1332) {
				class_2338 lv8 = lv4.method_1015();
				class_2248 lv9 = arg.method_8320(lv8).method_11614();
				class_1690 lv10 = new class_1690(arg, lv4.field_1329.field_1352, lv4.field_1329.field_1351, lv4.field_1329.field_1350);
				lv10.method_7541(this.field_7902);
				lv10.field_6031 = arg2.field_6031;
				if (!arg.method_8587(lv10, lv10.method_5829().method_1014(-0.1))) {
					return new class_1271<>(class_1269.field_5814, lv);
				} else {
					if (!arg.field_9236) {
						arg.method_8649(lv10);
					}

					if (!arg2.field_7503.field_7477) {
						lv.method_7934(1);
					}

					arg2.method_7259(class_3468.field_15372.method_14956(this));
					return new class_1271<>(class_1269.field_5812, lv);
				}
			} else {
				return new class_1271<>(class_1269.field_5811, lv);
			}
		}
	}
}
