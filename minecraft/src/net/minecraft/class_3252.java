package net.minecraft;

import com.mojang.datafixers.Dynamic;
import java.util.Random;
import java.util.function.Function;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class class_3252 extends class_3667<class_3277> {
	public class_3252(Function<Dynamic<?>, ? extends class_3277> function) {
		super(function);
	}

	public Stream<class_2338> method_15907(Random random, class_3277 arg, class_2338 arg2) {
		int i = arg.field_14214;
		int j = arg.field_14213;
		int k = arg.field_14212;
		return IntStream.range(0, i).mapToObj(kx -> {
			int l = random.nextInt(16);
			int m = random.nextInt(k) + random.nextInt(k) - k + j;
			int n = random.nextInt(16);
			return arg2.method_10069(l, m, n);
		});
	}
}
