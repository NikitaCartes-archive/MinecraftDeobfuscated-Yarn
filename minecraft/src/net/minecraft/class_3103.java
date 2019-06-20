package net.minecraft;

import com.mojang.datafixers.Dynamic;
import java.util.Random;
import java.util.function.Function;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class class_3103 extends class_3031<class_3111> {
	private static final Logger field_13700 = LogManager.getLogger();
	private static final class_1299<?>[] field_13699 = new class_1299[]{
		class_1299.field_6137, class_1299.field_6051, class_1299.field_6051, class_1299.field_6079
	};
	private static final class_2680 field_13698 = class_2246.field_10543.method_9564();

	public class_3103(Function<Dynamic<?>, ? extends class_3111> function) {
		super(function);
	}

	public boolean method_13548(class_1936 arg, class_2794<? extends class_2888> arg2, Random random, class_2338 arg3, class_3111 arg4) {
		int i = 3;
		int j = random.nextInt(2) + 2;
		int k = -j - 1;
		int l = j + 1;
		int m = -1;
		int n = 4;
		int o = random.nextInt(2) + 2;
		int p = -o - 1;
		int q = o + 1;
		int r = 0;

		for (int s = k; s <= l; s++) {
			for (int t = -1; t <= 4; t++) {
				for (int u = p; u <= q; u++) {
					class_2338 lv = arg3.method_10069(s, t, u);
					class_3614 lv2 = arg.method_8320(lv).method_11620();
					boolean bl = lv2.method_15799();
					if (t == -1 && !bl) {
						return false;
					}

					if (t == 4 && !bl) {
						return false;
					}

					if ((s == k || s == l || u == p || u == q) && t == 0 && arg.method_8623(lv) && arg.method_8623(lv.method_10084())) {
						r++;
					}
				}
			}
		}

		if (r >= 1 && r <= 5) {
			for (int s = k; s <= l; s++) {
				for (int t = 3; t >= -1; t--) {
					for (int u = p; u <= q; u++) {
						class_2338 lvx = arg3.method_10069(s, t, u);
						if (s != k && t != -1 && u != p && s != l && t != 4 && u != q) {
							if (arg.method_8320(lvx).method_11614() != class_2246.field_10034) {
								arg.method_8652(lvx, field_13698, 2);
							}
						} else if (lvx.method_10264() >= 0 && !arg.method_8320(lvx.method_10074()).method_11620().method_15799()) {
							arg.method_8652(lvx, field_13698, 2);
						} else if (arg.method_8320(lvx).method_11620().method_15799() && arg.method_8320(lvx).method_11614() != class_2246.field_10034) {
							if (t == -1 && random.nextInt(4) != 0) {
								arg.method_8652(lvx, class_2246.field_9989.method_9564(), 2);
							} else {
								arg.method_8652(lvx, class_2246.field_10445.method_9564(), 2);
							}
						}
					}
				}
			}

			for (int s = 0; s < 2; s++) {
				for (int t = 0; t < 3; t++) {
					int ux = arg3.method_10263() + random.nextInt(j * 2 + 1) - j;
					int v = arg3.method_10264();
					int w = arg3.method_10260() + random.nextInt(o * 2 + 1) - o;
					class_2338 lv3 = new class_2338(ux, v, w);
					if (arg.method_8623(lv3)) {
						int x = 0;

						for (class_2350 lv4 : class_2350.class_2353.field_11062) {
							if (arg.method_8320(lv3.method_10093(lv4)).method_11620().method_15799()) {
								x++;
							}
						}

						if (x == 1) {
							arg.method_8652(lv3, class_3443.method_14916(arg, lv3, class_2246.field_10034.method_9564()), 2);
							class_2621.method_11287(arg, random, lv3, class_39.field_356);
							break;
						}
					}
				}
			}

			arg.method_8652(arg3, class_2246.field_10260.method_9564(), 2);
			class_2586 lv5 = arg.method_8321(arg3);
			if (lv5 instanceof class_2636) {
				((class_2636)lv5).method_11390().method_8274(this.method_13547(random));
			} else {
				field_13700.error("Failed to fetch mob spawner entity at ({}, {}, {})", arg3.method_10263(), arg3.method_10264(), arg3.method_10260());
			}

			return true;
		} else {
			return false;
		}
	}

	private class_1299<?> method_13547(Random random) {
		return field_13699[random.nextInt(field_13699.length)];
	}
}
