package net.minecraft;

import com.mojang.datafixers.Dynamic;
import java.util.function.Function;

public class class_3869 extends class_3805 {
	public class_3869(Function<Dynamic<?>, ? extends class_3111> function) {
		super(function);
	}

	@Override
	protected class_2680 method_16843(class_1936 arg) {
		return arg.method_8409().nextFloat() < 0.95F ? class_2246.field_10261.method_9564() : class_2246.field_10009.method_9564();
	}
}
