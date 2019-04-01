package net.minecraft;

import com.mojang.datafixers.Dynamic;
import java.util.Random;
import java.util.function.Function;

public class class_3070 extends class_3031<class_3111> {
	public class_3070(Function<Dynamic<?>, ? extends class_3111> function) {
		super(function);
	}

	public boolean method_13408(class_1936 arg, class_2794<? extends class_2888> arg2, Random random, class_2338 arg3, class_3111 arg4) {
		while (arg.method_8623(arg3) && arg3.method_10264() > 2) {
			arg3 = arg3.method_10074();
		}

		if (arg.method_8320(arg3).method_11614() != class_2246.field_10491) {
			return false;
		} else {
			arg3 = arg3.method_10086(random.nextInt(4));
			int i = random.nextInt(4) + 7;
			int j = i / 4 + random.nextInt(2);
			if (j > 1 && random.nextInt(60) == 0) {
				arg3 = arg3.method_10086(10 + random.nextInt(30));
			}

			for (int k = 0; k < i; k++) {
				float f = (1.0F - (float)k / (float)i) * (float)j;
				int l = class_3532.method_15386(f);

				for (int m = -l; m <= l; m++) {
					float g = (float)class_3532.method_15382(m) - 0.25F;

					for (int n = -l; n <= l; n++) {
						float h = (float)class_3532.method_15382(n) - 0.25F;
						if ((m == 0 && n == 0 || !(g * g + h * h > f * f)) && (m != -l && m != l && n != -l && n != l || !(random.nextFloat() > 0.75F))) {
							class_2680 lv = arg.method_8320(arg3.method_10069(m, k, n));
							class_2248 lv2 = lv.method_11614();
							if (lv.method_11588() || class_2248.method_9519(lv2) || lv2 == class_2246.field_10491 || lv2 == class_2246.field_10295) {
								this.method_13153(arg, arg3.method_10069(m, k, n), class_2246.field_10225.method_9564());
							}

							if (k != 0 && l > 1) {
								lv = arg.method_8320(arg3.method_10069(m, -k, n));
								lv2 = lv.method_11614();
								if (lv.method_11588() || class_2248.method_9519(lv2) || lv2 == class_2246.field_10491 || lv2 == class_2246.field_10295) {
									this.method_13153(arg, arg3.method_10069(m, -k, n), class_2246.field_10225.method_9564());
								}
							}
						}
					}
				}
			}

			int k = j - 1;
			if (k < 0) {
				k = 0;
			} else if (k > 1) {
				k = 1;
			}

			for (int o = -k; o <= k; o++) {
				for (int l = -k; l <= k; l++) {
					class_2338 lv3 = arg3.method_10069(o, -1, l);
					int p = 50;
					if (Math.abs(o) == 1 && Math.abs(l) == 1) {
						p = random.nextInt(5);
					}

					while (lv3.method_10264() > 50) {
						class_2680 lv4 = arg.method_8320(lv3);
						class_2248 lv5 = lv4.method_11614();
						if (!lv4.method_11588()
							&& !class_2248.method_9519(lv5)
							&& lv5 != class_2246.field_10491
							&& lv5 != class_2246.field_10295
							&& lv5 != class_2246.field_10225) {
							break;
						}

						this.method_13153(arg, lv3, class_2246.field_10225.method_9564());
						lv3 = lv3.method_10074();
						if (--p <= 0) {
							lv3 = lv3.method_10087(random.nextInt(5) + 1);
							p = random.nextInt(5);
						}
					}
				}
			}

			return true;
		}
	}
}
