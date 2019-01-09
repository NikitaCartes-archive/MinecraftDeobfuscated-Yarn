package net.minecraft;

import com.mojang.datafixers.Dynamic;
import java.util.Random;
import java.util.function.Function;

public class class_3173 extends class_3031<class_3175> {
	public class_3173(Function<Dynamic<?>, ? extends class_3175> function) {
		super(function);
	}

	public boolean method_13929(class_1936 arg, class_2794<? extends class_2888> arg2, Random random, class_2338 arg3, class_3175 arg4) {
		if (arg4.field_13808.contains(arg.method_8320(arg3.method_10074()))
			&& arg4.field_13805.contains(arg.method_8320(arg3))
			&& arg4.field_13806.contains(arg.method_8320(arg3.method_10084()))) {
			arg.method_8652(arg3, arg4.field_13807, 2);
			return true;
		} else {
			return false;
		}
	}
}
