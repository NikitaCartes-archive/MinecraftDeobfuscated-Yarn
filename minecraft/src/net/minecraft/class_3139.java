package net.minecraft;

import com.mojang.datafixers.Dynamic;
import java.util.Random;
import java.util.function.Function;

public class class_3139 extends class_3031<class_3147> {
	public class_3139(Function<Dynamic<?>, ? extends class_3147> function) {
		super(function);
	}

	public boolean method_13696(class_1936 arg, class_2794<? extends class_2888> arg2, Random random, class_2338 arg3, class_3147 arg4) {
		int i = random.nextInt(5) - 3 + arg4.field_13762;

		for (int j = 0; j < i; j++) {
			int k = random.nextInt(arg4.field_13761.size());
			class_2975<?> lv = (class_2975<?>)arg4.field_13761.get(k);
			lv.method_12862(arg, arg2, random, arg3);
		}

		return true;
	}
}
