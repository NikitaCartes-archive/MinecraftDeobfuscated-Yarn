package net.minecraft;

import com.mojang.datafixers.Dynamic;
import java.util.Random;
import java.util.function.Function;
import java.util.stream.Stream;

public class class_3237 extends class_3284<class_3267> {
	public class_3237(Function<Dynamic<?>, ? extends class_3267> function) {
		super(function);
	}

	public Stream<class_2338> method_14343(class_1936 arg, class_2794<? extends class_2888> arg2, Random random, class_3267 arg3, class_2338 arg4) {
		if (random.nextFloat() < 1.0F / (float)arg3.field_14192) {
			int i = random.nextInt(16);
			int j = random.nextInt(16);
			class_2338 lv = arg.method_8598(class_2902.class_2903.field_13197, arg4.method_10069(i, 0, j));
			return Stream.of(lv);
		} else {
			return Stream.empty();
		}
	}
}
