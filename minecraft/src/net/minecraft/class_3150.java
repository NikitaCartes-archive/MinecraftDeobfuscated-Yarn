package net.minecraft;

import com.mojang.datafixers.Dynamic;
import java.util.Random;
import java.util.function.Function;

public class class_3150 extends class_3031<class_3141> {
	public class_3150(Function<Dynamic<?>, ? extends class_3141> function) {
		super(function);
	}

	public boolean method_13798(class_1936 arg, class_2794<? extends class_2888> arg2, Random random, class_2338 arg3, class_3141 arg4) {
		for (class_3226<?> lv : arg4.field_13744) {
			if (random.nextFloat() < lv.field_14011) {
				return lv.method_14271(arg, arg2, random, arg3);
			}
		}

		return arg4.field_13745.method_12862(arg, arg2, random, arg3);
	}
}
