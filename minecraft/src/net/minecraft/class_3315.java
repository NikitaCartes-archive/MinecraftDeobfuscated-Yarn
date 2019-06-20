package net.minecraft;

import com.mojang.datafixers.Dynamic;
import java.util.Random;
import java.util.function.Function;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class class_3315 extends class_3284<class_3113> {
	public class_3315(Function<Dynamic<?>, ? extends class_3113> function) {
		super(function);
	}

	public Stream<class_2338> method_14524(class_1936 arg, class_2794<? extends class_2888> arg2, Random random, class_3113 arg3, class_2338 arg4) {
		return IntStream.range(0, 16).mapToObj(i -> {
			int j = i / 4;
			int k = i % 4;
			int l = j * 4 + 1 + random.nextInt(3);
			int m = k * 4 + 1 + random.nextInt(3);
			return arg.method_8598(class_2902.class_2903.field_13197, arg4.method_10069(l, 0, m));
		});
	}
}
