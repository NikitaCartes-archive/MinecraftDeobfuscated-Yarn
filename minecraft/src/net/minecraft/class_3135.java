package net.minecraft;

import com.mojang.datafixers.Dynamic;
import java.util.Random;
import java.util.function.Function;

public class class_3135 extends class_3031<class_3137> {
	public class_3135(Function<Dynamic<?>, ? extends class_3137> function) {
		super(function);
	}

	public boolean method_13679(class_1936 arg, class_2794<? extends class_2888> arg2, Random random, class_2338 arg3, class_3137 arg4) {
		boolean bl = random.nextBoolean();
		return bl ? arg4.field_13740.method_12862(arg, arg2, random, arg3) : arg4.field_13739.method_12862(arg, arg2, random, arg3);
	}
}
