package net.minecraft;

import com.mojang.datafixers.Dynamic;
import java.util.Random;
import java.util.function.Function;

public class class_3177 extends class_3031<class_3179> {
	public class_3177(Function<Dynamic<?>, ? extends class_3179> function) {
		super(function);
	}

	public boolean method_13953(class_1936 arg, class_2794<? extends class_2888> arg2, Random random, class_2338 arg3, class_3179 arg4) {
		int i = random.nextInt(arg4.field_13827.size());
		class_2975<?> lv = (class_2975<?>)arg4.field_13827.get(i);
		return lv.method_12862(arg, arg2, random, arg3);
	}
}
