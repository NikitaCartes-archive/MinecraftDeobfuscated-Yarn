package net.minecraft;

import com.mojang.datafixers.Dynamic;
import java.util.Random;
import java.util.function.Function;

public class class_3511 extends class_3523<class_3527> {
	public class_3511(Function<Dynamic<?>, ? extends class_3527> function) {
		super(function);
	}

	public void method_15220(
		Random random, class_2791 arg, class_1959 arg2, int i, int j, int k, double d, class_2680 arg3, class_2680 arg4, int l, long m, class_3527 arg5
	) {
		if (d > 1.75) {
			class_3523.field_15701.method_15305(random, arg, arg2, i, j, k, d, arg3, arg4, l, m, class_3523.field_15678);
		} else if (d > -0.95) {
			class_3523.field_15701.method_15305(random, arg, arg2, i, j, k, d, arg3, arg4, l, m, class_3523.field_15691);
		} else {
			class_3523.field_15701.method_15305(random, arg, arg2, i, j, k, d, arg3, arg4, l, m, class_3523.field_15677);
		}
	}
}
