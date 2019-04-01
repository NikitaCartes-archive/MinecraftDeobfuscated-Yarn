package net.minecraft;

import com.mojang.datafixers.Dynamic;
import java.util.Random;
import java.util.function.Function;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class class_3305 extends class_3284<class_3299> {
	public class_3305(Function<Dynamic<?>, ? extends class_3299> function) {
		super(function);
	}

	public Stream<class_2338> method_15933(class_1936 arg, class_2794<? extends class_2888> arg2, Random random, class_3299 arg3, class_2338 arg4) {
		int i = arg3.field_14290;
		return IntStream.range(0, i).mapToObj(ix -> {
			int j = random.nextInt(16);
			int k = random.nextInt(arg2.method_12104());
			int l = random.nextInt(16);
			return arg4.method_10069(j, k, l);
		});
	}
}
