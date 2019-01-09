package net.minecraft;

import com.mojang.datafixers.Dynamic;
import java.util.Random;
import java.util.function.Function;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class class_3328 extends class_3667<class_3273> {
	public class_3328(Function<Dynamic<?>, ? extends class_3273> function) {
		super(function);
	}

	public Stream<class_2338> method_15953(Random random, class_3273 arg, class_2338 arg2) {
		return IntStream.range(0, random.nextInt(random.nextInt(arg.field_14204) + 1)).mapToObj(i -> {
			int j = random.nextInt(16);
			int k = random.nextInt(120) + 4;
			int l = random.nextInt(16);
			return arg2.method_10069(j, k, l);
		});
	}
}
