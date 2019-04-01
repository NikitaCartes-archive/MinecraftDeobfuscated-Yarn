package net.minecraft;

import com.google.gson.JsonObject;
import java.util.function.Function;

public class class_1866<T extends class_1860<?>> implements class_1865<T> {
	private final Function<class_2960, T> field_9046;

	public class_1866(Function<class_2960, T> function) {
		this.field_9046 = function;
	}

	@Override
	public T method_8121(class_2960 arg, JsonObject jsonObject) {
		return (T)this.field_9046.apply(arg);
	}

	@Override
	public T method_8122(class_2960 arg, class_2540 arg2) {
		return (T)this.field_9046.apply(arg);
	}

	@Override
	public void method_8124(class_2540 arg, T arg2) {
	}
}
