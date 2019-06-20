package net.minecraft;

import com.mojang.datafixers.Dynamic;
import java.util.Random;
import java.util.function.Function;

public class class_3126 extends class_3038 {
	public class_3126(Function<Dynamic<?>, ? extends class_3111> function) {
		super(function);
	}

	@Override
	public class_2680 method_13175(Random random, class_2338 arg) {
		double d = class_1959.field_9324.method_15437((double)arg.method_10263() / 200.0, (double)arg.method_10260() / 200.0);
		if (d < -0.8) {
			int i = random.nextInt(4);
			switch (i) {
				case 0:
					return class_2246.field_10048.method_9564();
				case 1:
					return class_2246.field_10270.method_9564();
				case 2:
					return class_2246.field_10315.method_9564();
				case 3:
				default:
					return class_2246.field_10156.method_9564();
			}
		} else if (random.nextInt(3) > 0) {
			int i = random.nextInt(4);
			switch (i) {
				case 0:
					return class_2246.field_10449.method_9564();
				case 1:
					return class_2246.field_10573.method_9564();
				case 2:
					return class_2246.field_10554.method_9564();
				case 3:
				default:
					return class_2246.field_9995.method_9564();
			}
		} else {
			return class_2246.field_10182.method_9564();
		}
	}
}
