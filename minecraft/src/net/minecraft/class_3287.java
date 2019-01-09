package net.minecraft;

import com.mojang.datafixers.Dynamic;
import java.util.Random;
import java.util.function.Function;
import java.util.stream.Stream;

public class class_3287 extends class_3667<class_3113> {
	public class_3287(Function<Dynamic<?>, ? extends class_3113> function) {
		super(function);
	}

	public Stream<class_2338> method_15923(Random random, class_3113 arg, class_2338 arg2) {
		Stream<class_2338> stream = Stream.empty();
		if (random.nextInt(14) == 0) {
			stream = Stream.concat(stream, Stream.of(arg2.method_10069(random.nextInt(16), 55 + random.nextInt(16), random.nextInt(16))));
			if (random.nextInt(4) == 0) {
				stream = Stream.concat(stream, Stream.of(arg2.method_10069(random.nextInt(16), 55 + random.nextInt(16), random.nextInt(16))));
			}

			return stream;
		} else {
			return Stream.empty();
		}
	}
}
