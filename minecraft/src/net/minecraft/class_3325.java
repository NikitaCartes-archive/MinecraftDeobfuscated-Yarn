package net.minecraft;

import com.mojang.datafixers.Dynamic;
import java.util.Random;
import java.util.function.Function;
import java.util.stream.Stream;

public class class_3325 extends class_3667<class_2990> {
	public class_3325(Function<Dynamic<?>, ? extends class_2990> function) {
		super(function);
	}

	public Stream<class_2338> method_15944(Random random, class_2990 arg, class_2338 arg2) {
		if (random.nextFloat() < arg.field_13407) {
			int i = random.nextInt(16);
			int j = random.nextInt(arg.field_13408 - arg.field_13409) + arg.field_13410;
			int k = random.nextInt(16);
			return Stream.of(arg2.method_10069(i, j, k));
		} else {
			return Stream.empty();
		}
	}
}
