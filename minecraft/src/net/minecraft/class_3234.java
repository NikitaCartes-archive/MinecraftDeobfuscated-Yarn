package net.minecraft;

import com.mojang.datafixers.Dynamic;
import java.util.BitSet;
import java.util.Random;
import java.util.function.Function;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class class_3234 extends class_3284<class_3269> {
	public class_3234(Function<Dynamic<?>, ? extends class_3269> function) {
		super(function);
	}

	public Stream<class_2338> method_14341(class_1936 arg, class_2794<? extends class_2888> arg2, Random random, class_3269 arg3, class_2338 arg4) {
		class_2791 lv = arg.method_16955(arg4);
		class_1923 lv2 = lv.method_12004();
		BitSet bitSet = lv.method_12025(arg3.field_14198);
		return IntStream.range(0, bitSet.length()).filter(i -> bitSet.get(i) && random.nextFloat() < arg3.field_14197).mapToObj(i -> {
			int j = i & 15;
			int k = i >> 4 & 15;
			int l = i >> 8;
			return new class_2338(lv2.method_8326() + j, l, lv2.method_8328() + k);
		});
	}
}
