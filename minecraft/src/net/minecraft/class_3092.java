package net.minecraft;

import com.mojang.datafixers.Dynamic;
import java.util.Random;
import java.util.Set;
import java.util.function.Function;

public class class_3092 extends class_3096<class_3111> {
	public class_3092(Function<Dynamic<?>, ? extends class_3111> function, boolean bl, int i, int j, class_2680 arg, class_2680 arg2) {
		super(function, bl, i, j, arg, arg2);
	}

	@Override
	public boolean method_12775(Set<class_2338> set, class_3747 arg, Random random, class_2338 arg2) {
		int i = this.method_13524(random);
		if (!this.method_13523(arg, arg2, i)) {
			return false;
		} else {
			this.method_13506(arg, arg2.method_10086(i), 2);

			for (int j = arg2.method_10264() + i - 2 - random.nextInt(4); j > arg2.method_10264() + i / 2; j -= 2 + random.nextInt(4)) {
				float f = random.nextFloat() * (float) (Math.PI * 2);
				int k = arg2.method_10263() + (int)(0.5F + class_3532.method_15362(f) * 4.0F);
				int l = arg2.method_10260() + (int)(0.5F + class_3532.method_15374(f) * 4.0F);

				for (int m = 0; m < 5; m++) {
					k = arg2.method_10263() + (int)(1.5F + class_3532.method_15362(f) * (float)m);
					l = arg2.method_10260() + (int)(1.5F + class_3532.method_15374(f) * (float)m);
					this.method_12773(set, arg, new class_2338(k, j - 3 + m / 2, l), this.field_13685);
				}

				int m = 1 + random.nextInt(2);
				int n = j;

				for (int o = j - m; o <= n; o++) {
					int p = o - n;
					this.method_13526(arg, new class_2338(k, o, l), 1 - p);
				}
			}

			for (int q = 0; q < i; q++) {
				class_2338 lv = arg2.method_10086(q);
				if (method_16432(arg, lv)) {
					this.method_12773(set, arg, lv, this.field_13685);
					if (q > 0) {
						this.method_13507(arg, random, lv.method_10067(), class_2541.field_11702);
						this.method_13507(arg, random, lv.method_10095(), class_2541.field_11699);
					}
				}

				if (q < i - 1) {
					class_2338 lv2 = lv.method_10078();
					if (method_16432(arg, lv2)) {
						this.method_12773(set, arg, lv2, this.field_13685);
						if (q > 0) {
							this.method_13507(arg, random, lv2.method_10078(), class_2541.field_11696);
							this.method_13507(arg, random, lv2.method_10095(), class_2541.field_11699);
						}
					}

					class_2338 lv3 = lv.method_10072().method_10078();
					if (method_16432(arg, lv3)) {
						this.method_12773(set, arg, lv3, this.field_13685);
						if (q > 0) {
							this.method_13507(arg, random, lv3.method_10078(), class_2541.field_11696);
							this.method_13507(arg, random, lv3.method_10072(), class_2541.field_11706);
						}
					}

					class_2338 lv4 = lv.method_10072();
					if (method_16432(arg, lv4)) {
						this.method_12773(set, arg, lv4, this.field_13685);
						if (q > 0) {
							this.method_13507(arg, random, lv4.method_10067(), class_2541.field_11702);
							this.method_13507(arg, random, lv4.method_10072(), class_2541.field_11706);
						}
					}
				}
			}

			return true;
		}
	}

	private void method_13507(class_3747 arg, Random random, class_2338 arg2, class_2746 arg3) {
		if (random.nextInt(3) > 0 && method_16424(arg, arg2)) {
			this.method_13153(arg, arg2, class_2246.field_10597.method_9564().method_11657(arg3, Boolean.valueOf(true)));
		}
	}

	private void method_13506(class_3747 arg, class_2338 arg2, int i) {
		int j = 2;

		for (int k = -2; k <= 0; k++) {
			this.method_13528(arg, arg2.method_10086(k), i + 1 - k);
		}
	}
}
