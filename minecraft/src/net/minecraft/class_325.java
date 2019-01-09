package net.minecraft;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class class_325 {
	private final class_2361<class_326> field_1996 = new class_2361<>(32);

	public static class_325 method_1706(class_324 arg) {
		class_325 lv = new class_325();
		lv.method_1708(
			(argx, i) -> i > 0 ? -1 : ((class_1768)argx.method_7909()).method_7800(argx),
			class_1802.field_8267,
			class_1802.field_8577,
			class_1802.field_8570,
			class_1802.field_8370
		);
		lv.method_1708((argx, i) -> class_1933.method_8377(0.5, 1.0), class_2246.field_10214, class_2246.field_10313);
		lv.method_1708((argx, i) -> {
			if (i != 1) {
				return -1;
			} else {
				class_2487 lvx = argx.method_7941("Explosion");
				int[] is = lvx != null && lvx.method_10573("Colors", 11) ? lvx.method_10561("Colors") : null;
				if (is == null) {
					return 9079434;
				} else if (is.length == 1) {
					return is[0];
				} else {
					int j = 0;
					int k = 0;
					int l = 0;

					for (int m : is) {
						j += (m & 0xFF0000) >> 16;
						k += (m & 0xFF00) >> 8;
						l += (m & 0xFF) >> 0;
					}

					j /= is.length;
					k /= is.length;
					l /= is.length;
					return j << 16 | k << 8 | l;
				}
			}
		}, class_1802.field_8450);
		lv.method_1708((argx, i) -> i > 0 ? -1 : class_1844.method_8064(argx), class_1802.field_8574, class_1802.field_8436, class_1802.field_8150);

		for (class_1826 lv2 : class_1826.method_8017()) {
			lv.method_1708((arg2, i) -> lv2.method_8016(i), lv2);
		}

		lv.method_1708(
			(arg2, i) -> {
				class_2680 lvx = ((class_1747)arg2.method_7909()).method_7711().method_9564();
				return arg.method_1697(lvx, null, null, i);
			},
			class_2246.field_10219,
			class_2246.field_10479,
			class_2246.field_10112,
			class_2246.field_10597,
			class_2246.field_10503,
			class_2246.field_9988,
			class_2246.field_10539,
			class_2246.field_10335,
			class_2246.field_10098,
			class_2246.field_10035,
			class_2246.field_10588
		);
		lv.method_1708((argx, i) -> i == 0 ? class_1844.method_8064(argx) : -1, class_1802.field_8087);
		lv.method_1708((argx, i) -> i == 0 ? -1 : class_1806.method_7999(argx), class_1802.field_8204);
		return lv;
	}

	public int method_1704(class_1799 arg, int i) {
		class_326 lv = this.field_1996.method_10200(class_2378.field_11142.method_10249(arg.method_7909()));
		return lv == null ? -1 : lv.getColor(arg, i);
	}

	public void method_1708(class_326 arg, class_1935... args) {
		for (class_1935 lv : args) {
			this.field_1996.method_10203(arg, class_1792.method_7880(lv.method_8389()));
		}
	}
}
