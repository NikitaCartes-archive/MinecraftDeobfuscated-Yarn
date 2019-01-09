package net.minecraft;

import com.mojang.datafixers.Dynamic;
import java.util.Random;
import java.util.function.Function;

public abstract class class_3805 extends class_3031<class_3111> {
	public class_3805(Function<Dynamic<?>, ? extends class_3111> function) {
		super(function);
	}

	public boolean method_16709(class_1936 arg, class_2794<? extends class_2888> arg2, Random random, class_2338 arg3, class_3111 arg4) {
		if (arg3.method_10264() < 5) {
			return false;
		} else {
			int i = 2 + random.nextInt(2);
			int j = 2 + random.nextInt(2);

			for (class_2338 lv : class_2338.method_10097(arg3.method_10069(-i, 0, -j), arg3.method_10069(i, 1, j))) {
				int k = arg3.method_10263() - lv.method_10263();
				int l = arg3.method_10260() - lv.method_10260();
				if ((float)(k * k + l * l) <= random.nextFloat() * 10.0F - random.nextFloat() * 6.0F) {
					this.method_16708(arg, lv);
				} else if ((double)random.nextFloat() < 0.031) {
					this.method_16708(arg, lv);
				}
			}

			return true;
		}
	}

	private boolean method_16707(class_1936 arg, class_2338 arg2) {
		class_2338 lv = arg2.method_10074();
		class_2680 lv2 = arg.method_8320(lv);
		return class_2248.method_9501(lv2.method_11628(arg, lv), class_2350.field_11036) || lv2.method_11614() == class_2246.field_10194;
	}

	private void method_16708(class_1936 arg, class_2338 arg2) {
		if (arg.method_8623(arg2) && this.method_16707(arg, arg2)) {
			arg.method_8652(arg2, this.method_16843(arg), 4);
		}
	}

	protected abstract class_2680 method_16843(class_1936 arg);
}
