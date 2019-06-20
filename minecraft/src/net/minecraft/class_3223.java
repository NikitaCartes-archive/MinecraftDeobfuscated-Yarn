package net.minecraft;

import com.google.common.collect.Lists;
import com.mojang.datafixers.Dynamic;
import java.util.List;
import java.util.Random;
import java.util.function.Function;

public class class_3223 extends class_3195<class_3111> {
	public class_3223(Function<Dynamic<?>, ? extends class_3111> function) {
		super(function);
	}

	@Override
	protected class_1923 method_14018(class_2794<?> arg, Random random, int i, int j, int k, int l) {
		int m = arg.method_12109().method_12560();
		int n = arg.method_12109().method_12552();
		int o = i + m * k;
		int p = j + m * l;
		int q = o < 0 ? o - m + 1 : o;
		int r = p < 0 ? p - m + 1 : p;
		int s = q / m;
		int t = r / m;
		((class_2919)random).method_12665(arg.method_12101(), s, t, 10387319);
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
			for (class_1959 lv2 : arg.method_12098().method_8763(i * 16 + 9, j * 16 + 9, 32)) {
				if (!arg.method_12097(lv2, class_3031.field_13528)) {
					return false;
				}
			}

			return true;
		} else {
			return false;
		}
	}

	@Override
	public class_3195.class_3774 method_14016() {
		return class_3223.class_3224::new;
	}

	@Override
	public String method_14019() {
		return "Mansion";
	}

	@Override
	public int method_14021() {
		return 8;
	}

	public static class class_3224 extends class_3449 {
		public class_3224(class_3195<?> arg, int i, int j, class_1959 arg2, class_3341 arg3, int k, long l) {
			super(arg, i, j, arg2, arg3, k, l);
		}

		@Override
		public void method_16655(class_2794<?> arg, class_3485 arg2, int i, int j, class_1959 arg3) {
			class_2470 lv = class_2470.values()[this.field_16715.nextInt(class_2470.values().length)];
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

			int m = (i << 4) + 7;
			int n = (j << 4) + 7;
			int o = arg.method_18028(m, n, class_2902.class_2903.field_13194);
			int p = arg.method_18028(m, n + l, class_2902.class_2903.field_13194);
			int q = arg.method_18028(m + k, n, class_2902.class_2903.field_13194);
			int r = arg.method_18028(m + k, n + l, class_2902.class_2903.field_13194);
			int s = Math.min(Math.min(o, p), Math.min(q, r));
			if (s >= 60) {
				class_2338 lv2 = new class_2338(i * 16 + 8, s + 1, j * 16 + 8);
				List<class_3471.class_3480> list = Lists.<class_3471.class_3480>newLinkedList();
				class_3471.method_15029(arg2, lv2, lv, list, this.field_16715);
				this.field_15325.addAll(list);
				this.method_14969();
			}
		}

		@Override
		public void method_14974(class_1936 arg, Random random, class_3341 arg2, class_1923 arg3) {
			super.method_14974(arg, random, arg2, arg3);
			int i = this.field_15330.field_14380;

			for (int j = arg2.field_14381; j <= arg2.field_14378; j++) {
				for (int k = arg2.field_14379; k <= arg2.field_14376; k++) {
					class_2338 lv = new class_2338(j, i, k);
					if (!arg.method_8623(lv) && this.field_15330.method_14662(lv)) {
						boolean bl = false;

						for (class_3443 lv2 : this.field_15325) {
							if (lv2.method_14935().method_14662(lv)) {
								bl = true;
								break;
							}
						}

						if (bl) {
							for (int l = i - 1; l > 1; l--) {
								class_2338 lv3 = new class_2338(j, l, k);
								if (!arg.method_8623(lv3) && !arg.method_8320(lv3).method_11620().method_15797()) {
									break;
								}

								arg.method_8652(lv3, class_2246.field_10445.method_9564(), 2);
							}
						}
					}
				}
			}
		}
	}
}
