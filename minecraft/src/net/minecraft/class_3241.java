package net.minecraft;

import com.mojang.datafixers.Dynamic;
import java.util.Random;
import java.util.function.Function;
import java.util.stream.Stream;

public class class_3241 extends class_3667<class_3267> {
	public class_3241(Function<Dynamic<?>, ? extends class_3267> function) {
		super(function);
	}

	public Stream<class_2338> method_14347(Random random, class_3267 arg, class_2338 arg2) {
		return random.nextFloat() < 1.0F / (float)arg.field_14192 ? Stream.of(arg2) : Stream.empty();
	}
}
