package net.minecraft;

import java.util.Random;
import javax.annotation.Nullable;

public abstract class class_2650 extends class_2647 {
	@Override
	public boolean method_11431(class_1936 arg, class_2338 arg2, class_2680 arg3, Random random) {
		for (int i = 0; i >= -1; i--) {
			for (int j = 0; j >= -1; j--) {
				if (method_11442(arg3, arg, arg2, i, j)) {
					return this.method_11444(arg, arg2, arg3, random, i, j);
				}
			}
		}

		return super.method_11431(arg, arg2, arg3, random);
	}

	@Nullable
	protected abstract class_2944<class_3111> method_11443(Random random);

	public boolean method_11444(class_1936 arg, class_2338 arg2, class_2680 arg3, Random random, int i, int j) {
		class_2944<class_3111> lv = this.method_11443(random);
		if (lv == null) {
			return false;
		} else {
			class_2680 lv2 = class_2246.field_10124.method_9564();
			arg.method_8652(arg2.method_10069(i, 0, j), lv2, 4);
			arg.method_8652(arg2.method_10069(i + 1, 0, j), lv2, 4);
			arg.method_8652(arg2.method_10069(i, 0, j + 1), lv2, 4);
			arg.method_8652(arg2.method_10069(i + 1, 0, j + 1), lv2, 4);
			if (lv.method_13151(arg, (class_2794<? extends class_2888>)arg.method_8398().method_12129(), random, arg2.method_10069(i, 0, j), class_3037.field_13603)) {
				return true;
			} else {
				arg.method_8652(arg2.method_10069(i, 0, j), arg3, 4);
				arg.method_8652(arg2.method_10069(i + 1, 0, j), arg3, 4);
				arg.method_8652(arg2.method_10069(i, 0, j + 1), arg3, 4);
				arg.method_8652(arg2.method_10069(i + 1, 0, j + 1), arg3, 4);
				return false;
			}
		}
	}

	public static boolean method_11442(class_2680 arg, class_1922 arg2, class_2338 arg3, int i, int j) {
		class_2248 lv = arg.method_11614();
		return lv == arg2.method_8320(arg3.method_10069(i, 0, j)).method_11614()
			&& lv == arg2.method_8320(arg3.method_10069(i + 1, 0, j)).method_11614()
			&& lv == arg2.method_8320(arg3.method_10069(i, 0, j + 1)).method_11614()
			&& lv == arg2.method_8320(arg3.method_10069(i + 1, 0, j + 1)).method_11614();
	}
}
