package net.minecraft;

import com.mojang.datafixers.Dynamic;
import java.util.Random;
import java.util.function.Function;

public class class_3005 extends class_3031<class_3111> {
	private static final class_2715 field_13450 = class_2715.method_11758(class_2246.field_10102);
	private final class_2680 field_13452 = class_2246.field_10007.method_9564();
	private final class_2680 field_13451 = class_2246.field_9979.method_9564();
	private final class_2680 field_13449 = class_2246.field_10382.method_9564();

	public class_3005(Function<Dynamic<?>, ? extends class_3111> function) {
		super(function);
	}

	public boolean method_12977(class_1936 arg, class_2794<? extends class_2888> arg2, Random random, class_2338 arg3, class_3111 arg4) {
		arg3 = arg3.method_10084();

		while (arg.method_8623(arg3) && arg3.method_10264() > 2) {
			arg3 = arg3.method_10074();
		}

		if (!field_13450.method_11760(arg.method_8320(arg3))) {
			return false;
		} else {
			for (int i = -2; i <= 2; i++) {
				for (int j = -2; j <= 2; j++) {
					if (arg.method_8623(arg3.method_10069(i, -1, j)) && arg.method_8623(arg3.method_10069(i, -2, j))) {
						return false;
					}
				}
			}

			for (int i = -1; i <= 0; i++) {
				for (int jx = -2; jx <= 2; jx++) {
					for (int k = -2; k <= 2; k++) {
						arg.method_8652(arg3.method_10069(jx, i, k), this.field_13451, 2);
					}
				}
			}

			arg.method_8652(arg3, this.field_13449, 2);

			for (class_2350 lv : class_2350.class_2353.field_11062) {
				arg.method_8652(arg3.method_10093(lv), this.field_13449, 2);
			}

			for (int i = -2; i <= 2; i++) {
				for (int jx = -2; jx <= 2; jx++) {
					if (i == -2 || i == 2 || jx == -2 || jx == 2) {
						arg.method_8652(arg3.method_10069(i, 1, jx), this.field_13451, 2);
					}
				}
			}

			arg.method_8652(arg3.method_10069(2, 1, 0), this.field_13452, 2);
			arg.method_8652(arg3.method_10069(-2, 1, 0), this.field_13452, 2);
			arg.method_8652(arg3.method_10069(0, 1, 2), this.field_13452, 2);
			arg.method_8652(arg3.method_10069(0, 1, -2), this.field_13452, 2);

			for (int i = -1; i <= 1; i++) {
				for (int jxx = -1; jxx <= 1; jxx++) {
					if (i == 0 && jxx == 0) {
						arg.method_8652(arg3.method_10069(i, 4, jxx), this.field_13451, 2);
					} else {
						arg.method_8652(arg3.method_10069(i, 4, jxx), this.field_13452, 2);
					}
				}
			}

			for (int i = 1; i <= 3; i++) {
				arg.method_8652(arg3.method_10069(-1, i, -1), this.field_13451, 2);
				arg.method_8652(arg3.method_10069(-1, i, 1), this.field_13451, 2);
				arg.method_8652(arg3.method_10069(1, i, -1), this.field_13451, 2);
				arg.method_8652(arg3.method_10069(1, i, 1), this.field_13451, 2);
			}

			return true;
		}
	}
}
