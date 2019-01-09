package net.minecraft;

import com.mojang.datafixers.Dynamic;
import java.util.Random;
import java.util.function.Function;

public class class_3084 extends class_3207 {
	public class_3084(Function<Dynamic<?>, ? extends class_3111> function, boolean bl, int i, class_2680 arg, class_2680 arg2, boolean bl2) {
		super(function, bl, i, arg, arg2, bl2);
	}

	@Override
	protected int method_14062(Random random) {
		return this.field_13900 + random.nextInt(7);
	}
}
