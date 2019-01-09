package net.minecraft;

import com.mojang.datafixers.Dynamic;
import java.util.Random;
import java.util.function.Function;

public class class_3026 extends class_3031<class_3111> {
	public class_3026(Function<Dynamic<?>, ? extends class_3111> function) {
		super(function);
	}

	public boolean method_13110(class_1936 arg, class_2794<? extends class_2888> arg2, Random random, class_2338 arg3, class_3111 arg4) {
		float f = (float)(random.nextInt(3) + 4);

		for (int i = 0; f > 0.5F; i--) {
			for (int j = class_3532.method_15375(-f); j <= class_3532.method_15386(f); j++) {
				for (int k = class_3532.method_15375(-f); k <= class_3532.method_15386(f); k++) {
					if ((float)(j * j + k * k) <= (f + 1.0F) * (f + 1.0F)) {
						this.method_13153(arg, arg3.method_10069(j, i, k), class_2246.field_10471.method_9564());
					}
				}
			}

			f = (float)((double)f - ((double)random.nextInt(2) + 0.5));
		}

		return true;
	}
}
