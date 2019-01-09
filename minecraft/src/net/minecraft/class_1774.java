package net.minecraft;

import java.util.List;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

public class class_1774 extends class_1792 {
	public class_1774(class_1792.class_1793 arg) {
		super(arg);
	}

	@Override
	public class_1269 method_7884(class_1838 arg) {
		class_1937 lv = arg.method_8045();
		class_2338 lv2 = arg.method_8037();
		class_2680 lv3 = lv.method_8320(lv2);
		if (lv3.method_11614() != class_2246.field_10540 && lv3.method_11614() != class_2246.field_9987) {
			return class_1269.field_5814;
		} else {
			class_2338 lv4 = lv2.method_10084();
			if (!lv.method_8623(lv4)) {
				return class_1269.field_5814;
			} else {
				double d = (double)lv4.method_10263();
				double e = (double)lv4.method_10264();
				double f = (double)lv4.method_10260();
				List<class_1297> list = lv.method_8335(null, new class_238(d, e, f, d + 1.0, e + 2.0, f + 1.0));
				if (!list.isEmpty()) {
					return class_1269.field_5814;
				} else {
					if (!lv.field_9236) {
						class_1511 lv5 = new class_1511(lv, d + 0.5, e, f + 0.5);
						lv5.method_6839(false);
						lv.method_8649(lv5);
						if (lv.field_9247 instanceof class_2880) {
							class_2881 lv6 = ((class_2880)lv.field_9247).method_12513();
							lv6.method_12522();
						}
					}

					arg.method_8041().method_7934(1);
					return class_1269.field_5812;
				}
			}
		}
	}

	@Environment(EnvType.CLIENT)
	@Override
	public boolean method_7886(class_1799 arg) {
		return true;
	}
}
