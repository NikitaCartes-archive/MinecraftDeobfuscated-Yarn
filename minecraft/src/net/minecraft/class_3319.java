package net.minecraft;

import com.mojang.datafixers.Dynamic;
import java.util.Random;
import java.util.function.Function;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class class_3319 extends class_3284<class_3278> {
	public class_3319(Function<Dynamic<?>, ? extends class_3278> function) {
		super(function);
	}

	public Stream<class_2338> method_15945(class_1936 arg, class_2794<? extends class_2888> arg2, Random random, class_3278 arg3, class_2338 arg4) {
		int i = random.nextInt(arg3.field_14215 - arg3.field_14216) + arg3.field_14216;
		return IntStream.range(0, i).mapToObj(ix -> {
			int j = random.nextInt(16);
			int k = random.nextInt(16);
			int l = arg.method_8589(class_2902.class_2903.field_13195, arg4.method_10263() + j, arg4.method_10260() + k);
			return new class_2338(arg4.method_10263() + j, l, arg4.method_10260() + k);
		});
	}
}
