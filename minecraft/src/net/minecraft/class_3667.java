package net.minecraft;

import com.mojang.datafixers.Dynamic;
import java.util.Random;
import java.util.function.Function;
import java.util.stream.Stream;

public abstract class class_3667<DC extends class_2998> extends class_3284<DC> {
	public class_3667(Function<Dynamic<?>, ? extends DC> function) {
		super(function);
	}

	@Override
	public final Stream<class_2338> method_14452(class_1936 arg, class_2794<? extends class_2888> arg2, Random random, DC arg3, class_2338 arg4) {
		return this.method_15941(random, arg3, arg4);
	}

	protected abstract Stream<class_2338> method_15941(Random random, DC arg, class_2338 arg2);
}
