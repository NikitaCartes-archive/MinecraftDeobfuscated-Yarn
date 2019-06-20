package net.minecraft;

import com.mojang.datafixers.Dynamic;
import java.util.Random;
import java.util.function.Function;
import java.util.stream.Stream;

public class class_3301 extends class_3284<class_3297> {
	public class_3301(Function<Dynamic<?>, ? extends class_3297> function) {
		super(function);
	}

	public Stream<class_2338> method_15930(class_1936 arg, class_2794<? extends class_2888> arg2, Random random, class_3297 arg3, class_2338 arg4) {
		if (random.nextInt(arg3.field_14289) == 0) {
			int i = random.nextInt(16);
			int j = random.nextInt(arg2.method_12104());
			int k = random.nextInt(16);
			return Stream.of(arg4.method_10069(i, j, k));
		} else {
			return Stream.empty();
		}
	}
}
