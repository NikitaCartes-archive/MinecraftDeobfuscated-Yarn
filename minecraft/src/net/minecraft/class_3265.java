package net.minecraft;

import com.mojang.datafixers.Dynamic;
import java.util.Random;
import java.util.function.Function;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class class_3265 extends class_3284<class_3276> {
	public class_3265(Function<Dynamic<?>, ? extends class_3276> function) {
		super(function);
	}

	public Stream<class_2338> method_15919(class_1936 arg, class_2794<? extends class_2888> arg2, Random random, class_3276 arg3, class_2338 arg4) {
		int i = arg3.field_14211;
		if (random.nextFloat() < arg3.field_14209) {
			i += arg3.field_14210;
		}

		return IntStream.range(0, i).mapToObj(ix -> {
			int j = random.nextInt(16);
			int k = random.nextInt(16);
			return arg.method_8598(class_2902.class_2903.field_13197, arg4.method_10069(j, 0, k));
		});
	}
}
