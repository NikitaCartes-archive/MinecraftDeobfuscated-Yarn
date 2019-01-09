package net.minecraft;

import com.mojang.datafixers.Dynamic;
import java.util.Random;
import java.util.function.Function;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class class_3334 extends class_3284<class_3273> {
	public class_3334(Function<Dynamic<?>, ? extends class_3273> function) {
		super(function);
	}

	public Stream<class_2338> method_15951(class_1936 arg, class_2794<? extends class_2888> arg2, Random random, class_3273 arg3, class_2338 arg4) {
		int i = arg.method_8615() / 2 + 1;
		return IntStream.range(0, arg3.field_14204).mapToObj(j -> {
			int k = random.nextInt(16);
			int l = i - 5 + random.nextInt(10);
			int m = random.nextInt(16);
			return arg4.method_10069(k, l, m);
		});
	}
}
