package net.minecraft;

import com.mojang.datafixers.Dynamic;
import java.util.Random;
import java.util.function.Function;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class class_3316 extends class_3284<class_3275> {
	public class_3316(Function<Dynamic<?>, ? extends class_3275> function) {
		super(function);
	}

	public Stream<class_2338> method_15943(class_1936 arg, class_2794<? extends class_2888> arg2, Random random, class_3275 arg3, class_2338 arg4) {
		double d = class_1959.field_9324.method_15437((double)arg4.method_10263() / arg3.field_14206, (double)arg4.method_10260() / arg3.field_14206);
		int i = (int)Math.ceil((d + arg3.field_14205) * (double)arg3.field_14208);
		return IntStream.range(0, i).mapToObj(ix -> {
			int j = random.nextInt(16);
			int k = random.nextInt(16);
			int l = arg.method_8589(arg3.field_14207, arg4.method_10263() + j, arg4.method_10260() + k);
			return new class_2338(arg4.method_10263() + j, l, arg4.method_10260() + k);
		});
	}
}
