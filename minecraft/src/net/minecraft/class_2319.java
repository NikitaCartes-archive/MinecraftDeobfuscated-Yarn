package net.minecraft;

import com.google.gson.JsonObject;
import com.mojang.brigadier.arguments.ArgumentType;
import java.util.function.Supplier;

public class class_2319<T extends ArgumentType<?>> implements class_2314<T> {
	private final Supplier<T> field_10928;

	public class_2319(Supplier<T> supplier) {
		this.field_10928 = supplier;
	}

	@Override
	public void method_10007(T argumentType, class_2540 arg) {
	}

	@Override
	public T method_10005(class_2540 arg) {
		return (T)this.field_10928.get();
	}

	@Override
	public void method_10006(T argumentType, JsonObject jsonObject) {
	}
}
