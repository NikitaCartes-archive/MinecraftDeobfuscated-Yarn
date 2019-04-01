package net.minecraft;

import com.mojang.datafixers.Dynamic;
import java.util.Random;
import java.util.function.Function;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class class_3282 extends class_3667<class_3113> {
	public class_3282(Function<Dynamic<?>, ? extends class_3113> function) {
		super(function);
	}

	public Stream<class_2338> method_15922(Random random, class_3113 arg, class_2338 arg2) {
		int i = 3 + random.nextInt(6);
		return IntStream.range(0, i).mapToObj(ix -> {
			int j = random.nextInt(16);
			int k = random.nextInt(28) + 4;
			int l = random.nextInt(16);
			return arg2.method_10069(j, k, l);
		});
	}
}
