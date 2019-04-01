package net.minecraft;

import com.mojang.datafixers.Dynamic;
import java.util.Random;
import java.util.function.Function;

public class class_2964 extends class_3031<class_3111> {
	public class_2964(Function<Dynamic<?>, ? extends class_3111> function) {
		super(function);
	}

	public boolean method_12843(class_1936 arg, class_2794<? extends class_2888> arg2, Random random, class_2338 arg3, class_3111 arg4) {
		if (arg.method_8623(arg3.method_10084()) && arg.method_8320(arg3).method_11614() == class_2246.field_10471) {
			class_2279.method_9744(arg, arg3.method_10084(), random, 8);
			return true;
		} else {
			return false;
		}
	}
}
