package net.minecraft;

import com.mojang.datafixers.Dynamic;
import java.util.Random;
import java.util.function.Function;

public class class_3021 extends class_3195<class_3111> {
	public class_3021(Function<Dynamic<?>, ? extends class_3111> function) {
		super(function);
	}

	@Override
	protected class_1923 method_14018(class_2794<?> arg, Random random, int i, int j, int k, int l) {
		int m = arg.method_12109().method_12554();
		int n = arg.method_12109().method_12557();
		int o = i + m * k;
		int p = j + m * l;
		int q = o < 0 ? o - m + 1 : o;
		int r = p < 0 ? p - m + 1 : p;
		int s = q / m;
		int t = r / m;
		((class_2919)random).method_12665(arg.method_12101(), s, t, 10387313);
		s *= m;
		t *= m;
		s += (random.nextInt(m - n) + random.nextInt(m - n)) / 2;
		t += (random.nextInt(m - n) + random.nextInt(m - n)) / 2;
		return new class_1923(s, t);
	}

	@Override
	public boolean method_14026(class_2794<?> arg, Random random, int i, int j) {
		class_1923 lv = this.method_14018(arg, random, i, j, 0, 0);
		if (i == lv.field_9181 && j == lv.field_9180) {
			class_1959 lv2 = arg.method_12098().method_8758(new class_2338((i << 4) + 9, 0, (j << 4) + 9));
			if (!arg.method_12097(lv2, class_3031.field_13553)) {
				return false;
			} else {
				int k = method_13085(i, j, arg);
				return k >= 60;
			}
		} else {
			return false;
		}
	}

	@Override
	public class_3195.class_3774 method_14016() {
		return class_3021.class_3022::new;
	}

	@Override
	public String method_14019() {
		return "EndCity";
	}

	@Override
	public int method_14021() {
		return 8;
	}

	private static int method_13085(int i, int j, class_2794<?> arg) {
		Random random = new Random((long)(i + j * 10387313));
		class_2470 lv = class_2470.values()[random.nextInt(class_2470.values().length)];
		int k = 5;
		int l = 5;
		if (lv == class_2470.field_11463) {
			k = -5;
		} else if (lv == class_2470.field_11464) {
			k = -5;
			l = -5;
		} else if (lv == class_2470.field_11465) {
			l = -5;
		}

		int m = arg.method_16397(i + 7, j + 7, class_2902.class_2903.field_13194);
		int n = arg.method_16397(i + 7, j + 7 + l, class_2902.class_2903.field_13194);
		int o = arg.method_16397(i + 7 + k, j + 7, class_2902.class_2903.field_13194);
		int p = arg.method_16397(i + 7 + k, j + 7 + l, class_2902.class_2903.field_13194);
		return Math.min(Math.min(m, n), Math.min(o, p));
	}

	public static class class_3022 extends class_3449 {
		public class_3022(class_3195<?> arg, int i, int j, class_1959 arg2, class_3341 arg3, int k, long l) {
			super(arg, i, j, arg2, arg3, k, l);
		}

		@Override
		public void method_16655(class_2794<?> arg, class_3485 arg2, int i, int j, class_1959 arg3) {
			class_2470 lv = class_2470.values()[this.field_16715.nextInt(class_2470.values().length)];
			int k = class_3021.method_13085(i, j, arg);
			if (k >= 60) {
				class_2338 lv2 = new class_2338(i * 16 + 8, k, j * 16 + 8);
				class_3342.method_14679(arg2, lv2, lv, this.field_15325, this.field_16715);
				this.method_14969();
			}
		}
	}
}
