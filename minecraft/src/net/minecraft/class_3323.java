package net.minecraft;

import com.mojang.datafixers.Dynamic;
import java.util.Random;
import java.util.function.Function;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class class_3323 extends class_3667<class_2997> {
	public class_3323(Function<Dynamic<?>, ? extends class_2997> function) {
		super(function);
	}

	public Stream<class_2338> method_15948(Random random, class_2997 arg, class_2338 arg2) {
		return IntStream.range(0, arg.field_13435).mapToObj(i -> {
			int j = random.nextInt(16);
			int k = random.nextInt(arg.field_13432 - arg.field_13433) + arg.field_13434;
			int l = random.nextInt(16);
			return arg2.method_10069(j, k, l);
		});
	}
}
