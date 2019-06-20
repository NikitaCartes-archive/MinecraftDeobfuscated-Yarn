package net.minecraft;

import com.google.common.collect.Lists;
import com.mojang.datafixers.Dynamic;
import java.util.List;
import java.util.Random;
import java.util.function.Function;
import java.util.stream.Stream;

public class class_3329 extends class_3667<class_3273> {
	public class_3329(Function<Dynamic<?>, ? extends class_3273> function) {
		super(function);
	}

	public Stream<class_2338> method_15947(Random random, class_3273 arg, class_2338 arg2) {
		List<class_2338> list = Lists.<class_2338>newArrayList();

		for (int i = 0; i < random.nextInt(random.nextInt(arg.field_14204) + 1) + 1; i++) {
			int j = random.nextInt(16);
			int k = random.nextInt(120) + 4;
			int l = random.nextInt(16);
			list.add(arg2.method_10069(j, k, l));
		}

		return list.stream();
	}
}
