package net.minecraft;

import com.mojang.datafixers.Dynamic;
import java.util.Random;
import java.util.function.Function;
import java.util.stream.Stream;

public class class_3293 extends class_3284<class_3297> {
	public class_3293(Function<Dynamic<?>, ? extends class_3297> function) {
		super(function);
	}

	public Stream<class_2338> method_15931(class_1936 arg, class_2794<? extends class_2888> arg2, Random random, class_3297 arg3, class_2338 arg4) {
		if (random.nextInt(arg3.field_14289 / 10) == 0) {
			int i = random.nextInt(16);
			int j = random.nextInt(random.nextInt(arg2.method_12104() - 8) + 8);
			int k = random.nextInt(16);
			if (j < arg.method_8615() || random.nextInt(arg3.field_14289 / 8) == 0) {
				return Stream.of(arg4.method_10069(i, j, k));
			}
		}

		return Stream.empty();
	}
}
