package net.minecraft;

import com.mojang.datafixers.Dynamic;
import java.util.Random;
import java.util.function.Function;

public class class_3001 extends class_3038 {
	public class_3001(Function<Dynamic<?>, ? extends class_3111> function) {
		super(function);
	}

	@Override
	public class_2680 method_13175(Random random, class_2338 arg) {
		return random.nextFloat() > 0.6666667F ? class_2246.field_10182.method_9564() : class_2246.field_10449.method_9564();
	}
}
