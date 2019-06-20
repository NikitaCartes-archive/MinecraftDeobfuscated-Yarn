package net.minecraft;

import com.mojang.datafixers.Dynamic;
import java.util.Objects;
import java.util.Random;
import java.util.function.Function;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class class_3303 extends class_3284<class_3003> {
	public class_3303(Function<Dynamic<?>, ? extends class_3003> function) {
		super(function);
	}

	public Stream<class_2338> method_15936(class_1936 arg, class_2794<? extends class_2888> arg2, Random random, class_3003 arg3, class_2338 arg4) {
		double d = class_1959.field_9324.method_15437((double)arg4.method_10263() / 200.0, (double)arg4.method_10260() / 200.0);
		int i = d < arg3.field_13444 ? arg3.field_13446 : arg3.field_13445;
		return IntStream.range(0, i).mapToObj(ix -> {
			int j = random.nextInt(16);
			int k = random.nextInt(16);
			int l = arg.method_8598(class_2902.class_2903.field_13197, arg4.method_10069(j, 0, k)).method_10264() + 32;
			if (l <= 0) {
				return null;
			} else {
				int m = random.nextInt(l);
				return arg4.method_10069(j, m, k);
			}
		}).filter(Objects::nonNull);
	}
}
