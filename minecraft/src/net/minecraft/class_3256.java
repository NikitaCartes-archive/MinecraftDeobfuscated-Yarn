package net.minecraft;

import com.mojang.datafixers.Dynamic;
import java.util.Objects;
import java.util.Random;
import java.util.function.Function;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class class_3256 extends class_3284<class_3273> {
	public class_3256(Function<Dynamic<?>, ? extends class_3273> function) {
		super(function);
	}

	public Stream<class_2338> method_15905(class_1936 arg, class_2794<? extends class_2888> arg2, Random random, class_3273 arg3, class_2338 arg4) {
		return IntStream.range(0, arg3.field_14204).mapToObj(i -> {
			int j = random.nextInt(16);
			int k = random.nextInt(16);
			int l = arg.method_8598(class_2902.class_2903.field_13197, arg4.method_10069(j, 0, k)).method_10264() * 2;
			if (l <= 0) {
				return null;
			} else {
				int m = random.nextInt(l);
				return arg4.method_10069(j, m, k);
			}
		}).filter(Objects::nonNull);
	}
}
