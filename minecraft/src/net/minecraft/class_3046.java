package net.minecraft;

import com.mojang.datafixers.Dynamic;
import java.util.Random;
import java.util.function.Function;

public class class_3046 extends class_3038 {
	private static final class_2248[] field_13628 = new class_2248[]{
		class_2246.field_10182,
		class_2246.field_10449,
		class_2246.field_10086,
		class_2246.field_10226,
		class_2246.field_10573,
		class_2246.field_10270,
		class_2246.field_10048,
		class_2246.field_10156,
		class_2246.field_10315,
		class_2246.field_10554,
		class_2246.field_9995,
		class_2246.field_10548
	};

	public class_3046(Function<Dynamic<?>, ? extends class_3111> function) {
		super(function);
	}

	@Override
	public class_2680 method_13175(Random random, class_2338 arg) {
		double d = class_3532.method_15350(
			(1.0 + class_1959.field_9324.method_15437((double)arg.method_10263() / 48.0, (double)arg.method_10260() / 48.0)) / 2.0, 0.0, 0.9999
		);
		class_2248 lv = field_13628[(int)(d * (double)field_13628.length)];
		return lv == class_2246.field_10086 ? class_2246.field_10449.method_9564() : lv.method_9564();
	}
}
