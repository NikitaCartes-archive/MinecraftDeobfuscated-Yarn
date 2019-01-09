package net.minecraft;

import com.mojang.datafixers.Dynamic;
import java.util.Objects;
import java.util.Random;
import java.util.function.Function;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class class_3245 extends class_3284<class_3113> {
	public class_3245(Function<Dynamic<?>, ? extends class_3113> function) {
		super(function);
	}

	public Stream<class_2338> method_14373(class_1936 arg, class_2794<? extends class_2888> arg2, Random random, class_3113 arg3, class_2338 arg4) {
		int i = random.nextInt(5);
		return IntStream.range(0, i).mapToObj(ix -> {
			int j = random.nextInt(16);
			int k = random.nextInt(16);
			int l = arg.method_8598(class_2902.class_2903.field_13197, arg4.method_10069(j, 0, k)).method_10264();
			if (l > 0) {
				int m = l - 1;
				return new class_2338(arg4.method_10263() + j, m, arg4.method_10260() + k);
			} else {
				return null;
			}
		}).filter(Objects::nonNull);
	}
}
