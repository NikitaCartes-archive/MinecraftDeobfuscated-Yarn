package net.minecraft;

import com.mojang.datafixers.Dynamic;
import java.util.Random;
import java.util.function.Function;

public class class_3211 extends class_3195<class_3812> {
	public class_3211(Function<Dynamic<?>, ? extends class_3812> function) {
		super(function);
	}

	@Override
	protected class_1923 method_14018(class_2794<?> arg, Random random, int i, int j, int k, int l) {
		int m = arg.method_12109().method_12558();
		int n = arg.method_12109().method_12559();
		int o = i + m * k;
		int p = j + m * l;
		int q = o < 0 ? o - m + 1 : o;
		int r = p < 0 ? p - m + 1 : p;
		int s = q / m;
		int t = r / m;
		((class_2919)random).method_12665(arg.method_12101(), s, t, 10387312);
		s *= m;
		t *= m;
		s += random.nextInt(m - n);
		t += random.nextInt(m - n);
		return new class_1923(s, t);
	}

	@Override
	public boolean method_14026(class_2794<?> arg, Random random, int i, int j) {
		class_1923 lv = this.method_14018(arg, random, i, j, 0, 0);
		if (i == lv.field_9181 && j == lv.field_9180) {
			class_1959 lv2 = arg.method_12098().method_8758(new class_2338((i << 4) + 9, 0, (j << 4) + 9));
			return arg.method_12097(lv2, class_3031.field_13587);
		} else {
			return false;
		}
	}

	@Override
	public class_3195.class_3774 method_14016() {
		return class_3211.class_3212::new;
	}

	@Override
	public String method_14019() {
		return "Village";
	}

	@Override
	public int method_14021() {
		return 8;
	}

	public static class class_3212 extends class_3449 {
		public class_3212(class_3195<?> arg, int i, int j, class_1959 arg2, class_3341 arg3, int k, long l) {
			super(arg, i, j, arg2, arg3, k, l);
		}

		@Override
		public void method_16655(class_2794<?> arg, class_3485 arg2, int i, int j, class_1959 arg3) {
			class_3812 lv = arg.method_12105(arg3, class_3031.field_13587);
			class_2338 lv2 = new class_2338(i * 16, 0, j * 16);
			class_3813.method_16753(arg, arg2, lv2, this.field_15325, this.field_16715, lv);
			this.method_14969();
		}
	}
}
