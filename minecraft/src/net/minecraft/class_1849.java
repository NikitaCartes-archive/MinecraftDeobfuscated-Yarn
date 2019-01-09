package net.minecraft;

import com.google.common.collect.Lists;
import java.util.List;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

public class class_1849 extends class_1852 {
	public class_1849(class_2960 arg) {
		super(arg);
	}

	@Override
	public boolean method_8115(class_1263 arg, class_1937 arg2) {
		if (!(arg instanceof class_1715)) {
			return false;
		} else {
			class_1799 lv = class_1799.field_8037;
			List<class_1799> list = Lists.<class_1799>newArrayList();

			for (int i = 0; i < arg.method_5439(); i++) {
				class_1799 lv2 = arg.method_5438(i);
				if (!lv2.method_7960()) {
					if (lv2.method_7909() instanceof class_1768) {
						if (!lv.method_7960()) {
							return false;
						}

						lv = lv2;
					} else {
						if (!(lv2.method_7909() instanceof class_1769)) {
							return false;
						}

						list.add(lv2);
					}
				}
			}

			return !lv.method_7960() && !list.isEmpty();
		}
	}

	@Override
	public class_1799 method_8116(class_1263 arg) {
		class_1799 lv = class_1799.field_8037;
		int[] is = new int[3];
		int i = 0;
		int j = 0;
		class_1768 lv2 = null;

		for (int k = 0; k < arg.method_5439(); k++) {
			class_1799 lv3 = arg.method_5438(k);
			if (!lv3.method_7960()) {
				class_1792 lv4 = lv3.method_7909();
				if (lv4 instanceof class_1768) {
					lv2 = (class_1768)lv4;
					if (!lv.method_7960()) {
						return class_1799.field_8037;
					}

					lv = lv3.method_7972();
					lv.method_7939(1);
					if (lv2.method_7801(lv3)) {
						int l = lv2.method_7800(lv);
						float f = (float)(l >> 16 & 0xFF) / 255.0F;
						float g = (float)(l >> 8 & 0xFF) / 255.0F;
						float h = (float)(l & 0xFF) / 255.0F;
						i = (int)((float)i + Math.max(f, Math.max(g, h)) * 255.0F);
						is[0] = (int)((float)is[0] + f * 255.0F);
						is[1] = (int)((float)is[1] + g * 255.0F);
						is[2] = (int)((float)is[2] + h * 255.0F);
						j++;
					}
				} else {
					if (!(lv4 instanceof class_1769)) {
						return class_1799.field_8037;
					}

					float[] fs = ((class_1769)lv4).method_7802().method_7787();
					int m = (int)(fs[0] * 255.0F);
					int n = (int)(fs[1] * 255.0F);
					int o = (int)(fs[2] * 255.0F);
					i += Math.max(m, Math.max(n, o));
					is[0] += m;
					is[1] += n;
					is[2] += o;
					j++;
				}
			}
		}

		if (lv2 == null) {
			return class_1799.field_8037;
		} else {
			int kx = is[0] / j;
			int p = is[1] / j;
			int q = is[2] / j;
			float r = (float)i / (float)j;
			float f = (float)Math.max(kx, Math.max(p, q));
			kx = (int)((float)kx * r / f);
			p = (int)((float)p * r / f);
			q = (int)((float)q * r / f);
			int var25 = (kx << 8) + p;
			var25 = (var25 << 8) + q;
			lv2.method_7799(lv, var25);
			return lv;
		}
	}

	@Environment(EnvType.CLIENT)
	@Override
	public boolean method_8113(int i, int j) {
		return i * j >= 2;
	}

	@Override
	public class_1862<?> method_8119() {
		return class_1865.field_9028;
	}
}
