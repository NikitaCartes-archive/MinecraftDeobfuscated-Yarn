package net.minecraft;

import com.google.common.collect.Lists;
import com.mojang.datafixers.Dynamic;
import java.util.List;
import java.util.Random;
import java.util.function.Function;

public class class_3116 extends class_3195<class_3111> {
	private static final List<class_1959.class_1964> field_13716 = Lists.<class_1959.class_1964>newArrayList(
		new class_1959.class_1964(class_1299.field_6118, 1, 2, 4)
	);

	public class_3116(Function<Dynamic<?>, ? extends class_3111> function) {
		super(function);
	}

	@Override
	protected class_1923 method_14018(class_2794<?> arg, Random random, int i, int j, int k, int l) {
		int m = arg.method_12109().method_12553();
		int n = arg.method_12109().method_12556();
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
			for (class_1959 lv2 : arg.method_12098().method_8763(i * 16 + 9, j * 16 + 9, 16)) {
				if (!arg.method_12097(lv2, class_3031.field_13588)) {
					return false;
				}
			}

			for (class_1959 lv3 : arg.method_12098().method_8763(i * 16 + 9, j * 16 + 9, 29)) {
				if (lv3.method_8688() != class_1959.class_1961.field_9367 && lv3.method_8688() != class_1959.class_1961.field_9369) {
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
		return class_3116.class_3117::new;
	}

	@Override
	public String method_14019() {
		return "Monument";
	}

	@Override
	public int method_14021() {
		return 8;
	}

	@Override
	public List<class_1959.class_1964> method_13149() {
		return field_13716;
	}

	public static class class_3117 extends class_3449 {
		private boolean field_13717;

		public class_3117(class_3195<?> arg, int i, int j, class_1959 arg2, class_3341 arg3, int k, long l) {
			super(arg, i, j, arg2, arg3, k, l);
		}

		@Override
		public void method_16655(class_2794<?> arg, class_3485 arg2, int i, int j, class_1959 arg3) {
			this.method_16588(i, j);
		}

		private void method_16588(int i, int j) {
			int k = i * 16 - 29;
			int l = j * 16 - 29;
			class_2350 lv = class_2350.class_2353.field_11062.method_10183(this.field_16715);
			this.field_15325.add(new class_3366.class_3374(this.field_16715, k, l, lv));
			this.method_14969();
			this.field_13717 = true;
		}

		@Override
		public void method_14974(class_1936 arg, Random random, class_3341 arg2, class_1923 arg3) {
			if (!this.field_13717) {
				this.field_15325.clear();
				this.method_16588(this.method_14967(), this.method_14966());
			}

			super.method_14974(arg, random, arg2, arg3);
		}
	}
}
