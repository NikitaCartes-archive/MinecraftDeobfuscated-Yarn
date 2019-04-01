package net.minecraft;

import java.util.List;

public interface class_1768 {
	default boolean method_7801(class_1799 arg) {
		class_2487 lv = arg.method_7941("display");
		return lv != null && lv.method_10573("color", 99);
	}

	default int method_7800(class_1799 arg) {
		class_2487 lv = arg.method_7941("display");
		return lv != null && lv.method_10573("color", 99) ? lv.method_10550("color") : 10511680;
	}

	default void method_7798(class_1799 arg) {
		class_2487 lv = arg.method_7941("display");
		if (lv != null && lv.method_10545("color")) {
			lv.method_10551("color");
		}
	}

	default void method_7799(class_1799 arg, int i) {
		arg.method_7911("display").method_10569("color", i);
	}

	static class_1799 method_19261(class_1799 arg, List<class_1769> list) {
		class_1799 lv = class_1799.field_8037;
		int[] is = new int[3];
		int i = 0;
		int j = 0;
		class_1768 lv2 = null;
		class_1792 lv3 = arg.method_7909();
		if (lv3 instanceof class_1768) {
			lv2 = (class_1768)lv3;
			lv = arg.method_7972();
			lv.method_7939(1);
			if (lv2.method_7801(arg)) {
				int k = lv2.method_7800(lv);
				float f = (float)(k >> 16 & 0xFF) / 255.0F;
				float g = (float)(k >> 8 & 0xFF) / 255.0F;
				float h = (float)(k & 0xFF) / 255.0F;
				i = (int)((float)i + Math.max(f, Math.max(g, h)) * 255.0F);
				is[0] = (int)((float)is[0] + f * 255.0F);
				is[1] = (int)((float)is[1] + g * 255.0F);
				is[2] = (int)((float)is[2] + h * 255.0F);
				j++;
			}

			for (class_1769 lv4 : list) {
				float[] fs = lv4.method_7802().method_7787();
				int l = (int)(fs[0] * 255.0F);
				int m = (int)(fs[1] * 255.0F);
				int n = (int)(fs[2] * 255.0F);
				i += Math.max(l, Math.max(m, n));
				is[0] += l;
				is[1] += m;
				is[2] += n;
				j++;
			}
		}

		if (lv2 == null) {
			return class_1799.field_8037;
		} else {
			int k = is[0] / j;
			int o = is[1] / j;
			int p = is[2] / j;
			float h = (float)i / (float)j;
			float q = (float)Math.max(k, Math.max(o, p));
			k = (int)((float)k * h / q);
			o = (int)((float)o * h / q);
			p = (int)((float)p * h / q);
			int var26 = (k << 8) + o;
			var26 = (var26 << 8) + p;
			lv2.method_7799(lv, var26);
			return lv;
		}
	}
}
