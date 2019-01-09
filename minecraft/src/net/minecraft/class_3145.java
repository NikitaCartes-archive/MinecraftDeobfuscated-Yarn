package net.minecraft;

import com.mojang.datafixers.Dynamic;
import java.util.Random;
import java.util.function.Function;

public abstract class class_3145<C extends class_3037> extends class_3195<C> {
	public class_3145(Function<Dynamic<?>, ? extends C> function) {
		super(function);
	}

	@Override
	protected class_1923 method_14018(class_2794<?> arg, Random random, int i, int j, int k, int l) {
		int m = this.method_13773(arg);
		int n = this.method_13775(arg);
		int o = i + m * k;
		int p = j + m * l;
		int q = o < 0 ? o - m + 1 : o;
		int r = p < 0 ? p - m + 1 : p;
		int s = q / m;
		int t = r / m;
		((class_2919)random).method_12665(arg.method_12101(), s, t, this.method_13774());
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
			class_1959 lv2 = arg.method_12098().method_8758(new class_2338(i * 16 + 9, 0, j * 16 + 9));
			if (arg.method_12097(lv2, this)) {
				return true;
			}
		}

		return false;
	}

	protected int method_13773(class_2794<?> arg) {
		return arg.method_12109().method_12567();
	}

	protected int method_13775(class_2794<?> arg) {
		return arg.method_12109().method_12568();
	}

	protected abstract int method_13774();
}
