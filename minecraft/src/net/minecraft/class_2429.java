package net.minecraft;

import com.google.common.collect.Maps;
import java.util.Map;

public class class_2429 extends class_2248 {
	private static final class_2350[] field_11334 = class_2350.values();
	public static final class_2746 field_11332 = class_2741.field_12489;
	public static final class_2746 field_11335 = class_2741.field_12487;
	public static final class_2746 field_11331 = class_2741.field_12540;
	public static final class_2746 field_11328 = class_2741.field_12527;
	public static final class_2746 field_11327 = class_2741.field_12519;
	public static final class_2746 field_11330 = class_2741.field_12546;
	public static final Map<class_2350, class_2746> field_11329 = class_156.method_654(Maps.newEnumMap(class_2350.class), enumMap -> {
		enumMap.put(class_2350.field_11043, field_11332);
		enumMap.put(class_2350.field_11034, field_11335);
		enumMap.put(class_2350.field_11035, field_11331);
		enumMap.put(class_2350.field_11039, field_11328);
		enumMap.put(class_2350.field_11036, field_11327);
		enumMap.put(class_2350.field_11033, field_11330);
	});
	protected final class_265[] field_11333;

	protected class_2429(float f, class_2248.class_2251 arg) {
		super(arg);
		this.field_11333 = this.method_10370(f);
	}

	private class_265[] method_10370(float f) {
		float g = 0.5F - f;
		float h = 0.5F + f;
		class_265 lv = class_2248.method_9541(
			(double)(g * 16.0F), (double)(g * 16.0F), (double)(g * 16.0F), (double)(h * 16.0F), (double)(h * 16.0F), (double)(h * 16.0F)
		);
		class_265[] lvs = new class_265[field_11334.length];

		for (int i = 0; i < field_11334.length; i++) {
			class_2350 lv2 = field_11334[i];
			lvs[i] = class_259.method_1081(
				0.5 + Math.min((double)(-f), (double)lv2.method_10148() * 0.5),
				0.5 + Math.min((double)(-f), (double)lv2.method_10164() * 0.5),
				0.5 + Math.min((double)(-f), (double)lv2.method_10165() * 0.5),
				0.5 + Math.max((double)f, (double)lv2.method_10148() * 0.5),
				0.5 + Math.max((double)f, (double)lv2.method_10164() * 0.5),
				0.5 + Math.max((double)f, (double)lv2.method_10165() * 0.5)
			);
		}

		class_265[] lvs2 = new class_265[64];

		for (int j = 0; j < 64; j++) {
			class_265 lv3 = lv;

			for (int k = 0; k < field_11334.length; k++) {
				if ((j & 1 << k) != 0) {
					lv3 = class_259.method_1084(lv3, lvs[k]);
				}
			}

			lvs2[j] = lv3;
		}

		return lvs2;
	}

	@Override
	public boolean method_9579(class_2680 arg, class_1922 arg2, class_2338 arg3) {
		return false;
	}

	@Override
	public class_265 method_9530(class_2680 arg, class_1922 arg2, class_2338 arg3, class_3726 arg4) {
		return this.field_11333[this.method_10368(arg)];
	}

	protected int method_10368(class_2680 arg) {
		int i = 0;

		for (int j = 0; j < field_11334.length; j++) {
			if ((Boolean)arg.method_11654((class_2769)field_11329.get(field_11334[j]))) {
				i |= 1 << j;
			}
		}

		return i;
	}
}
